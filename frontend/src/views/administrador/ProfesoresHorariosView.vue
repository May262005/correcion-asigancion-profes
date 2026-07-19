<template>
  <div class="perfil-container-full">
    <nav class="nav">
      <div class="nav-container">
        <router-link to="/dashboard" class="nav-figure">
          <img src="../../imagenes/logo.png" class="nav-logo" alt="Logo Cronos" />
        </router-link>

        <label class="nav-toggle" for="menu-input">
          <input type="checkbox" id="menu-input" class="nav-input" />
        </label>

        <ul class="nav-list">
          <li class="nav-item"><router-link to="/dashboard" class="nav-link">Panel de control</router-link></li>
          <li class="nav-item"><router-link :to="{ name: 'perfil-admin' }" class="nav-link">Perfil</router-link></li>
        </ul>
      </div>
    </nav>

    <div v-if="cargandoAccion" class="action-loading">
      <div class="spinner"></div>
      <p>Procesando...</p>
    </div>

    <main class="main-content">
      <!-- HEADER CON BOTÓN REGRESAR -->
      <div class="page-header">
        <div class="header-title-flex">
          <button @click="goBack" class="btn-back-icon" title="Volver a Profesores">
            <i class="fas fa-arrow-left"></i>
          </button>
          <div class="header-text">
            <h1>Disponibilidad del Profesor</h1>
            <p v-if="nombreProfesor">Gestionando disponibilidad de <strong>{{ nombreProfesor }}</strong></p>
            <p v-else>Selecciona las horas disponibles del profesor</p>
          </div>
          <div class="header-spacer"></div>
        </div>
      </div>

      <div v-if="cargando" class="loading-container">
        <div class="spinner"></div>
        <p>Cargando información...</p>
      </div>

      <div v-else-if="profesorSeleccionado && horasMostradas.length" class="horario-card">
        <div class="card-header-flex">
          <div class="instr">
            <i class="fas fa-info-circle"></i>
            <span>Haz clic en los cuadros para marcar/desmarcar la disponibilidad del profesor.</span>
          </div>
          <div class="actions">
            <button @click="guardarCambios" class="btn-primary-save" :disabled="cargandoAccion">
              <i class="fas fa-save"></i> {{ cargandoAccion ? 'Guardando...' : 'Guardar Cambios' }}
            </button>
          </div>
        </div>

        <!-- Selector de turno -->
        <div class="turno-selector" v-if="turnos.length">
          <label for="turno">Filtrar por turno:</label>
          <select id="turno" v-model="turnoSeleccionado" @change="filtrarHorasPorTurno">
            <option value="">Todas las horas (7:00 - 22:00)</option>
            <option v-for="turno in turnos" :key="turno.id" :value="turno.id">
              {{ turno.nombre }}
            </option>
          </select>
        </div>

        <!-- Selector de color -->
        <div class="color-selector">
          <div
            v-for="color in colores"
            :key="color.estado"
            class="color-option"
            :class="{ active: color.estado === colorSeleccionado }"
            @click="seleccionarColor(color.estado)"
          >
            <div class="color-circle" :style="{ backgroundColor: color.color }"></div>
            <span>{{ color.nombre }}</span>
          </div>
        </div>

        <div class="tabla-disponibilidad-wrapper">
          <table class="tabla-dispo">
            <thead>
              <tr>
                <th class="hora-col">Hora</th>
                <th v-for="dia in diasSemana" :key="dia.valor">{{ dia.nombre }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="hora in horasMostradas" :key="hora">
                <td class="hora-label">{{ hora }}</td>
                <td
                  v-for="dia in diasSemana"
                  :key="dia.valor"
                  :class="['celda-dispo', getCeldaClase(dia.valor, hora)]"
                  @click="pintarCelda(dia.valor, hora)"
                >
                  <i v-if="estadoCelda(dia.valor, hora) === 'disponible'" class="fas fa-check"></i>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="btn-group">
          <router-link to="/profesores">
            <button class="btn-secondary">Cancelar</button>
          </router-link>
          <button class="btn-primary" @click="guardarCambios">Guardar Cambios</button>
        </div>
      </div>

      <div v-else class="empty-state">
        <div class="empty-content">
          <i class="fas fa-calendar-times"></i>
          <h3>No se pudo cargar la información</h3>
          <p>Por favor, intenta de nuevo más tarde</p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '../../utils/axios-config'
import '../../assets/styles.css'
import Swal from 'sweetalert2'

const router = useRouter()

const goBack = () => {
  router.back()
}

const API_HORARIOS = `http://localhost:3000/horario-profesor`
const API_PROFESORES = `http://localhost:3000/profesores`
const API_TURNOS = `/api/turnos`

const profesorSeleccionado = ref('')
const horariosProfesor = ref([])
const nombreProfesor = ref('')
const turnos = ref([])
const turnoSeleccionado = ref('')
const todasLasHoras = ref([])
const horasMostradas = ref([])
const cambiosPendientes = ref(new Set())

const cargando = ref(false)
const cargandoAccion = ref(false)

const diasSemana = [
  { valor: '1', nombre: 'Lunes' },
  { valor: '2', nombre: 'Martes' },
  { valor: '3', nombre: 'Miércoles' },
  { valor: '4', nombre: 'Jueves' },
  { valor: '5', nombre: 'Viernes' },
]

const colores = [
  { nombre: 'Disponible', estado: 'disponible', color: '#b8e1f4' },
  { nombre: 'No disponible', estado: 'no_disponible', color: '#eb9696' },
]

const colorSeleccionado = ref('disponible')

const seleccionarColor = (estado) => {
  colorSeleccionado.value = estado
}

const normalizarHora = (hora) => hora.slice(0, 5)

const generarTodasLasHoras = () => {
  const horas = []
  for (let h = 7; h <= 22; h++) {
    horas.push(h.toString().padStart(2, '0') + ':00')
  }
  todasLasHoras.value = horas
  horasMostradas.value = horas
}

// Función para obtener la clase de la celda (disponible o no_disponible)
const getCeldaClase = (dia, hora) => {
  const estado = estadoCelda(dia, hora)
  if (estado === 'disponible') return 'disponible'
  if (estado === 'no_disponible') return 'no_disponible'
  return ''
}

const cargarDatosProfesor = async () => {
  const id = useRoute().params.id
  if (!id) return

  cargando.value = true
  profesorSeleccionado.value = id
  
  generarTodasLasHoras()
  
  try {
    const resProf = await axios.get(`${API_PROFESORES}/${id}`)
    const prof = resProf.data
    nombreProfesor.value = `${prof.usuario?.nombre || ''} ${prof.usuario?.apellido_paterno || ''}`
    await cargarTurnos()
    await cargarHorarioProfesor()
  } catch (error) {
    console.error('Error al cargar datos del profesor:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'No se pudo cargar la información del profesor',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#E54848',
      width: '450px',
    })
  } finally {
    cargando.value = false
  }
}

const cargarTurnos = async () => {
  try {
    const res = await axios.get(API_TURNOS)
    turnos.value = res.data
  } catch (error) {
    console.error('Error al cargar turnos:', error)
  }
}

const filtrarHorasPorTurno = () => {
  if (!turnoSeleccionado.value) {
    horasMostradas.value = todasLasHoras.value
    return
  }

  const turno = turnos.value.find((t) => t.id === turnoSeleccionado.value)
  if (!turno) {
    horasMostradas.value = todasLasHoras.value
    return
  }

  const horaInicioRaw = turno.horaInicio || turno.hora_inicio
  const horaFinRaw = turno.horaFin || turno.hora_fin

  if (!horaInicioRaw || !horaFinRaw) {
    horasMostradas.value = todasLasHoras.value
    return
  }

  const horaInicio = parseInt(horaInicioRaw.split(':')[0])
  const horaFin = parseInt(horaFinRaw.split(':')[0])

  const horasFiltradas = []
  for (let h = horaInicio; h <= horaFin; h++) {
    horasFiltradas.push(h.toString().padStart(2, '0') + ':00')
  }
  horasMostradas.value = horasFiltradas
}

const cargarHorarioProfesor = async () => {
  try {
    const res = await axios.get(`${API_HORARIOS}/profesor/${profesorSeleccionado.value}`)
    
    horariosProfesor.value = res.data.map((h) => ({
      id: h.id,
      dia: String(h.dia),
      hora: normalizarHora(h.hora),
      estado: h.disponible ? 'disponible' : 'no_disponible',
      disponible: h.disponible,
      id_profesor: h.id_profesor || Number(profesorSeleccionado.value),
    }))
  } catch (error) {
    console.error('Error al cargar horario del profesor:', error)
    horariosProfesor.value = []
  }
}

const marcarCambio = (dia, hora) => {
  const clave = `${dia}-${hora}`
  cambiosPendientes.value.add(clave)
}

const pintarCelda = (dia, hora) => {
  const index = horariosProfesor.value.findIndex(
    (h) => h.dia === String(dia) && h.hora === hora
  )

  if (index !== -1) {
    const celda = horariosProfesor.value[index]

    if (celda.estado === colorSeleccionado.value) {
      // Si hace clic en el mismo color, eliminar el registro
      horariosProfesor.value.splice(index, 1)
      marcarCambio(dia, hora)
      return
    }

    // Cambiar estado
    celda.estado = colorSeleccionado.value
    celda.disponible = colorSeleccionado.value === 'disponible'
    marcarCambio(dia, hora)
    return
  }

  // Crear nuevo registro
  horariosProfesor.value.push({
    dia: String(dia),
    hora,
    estado: colorSeleccionado.value,
    disponible: colorSeleccionado.value === 'disponible',
    id_profesor: Number(profesorSeleccionado.value),
  })
  marcarCambio(dia, hora)
}

const estadoCelda = (dia, hora) => {
  const registro = horariosProfesor.value.find((h) => h.dia === String(dia) && h.hora === hora)
  return registro ? registro.estado : ''
}

const guardarCambios = async () => {
  if (cambiosPendientes.value.size === 0) {
    await Swal.fire({
      icon: 'info',
      title: 'Sin cambios',
      text: 'No hay modificaciones para guardar',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      width: '450px',
    })
    return
  }

  cargandoAccion.value = true
  try {
    const promesas = []

    for (const clave of cambiosPendientes.value) {
      const [dia, hora] = clave.split('-')
      const registro = horariosProfesor.value.find(
        (h) => h.dia === dia && h.hora === hora
      )

      if (registro) {
        if (registro.id) {
          promesas.push(
            axios.patch(`${API_HORARIOS}/${registro.id}`, {
              disponible: registro.disponible,
            })
          )
        } else {
          promesas.push(
            axios.post(API_HORARIOS, {
              dia: registro.dia,
              hora: registro.hora,
              disponible: registro.disponible,
              id_profesor: registro.id_profesor,
            })
          )
        }
      } else {
        const registroOriginal = horariosProfesor.value.find(
          (h) => h.dia === dia && h.hora === hora && h.id
        )
        if (registroOriginal?.id) {
          promesas.push(axios.delete(`${API_HORARIOS}/${registroOriginal.id}`))
        }
      }
    }

    await Promise.all(promesas)
    cargandoAccion.value = false
    
    await Swal.fire({
      icon: 'success',
      title: '¡Guardado!',
      text: 'Disponibilidad actualizada correctamente',
      showConfirmButton: false,
      timer: 2000,
      background: '#ffffff',
      color: '#213547',
      iconColor: '#3ABEF9',
      width: '450px',
    })

    cambiosPendientes.value.clear()
    router.push('/profesores')

  } catch (error) {
    cargandoAccion.value = false
    console.error('Error al guardar cambios:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'No se pudo guardar la disponibilidad',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#E54848',
      width: '450px',
    })
  } finally {
    cargandoAccion.value = false
  }
}

onMounted(cargarDatosProfesor)
</script>

<style scoped>
.perfil-container-full {
  background-color: #e3f2fd;
  min-height: 100vh;
  width: 100%;
}

.main-content {
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

/* Tarjeta */
.horario-card {
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
}

.card-header-flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f1f5f9;
  flex-wrap: wrap;
  gap: 15px;
}

.instr {
  color: #64748b;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* Selector de turno */
.turno-selector {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 25px;
  flex-wrap: wrap;
}

.turno-selector label {
  font-weight: 600;
  color: #475569;
}

.turno-selector select {
  padding: 8px 15px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: white;
  font-size: 0.9rem;
  cursor: pointer;
}

/* Selector de color */
.color-selector {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 30px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.color-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s;
  padding: 8px 16px;
  border-radius: 12px;
}

.color-option:hover {
  background: #f8fafc;
}

.color-option.active {
  background: #eef2ff;
}

.color-circle {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  margin-bottom: 8px;
  transition: all 0.2s;
  border: 2px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.color-option.active .color-circle {
  transform: scale(1.1);
}

.color-option span {
  font-size: 0.8rem;
  font-weight: 600;
  color: #475569;
}

/* Tabla */
.tabla-disponibilidad-wrapper {
  overflow-x: auto;
  margin-bottom: 30px;
}

.tabla-dispo {
  width: 100%;
  border-collapse: separate;
  border-spacing: 4px;
}

.tabla-dispo th {
  background: #f8fafc;
  color: #475569;
  padding: 12px;
  border-radius: 8px;
  font-size: 0.85rem;
  text-transform: uppercase;
}

.hora-label {
  background: #f1f5f9;
  color: #475569;
  font-weight: bold;
  text-align: center;
  padding: 10px;
  border-radius: 8px;
  width: 100px;
}

.celda-dispo {
  height: 45px;
  background: #fdfdfd;
  border: 2px dashed #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: center;
}

.celda-dispo:hover {
  border-color: #3abef9;
  background: #f0f9ff;
}

.celda-dispo.disponible {
  background: #b8e1f4 !important;
  border-style: solid;
  border-color: #b8e1f4;
}

.celda-dispo.no_disponible {
  background: #eb9696 !important;
  border-style: solid;
  border-color: #eb9696;
}

.celda-dispo.disponible i {
  color: #213547;
  font-size: 1rem;
}

/* Botones */
.btn-group {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eef2f6;
}

.btn-primary-save {
  background: #1e293b;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 10px;
  font-weight: bold;
  cursor: pointer;
  transition: 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-primary-save:hover {
  background: #0f172a;
  transform: translateY(-2px);
}

.btn-primary-save:disabled {
  background: #94a3b8;
  cursor: not-allowed;
}

.btn-primary {
  background: #3abef9;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 10px;
  font-weight: bold;
  cursor: pointer;
  transition: 0.3s;
}

.btn-primary:hover {
  background: #29a9e0;
}

.btn-secondary {
  background: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
  padding: 10px 24px;
  border-radius: 10px;
  font-weight: bold;
  cursor: pointer;
  transition: 0.3s;
}

.btn-secondary:hover {
  background: #e2e8f0;
}

/* Estados */
.loading-container {
  text-align: center;
  padding: 50px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e2e8f0;
  border-top-color: #3abef9;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 15px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.action-loading {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  padding: 30px;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  text-align: center;
  z-index: 9999;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 20px;
}

.empty-content i {
  font-size: 4rem;
  color: #cbd5e1;
  margin-bottom: 20px;
}

.empty-content h3 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.empty-content p {
  color: #7f8c8d;
}

/* Responsive */
@media (max-width: 768px) {
  .main-content {
    padding: 20px 15px;
  }
  
  .horario-card {
    padding: 20px;
  }
  
  .color-selector {
    gap: 15px;
  }
  
  .color-circle {
    width: 35px;
    height: 35px;
  }
  
  .tabla-dispo th,
  .tabla-dispo td {
    padding: 8px 4px;
    font-size: 0.75rem;
  }
  
  .hora-col, .hora-label {
    width: 70px;
    font-size: 0.7rem;
  }
  
  .turno-selector {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .btn-group {
    flex-direction: column;
  }
  
  .btn-group button {
    width: 100%;
  }
  
  .card-header-flex {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 600px) {
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
}

@media (max-width: 480px) {
  .tabla-dispo th,
  .tabla-dispo td {
    padding: 6px 2px;
    font-size: 0.7rem;
  }
  
  .hora-col, .hora-label {
    width: 60px;
    font-size: 0.65rem;
  }
  
  .color-option span {
    font-size: 0.7rem;
  }
  
  .color-circle {
    width: 30px;
    height: 30px;
  }
}
</style>