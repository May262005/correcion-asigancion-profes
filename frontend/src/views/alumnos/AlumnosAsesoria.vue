<template>
  <div class="asesoria-container">
    <nav class="nav">
      <div class="nav-container">
        <router-link to="/alumno" class="nav-figure">
          <img src="../../imagenes/logo.png" class="nav-logo" alt="Logo Cronos" />
        </router-link>
        <ul class="nav-list">
          <li class="nav-item"><router-link to="/alumno" class="nav-link">Inicio</router-link></li>
          <li class="nav-item"><router-link to="/alumno/perfil" class="nav-link">Perfil</router-link></li>
        </ul>
      </div>
    </nav>

    <main class="asesoria-main">
      <div class="asesoria-header">
        <button @click="goBack" class="btn-back-icon" title="Volver al Inicio">
          <i class="fas fa-arrow-left"></i>
        </button>
        <div class="header-text">
          <h1>Gestión de Asesorías</h1>
          <p>Agenda nuevas sesiones o consulta tu historial académico</p>
        </div>
        <div class="header-spacer"></div>
      </div>

      <div class="asesoria-grid">
        <!-- Formulario para agendar -->
        <div class="asesoria-card">
          <div class="card-header">
            <i class="fas fa-calendar-plus"></i>
            <h2>Agendar Nueva Cita</h2>
          </div>

          <form @submit.prevent="confirmarAgendamiento">
            <div class="form-group">
              <label>Profesor *</label>
              <select v-model="form.id_profesor" @change="onProfesorChange" required>
                <option value="" disabled>Selecciona un docente...</option>
                <option v-for="profe in profesores" :key="profe.id" :value="profe.id">
                  {{ mostrarNombreProfesor(profe) }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label>Tema o Duda específica *</label>
              <input 
                v-model="form.tema_duda" 
                type="text" 
                placeholder="Ej: Duda sobre el proyecto final"
                required 
              />
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Fecha *</label>
                <input 
                  v-model="form.fecha_asesoria" 
                  type="date" 
                  :min="fechaMinima" 
                  @change="cargarHorariosDisponibles"
                  required 
                />
              </div>

              <div class="form-group">
                <label>Hora *</label>
                <select 
                  v-model="form.hora_asesoria" 
                  :disabled="!horariosDisponibles.length || !form.fecha_asesoria"
                  required
                >
                  <option value="" disabled>
                    {{ !form.fecha_asesoria ? 'Selecciona fecha primero' : 
                       horariosDisponibles.length ? 'Selecciona una hora' : 'Sin horarios disponibles' }}
                  </option>
                  <option v-for="h in horariosDisponibles" :key="h" :value="h">
                    {{ h }}
                  </option>
                </select>
              </div>
            </div>

            <div class="form-buttons">
              <button type="submit" class="btn-primary" :disabled="enviando">
                <i class="fas fa-check"></i>
                {{ enviando ? 'Procesando...' : 'Confirmar Asesoría' }}
              </button>
              <button type="button" class="btn-secondary" @click="resetForm">
                <i class="fas fa-times"></i> Cancelar
              </button>
            </div>
          </form>
        </div>

        <!-- Historial de asesorías -->
        <div class="asesoria-card">
          <div class="card-header">
            <i class="fas fa-history"></i>
            <h2>Mi Historial</h2>
            <span class="historial-count">{{ historial.length }} asesorías</span>
          </div>

          <div class="table-container">
            <div v-if="cargandoHistorial" class="loading-history">
              <i class="fas fa-spinner fa-spin"></i>
              <p>Cargando historial...</p>
            </div>
            <div v-else-if="errorCargando" class="empty-history">
              <i class="fas fa-exclamation-triangle"></i>
              <p>Error al cargar el historial</p>
              <small>{{ errorMensaje }}</small>
            </div>
            <table v-else-if="historial.length > 0" class="historial-table">
              <thead>
                <tr>
                  <th>Profesor</th>
                  <th>Fecha / Hora</th>
                  <th>Tema</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="cita in historial" :key="cita.id">
                  <td data-label="Profesor">
                    <div class="date-cell">
                      <span>{{ obtenerNombreProfesor(cita.idProfesor) }}</span>
                    </div>
                  </td>
                  <td data-label="Fecha / Hora">
                    <div class="date-cell">
                      <span>{{ formatFecha(cita.fechaAsesoria) }}</span>
                      <small>{{ cita.horaAsesoria }}</small>
                    </div>
                  </td>
                  <td data-label="Asunto">{{ cita.temaDuda || 'Sin tema' }}</td>
                  <td data-label="Estado">
                    <span :class="['status-badge', cita.estado || 'pendiente']">
                      {{ getEstadoTexto(cita.estado || 'pendiente') }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
            <div v-else class="empty-history">
              <i class="fas fa-calendar-check"></i>
              <p>No tienes asesorías registradas aún.</p>
              <small>Completa el formulario para agendar tu primera asesoría</small>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/utils/axios-config'
import Swal from 'sweetalert2'

const router = useRouter()

const goBack = () => {
  router.push('/alumno')
}

// OBTENER ID DEL ESTUDIANTE DESDE id_rol
const idEstudiante = localStorage.getItem('id_rol')
console.log('=========================================')
console.log('🔴 ID_ROL del localStorage:', idEstudiante)
console.log('=========================================')

// Estado
const profesores = ref([])
const horariosDisponibles = ref([])
const historial = ref([])
const enviando = ref(false)
const cargandoHistorial = ref(false)
const errorCargando = ref(false)
const errorMensaje = ref('')

const form = ref({
  id_profesor: '',
  tema_duda: '',
  fecha_asesoria: '',
  hora_asesoria: ''
})

const fechaMinima = computed(() => {
  const hoy = new Date()
  hoy.setDate(hoy.getDate() + 1)
  return hoy.toISOString().split('T')[0]
})

const formatFecha = (fecha) => {
  if (!fecha) return ''
  const partes = fecha.split('-')
  return `${partes[2]}/${partes[1]}/${partes[0]}`
}

const getEstadoTexto = (estado) => {
  const estados = {
    'pendiente': 'Pendiente',
    'aceptada': 'Aceptada',
    'rechazada': 'Rechazada',
    'completada': 'Completada'
  }
  return estados[estado] || estado
}

const mostrarNombreProfesor = (profe) => {
  if (!profe) return 'Docente'
  return profe.nombre_mostrar || profe.nombreCompleto || 'Docente'
}

const obtenerNombreProfesor = (idProfesor) => {
  const profesor = profesores.value.find(p => Number(p.id) === Number(idProfesor))
  const nombre = mostrarNombreProfesor(profesor)
  return nombre !== 'Docente' ? nombre : `Profesor #${idProfesor}`
}

const resetForm = () => {
  form.value = {
    id_profesor: '',
    tema_duda: '',
    fecha_asesoria: '',
    hora_asesoria: ''
  }
  horariosDisponibles.value = []
}

const onProfesorChange = () => {
  form.value.fecha_asesoria = ''
  form.value.hora_asesoria = ''
  horariosDisponibles.value = []
}

const cargarProfesores = async () => {
  try {
    const res = await axios.get('/api/profesores')
    profesores.value = Array.isArray(res.data) ? res.data : []
    console.log('✅ Profesores cargados:', profesores.value.length)
  } catch (error) {
    console.error("❌ Error al cargar profesores:", error)
    Swal.fire('Error', 'No se pudieron cargar los profesores', 'error')
  }
}

const cargarHistorial = async () => {
  if (!idEstudiante) {
    console.error('❌ No hay id_rol en localStorage')
    errorCargando.value = true
    errorMensaje.value = 'No se encontró ID de estudiante'
    return
  }
  
  cargandoHistorial.value = true
  errorCargando.value = false
  
  try {
    const url = `/api/mentorship/asesorias/estudiante/${idEstudiante}`
    console.log('🔍 URL de consulta:', url)
    
    const res = await axios.get(url)
    
    if (res.data && Array.isArray(res.data)) {
      historial.value = res.data
      console.log(`✅ Historial cargado: ${historial.value.length} asesorías`)
    } else {
      historial.value = []
      console.log('⚠️ No hay asesorías')
    }
  } catch (error) {
    console.error("❌ Error al cargar historial:", error)
    errorCargando.value = true
    errorMensaje.value = error.response?.data?.message || 'Error al cargar historial'
    historial.value = []
    
    if (error.response?.status === 401) {
      Swal.fire('Sesión expirada', 'Inicia sesión nuevamente', 'warning')
      router.push('/login')
    }
  } finally {
    cargandoHistorial.value = false
  }
}

const cargarHorariosDisponibles = async () => {
  if (form.value.id_profesor && form.value.fecha_asesoria) {
    try {
      const res = await axios.get('/api/mentorship/asesorias/horarios-disponibles', {
        params: {
          idProfesor: form.value.id_profesor,
          fecha: form.value.fecha_asesoria
        }
      })
      horariosDisponibles.value = res.data
      form.value.hora_asesoria = ''
      
      if (horariosDisponibles.value.length === 0) {
        Swal.fire('Sin horarios', 'No hay horarios disponibles para esta fecha', 'info')
      }
    } catch (error) {
      console.error("Error al cargar horarios:", error)
      horariosDisponibles.value = []
    }
  } else {
    horariosDisponibles.value = []
  }
}

const confirmarAgendamiento = async () => {
  if (!form.value.id_profesor || !form.value.tema_duda || 
      !form.value.fecha_asesoria || !form.value.hora_asesoria) {
    Swal.fire('Campos incompletos', 'Completa todos los campos', 'warning')
    return
  }

  if (!idEstudiante) {
    Swal.fire('Error', 'No se encontró ID de estudiante', 'error')
    return
  }

  const profesorSeleccionado = profesores.value.find(p => p.id == form.value.id_profesor)
  
  const result = await Swal.fire({
    title: '¿Confirmar asesoría?',
    html: `
      <div style="text-align: left">
        <p><strong>Profesor:</strong> ${mostrarNombreProfesor(profesorSeleccionado) || 'No especificado'}</p>
        <p><strong>Fecha:</strong> ${formatFecha(form.value.fecha_asesoria)}</p>
        <p><strong>Hora:</strong> ${form.value.hora_asesoria}</p>
        <p><strong>Tema:</strong> ${form.value.tema_duda}</p>
      </div>
    `,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Sí, agendar',
    cancelButtonText: 'Cancelar',
    confirmButtonColor: '#3abef9'
  })

  if (result.isConfirmed) {
    enviando.value = true
    try {
      const payload = {
        idEstudiante: parseInt(idEstudiante),
        idProfesor: parseInt(form.value.id_profesor),
        idAsignatura: 1,
        temaDuda: form.value.tema_duda,
        fechaAsesoria: form.value.fecha_asesoria,
        horaAsesoria: `${form.value.hora_asesoria}:00`
      }

      console.log('📤 Enviando payload con camelCase:', payload)
      const response = await axios.post('/api/mentorship/asesorias', payload)
      console.log('✅ Respuesta:', response.data)

      await Swal.fire({
        title: '¡Éxito!',
        text: 'Asesoría agendada correctamente',
        icon: 'success',
        timer: 2000,
        showConfirmButton: false
      })
      
      await cargarHistorial()
      resetForm()
      
    } catch (error) {
      console.error("❌ Error al agendar:", error)
      let mensajeError = 'Error al agendar la asesoría'
      
      if (error.response?.data?.message) {
        mensajeError = error.response.data.message
      }
      
      Swal.fire('Error', mensajeError, 'error')
    } finally {
      enviando.value = false
    }
  }
}

onMounted(async () => {
  console.log('=========================================')
  console.log('🚀 INICIANDO COMPONENTE DE ASESORÍAS')
  console.log('👨‍🎓 ID Estudiante (id_rol):', idEstudiante)
  console.log('=========================================')
  
  await Promise.all([
    cargarProfesores(),
    cargarHistorial()
  ])
  
  console.log('✅ Componente inicializado correctamente')
})
</script>

<style scoped>
.asesoria-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.asesoria-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.asesoria-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30px;
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

.asesoria-grid {
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  gap: 30px;
}

.asesoria-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  padding: 30px;
  border: 1px solid #f0f0f0;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eef2f6;
}

.card-header i {
  font-size: 1.5rem;
  color: #3abef9;
}

.card-header h2 {
  font-size: 1.3rem;
  margin: 0;
  color: #2c3e50;
}

.historial-count {
  margin-left: auto;
  background: #e0f2fe;
  color: #0369a1;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-weight: 600;
  font-size: 0.85rem;
  margin-bottom: 8px;
  color: #475569;
}

.form-group select,
.form-group input {
  padding: 12px 15px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.form-group select:focus,
.form-group input:focus {
  outline: none;
  border-color: #3abef9;
  box-shadow: 0 0 0 3px rgba(58, 190, 249, 0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.form-buttons {
  display: flex;
  gap: 15px;
  margin-top: 25px;
}

.btn-primary {
  flex: 2;
  background: #3abef9;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
}

.btn-primary:hover:not(:disabled) {
  background: #29a9e0;
  transform: translateY(-1px);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  flex: 1;
  background: #f1f5f9;
  color: #64748b;
  border: 1px solid #e2e8f0;
  padding: 12px 20px;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: #e2e8f0;
}

.table-container {
  overflow-x: auto;
  max-height: 500px;
  overflow-y: auto;
}

.historial-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.85rem;
}

.historial-table th {
  text-align: left;
  padding: 12px;
  background: #f8fafc;
  color: #475569;
  font-weight: 600;
  border-bottom: 2px solid #e2e8f0;
  position: sticky;
  top: 0;
  z-index: 10;
}

.historial-table td {
  padding: 15px 12px;
  border-bottom: 1px solid #e2e8f0;
  color: #334155;
}

.historial-table tbody tr:hover {
  background-color: #f8fafc;
}

.date-cell {
  display: flex;
  flex-direction: column;
}

.date-cell span {
  font-weight: 500;
}

.date-cell small {
  font-size: 0.7rem;
  color: #3abef9;
  font-weight: 500;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.7rem;
  font-weight: 600;
  text-transform: uppercase;
}

.status-badge.pendiente {
  background: #fff7ed;
  color: #ea580c;
}

.status-badge.aceptada {
  background: #e0f2fe;
  color: #0369a1;
}

.status-badge.completada {
  background: #f0fdf4;
  color: #16a34a;
}

.status-badge.rechazada {
  background: #fef2f2;
  color: #dc2626;
}

.empty-history,
.loading-history {
  text-align: center;
  padding: 50px 20px;
  color: #94a3b8;
}

.empty-history i,
.loading-history i {
  font-size: 3rem;
  margin-bottom: 15px;
  color: #cbd5e1;
}

.empty-history p,
.loading-history p {
  margin: 0 0 8px 0;
}

.empty-history small {
  font-size: 0.8rem;
}

.loading-history i {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@media (max-width: 900px) {
  .asesoria-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .asesoria-main {
    padding: 20px 15px;
  }
}

@media (max-width: 600px) {
  .asesoria-header {
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

  .form-row {
    grid-template-columns: 1fr;
    gap: 0;
  }

  .form-buttons {
    flex-direction: column;
  }

  .form-buttons button {
    width: 100%;
  }

  .historial-table thead {
    display: none;
  }

  .historial-table tbody tr {
    display: block;
    margin-bottom: 15px;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 10px;
  }

  .historial-table td {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 12px;
    text-align: right;
    border-bottom: 1px solid #e2e8f0;
  }

  .historial-table td:last-child {
    border-bottom: none;
  }

  .historial-table td::before {
    content: attr(data-label);
    font-weight: 600;
    color: #334155;
    text-align: left;
    margin-right: 15px;
  }
}
</style>