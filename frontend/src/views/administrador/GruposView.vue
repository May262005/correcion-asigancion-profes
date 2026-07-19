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

    <main class="grupos-main">
      <!-- HEADER CON BOTÓN REGRESAR -->
      <div class="grupos-header">
        <button @click="goBack" class="btn-back-icon" title="Volver al Dashboard">
          <i class="fas fa-arrow-left"></i>
        </button>
        <div class="header-text">
          <h1>Lista de Grupos</h1>
          <p>Gestiona los grupos y secciones académicas</p>
        </div>
        <div class="header-spacer"></div>
      </div>

      <div v-if="cargando" class="loading-container">
        <div class="spinner"></div>
        <p>Cargando información...</p>
      </div>

      <div class="grupos-card" v-else-if="grupos.length > 0">
        <div class="table-container">
          <table class="styled-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Abreviatura</th>
                <th>Grado</th>
                <th>División</th>
                <th>Turno</th>
                <th>Tutor</th>
                <th>Color</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="grupo in currentGrupos" :key="grupo.id">
                <td :data-label="'Nombre'">{{ grupo.nombre }}</td>
                <td :data-label="'Abreviatura'">{{ grupo.abreviatura }}</td>
                <td :data-label="'Grado'">{{ grupo.grado }}</td>
                <td :data-label="'División'">{{ grupo.division?.nombre || '-' }}</td>
                <td :data-label="'Turno'">{{ grupo.turno?.nombre || '-' }}</td>
                <td :data-label="'Tutor'">{{ grupo.tutor?.usuario?.nombre }} {{ grupo.tutor?.usuario?.apellido_paterno || '' }}</td>
                <td :data-label="'Color'">
                  <div class="color-box" :style="{ backgroundColor: grupo.colorIdentificador }"></div>
                </td>
                <td :data-label="'Acciones'">
                  <button class="btn-secondary btn-accion" @click="editarGrupo(grupo)">Editar</button>
                  <button class="btn-danger btn-accion" @click="eliminarGrupo(grupo.id)">Eliminar</button>
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
            <i class="fas fa-plus"></i> Agregar Grupo
          </button>
        </div>
      </div>

      <div v-else class="empty-state">
        <div class="empty-content">
          <i class="fas fa-users"></i>
          <h3>No hay grupos registrados</h3>
          <p>Comienza agregando tu primer grupo al sistema</p>
        </div>
      </div>
    </main>

    <!-- MODAL -->
    <div v-if="mostrarFormulario" class="form-overlay" @click.self="cerrarFormulario">
      <div class="form-modal">
        <div class="modal-header">
          <h2>{{ modoEdicion ? 'Editar Grupo' : 'Agregar Grupo' }}</h2>
          <button class="close-btn" @click="cerrarFormulario">&times;</button>
        </div>

        <div class="modal-body">
          <form @submit.prevent="guardarGrupo">
            <div class="form-grid-layout">
              <div class="input-group">
                <label>Nombre del grupo</label>
                <input v-model="formGrupo.nombre" type="text" placeholder="Nombre del grupo" required />
              </div>

              <div class="input-group">
                <label>Abreviatura</label>
                <input v-model="formGrupo.abreviatura" type="text" placeholder="Abreviatura" maxlength="10" />
              </div>

              <div class="input-group">
                <label>Grado</label>
                <input v-model="formGrupo.grado" type="number" placeholder="Grado" required />
              </div>

              <div class="input-group">
                <label>División</label>
                <select v-model.number="formGrupo.id_division" required>
                  <option value="" disabled>Seleccione una división</option>
                  <option v-for="division in divisiones" :key="division.id" :value="division.id">
                    {{ division.nombre }}
                  </option>
                </select>
              </div>

              <div class="input-group">
                <label>Turno</label>
                <select v-model.number="formGrupo.id_turno" required>
                  <option value="" disabled>Seleccione un turno</option>
                  <option v-for="turno in turnos" :key="turno.id" :value="turno.id">
                    {{ turno.nombre }}
                  </option>
                </select>
              </div>

              <div class="input-group">
                <label>Tutor</label>
                <select v-model.number="formGrupo.tutor_id" required>
                  <option value="" disabled>Seleccione un tutor</option>
                  <option v-for="profesor in profesores" :key="profesor.id" :value="profesor.id">
                    {{ profesor.usuario.nombre }} {{ profesor.usuario.apellido_paterno }}
                  </option>
                </select>
              </div>

              <div class="input-group full-width">
                <label>Color identificador</label>
                <input v-model="formGrupo.color_identificador" type="color" class="color-pointer" />
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
import axios from '../../utils/axios-config'
import '../../assets/styles.css'
import Swal from 'sweetalert2'

const router = useRouter()
const API_GRUPOS = `http://localhost:3000/grupos`
const API_DIVISIONES = `http://localhost:3000/divisiones`
const API_TURNOS = `/api/turnos`
const API_PROFESORES = `http://localhost:3000/profesores`

const goBack = () => {
  router.back()
}

const grupos = ref([])
const divisiones = ref([])
const turnos = ref([])
const profesores = ref([])

const itemsPerPage = 3
const currentPage = ref(1)
const mostrarFormulario = ref(false)
const modoEdicion = ref(false)
const grupoEditando = ref(null)

const cargando = ref(false)
const cargandoAccion = ref(false)

const formGrupo = ref({
  nombre: '',
  abreviatura: '',
  grado: '',          
  id_division: '',
  id_turno: '',
  tutor_id: '',
  color_identificador: '#88B7F3'
})

const totalPages = computed(() => Math.ceil(grupos.value.length / itemsPerPage))
const indexOfLastGrupo = computed(() => currentPage.value * itemsPerPage)
const indexOfFirstGrupo = computed(() => indexOfLastGrupo.value - itemsPerPage)
const currentGrupos = computed(() => grupos.value.slice(indexOfFirstGrupo.value, indexOfLastGrupo.value))

const handlePageChange = (pageNumber) => {
  currentPage.value = pageNumber
}

const obtenerGrupos = async () => {
  cargando.value = true
  try {
    const res = await axios.get(API_GRUPOS)
    grupos.value = res.data
  } catch (error) {
    console.error('Error al obtener grupos:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'No se pudieron cargar los grupos',
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

const obtenerDivisiones = async () => {
  try {
    const res = await axios.get(API_DIVISIONES)
    divisiones.value = res.data
  } catch (error) {
    console.error('Error al obtener divisiones:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'No se pudieron cargar las divisiones',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#E54848',
      width: '450px',
    })
  }
}

const obtenerTurnos = async () => {
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
  }
}

const obtenerProfesores = async () => {
  try {
    const res = await axios.get(API_PROFESORES)
    profesores.value = res.data
  } catch (error) {
    console.error('Error al obtener profesores:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'No se pudieron cargar los profesores',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#E54848',
      width: '450px',
    })
  }
}

const abrirFormularioNuevo = () => {
  modoEdicion.value = false
  grupoEditando.value = null
  formGrupo.value = {
    nombre: '',
    abreviatura: '',
    grado: '',
    id_division: '',
    id_turno: '',
    tutor_id: '',
    color_identificador: '#88B7F3'
  }
  mostrarFormulario.value = true
}

const editarGrupo = (grupo) => {
  modoEdicion.value = true
  grupoEditando.value = grupo.id

  formGrupo.value = {
    nombre: grupo.nombre || '',
    abreviatura: grupo.abreviatura || '',
    grado: String(grupo.grado || ''), 
    id_division: grupo.id_division || grupo.division?.id || '',
    id_turno: grupo.id_turno || grupo.turno?.id || '',
    tutor_id: grupo.tutor_id || grupo.tutor?.id || '',
    color_identificador: grupo.colorIdentificador || '#88B7F3'
  }

  mostrarFormulario.value = true
}

const guardarGrupo = async () => {
  cargandoAccion.value = true
  try {
    formGrupo.value.grado = String(formGrupo.value.grado)
    if (modoEdicion.value) {
      await axios.patch(`${API_GRUPOS}/${grupoEditando.value}`, formGrupo.value)
      cargandoAccion.value = false
      await Swal.fire({ 
        icon: 'success', 
        title: '¡Actualizado!', 
        text: 'Grupo actualizado correctamente', 
        showConfirmButton: false, 
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
    } else {
      await axios.post(API_GRUPOS, formGrupo.value)
      cargandoAccion.value = false
      await Swal.fire({ 
        icon: 'success', 
        title: '¡Agregado!', 
        text: 'Grupo agregado correctamente', 
        showConfirmButton: false, 
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px', 
      })
    }

    cerrarFormulario()
    await obtenerGrupos()

  } catch (error) {
    cargandoAccion.value = false
    console.error('Error al guardar grupo:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: error.response?.data?.message || 'Error al guardar el grupo',
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

const eliminarGrupo = async (id) => {
  const confirm = await Swal.fire({
    title: '¿Eliminar grupo?',
    text: 'Esto podría afectar a los alumnos asociados. Esta acción no se puede deshacer.',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Sí, eliminar',
    cancelButtonText: 'Cancelar',
    confirmButtonColor: '#E54848',
    cancelButtonColor: '#88B7F3',
    background: '#ffffff',
    color: '#213547',
    iconColor: '#E54848',
    width: '500px',
  })

  if (confirm.isConfirmed) {
    cargandoAccion.value = true
    try {
      await axios.delete(`${API_GRUPOS}/${id}`)
      cargandoAccion.value = false
      await Swal.fire({ 
        icon: 'success', 
        title: 'Eliminado', 
        text: 'Grupo eliminado correctamente', 
        showConfirmButton: false, 
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
      await obtenerGrupos()

      if (currentGrupos.value.length === 0 && currentPage.value > 1) {
        currentPage.value--
      }

    } catch (error) {
      cargandoAccion.value = false
      console.error('Error al eliminar grupo:', error)
      await Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'No se pudo eliminar el grupo. Puede estar asociado a otras entidades.',
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
}

const cerrarFormulario = () => {
  mostrarFormulario.value = false
  modoEdicion.value = false
  grupoEditando.value = null
}

onMounted(() => {
  obtenerGrupos()
  obtenerDivisiones()
  obtenerTurnos()
  obtenerProfesores()
})
</script>

<style scoped>
.grupos-main {
  max-width: 1400px;
  margin: 40px auto;
  padding: 0 20px;
}

/* Header con botón a la izquierda y título centrado */
.grupos-header {
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

.grupos-card {
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

.color-box {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
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

.color-pointer {
  width: 100%;
  height: 45px;
  cursor: pointer;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  padding: 5px;
}

/* MODAL */
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
  max-width: 700px;
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
  .grupos-main {
    padding: 0 15px;
  }
  
  .grupos-card {
    padding: 20px;
  }
  
  .form-grid-layout {
    grid-template-columns: 1fr;
  }
  
  .input-group.full-width {
    grid-column: span 1;
  }
  
  .styled-table th,
  .styled-table td {
    padding: 10px 12px;
  }
}

@media (max-width: 600px) {
  .grupos-header {
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
  
  .modal-footer {
    flex-direction: column-reverse;
  }
  
  .modal-footer button {
    width: 100%;
  }
}
</style>