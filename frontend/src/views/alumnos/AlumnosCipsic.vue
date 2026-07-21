<template>
  <div class="psicologia-container">
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

    <main class="psicologia-main">
      <div class="psicologia-header">
        <button @click="goBack" class="btn-back-icon" title="Volver al Inicio">
          <i class="fas fa-arrow-left"></i>
        </button>
        <div class="header-text">
          <h1>Atención Psicológica</h1>
          <p>Un espacio seguro para tu bienestar emocional. Sesiones de 60 minutos.</p>
        </div>
        <div class="header-spacer"></div>
      </div>

      <div class="psicologia-grid">
        <div class="psicologia-card">
          <div class="card-header">
            <i class="fas fa-heart"></i>
            <h2>Agendar Cita</h2>
          </div>

          <form @submit.prevent="confirmarCita">
            <div class="form-group">
              <label>Especialista</label>
              <select v-model="form.id_profesor" required>
                <option value="" disabled>Selecciona un psicólogo(a)</option>
                <option v-for="psi in psicologos" :key="psi.id" :value="psi.id">
                  {{ psi.titulo }} {{ psi.usuario?.nombre }} {{ psi.usuario?.apellido_paterno }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label>Motivo de consulta (Confidencial)</label>
              <textarea v-model="form.motivo_consulta" placeholder="¿En qué podemos apoyarte hoy?" rows="3"></textarea>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Fecha sugerida</label>
                <input v-model="form.fecha_cita" type="date" :min="fechaMinima" required />
              </div>

              <div class="form-group">
                <label>Hora disponible</label>
                <select v-model="form.hora_cita" required>
                  <option value="" disabled>Elegir horario</option>
                  <option v-for="h in horariosDisponibles" :key="h" :value="h">
                    {{ h }} - {{ calcularHoraFin(h) }}
                  </option>
                </select>
              </div>
            </div>

            <div class="info-alert">
              <i class="fas fa-clock"></i>
              <span>Reserva bloqueada por 1 hora académica.</span>
            </div>

            <div class="form-buttons">
              <button type="submit" class="btn-primary" :disabled="enviando">
                <i class="fas fa-check"></i>
                {{ enviando ? 'Agendando...' : 'Confirmar Cita' }}
              </button>
              <button type="button" class="btn-secondary" @click="resetForm">
                <i class="fas fa-times"></i> Cancelar
              </button>
            </div>
          </form>
        </div>

        <div class="psicologia-card">
          <div class="card-header">
            <i class="fas fa-clipboard-list"></i>
            <h2>Mis Citas Previas</h2>
            <span class="historial-count">{{ historialCitas.length }} citas</span>
          </div>

          <div class="table-container">
            <div v-if="cargandoHistorial" class="loading-history">
              <i class="fas fa-spinner fa-spin"></i>
              <p>Cargando historial...</p>
            </div>
            <table v-else-if="historialCitas.length > 0" class="historial-table">
              <thead>
                <tr>
                  <th>Psicólogo(a)</th>
                  <th>Fecha / Hora</th>
                  <th>Motivo</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="cita in historialCitas" :key="cita.id">
                  <td data-label="Psicólogo(a)">
                    <div class="date-cell">
                      <span>Psicólogo #{{ cita.idProfesor }}</span>
                    </div>
                  </td>
                  <td data-label="Fecha / Hora">
                    <div class="date-cell">
                      <span>{{ formatFecha(cita.fechaCita) }}</span>
                      <small>{{ cita.horaCita }}</small>
                    </div>
                   </td>
                  <td data-label="Motivo">{{ cita.motivoConsulta || 'No especificado' }}</td>
                  <td data-label="Estado">
                    <span :class="['status-badge', cita.estado.toLowerCase()]">
                      {{ getEstadoTexto(cita.estado) }}
                    </span>
                   </td>
                </tr>
              </tbody>
            </table>
            <div v-else-if="!cargandoHistorial" class="empty-history">
              <i class="fas fa-calendar-check"></i>
              <p>No tienes citas registradas recientemente.</p>
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
const goBack = () => router.push('/alumno')

// OBTENER ID DEL ESTUDIANTE DESDE id_rol (misma lógica que asesorías)
const idEstudiante = localStorage.getItem('id_rol')
console.log('=========================================')
console.log('🔴 ID_ROL para psicología:', idEstudiante)
console.log('=========================================')

const psicologos = ref([])
const historialCitas = ref([])
const enviando = ref(false)
const cargandoHistorial = ref(false)
const horariosDisponibles = ref(['09:00', '10:00', '11:00', '12:00', '14:00', '15:00', '16:00', '17:00'])

const form = ref({
  id_profesor: '',
  motivo_consulta: '',
  fecha_cita: '',
  hora_cita: ''
})

const fechaMinima = computed(() => {
  const hoy = new Date()
  hoy.setDate(hoy.getDate() + 1)
  return hoy.toISOString().split('T')[0]
})

const calcularHoraFin = (h) => {
  if (!h) return ''
  const [hora, min] = h.split(':')
  return `${(parseInt(hora) + 1).toString().padStart(2, '0')}:${min}`
}

const formatFecha = (fecha) => {
  if (!fecha) return ''
  // Si es objeto Date o string ISO
  const fechaObj = new Date(fecha)
  if (!isNaN(fechaObj.getTime())) {
    return fechaObj.toLocaleDateString('es-MX')
  }
  return fecha
}

const getEstadoTexto = (estado) => {
  const estados = {
    'pendiente': 'Pendiente',
    'aceptada': 'Aceptada',
    'rechazada': 'Rechazada',
    'finalizada': 'Finalizada',
    'cancelada': 'Cancelada'
  }
  return estados[estado.toLowerCase()] || estado
}

const resetForm = () => {
  form.value = {
    id_profesor: '',
    motivo_consulta: '',
    fecha_cita: '',
    hora_cita: ''
  }
}

const cargarPsicologos = async () => {
  try {
    const res = await axios.get('/api/profesores/psicologos')
    psicologos.value = res.data
    console.log('✅ Psicólogos cargados:', psicologos.value.length)
  } catch (error) {
    console.error("❌ Error cargando psicólogos", error)
    Swal.fire('Error', 'No se pudieron cargar los psicólogos', 'error')
  }
}

const cargarHistorialCitas = async () => {
  if (!idEstudiante) {
    console.error('❌ No hay id_rol en localStorage')
    return
  }
  
  cargandoHistorial.value = true
  try {
    console.log('🔍 Consultando citas para estudiante ID:', idEstudiante)
    const res = await axios.get(`/api/psychology/citas/estudiante/${idEstudiante}`)
    
    if (res.data && Array.isArray(res.data)) {
      historialCitas.value = res.data
      console.log(`✅ Historial cargado: ${historialCitas.value.length} citas`)
    } else {
      historialCitas.value = []
    }
  } catch (error) {
    console.error("❌ Error historial:", error)
    historialCitas.value = []
    if (error.response?.status === 401) {
      Swal.fire('Sesión expirada', 'Inicia sesión nuevamente', 'warning')
      router.push('/login')
    }
  } finally {
    cargandoHistorial.value = false
  }
}

const confirmarCita = async () => {
  if (!form.value.id_profesor || !form.value.fecha_cita || !form.value.hora_cita) {
    Swal.fire('Campos incompletos', 'Completa todos los campos', 'warning')
    return
  }

  if (!idEstudiante) {
    Swal.fire('Error', 'No se encontró ID de estudiante', 'error')
    return
  }

  const result = await Swal.fire({
    title: '¿Confirmar cita?',
    text: `Se reservará el espacio el día ${form.value.fecha_cita} a las ${form.value.hora_cita}`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#3abef9',
    confirmButtonText: 'Sí, agendar',
    cancelButtonText: 'Cancelar'
  })

  if (result.isConfirmed) {
    enviando.value = true
    try {
      const payload = {
        idEstudiante: parseInt(idEstudiante),
        idProfesor: parseInt(form.value.id_profesor),
        fechaCita: form.value.fecha_cita,
        horaCita: form.value.hora_cita,
        motivoConsulta: form.value.motivo_consulta || 'Sin motivo especificado'
      }

      console.log('📤 Enviando payload con camelCase:', payload)
      const response = await axios.post('/api/psychology/citas', payload)
      console.log('✅ Respuesta:', response.data)

      await Swal.fire('¡Listo!', 'Tu cita ha sido agendada con éxito.', 'success')

      resetForm()
      await cargarHistorialCitas()

    } catch (error) {
      console.error("❌ Error al agendar:", error)
      const msg = error.response?.data?.message || 'Error interno del servidor al procesar la cita.'
      Swal.fire('Error', msg, 'error')
    } finally {
      enviando.value = false
    }
  }
}

onMounted(() => {
  console.log('🚀 INICIANDO COMPONENTE DE PSICOLOGÍA')
  console.log('👨‍🎓 ID Estudiante (id_rol):', idEstudiante)
  cargarPsicologos()
  cargarHistorialCitas()
})
</script>

<style scoped>
.psicologia-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.psicologia-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* Header con botón regresar */
.psicologia-header {
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

.psicologia-grid {
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  gap: 30px;
}

.psicologia-card {
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
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
.form-group input,
.form-group textarea {
  padding: 12px 15px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.form-group select:focus,
.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3abef9;
  box-shadow: 0 0 0 3px rgba(58, 190, 249, 0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.info-alert {
  background: #f0f9ff;
  border-left: 4px solid #3abef9;
  padding: 12px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 20px 0;
  font-size: 0.85rem;
  color: #0369a1;
}

.form-buttons {
  display: flex;
  gap: 15px;
  margin-top: 10px;
}

.btn-primary {
  flex: 2;
  background: #3abef9;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-primary:hover:not(:disabled) {
  background: #0ea5e9;
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
  padding: 12px;
  border-radius: 10px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: 0.2s;
}

.btn-secondary:hover {
  background: #e2e8f0;
}

.table-container {
  overflow-x: auto;
  max-height: 500px;
  overflow-y: auto;
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

.status-badge.finalizada {
  background: #f0fdf4;
  color: #16a34a;
}

.status-badge.rechazada, .status-badge.cancelada {
  background: #fef2f2;
  color: #dc2626;
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
  margin: 0;
}

.loading-history i {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* Responsive */
@media (max-width: 900px) {
  .psicologia-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .psicologia-main {
    padding: 20px 15px;
  }
}

@media (max-width: 600px) {
  .psicologia-header {
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