from sqlalchemy import text
from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from fastapi.middleware.cors import CORSMiddleware 
from db_connector.database import get_db 
from db_connector.data_access import fetch_all_data_for_solver
from solver_service.scheduler import generate_schedule_for_all_groups, ScheduleResult

import requests
import asyncio
from contextlib import asynccontextmanager

# --- CONFIGURACIÓN DE REGISTRO EN EUREKA (NATAL DE PYTHON) ---
EUREKA_SERVER = "http://eureka-server:8761/eureka/apps/GENE-SERVICE"
INSTANCE_DATA = {
    "instance": {
        "instanceId": "gene-service:8000",
        "hostName": "gene-service",
        "app": "GENE-SERVICE",
        "ipAddr": "gene-service",
        "status": "UP",
        "overriddenStatus": "UNKNOWN",
        "port": {"$": 8000, "@enabled": "true"},
        "securePort": {"$": 443, "@enabled": "false"},
        "countryId": 1,
        "dataCenterInfo": {
            "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
            "name": "MyOwn"
        },
        "metadata": {"@class": "java.util.Collections$EmptyMap"}
    }
}

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Función para intentar registrarse en bucle hasta tener éxito
    def register_in_eureka():
        while True:
            try:
                response = requests.post(EUREKA_SERVER, json=INSTANCE_DATA, timeout=5)
                # El código 204 o 200 significa éxito total
                if response.status_code in [200, 204]:
                    print("✅ [Cronos] Registrado con éxito en el servidor Eureka")
                    break
                else:
                    print(f"⏳ Eureka respondió con código {response.status_code}. Reintentando en 5 segundos...")
            except Exception as e:
                print("⏳ Esperando a que Eureka Server inicie...")
            
            import time
            time.sleep(5)

    # Lanzamos el registro inicial
    register_in_eureka()
    
    # Mantener latido (Heartbeat) cada 30 segundos
    async def send_heartbeat():
        while True:
            await asyncio.sleep(30)
            try:
                # Si Eureka lo llega a borrar temporalmente por latencia, el PUT le recordará existir
                response = requests.put(f"{EUREKA_SERVER}/gene-service:8000", timeout=5)
                if response.status_code == 404:
                    # Si Eureka responde 404, significa que perdió el registro; volvemos a enviarlo
                    requests.post(EUREKA_SERVER, json=INSTANCE_DATA, timeout=5)
            except Exception:
                pass
                
    asyncio.create_task(send_heartbeat())
    yield
    
    # Al apagar: Eliminar de Eureka
    try:
        requests.delete(f"{EUREKA_SERVER}/gene-service:8000", timeout=5)
        print("🗑️ Eliminado de Eureka con éxito")
    except Exception:
        pass
# Agregamos el ciclo de vida (lifespan) a FastAPI
app = FastAPI(title="Solver Service", lifespan=lifespan)

origins = ["*"] 

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
    expose_headers=["*"],
)

@app.post("/generate")
async def generate_schedule_endpoint(
    db: Session = Depends(get_db) 
):
    """
    ¡NUEVA ESTRATEGIA! Genera horarios asignando MATERIA POR MATERIA.
    
    Características:
    - Usa aulas asignadas a profesores (tabla profesor_aula)
    - Asigna por materia: todos los grupos de ADS, luego Inglés, etc.
    - Inglés consistente: mismo horario para todos los bloques de un grupo
    - Sin conflictos de aulas: verificación global
    """
    try:
        # ============================================
        # 1. CARGAR DATOS
        # ============================================
        data = fetch_all_data_for_solver(db)
        
        print("\n" + "="*60)
        print("--- INICIANDO GENERACIÓN DE HORARIOS ---")
        print(f"Cursos: {len(data['courses'])}")
        print(f"Aulas: {len(data['rooms'])}")
        print(f"Slots: {len(data['timeslots'])}")
        print(f"Profesores: {len(data['professors'])}")
        print(f"Grupos: {len(data.get('groups', []))}")
        print(f"Asignaciones profesor-materia-grupo: {len(data.get('professor_course_group_assignments', []))}")
        print(f"Aulas asignadas a profesores: {len(data.get('professor_rooms', {}))}")
        print("="*60 + "\n")
        
        # ============================================
        # 2. VALIDACIONES
        # ============================================
        
        # Verificar asignaciones profesor-materia-grupo
        if not data.get('professor_course_group_assignments'):
            print("⚠️ ADVERTENCIA CRÍTICA: No hay asignaciones profesor-materia-grupo")
            print("   El sistema necesita datos en la tabla 'profesor_asignatura_grupo'")
            print("\n   Ejecuta este SQL para crear asignaciones:\n")
            print("""
            INSERT INTO profesor_asignatura_grupo (id_profesor_asignatura, id_grupo)
            SELECT pa.id, g.id
            FROM profesor_asignatura pa
            CROSS JOIN grupo g
            WHERE NOT EXISTS (
                SELECT 1 FROM profesor_asignatura_grupo pag 
                WHERE pag.id_profesor_asignatura = pa.id 
                AND pag.id_grupo = g.id
            );
            """)
            raise HTTPException(
                status_code=400,
                detail="No hay asignaciones profesor-materia-grupo. Verifica la tabla 'profesor_asignatura_grupo'."
            )
        
        # Verificar aulas de profesores
        if not data.get('professor_rooms'):
            print("⚠️ ADVERTENCIA: No hay aulas asignadas a profesores")
            print("   Verifica la tabla 'profesor_aula' con id_periodo = 1")
            print("\n   Ejecuta este SQL para asignar aulas a profesores:\n")
            print("""
            INSERT INTO profesor_aula (id_profesor, id_aula, id_periodo)
            SELECT p.id, a.id, 1
            FROM profesor p
            CROSS JOIN LATERAL (
                SELECT id FROM aula LIMIT 1 OFFSET (p.id - 1) % (SELECT COUNT(*) FROM aula)
            ) a
            WHERE NOT EXISTS (
                SELECT 1 FROM profesor_aula pa 
                WHERE pa.id_profesor = p.id AND pa.id_periodo = 1
            );
            """)
            print("   ⚠️ Continuando sin aulas asignadas... esto causará errores\n")
        
        # Verificar grupos
        groups_to_process = data.get('groups', [])
        
        if not groups_to_process:
            raise HTTPException(
                status_code=400, 
                detail="No se encontraron grupos en la base de datos"
            )

        # ============================================
        # 3. PREPARAR MAPEOS
        # ============================================
        professor_map = {p.id: p.name for p in data['professors']}
        course_map = {c.id: c.name for c in data['courses']}
        room_map = {r.id: r.name for r in data['rooms']}
        building_map = {r.id: r.building_name or "N/A" for r in data['rooms']}
        timeslot_map = {ts.id: (ts.day, ts.start_time) for ts in data['timeslots']}
        
        # ============================================
        # 4. LIMPIAR HORARIOS ANTERIORES
        # ============================================
        print("🗑️  Limpiando horarios anteriores...")
        group_ids_str = ','.join([str(g.id) for g in groups_to_process])
        db.execute(text(f"DELETE FROM horario_clases WHERE id_grupo IN ({group_ids_str})"))
        db.commit()
        print("✅ Horarios anteriores eliminados\n")
        
        # ============================================
        # 5. GENERAR HORARIOS (NUEVA ESTRATEGIA)
        # ============================================
        all_schedules = generate_schedule_for_all_groups(
            courses=data['courses'], 
            rooms=data['rooms'], 
            timeslots=data['timeslots'], 
            professors=data['professors'],
            assignments=data['professor_course_group_assignments'],
            professor_rooms=data.get('professor_rooms', {}),
            groups=groups_to_process
        )
        
        # ============================================
        # 6. GUARDAR EN BASE DE DATOS
        # ============================================
        print("\n" + "="*60)
        print("💾 GUARDANDO HORARIOS EN BASE DE DATOS")
        print("="*60 + "\n")
        
        final_results = []
        
        for group in groups_to_process:
            group_id = group.id
            schedule = all_schedules.get(group_id, {})
            
            if not schedule:
                print(f"⚠️ No se generó horario para Grupo {group_id}")
                continue
            
            print(f"💾 Guardando horario para {group.name}...")
            
            group_schedule_data = {}
            saved_count = 0
            
            for block_id, (slot_id, room_id, course_id) in schedule.items():
                # Extraer día y hora del slot_id
                id_dia = slot_id // 1000
                id_hora_entera = slot_id % 1000 
                
                # Obtener información del curso
                course = next((c for c in data['courses'] if c.id == course_id), None)
                if not course:
                    print(f"  ⚠️ Curso {course_id} no encontrado")
                    continue
                
                # Obtener el profesor correcto para este grupo y materia
                assignment = next(
                    (a for a in data['professor_course_group_assignments'] 
                     if a.group_id == group_id and a.course_id == course_id), 
                    None
                )
                
                if not assignment:
                    print(f"  ⚠️ No se encontró asignación profesor-materia para grupo {group_id}, materia {course_id}")
                    continue
                
                id_profesor = assignment.professor_id
                id_profesor_asignatura = assignment.professor_asignatura_id
                
                # Formatear para respuesta JSON
                dia_str, hora_str_raw = timeslot_map.get(slot_id, ('Desconocido', 'Desconocida'))
                hora_formateada = str(hora_str_raw)[:5] if hora_str_raw else '00:00'
                
                class_info = {
                    "materia": course_map.get(course_id, "Materia Desconocida"),
                    "profesor": professor_map.get(id_profesor, "Profesor Desconocido"),
                    "aula": room_map.get(room_id, "Aula Desconocida"),
                    "edificio": building_map.get(room_id, "N/A")
                }

                if dia_str not in group_schedule_data:
                    group_schedule_data[dia_str] = {}
                
                group_schedule_data[dia_str][hora_formateada] = class_info
                
                # Insertar en horario_clases
                try:
                    insert_query = text("""
                        INSERT INTO horario_clases (
                            id_profesor_asignatura, 
                            id_aula, 
                            id_grupo, 
                            dia, 
                            hora 
                        ) 
                        VALUES (
                            :prof_asig, 
                            :aula, 
                            :grupo, 
                            :dia, 
                            :hora
                        )
                    """)
                    
                    db.execute(
                        insert_query,
                        {
                            "prof_asig": id_profesor_asignatura,
                            "aula": room_id,
                            "grupo": group_id,
                            "dia": id_dia,
                            "hora": f"{id_hora_entera}:00:00"
                        }
                    )
                    saved_count += 1
                    
                except Exception as e:
                    print(f"  ❌ Error al insertar clase: {e}")
                    continue
            
            # Commit por grupo
            try:
                db.commit()
                print(f"  ✅ {saved_count} clases guardadas para {group.name}")
            except Exception as e:
                db.rollback()
                print(f"  ❌ Error al guardar grupo {group.name}: {e}")
                continue
            
            # Agregar a resultados finales
            final_results.append({
                "id": group_id,
                "nombre": group.name,
                "tutor": getattr(group, 'tutor', 'N/A'), 
                "data": group_schedule_data
            })
        
        # ============================================
        # 7. RESUMEN FINAL
        # ============================================
        print("\n" + "="*60)
        print("✅ PROCESO COMPLETADO")
        print("="*60)
        print(f"📊 Grupos procesados: {len(final_results)}/{len(groups_to_process)}")
        print(f"💾 Horarios guardados en base de datos")
        print("="*60 + "\n")
        
        return final_results 
        
    except HTTPException as e:
        db.rollback() 
        raise e
    except Exception as e:
        db.rollback()
        print(f"\n❌ ERROR CRÍTICO: {e}")
        import traceback
        traceback.print_exc()
        raise HTTPException(
            status_code=500, 
            detail=f"Error en generación: {str(e)}"
        )


@app.get("/")
async def root():
    """Endpoint raíz para verificar que el servicio está funcionando."""
    return {
        "message": "Solver Service - Sistema de Generación de Horarios",
        "version": "2.0",
        "strategy": "Asignación por Materia",
        "features": [
            "Aulas asignadas por profesor",
            "Asignación por materia",
            "Inglés con hora consistente",
            "Sin conflictos de aulas"
        ]
    }


@app.get("/health")
async def health_check(db: Session = Depends(get_db)):
    """Verifica el estado del servicio y la conexión a la base de datos."""
    try:
        # Verificar conexión a la base de datos
        db.execute(text("SELECT 1"))
        
        # Verificar tablas críticas
        tables_check = {}
        
        critical_tables = [
            "profesor",
            "asignatura", 
            "aula",
            "grupo",
            "profesor_asignatura",
            "profesor_asignatura_grupo",
            "profesor_aula",
            "horario_clases"
        ]
        
        for table in critical_tables:
            try:
                result = db.execute(text(f"SELECT COUNT(*) FROM {table}")).scalar()
                tables_check[table] = {"status": "ok", "count": result}
            except Exception as e:
                tables_check[table] = {"status": "error", "error": str(e)}
        
        return {
            "status": "healthy",
            "database": "connected",
            "tables": tables_check
        }
        
    except Exception as e:
        return {
            "status": "unhealthy",
            "database": "disconnected",
            "error": str(e)
        }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)