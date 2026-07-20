<template>
  <div>
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

    <main class="turnos-main">
      <div class="turnos-header">
        <button @click="goBack" class="btn-back-icon" title="Volver al Dashboard">
          <i class="fas fa-arrow-left"></i>
        </button>
        <div class="header-text">
          <h1>Lista de Turnos</h1>
          <p>Gestiona los turnos y horarios de la institución</p>
        </div>
        <div class="header-spacer"></div>
      </div>

      <div v-if="cargando" class="loading-container">
        <div class="spinner"></div>
        <p>Cargando información...</p>
      </div>

      <div class="turnos-card" v-else-if="turnos.length > 0">
        <div class="table-container">
          <table class="styled-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Día Inicio</th>
                <th>Día Fin</th>
                <th>Hora Inicio</th>
                <th>Hora Fin</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="turno in currentTurnos" :key="turno.id">
                <td :data-label="'Nombre'">{{ turno.nombre }}</td>
                <td :data-label="'Día Inicio'">{{ obtenerNombreDia(turno.diaInicio) }}</td>
                <td :data-label="'Día Fin'">{{ obtenerNombreDia(turno.diaFin) }}</td>
                <td :data-label="'Hora Inicio'">{{ turno.horaInicio }}</td>
                <td :data-label="'Hora Fin'">{{ turno.horaFin }}</td>
                <td :data-label="'Acciones'">
                  <button class="btn-secondary btn-accion" @click="editarTurno(turno)">Editar</button>
                  <button class="btn-danger btn-accion" @click="eliminarTurno(turno.id)">Eliminar</button>
                </td>
              </tr>
            </tbody>
          </table>

          <div class="pagination">
            <button
              v-for="pageNumber in totalPages"
              :key="pageNumber"
              @click="handlePageChange(pageNumber)"
              :class="{ active: pageNumber === currentPage }"
            >
              {{ pageNumber }}
            </button>
          </div>
        </div>

        <div class="btn-div">
          <button class="btn-primary" @click="abrirFormularioNuevo">
            <i class="fas fa-plus"></i> Agregar Turno
          </button>
        </div>
      </div>

      <div v-else class="empty-state">
        <div class="empty-content">
          <i class="fas fa-clock"></i>
          <h3>No hay turnos registrados</h3>
          <p>Comienza agregando tu primer turno al sistema</p>
        </div>
      </div>
    </main>

    <div v-if="mostrarFormulario" class="form-overlay" @click.self="cerrarFormulario">
      <div class="form-modal">
        <div class="modal-header">
          <h2>{{ modoEdicion ? 'Editar Turno' : 'Agregar Turno' }}</h2>
          <button class="close-btn" @click="cerrarFormulario">&times;</button>
        </div>

        <div class="modal-body">
          <form @submit.prevent="guardarTurno">
            <div class="form-grid-layout">
              <div class="input-group full-width">
                <label>Nombre del turno</label>
                <input v-model="formTurno.nombre" type="text" placeholder="Nombre del turno" required />
              </div>

              <div class="input-group">
                <label>Día de inicio</label>
                <select v-model.number="formTurno.diaInicio" required>
                  <option disabled value="">Seleccione un día</option>
                  <option v-for="(nombre, numero) in diasSemana" :key="numero" :value="numero">
                    {{ nombre }}
                  </option>
                </select>
              </div>

              <div class="input-group">
                <label>Día de fin</label>
                <select v-model.number="formTurno.diaFin" required>
                  <option disabled value="">Seleccione un día</option>
                  <option v-for="(nombre, numero) in diasSemana" :key="numero" :value="numero">
                    {{ nombre }}
                  </option>
                </select>
              </div>

              <div class="input-group">
                <label>Hora de Inicio</label>
                <input v-model="formTurno.horaInicio" type="time" required />
              </div>

              <div class="input-group">
                <label>Hora de Fin</label>
                <input v-model="formTurno.horaFin" type="time" required />
              </div>
            </div>

            <div class="modal-footer">
              <button type="submit" class="btn-primary">{{ modoEdicion ? 'Actualizar' : 'Guardar' }}</button>
              <button type="button" class="btn-secondary" @click="cerrarFormulario">Cancelar</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/utils/axios-config'
import Swal from 'sweetalert2'

const router = useRouter()
const API_TURNOS = '/api/turnos'

const goBack = () => {
  router.back()
}

const diasSemana = {
  1: 'Lunes',
  2: 'Martes',
  3: 'Miércoles',
  4: 'Jueves',
  5: 'Viernes',
  6: 'Sábado',
  7: 'Domingo'
}

const obtenerNombreDia = (numero) => {
  return diasSemana[numero] || '-'
}

const turnos = ref([])
const mostrarFormulario = ref(false)
const modoEdicion = ref(false)
const turnoEditando = ref(null)

const formTurno = ref({
  nombre: '',
  diaInicio: '',
  diaFin: '',
  horaInicio: '',
  horaFin: ''
})

const itemsPerPage = 3
const currentPage = ref(1)

const cargando = ref(false)
const cargandoAccion = ref(false)

const totalPages = computed(() => Math.ceil(turnos.value.length / itemsPerPage))
const indexOfLastTurno = computed(() => currentPage.value * itemsPerPage)
const indexOfFirstTurno = computed(() => indexOfLastTurno.value - itemsPerPage)
const currentTurnos = computed(() => turnos.value.slice(indexOfFirstTurno.value, indexOfLastTurno.value))

const handlePageChange = (pageNumber) => {
  currentPage.value = pageNumber
}

const obtenerTurnos = async () => {
  cargando.value = true
  try {
    const res = await axios.get(API_TURNOS)
    turnos.value = res.data
  } catch (error) {
    console.error('Error al obtener turnos:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'No se pudieron cargar los turnos',
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

const abrirFormularioNuevo = () => {
  modoEdicion.value = false
  turnoEditando.value = null
  formTurno.value = {
    nombre: '',
    diaInicio: '',
    diaFin: '',
    horaInicio: '',
    horaFin: ''
  }
  mostrarFormulario.value = true
}

const editarTurno = (turno) => {
  modoEdicion.value = true
  turnoEditando.value = turno.id
  formTurno.value = {
    nombre: turno.nombre,
    diaInicio: turno.diaInicio,
    diaFin: turno.diaFin,
    horaInicio: turno.horaInicio,
    horaFin: turno.horaFin
  }
  mostrarFormulario.value = true
}

const guardarTurno = async () => {
  cargandoAccion.value = true
  
  console.log('📤 Enviando al backend:', JSON.stringify(formTurno.value, null, 2))
  
  try {
    let response
    if (modoEdicion.value) {
      console.log('✏️ Modo edición - ID:', turnoEditando.value)
      response = await axios.patch(`${API_TURNOS}/${turnoEditando.value}`, formTurno.value)
    } else {
      response = await axios.post(API_TURNOS, formTurno.value)
    }
    
    console.log('✅ Respuesta:', response.data)
    
    cargandoAccion.value = false
    await Swal.fire({
      icon: 'success',
      title: modoEdicion.value ? '¡Actualizado!' : '¡Agregado!',
      text: modoEdicion.value ? 'Turno actualizado correctamente' : 'Turno agregado correctamente',
      showConfirmButton: false,
      timer: 2000,
      background: '#ffffff',
      color: '#213547',
      iconColor: '#3ABEF9',
      width: '450px',
    })
    
    cerrarFormulario()
    await obtenerTurnos()
  } catch (error) {
    cargandoAccion.value = false
    console.error('❌ Error al guardar turno:', error)
    console.error('❌ Detalles:', error.response?.data)
    
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: error.response?.data?.message || 'No se pudo guardar el turno',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#E54848',
      width: '450px',
    })
  }
}

const eliminarTurno = async (id) => {
  const confirm = await Swal.fire({
    title: '¿Eliminar turno?',
    text: 'Esta acción no se puede deshacer.',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Sí, eliminar',
    cancelButtonText: 'Cancelar',
    confirmButtonColor: '#E54848',
    cancelButtonColor: '#88B7F3',
    background: '#ffffff',
    color: '#213547',
    iconColor: '#E54848',
    width: '450px',
  })

  if (confirm.isConfirmed) {
    cargandoAccion.value = true
    try {
      await axios.delete(`${API_TURNOS}/${id}`)
      cargandoAccion.value = false
      await Swal.fire({
        icon: 'success',
        title: 'Eliminado',
        text: 'Turno eliminado correctamente',
        showConfirmButton: false,
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
      await obtenerTurnos()
      if (currentTurnos.value.length === 0 && currentPage.value > 1) {
        currentPage.value--
      }
    } catch (error) {
      cargandoAccion.value = false
      console.error('Error al eliminar turno:', error)
      await Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'No se pudo eliminar el turno',
        confirmButtonColor: '#3ABEF9',
        background: '#ffffff',
        color: '#213547',
        iconColor: '#E54848',
        width: '450px',
      })
    }
  }
}

const cerrarFormulario = () => {
  mostrarFormulario.value = false
  modoEdicion.value = false
  turnoEditando.value = null
}

onMounted(() => {
  obtenerTurnos()
})
</script>

<style scoped>
.turnos-main {
  max-width: 1000px;
  margin: 40px auto;
  padding: 0 20px;
}

.turnos-header {
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

.turnos-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  padding: 30px;
  border: 1px solid #f0f0f0;
}

.table-container {
  width: 100%;
  overflow-x: auto;
}

.styled-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

.styled-table thead tr {
  background-color: #88B7F3;
  color: white;
  text-align: left;
}

.styled-table th,
.styled-table td {
  padding: 12px 15px;
  border-bottom: 1px solid #e2e8f0;
}

.styled-table tbody tr:hover {
  background-color: #f8fafc;
}

.btn-accion {
  width: auto;
  padding: 6px 12px;
  margin: 0 4px;
  font-size: 0.75rem;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 25px;
  flex-wrap: wrap;
}

.pagination button {
  background: #f1f5f9;
  color: #334155;
  border: none;
  padding: 8px 14px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.pagination button:hover {
  background: #3abef9;
  color: white;
}

.pagination button.active {
  background: #3abef9;
  color: white;
}

.btn-div {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.btn-primary i {
  margin-right: 8px;
}

.form-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.form-modal {
  background: white;
  width: 90%;
  max-width: 550px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.modal-header {
  padding: 20px 25px;
  background: #f8fafc;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.25rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #94a3b8;
}

.close-btn:hover {
  color: #ef4444;
}

.modal-body {
  padding: 25px;
  max-height: 70vh;
  overflow-y: auto;
}

.form-grid-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.input-group {
  display: flex;
  flex-direction: column;
  text-align: left;
}

.input-group.full-width {
  grid-column: span 2;
}

.input-group label {
  font-size: 0.85rem;
  font-weight: 600;
  margin-bottom: 6px;
  color: #475569;
}

.input-group input,
.input-group select {
  padding: 10px 14px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  font-size: 0.9rem;
  transition: all 0.2s;
  background: white;
}

.input-group input:focus,
.input-group select:focus {
  outline: none;
  border-color: #3abef9;
  box-shadow: 0 0 0 3px rgba(58, 190, 249, 0.1);
}

.modal-footer {
  padding: 20px 25px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
}

.btn-secondary {
  background: white;
  color: #64748b;
  border: 1px solid #e2e8f0;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
}

.btn-secondary:hover {
  background: #f1f5f9;
}

@media (max-width: 768px) {
  .turnos-main {
    padding: 0 15px;
  }
  
  .turnos-card {
    padding: 20px;
  }
  
  .form-grid-layout {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .input-group.full-width {
    grid-column: span 1;
  }
  
  .styled-table th,
  .styled-table td {
    padding: 10px 12px;
  }
  
  .btn-accion {
    margin: 2px;
  }
}

@media (max-width: 600px) {
  .turnos-header {
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
  .styled-table thead {
    display: none;
  }
  
  .styled-table tbody tr {
    display: block;
    margin-bottom: 15px;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 10px;
  }
  
  .styled-table td {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 10px;
    border-bottom: 1px solid #e2e8f0;
    text-align: right;
  }
  
  .styled-table td::before {
    content: attr(data-label);
    font-weight: 600;
    color: #334155;
    text-align: left;
  }
  
  .styled-table td:last-child {
    border-bottom: none;
  }
  
  .btn-accion {
    width: auto;
  }
  
  .modal-footer {
    flex-direction: column-reverse;
  }
  
  .modal-footer button {
    width: 100%;
  }
}
</style>