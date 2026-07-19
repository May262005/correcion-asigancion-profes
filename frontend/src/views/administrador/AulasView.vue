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

    <main class="aulas-main">
      <!-- HEADER CON BOTÓN REGRESAR -->
      <div class="aulas-header">
        <button @click="goBack" class="btn-back-icon" title="Volver al Dashboard">
          <i class="fas fa-arrow-left"></i>
        </button>
        <div class="header-text">
          <h1>Lista de Aulas</h1>
          <p>Gestiona los espacios físicos disponibles en la institución</p>
        </div>
        <div class="header-spacer"></div>
      </div>

      <div v-if="cargando" class="loading-container">
        <div class="spinner"></div>
        <p>Cargando información...</p>
      </div>

      <div class="aulas-card" v-else-if="aulas.length > 0">
        <div class="table-container">
          <table class="styled-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Abreviatura</th>
                <th>Capacidad</th>
                <th>Ubicación</th>
                <th>Edificio</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="aula in currentAulas" :key="aula.id">
                <td :data-label="'Nombre'">{{ aula.nombre }}</td>
                <td :data-label="'Abreviatura'">{{ aula.abreviatura || '-' }}</td>
                <td :data-label="'Capacidad'">{{ aula.capacidad }}</td>
                <td :data-label="'Ubicación'">{{ aula.ubicacion }}</td>
                <td :data-label="'Edificio'">{{ aula.edificio?.nombre || '-' }}</td>
                <td :data-label="'Acciones'">
                  <button class="btn-secondary btn-accion" @click="editarAula(aula)">Editar</button>
                  <button class="btn-danger btn-accion" @click="eliminarAula(aula.id)">Eliminar</button>
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
            <i class="fas fa-plus"></i> Agregar Aula
          </button>
        </div>
      </div>

      <div v-else class="empty-state">
        <div class="empty-content">
          <i class="fas fa-building"></i>
          <h3>No hay aulas registradas</h3>
          <p>Comienza agregando tu primera aula al sistema</p>
        </div>
      </div>
    </main>

    <!-- MODAL (ESTILO PERFIL) -->
    <div v-if="mostrarFormulario" class="form-overlay" @click.self="cerrarFormulario">
      <div class="form-modal">
        <div class="modal-header">
          <h2>{{ modoEdicion ? 'Editar Aula' : 'Agregar Aula' }}</h2>
          <button class="close-btn" @click="cerrarFormulario">&times;</button>
        </div>

        <div class="modal-body">
          <form @submit.prevent="guardarAula">
            <div class="form-grid-layout">
              <div class="input-group">
                <label>Nombre del aula</label>
                <input v-model="formAula.nombre" type="text" placeholder="Nombre del aula" required />
              </div>

              <div class="input-group">
                <label>Abreviatura</label>
                <input v-model="formAula.abreviatura" type="text" placeholder="Abreviatura (opcional)" maxlength="10" />
              </div>

              <div class="input-group">
                <label>Capacidad</label>
                <input v-model.number="formAula.capacidad" type="number" placeholder="Capacidad" required />
              </div>

              <div class="input-group">
                <label>Ubicación</label>
                <select v-model="formAula.ubicacion" required>
                  <option value="" disabled>Seleccione ubicación</option>
                  <option value="Planta alta">Planta alta</option>
                  <option value="Planta baja">Planta baja</option>
                </select>
              </div>

              <div class="input-group full-width">
                <label>Edificio</label>
                <select v-model.number="formAula.id_edificio" required>
                  <option value="" disabled>Seleccione un edificio</option>
                  <option v-for="edificio in edificios" :key="edificio.id" :value="edificio.id">
                    {{ edificio.nombre }}
                  </option>
                </select>
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
const BASE_URL = `http://localhost:3000`

const API_AULAS = `${BASE_URL}/aulas`
const API_EDIFICIOS = `${BASE_URL}/edificios`

const goBack = () => {
  router.back()
}

const aulas = ref([])
const edificios = ref([])
const cargando = ref(false)
const cargandoAccion = ref(false)

const itemsPerPage = 3
const currentPage = ref(1)
const mostrarFormulario = ref(false)
const modoEdicion = ref(false)
const aulaEditando = ref(null)

const formAula = ref({
  nombre: '',
  abreviatura: '',
  capacidad: '',
  ubicacion: '',
  id_edificio: ''
})

const totalPages = computed(() => Math.ceil(aulas.value.length / itemsPerPage))
const indexOfLastAula = computed(() => currentPage.value * itemsPerPage)
const indexOfFirstAula = computed(() => indexOfLastAula.value - itemsPerPage)
const currentAulas = computed(() => aulas.value.slice(indexOfFirstAula.value, indexOfLastAula.value))

const handlePageChange = (pageNumber) => {
  currentPage.value = pageNumber
}

const obtenerAulas = async () => {
  cargando.value = true
  try {
    const res = await axios.get(API_AULAS)
    aulas.value = res.data
  } catch (error) {
    console.error('Error al obtener aulas:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'No se pudieron cargar las aulas',
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

const obtenerEdificios = async () => {
  try {
    const res = await axios.get(API_EDIFICIOS)
    edificios.value = res.data
  } catch (error) {
    console.error('Error al obtener edificios:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'No se pudieron cargar los edificios',
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
  aulaEditando.value = null
  formAula.value = { 
    nombre: '', 
    abreviatura: '', 
    capacidad: '', 
    ubicacion: '', 
    id_edificio: '' 
  }
  mostrarFormulario.value = true
}

const editarAula = (aula) => {
  modoEdicion.value = true
  aulaEditando.value = aula.id
  formAula.value = {
    nombre: aula.nombre || '',
    abreviatura: aula.abreviatura || '',
    capacidad: aula.capacidad || '',
    ubicacion: aula.ubicacion || '',
    id_edificio: aula.id_edificio || aula.edificio?.id || ''
  }
  mostrarFormulario.value = true
}

const guardarAula = async () => {
  cargandoAccion.value = true
  try {
    if (modoEdicion.value) {
      await axios.patch(`${API_AULAS}/${aulaEditando.value}`, formAula.value)
      cargandoAccion.value = false
      await Swal.fire({
        icon: 'success',
        title: '¡Actualizado!',
        text: 'Aula actualizada correctamente',
        showConfirmButton: false,
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
    } else {
      await axios.post(API_AULAS, formAula.value)
      cargandoAccion.value = false
      await Swal.fire({
        icon: 'success',
        title: '¡Agregado!',
        text: 'Aula agregada correctamente',
        showConfirmButton: false,
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
    }
    cerrarFormulario()
    await obtenerAulas()
  } catch (error) {
    cargandoAccion.value = false
    console.error('Error al guardar aula:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: error.response?.data?.message || 'Error al guardar el aula',
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

const eliminarAula = async (id) => {
  const confirm = await Swal.fire({
    title: '¿Eliminar aula?',
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
      await axios.delete(`${API_AULAS}/${id}`)
      cargandoAccion.value = false
      await Swal.fire({
        icon: 'success',
        title: 'Eliminado',
        text: 'Aula eliminada correctamente',
        showConfirmButton: false,
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
      await obtenerAulas()
      if (currentAulas.value.length === 0 && currentPage.value > 1) {
        currentPage.value--
      }
    } catch (error) {
      cargandoAccion.value = false
      console.error('Error al eliminar aula:', error)
      await Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'No se pudo eliminar el aula',
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
  aulaEditando.value = null
}

onMounted(() => {
  obtenerAulas()
  obtenerEdificios()
})
</script>

<style scoped>
/* =======================
   LISTA DE AULAS - CON BOTÓN REGRESAR
======================= */

.aulas-main {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
}

/* Header con botón a la izquierda y título centrado */
.aulas-header {
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
.aulas-card {
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
  max-width: 650px;
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

/* Responsive */
@media (max-width: 768px) {
  .aulas-main {
    padding: 0 15px;
  }
  
  .aulas-card {
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
  .aulas-header {
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