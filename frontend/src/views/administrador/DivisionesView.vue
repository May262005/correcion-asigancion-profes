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

    <main class="divisiones-main">
      <!-- HEADER CON BOTÓN REGRESAR -->
      <div class="divisiones-header">
        <button @click="goBack" class="btn-back-icon" title="Volver al Dashboard">
          <i class="fas fa-arrow-left"></i>
        </button>
        <div class="header-text">
          <h1>Lista de Divisiones</h1>
          <p>Gestiona las divisiones académicas de la institución</p>
        </div>
        <div class="header-spacer"></div>
      </div>

      <div v-if="cargando" class="loading-container">
        <div class="spinner"></div>
        <p>Cargando información...</p>
      </div>

      <div class="divisiones-card" v-else-if="divisiones.length > 0">
        <div class="table-container">
          <table class="styled-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Abreviatura</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="division in currentDivisiones" :key="division.id">
                <td :data-label="'Nombre'">{{ division.nombre }}</td>
                <td :data-label="'Abreviatura'">{{ division.abreviatura || '-' }}</td>
                <td :data-label="'Acciones'">
                  <button class="btn-secondary btn-accion" @click="editarDivision(division)">Editar</button>
                  <button class="btn-danger btn-accion" @click="eliminarDivision(division.id)">Eliminar</button>
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
            <i class="fas fa-plus"></i> Agregar División
          </button>
        </div>
      </div>

      <div v-else class="empty-state">
        <div class="empty-content">
          <i class="fas fa-building"></i>
          <h3>No hay divisiones registradas</h3>
          <p>Comienza agregando tu primera división al sistema</p>
        </div>
      </div>
    </main>

    <!-- MODAL (ESTILO PERFIL) -->
    <div v-if="mostrarFormulario" class="form-overlay" @click.self="cerrarFormulario">
      <div class="form-modal">
        <div class="modal-header">
          <h2>{{ modoEdicion ? 'Editar División' : 'Agregar División' }}</h2>
          <button class="close-btn" @click="cerrarFormulario">&times;</button>
        </div>

        <div class="modal-body">
          <form @submit.prevent="guardarDivision">
            <div class="form-grid-layout">
              <div class="input-group full-width">
                <label>Nombre de la división</label>
                <input v-model="formDivision.nombre" type="text" placeholder="Nombre de la división" required />
              </div>

              <div class="input-group full-width">
                <label>Abreviatura</label>
                <input v-model="formDivision.abreviatura" type="text" placeholder="Abreviatura (ej: ING, MED)" maxlength="10" />
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
const API_URL = `http://localhost:8080/api/divisiones`

const goBack = () => {
  router.back()
}

const divisiones = ref([])
const cargando = ref(false)
const cargandoAccion = ref(false)
const itemsPerPage = 3
const currentPage = ref(1)
const mostrarFormulario = ref(false)
const modoEdicion = ref(false)
const divisionEditando = ref(null)

const formDivision = ref({
  nombre: '',
  abreviatura: ''
})

const totalPages = computed(() => Math.ceil(divisiones.value.length / itemsPerPage))
const indexOfLastDivision = computed(() => currentPage.value * itemsPerPage)
const indexOfFirstDivision = computed(() => indexOfLastDivision.value - itemsPerPage)
const currentDivisiones = computed(() => 
  divisiones.value.slice(indexOfFirstDivision.value, indexOfLastDivision.value)
)

const handlePageChange = (pageNumber) => {
  currentPage.value = pageNumber
}

const obtenerDivisiones = async () => {
  cargando.value = true
  try {
    const res = await axios.get(API_URL)
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
  } finally {
    cargando.value = false
  }
}

const abrirFormularioNuevo = () => {
  modoEdicion.value = false
  divisionEditando.value = null
  formDivision.value = {
    nombre: '',
    abreviatura: ''
  }
  mostrarFormulario.value = true
}

const editarDivision = (division) => {
  modoEdicion.value = true
  divisionEditando.value = division.id
  formDivision.value = {
    nombre: division.nombre || '',
    abreviatura: division.abreviatura || ''
  }
  mostrarFormulario.value = true
}

const guardarDivision = async () => {
  cargandoAccion.value = true
  try {
    if (modoEdicion.value) {
      await axios.patch(`${API_URL}/${divisionEditando.value}`, formDivision.value)
      cargandoAccion.value = false
      await Swal.fire({
        icon: 'success',
        title: '¡Actualizado!',
        text: 'División actualizada correctamente',
        showConfirmButton: false,
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
    } else {
      await axios.post(API_URL, formDivision.value)
      cargandoAccion.value = false
      await Swal.fire({
        icon: 'success',
        title: '¡Agregado!',
        text: 'División agregada correctamente',
        showConfirmButton: false,
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
    }
    cerrarFormulario()
    await obtenerDivisiones()
  } catch (error) {
    cargandoAccion.value = false
    console.error('Error al guardar división:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: error.response?.data?.message || 'Error al guardar la división',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#E54848',
      width: '500px',
    })
  } finally {
    cargandoAccion.value = false
  }
}

const eliminarDivision = async (id) => {
  const confirm = await Swal.fire({
    title: '¿Eliminar división?',
    text: 'Esto podría afectar a las materias asociadas. Esta acción no se puede deshacer.',
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
      await axios.delete(`${API_URL}/${id}`)
      cargandoAccion.value = false
      await Swal.fire({
        icon: 'success',
        title: 'Eliminado',
        text: 'División eliminada correctamente',
        showConfirmButton: false,
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
      await obtenerDivisiones()
      
      if (currentDivisiones.value.length === 0 && currentPage.value > 1) {
        currentPage.value--
      }
    } catch (error) {
      cargandoAccion.value = false
      console.error('Error al eliminar división:', error)
      await Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'No se pudo eliminar la división. Puede estar asociada a otras entidades.',
        confirmButtonColor: '#3ABEF9',
        background: '#ffffff',
        color: '#213547',
        iconColor: '#E54848',
        width: '500px',
      })
    } finally {
      cargandoAccion.value = false
    }
  }
}

const cerrarFormulario = () => {
  mostrarFormulario.value = false
  modoEdicion.value = false
  divisionEditando.value = null
}

onMounted(() => {
  obtenerDivisiones()
})
</script>

<style scoped>
/* =======================
   LISTA DE DIVISIONES - CON BOTÓN REGRESAR
======================= */

.divisiones-main {
  max-width: 1000px;
  margin: 40px auto;
  padding: 0 20px;
}

/* Header con botón a la izquierda y título centrado */
.divisiones-header {
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

/* Tarjeta blanca */
.divisiones-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  padding: 30px;
  border: 1px solid #f0f0f0;
}

/* Tabla */
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

/* Botones de acción */
.btn-accion {
  width: auto;
  padding: 6px 12px;
  margin: 0 4px;
  font-size: 0.75rem;
}

/* Paginación */
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

/* Botón agregar */
.btn-div {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.btn-primary i {
  margin-right: 8px;
}

/* =======================
   MODAL (ESTILO PERFIL)
======================= */
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
  max-width: 500px;
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
  grid-template-columns: 1fr;
  gap: 15px;
}

.input-group {
  display: flex;
  flex-direction: column;
  text-align: left;
}

.input-group.full-width {
  grid-column: span 1;
}

.input-group label {
  font-size: 0.85rem;
  font-weight: 600;
  margin-bottom: 6px;
  color: #475569;
}

.input-group input {
  padding: 10px 14px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.input-group input:focus {
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

/* Responsive */
@media (max-width: 768px) {
  .divisiones-main {
    padding: 0 15px;
  }
  
  .divisiones-card {
    padding: 20px;
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
  .divisiones-header {
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