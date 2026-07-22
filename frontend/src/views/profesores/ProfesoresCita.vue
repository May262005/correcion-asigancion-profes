<template>
  <div class="psicologia-container">
    <nav class="nav">
      <div class="nav-container">
        <router-link to="/profesor" class="nav-figure">
          <img src="../../imagenes/logo.png" class="nav-logo" alt="Logo Cronos" />
        </router-link>
        <label class="nav-toggle" for="nav-input">
          <input type="checkbox" id="nav-input" class="nav-input" />
        </label>
        <ul class="nav-list">
          <li class="nav-item"><router-link to="/profesor" class="nav-link">Inicio</router-link></li>
          <li class="nav-item"><router-link to="/profesor/perfil" class="nav-link">Perfil</router-link></li>
        </ul>
      </div>
    </nav>

    <main class="main-content">
      <!-- HEADER CON BOTÓN REGRESAR -->
      <div class="page-header">
        <div class="header-title-flex">
          <button @click="goBack" class="btn-back-icon" title="Volver al Inicio">
            <i class="fas fa-arrow-left"></i>
          </button>
          <div class="header-text">
            <h1>Agenda Psicológica</h1>
            <p>Seguimiento y gestión de citas de atención psicopedagógica.</p>
          </div>
          <div class="header-spacer"></div>
        </div>
      </div>

      <!-- Indicador de carga -->
      <div v-if="cargando" class="loading-container">
        <div class="spinner"></div>
        <p>Cargando citas...</p>
      </div>

      <!-- Contenido principal -->
      <div v-else class="view-grid single-column">
        <section class="history-section">
          <div class="card">
            <div class="card-header-flex">
              <div class="card-header">
                <i class="fas fa-brain"></i>
                <h2>Mis Sesiones Programadas</h2>
              </div>
              <div class="stats-badge">
                {{ citas.length }} Sesiones programadas
              </div>
            </div>
            
            <div class="table-container">
              <table v-if="citas.length > 0">
                <thead>
                  <tr>
                    <th>Alumno</th>
                    <th>Fecha / Hora</th>
                    <th>Motivo de Consulta</th>
                    <th>Estado</th>
                    <th class="text-center">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="cita in citas" :key="cita.id">
                    <td data-label="Alumno">
                      <div class="user-info-cell">
                        <div class="user-avatar-mini">{{ obtenerInicialNombre(cita) }}</div>
                        <span>{{ obtenerNombreCompleto(cita) }}</span>
                      </div>
                    </td>
                    <td data-label="Fecha / Hora">
                      <div class="dateTime-cell">
                        <span>{{ cita.fechaCita }}</span>
                        <small>{{ cita.horaCita }}</small>
                      </div>
                    </td>
                    <td data-label="Motivo de Consulta">{{ cita.motivoConsulta || 'No especificado' }}</td>
                    <td data-label="Estado">
                      <span :class="['status-badge', cita.estado.toLowerCase()]">
                        {{ cita.estado === 'pendiente' ? 'Pendiente' : 'Finalizada' }}
                      </span>
                    </td>
                    <td data-label="Acciones">
                      <div class="btn-actions-group">
                        <button 
                          v-if="cita.estado.toLowerCase() === 'pendiente'" 
                          @click="concluirCita(cita.id)" 
                          class="btn-icon-action check" 
                          title="Marcar como concluida"
                        >
                          <i class="fas fa-check-circle"></i>
                        </button>
                        <button 
                          @click="eliminarCita(cita.id)" 
                          class="btn-icon-action delete" 
                          title="Eliminar cita"
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
                <p>No hay citas programadas en tu agenda</p>
                <small>Las citas que agenden los alumnos aparecerán aquí automáticamente</small>
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

// Función para volver al inicio
const goBack = () => {
  router.push('/profesor')
}

// Obtener ID del profesor desde localStorage
const obtenerIdProfesor = () => {
  const idRol = localStorage.getItem('id_rol')
  if (idRol) {
    console.log('✅ ID obtenido desde id_rol:', idRol)
    return parseInt(idRol)
  }

  const token = localStorage.getItem('access_token')
  if (token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      if (payload?.sub) {
        console.log('✅ ID obtenido desde JWT (sub):', payload.sub)
        return parseInt(payload.sub)
      }
    } catch (e) {
      console.warn('No se pudo decodificar el token JWT:', e)
    }
  }

  const id = localStorage.getItem('id')
  if (id) {
    console.log('✅ ID obtenido desde id:', id)
    return parseInt(id)
  }

  console.error('❌ No se encontró ningún ID de profesor')
  return null
}

// Funciones de utilidad
const formatearFecha = (fechaISO) => {
  if (!fechaISO) return 'Fecha no disponible'
  const fecha = new Date(fechaISO)
  return fecha.toLocaleDateString('es-MX', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const formatearHora = (fechaISO) => {
  if (!fechaISO) return 'Hora no disponible'
  const fecha = new Date(fechaISO)
  return fecha.toLocaleTimeString('es-MX', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: true
  })
}

const obtenerNombreCompleto = (cita) => {
  const estudiante = estudiantes.value.find(e => Number(e.id) === Number(cita.idEstudiante))
  return estudiante?.nombreCompleto || `Alumno #${cita.idEstudiante}`
}

const obtenerInicialNombre = (cita) => {
  const nombreCompleto = obtenerNombreCompleto(cita)
  return nombreCompleto.startsWith('Alumno #') ? '#' : nombreCompleto.charAt(0).toUpperCase()
}

const citas = ref([])
const estudiantes = ref([])
const cargando = ref(false)

const cargarEstudiantes = async () => {
  try {
    const res = await axios.get('/api/estudiantes')
    estudiantes.value = Array.isArray(res.data) ? res.data : []
  } catch (error) {
    console.error('❌ Error al cargar estudiantes:', error)
    estudiantes.value = []
  }
}

// Cargar citas del profesor
const cargarCitas = async () => {
  const idProfesor = obtenerIdProfesor()
  
  if (!idProfesor) {
    console.error('❌ No se pudo identificar al profesor')
    await Swal.fire({
      title: 'Error de autenticación',
      text: 'No se pudo identificar al profesor. Por favor, inicia sesión nuevamente.',
      icon: 'error',
      confirmButtonText: 'Entendido'
    })
    return
  }

  cargando.value = true
  console.log(`📡 Cargando citas para el psicólogo ID: ${idProfesor}`)
  
  try {
    const res = await axios.get(`/api/psychology/citas/profesor/${idProfesor}`)
    citas.value = res.data
    console.log(`✅ Citas cargadas exitosamente: ${citas.value.length} registros`)
  } catch (error) {
    console.error('❌ Error al cargar la agenda:', error)
    
    if (error.response) {
      if (error.response.status === 404) {
        Swal.fire({
          title: 'Sin citas',
          text: 'No se encontraron citas programadas para ti en este momento',
          icon: 'info',
          confirmButtonText: 'Entendido'
        })
      } else if (error.response.status === 401) {
        Swal.fire({
          title: 'Sesión expirada',
          text: 'Por favor, inicia sesión nuevamente',
          icon: 'warning',
          confirmButtonText: 'Aceptar'
        }).then(() => {
          router.push('/login')
        })
      } else {
        Swal.fire({
          title: 'Error',
          text: `No se pudieron cargar las citas: ${error.response.data?.message || 'Error desconocido'}`,
          icon: 'error'
        })
      }
    } else if (error.request) {
      Swal.fire('Error de conexión', 'No se pudo conectar con el servidor', 'error')
    } else {
      Swal.fire('Error', 'Ocurrió un error inesperado', 'error')
    }
  } finally {
    cargando.value = false
  }
}

// Concluir cita
const concluirCita = async (id) => {
  const result = await Swal.fire({
    title: '¿Concluir sesión?',
    text: "La cita se marcará como finalizada en el historial del alumno.",
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Sí, finalizar',
    cancelButtonText: 'Cancelar',
    confirmButtonColor: '#16a34a'
  })

  if (result.isConfirmed) {
    try {
      await axios.patch(`/api/psychology/citas/${id}/estado?estado=completada`)
      Swal.fire('¡Hecho!', 'La sesión ha sido marcada como finalizada.', 'success')
      await cargarCitas()
    } catch (e) {
      console.error('Error al finalizar:', e)
      Swal.fire('Error', 'No se pudo actualizar el estado de la cita', 'error')
    }
  }
}

// Eliminar cita
const eliminarCita = async (id) => {
  const result = await Swal.fire({
    title: '¿Eliminar registro?',
    text: "Esta acción borrará la cita permanentemente. ¿Estás seguro?",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Sí, eliminar',
    cancelButtonText: 'Cancelar',
    confirmButtonColor: '#dc2626'
  })

  if (result.isConfirmed) {
    try {
      await axios.delete(`/api/psychology/citas/${id}`)
      Swal.fire('Eliminada', 'La cita ha sido eliminada correctamente.', 'success')
      await cargarCitas()
    } catch (e) {
      console.error('Error al eliminar:', e)
      Swal.fire('Error', 'No se pudo eliminar la cita', 'error')
    }
  }
}

// Cargar citas al montar el componente
onMounted(async () => {
  console.log('🚀 Componente de Agenda Psicológica montado')
  await Promise.all([
    cargarEstudiantes(),
    cargarCitas()
  ])
})
</script>

<style scoped>
/* =======================
   CRONOS - ESTILOS BASE Y NAVBAR
   Mantiene colores originales: #88B7F3, #3ABEF9, #213547, #DFF2FE
======================= */

/* ===== RESET Y BASE ===== */
.psicologia-container {
  background-color: #DFF2FE;
  min-height: 100vh;
  font-family: 'Inter', sans-serif;
  color: #213547;
  font-size: 16px;
  line-height: 24px;
  font-weight: 400;
}

/* ===== TIPOGRAFÍA ===== */
h1 {
  font-size: 32px;
  font-weight: 700;
  line-height: 40px;
  color: #213547;
  margin-bottom: 30px;
  text-align: center;
}

h2 {
  font-size: 24px;
  font-weight: 700;
  line-height: 32px;
  color: #213547;
  margin-bottom: 10px;
}

/* ===== NAVBAR ===== */
.nav {
  background: #88B7F3;
  position: relative;
  z-index: 1000;
  width: 100%;
}

.nav-container {
  width: 100%;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100px;
  --icon-toggle-menu: url(../imagenes/menu.svg);
  --transform-menu-show: translate(-100%);
}

.nav-container:has(.nav-input:checked) {
  --icon-toggle-menu: url(../imagenes/cerrar.svg);
  --transform-menu-show: translate(0);
}

.nav-figure {
  z-index: 1002;
  position: relative;
} 

.nav-logo {
  margin-left: 10px;
  margin-top: 8px;
  width: 100px;
}

.nav-toggle {
  z-index: 1002;
  position: relative;
  margin-top: 5px;
  width: 40px;
  height: 40px;
  margin-right: 10px;
  background-image: var(--icon-toggle-menu);
  background-size: cover;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-input {
  display: none;
  width: 40px;
  height: 40px;
  opacity: 0;
  cursor: pointer;
}

.nav-list {
  transform: var(--transform-menu-show);
  position: fixed;
  inset: 0;
  display: grid;
  gap: 2rem;
  padding: 1.5rem 5%;
  background-color: #88B7F3;
  list-style: none;
  place-content: center;
  transition: .4s transform;
  z-index: 1001;
}

.nav-item {
  text-align: center;
}

.nav-link {
  text-decoration: none;
  color: #f6f6f6;
  font-size: 30px;
  font-weight: 500;
  transition: color 0.3s;
}

.nav-link:hover {
  opacity: 0.85;
}

/* Desktop Navbar */
@media (min-width: 961px) {
  .nav {
    position: relative;
    z-index: 1000;
    width: 100%;
  }
  
  .nav-container {
    --transform-menu-show: translate(0);
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .nav-logo {
    margin-left: 10px;
    margin-top: 8px;
    width: 100px;
  }
  
  .nav-toggle {
    display: none;
  }
  
  .nav-list {
    position: static;
    display: flex;
    gap: 2rem;
    padding: 0;
    margin-right: 10px;
    background-color: transparent;
    transition: unset;
    z-index: auto;
  }
  
  .nav-link {
    font-size: 25px;
    margin-right: 10px;
  }
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

/* Loading */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  gap: 20px;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #A6E7F0;
  border-top-color: #3ABEF9;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Card */
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
.status-badge.finalizada { background: #f0fdf4; color: #16a34a; }

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

.empty-history small {
  display: block;
  margin-top: 8px;
  font-size: 0.8rem;
}

.main-content {
  padding: 20px;
  min-height: calc(100vh - 100px);
  max-width: 1200px;
  margin: 0 auto;
}

/* Responsive */
@media (max-width: 768px) {
  .main-content {
    padding: 1rem;
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
  
  .card-header-flex {
    flex-direction: column;
    text-align: center;
  }
  
  th, td {
    padding: 10px 8px;
  }
  
  .btn-icon-action {
    font-size: 1rem;
  }
}

@media (max-width: 600px) {
  .card {
    padding: 20px;
  }
  
  table, thead, tbody, th, td, tr {
    display: block;
  }
  
  thead {
    display: none;
  }
  
  tr {
    margin-bottom: 15px;
    border: 1px solid #f1f5f9;
    border-radius: 12px;
    padding: 10px;
  }
  
  td {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #f1f5f9;
    padding: 10px;
  }
  
  td:last-child {
    border-bottom: none;
  }
  
  td::before {
    content: attr(data-label);
    font-weight: bold;
    color: #64748b;
    font-size: 0.8rem;
  }
  
  .btn-actions-group {
    justify-content: flex-end;
  }
}
</style>