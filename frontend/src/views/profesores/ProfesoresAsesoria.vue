<template>
  <div class="asesoria-container">
    <nav class="nav">
      <div class="nav-container">
        <router-link to="/profesor" class="nav-figure">
          <img src="../../imagenes/logo.png" class="nav-logo" alt="Logo Cronos" />
        </router-link>
        <ul class="nav-list">
          <li class="nav-item"><router-link to="/profesor" class="nav-link">Inicio</router-link></li>
          <li class="nav-item"><router-link to="/profesor/perfil" class="nav-link">Perfil</router-link></li>
        </ul>
      </div>
    </nav>

    <main class="asesoria-content">
      <!-- HEADER CON BOTÓN REGRESAR -->
      <div class="page-header">
        <div class="header-title-flex">
          <button @click="goBack" class="btn-back-icon" title="Volver al Inicio">
            <i class="fas fa-arrow-left"></i>
          </button>
          <div class="header-text">
            <h1>Panel de Asesorías</h1>
            <p>Gestiona las solicitudes de tus alumnos y realiza el seguimiento académico.</p>
          </div>
          <div class="header-spacer"></div>
        </div>
      </div>

      <div class="view-grid single-column">
        <section class="history-section">
          <div class="card">
            <div class="card-header-flex">
              <div class="card-header">
                <i class="fas fa-clipboard-list"></i>
                <h2>Asesorías Solicitadas</h2>
              </div>
              <div class="stats-badge">
                {{ asesorias.length }} Sesiones programadas
              </div>
            </div>
            
            <div class="table-container">
              <table v-if="asesorias.length > 0">
                <thead>
                  <tr>
                    <th>Alumno</th>
                    <th>Fecha / Hora</th>
                    <th>Asunto / Tema</th>
                    <th>Estado</th>
                    <th class="text-center">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="cita in asesorias" :key="cita.id">
                    <td data-label="Alumno">
                      <div class="user-info-cell">
                        <div class="user-avatar-mini">{{ obtenerInicialEstudiante(cita.idEstudiante) }}</div>
                        <span>{{ obtenerNombreEstudiante(cita.idEstudiante) }}</span>
                      </div>
                    </td>
                    <td data-label="Fecha / Hora">
                      <div class="dateTime-cell">
                        <span>{{ cita.fechaAsesoria }}</span>
                        <small>{{ cita.horaAsesoria }}</small>
                      </div>
                    </td>
                    <td data-label="Asunto">{{ cita.temaDuda || 'Sin tema' }}</td>
                    <td data-label="Estado">
                      <span :class="['status-badge', (cita.estado || 'pendiente').toLowerCase()]">
                        {{ cita.estado || 'pendiente' }}
                      </span>
                    </td>
                    <td data-label="Acciones">
                      <div class="btn-actions-group">
                        <button 
                          v-if="(cita.estado || 'pendiente').toLowerCase() === 'pendiente'"
                          @click="marcarComoTomada(cita.id)" 
                          class="btn-icon-action check" 
                          title="Marcar como tomada"
                        >
                          <i class="fas fa-check-circle"></i>
                        </button>
                        
                        <button 
                          @click="eliminarAsesoria(cita.id)" 
                          class="btn-icon-action delete" 
                          title="Eliminar asesoría"
                        >
                          <i class="fas fa-trash-alt"></i>
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
              <div v-else class="empty-history">
                <i class="fas fa-calendar-check"></i>
                <p>No tienes asesorías pendientes por el momento.</p>
              </div>
            </div>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/utils/axios-config'
import Swal from 'sweetalert2'

const router = useRouter()

const goBack = () => {
  router.push('/profesor')
}

const obtenerIdProfesor = () => {
  // 1. Prioridad: id_rol del localStorage
  const idRol = localStorage.getItem('id_rol')
  if (idRol) {
    console.log('ID obtenido desde id_rol:', idRol)
    return idRol
  }

  // 2. Fallback: JWT sub
  const token = localStorage.getItem('access_token')
  if (token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      if (payload?.sub) {
        console.log('ID obtenido desde JWT (sub):', payload.sub)
        return payload.sub
      }
    } catch (e) {
      console.warn('No se pudo decodificar el token JWT:', e)
    }
  }

  // 3. Último fallback: id directo
  const id = localStorage.getItem('id')
  if (id) {
    console.log('ID obtenido desde id:', id)
    return id
  }

  console.error('No se encontró ningún ID de profesor')
  return null
}

const idProfesor = obtenerIdProfesor()
const asesorias = ref([])
const estudiantes = ref([])

onMounted(async () => {
  await Promise.all([
    cargarEstudiantes(),
    cargarAsesorias()
  ])
})

const obtenerNombreEstudiante = (idEstudiante) => {
  const estudiante = estudiantes.value.find(e => Number(e.id) === Number(idEstudiante))
  return estudiante?.nombreCompleto || `Alumno #${idEstudiante}`
}

const obtenerInicialEstudiante = (idEstudiante) => {
  const nombre = obtenerNombreEstudiante(idEstudiante)
  return nombre.startsWith('Alumno #') ? '#' : nombre.charAt(0).toUpperCase()
}

const cargarEstudiantes = async () => {
  try {
    const res = await axios.get('/api/estudiantes')
    estudiantes.value = Array.isArray(res.data) ? res.data : []
  } catch (error) {
    console.error('Error al cargar estudiantes', error)
    estudiantes.value = []
  }
}

const cargarAsesorias = async () => {
  if (!idProfesor) {
    console.error('No se pudo identificar al profesor')
    return
  }
  try {
    const res = await axios.get(`/api/mentorship/asesorias/profesor/${idProfesor}`)
    asesorias.value = res.data
  } catch (error) {
    console.error("Error al cargar asesorías", error)
  }
}

const marcarComoTomada = async (id) => {
  const result = await Swal.fire({
    title: '¿Confirmar asesoría tomada?',
    text: "La sesión se marcará como completada en el historial del alumno.",
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Sí, completar',
    cancelButtonText: 'Cancelar',
    confirmButtonColor: '#16a34a'
  })

  if (result.isConfirmed) {
    try {
      await axios.patch(`/api/mentorship/asesorias/${id}/estado?estado=completada`)
      Swal.fire('¡Hecho!', 'La asesoría ha sido marcada como tomada.', 'success')
      cargarAsesorias()
    } catch (error) {
      Swal.fire('Error', 'No se pudo actualizar el estado.', 'error')
    }
  }
}

const eliminarAsesoria = async (id) => {
  const result = await Swal.fire({
    title: '¿Eliminar asesoría?',
    text: "Esta acción no se puede deshacer y notificará al alumno.",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Sí, eliminar',
    cancelButtonText: 'No, mantener',
    confirmButtonColor: '#dc2626'
  })

  if (result.isConfirmed) {
    try {
      await axios.delete(`/api/mentorship/asesorias/${id}`)
      Swal.fire('Eliminada', 'El registro ha sido borrado.', 'success')
      cargarAsesorias()
    } catch (error) {
      Swal.fire('Error', 'Hubo un problema al eliminar.', 'error')
    }
  }
}
</script>

<style scoped>
.asesoria-container {
  background-color: #e3f2fd;
  min-height: 100vh;
}

.asesoria-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* Header con botón regresar */
.page-header {
  margin-bottom: 30px;
}

.header-title-flex {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  position: relative;
}

.btn-back-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: white;
  color: #3abef9;
  border-radius: 50%;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
  cursor: pointer;
  flex-shrink: 0;
}

.btn-back-icon i {
  font-size: 1rem;
  transition: transform 0.3s ease;
}

.btn-back-icon:hover {
  background: #3abef9;
  color: white;
  border-color: #3abef9;
  transform: translateX(-3px);
  box-shadow: 0 4px 10px rgba(58, 190, 249, 0.25);
}

.btn-back-icon:hover i {
  transform: translateX(-2px);
}

.header-text {
  flex: 1;
  text-align: center;
}

.header-text h1 {
  font-size: 2rem;
  color: #2c3e50;
  margin-bottom: 5px;
}

.header-text p {
  color: #7f8c8d;
  margin: 0;
}

.header-spacer {
  width: 40px;
  flex-shrink: 0;
}

.card {
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
}

.card-header-flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  flex-wrap: wrap;
  gap: 15px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #3abef9;
}

.card-header h2 {
  font-size: 1.3rem;
  margin: 0;
  color: #2c3e50;
}

.stats-badge {
  background: #e0f2fe;
  color: #0369a1;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 700;
}

.table-container {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  text-align: left;
  padding: 12px;
  background: #f8fafc;
  color: #64748b;
  font-size: 0.85rem;
}

td {
  padding: 15px 12px;
  border-bottom: 1px solid #f1f5f9;
  font-size: 0.95rem;
}

.user-info-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar-mini {
  width: 30px;
  height: 30px;
  background: #3abef9;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.8rem;
}

.dateTime-cell {
  display: flex;
  flex-direction: column;
}

.dateTime-cell small {
  color: #3abef9;
  font-weight: 500;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: bold;
}

.status-badge.pendiente { background: #fff7ed; color: #ea580c; }
.status-badge.completada { background: #f0fdf4; color: #16a34a; }
.status-badge.cancelada { background: #fef2f2; color: #dc2626; }

.btn-actions-group {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.btn-icon-action {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  transition: transform 0.2s;
}

.btn-icon-action.check { color: #16a34a; }
.btn-icon-action.delete { color: #ef4444; }

.btn-icon-action:hover {
  transform: scale(1.2);
}

.empty-history {
  text-align: center;
  padding: 50px;
  color: #94a3b8;
}

.empty-history i {
  font-size: 3rem;
  color: #cbd5e1;
  margin-bottom: 15px;
}

@media (max-width: 600px) {
  .asesoria-content {
    padding: 20px 15px;
  }
  
  .header-title-flex {
    flex-wrap: wrap;
    justify-content: center;
    gap: 10px;
  }
  
  .header-spacer {
    display: none;
  }
  
  .btn-back-icon {
    position: absolute;
    left: 0;
    top: 0;
  }
  
  .header-text {
    flex: none;
    width: 100%;
    margin-top: 10px;
  }
  
  .header-text h1 {
    font-size: 1.5rem;
  }
  
  th, td {
    padding: 10px 8px;
  }
  
  .btn-icon-action {
    font-size: 1rem;
  }
}
</style>