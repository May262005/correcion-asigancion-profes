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

    <main class="materias-main">
      <!-- HEADER CON BOTÓN REGRESAR -->
      <div class="materias-header">
        <button @click="goBack" class="btn-back-icon" title="Volver al Dashboard">
          <i class="fas fa-arrow-left"></i>
        </button>
        <div class="header-text">
          <h1>Lista de Materias</h1>
          <p>Gestiona las asignaturas de la institución</p>
        </div>
        <div class="header-spacer"></div>
      </div>

      <div v-if="cargando" class="loading-container">
        <div class="spinner"></div>
        <p>Cargando información...</p>
      </div>

      <div class="materias-card" v-else-if="materias.length > 0">
        <div class="table-container">
          <table class="styled-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Abreviatura</th>
                <th>Tipo</th>
                <th>División</th>
                <th>Color</th>
                <th>Horas Semanales</th>
                <th>Duración Mín</th>
                <th>Duración Máx</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="materia in currentMaterias" :key="materia.id">
                <td :data-label="'Nombre'">{{ materia.nombre }}</td>
                <td :data-label="'Abreviatura'">{{ materia.abreviatura || '-' }}</td>
                <td :data-label="'Tipo'">
                  {{ materia.tipo === 'especialidad' ? 'Especialidad'
                     : materia.tipo === 'tronco_comun' ? 'Tronco común' : '-' }}
                </td>
                <td :data-label="'División'">{{ obtenerNombreDivision(materia.divisionId) || '-' }}</td>
                <td :data-label="'Color'">
                  <div class="color-box" :style="{ backgroundColor: materia.colorIdentificador }"></div>
                </td>
                <td :data-label="'Horas Semanales'">{{ materia.horasSemanales || 0 }}</td>
                <td :data-label="'Duración Mín'">{{ materia.duracionBloqueHorasMin || '-' }}</td>
                <td :data-label="'Duración Máx'">{{ materia.duracionBloqueHorasMax || '-' }}</td>
                <td :data-label="'Acciones'">
                  <button class="btn-secondary btn-accion" @click="editarMateria(materia)">Editar</button>
                  <button class="btn-danger btn-accion" @click="eliminarMateria(materia.id)">Eliminar</button>
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
            <i class="fas fa-plus"></i> Agregar Materia
          </button>
        </div>
      </div>

      <div v-else class="empty-state">
        <div class="empty-content">
          <i class="fas fa-book"></i>
          <h3>No hay materias registradas</h3>
          <p>Comienza agregando tu primera materia al sistema</p>
        </div>
      </div>
    </main>

    <!-- MODAL -->
    <div v-if="mostrarFormulario" class="form-overlay" @click.self="cerrarFormulario">
      <div class="form-modal">
        <div class="modal-header">
          <h2>{{ modoEdicion ? 'Editar Materia' : 'Agregar Materia' }}</h2>
          <button class="close-btn" @click="cerrarFormulario">&times;</button>
        </div>

        <div class="modal-body">
          <form @submit.prevent="guardarMateria">
            <div class="form-grid-layout">
              <div class="input-group">
                <label>Nombre *</label>
                <input v-model="formMateria.nombre" type="text" placeholder="Nombre de la materia" required />
              </div>

              <div class="input-group">
                <label>Abreviatura</label>
                <input v-model="formMateria.abreviatura" type="text" placeholder="Abreviatura" maxlength="10" />
              </div>

              <div class="input-group">
                <label>Tipo *</label>
                <select v-model="formMateria.tipo" required>
                  <option value="" disabled>Seleccione tipo</option>
                  <option value="tronco_comun">Tronco común</option>
                  <option value="especialidad">Especialidad</option>
                </select>
              </div>

              <div class="input-group">
                <label>División *</label>
                <select v-model.number="formMateria.divisionId" required>
                  <option value="" disabled>Seleccione división</option>
                  <option v-for="division in divisiones" :key="division.id" :value="division.id">
                    {{ division.nombre }}
                  </option>
                </select>
              </div>

              <div class="input-group">
                <label>Color</label>
                <input type="color" v-model="formMateria.colorIdentificador" class="color-pointer" />
              </div>

              <div class="input-group">
                <label>Horas semanales *</label>
                <input type="number" v-model.number="formMateria.horasSemanales" min="0" required />
              </div>

              <div class="input-group">
                <label>Duración mínima (horas) *</label>
                <input type="number" v-model.number="formMateria.duracionBloqueHorasMin" min="1" required />
              </div>

              <div class="input-group">
                <label>Duración máxima (horas) *</label>
                <input 
                  type="number" 
                  v-model.number="formMateria.duracionBloqueHorasMax" 
                  :min="formMateria.duracionBloqueHorasMin" 
                  required 
                />
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
import Swal from 'sweetalert2'
import '../../assets/styles.css'

const router = useRouter()

// ============================================================
// CONFIGURACIÓN DE API
// ============================================================
const API_URL = `http://localhost:8080/api/curriculum`
const API_DIVISIONES = `http://localhost:8080/api/divisiones`

// ============================================================
// NAVEGACIÓN
// ============================================================
const goBack = () => {
  router.back()
}

// ============================================================
// ESTADO
// ============================================================
const materias = ref([])
const divisiones = ref([])

const itemsPerPage = 5
const currentPage = ref(1)
const mostrarFormulario = ref(false)
const modoEdicion = ref(false)
const materiaEditando = ref(null)

const cargando = ref(false)
const cargandoAccion = ref(false)

// ============================================================
// FORMULARIO (usando camelCase como el backend)
// ============================================================
const formMateria = ref({
  nombre: '',
  abreviatura: '',
  tipo: '',
  divisionId: '',
  colorIdentificador: '#88B7F3',
  horasSemanales: 0,
  duracionBloqueHorasMin: 1,
  duracionBloqueHorasMax: 2
})

// ============================================================
// COMPUTADAS
// ============================================================
const totalPages = computed(() => Math.ceil(materias.value.length / itemsPerPage))
const indexOfLastMateria = computed(() => currentPage.value * itemsPerPage)
const indexOfFirstMateria = computed(() => indexOfLastMateria.value - itemsPerPage)
const currentMaterias = computed(() =>
  materias.value.slice(indexOfFirstMateria.value, indexOfLastMateria.value)
)

// ============================================================
// MÉTODOS
// ============================================================
const handlePageChange = (pageNumber) => {
  currentPage.value = pageNumber
}

const obtenerNombreDivision = (divisionId) => {
  if (!divisionId) return '-'
  const division = divisiones.value.find(d => d.id === divisionId)
  return division ? division.nombre : '-'
}

// ============================================================
// CRUD - OBTENER MATERIAS
// ============================================================
const obtenerMaterias = async () => {
  cargando.value = true
  try {
    const res = await axios.get(API_URL)
    // ✅ Los datos del backend ya vienen en camelCase
    materias.value = res.data
    console.log('Materias cargadas:', materias.value)
  } catch (error) {
    console.error('Error al obtener materias:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'No se pudieron cargar las materias',
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

// ============================================================
// CRUD - OBTENER DIVISIONES
// ============================================================
const obtenerDivisiones = async () => {
  try {
    const res = await axios.get(API_DIVISIONES)
    divisiones.value = res.data
    console.log('Divisiones cargadas:', divisiones.value)
  } catch (error) {
    console.error('Error al obtener divisiones:', error)
    // Si no hay servicio de divisiones, usa datos mock
    divisiones.value = [
      { id: 1, nombre: 'División 1' },
      { id: 2, nombre: 'División 2' },
      { id: 3, nombre: 'División 3' }
    ]
    await Swal.fire({
      icon: 'warning',
      title: 'Aviso',
      text: 'No se pudieron cargar las divisiones, usando datos de ejemplo',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#F59E0B',
      width: '450px',
    })
  }
}

// ============================================================
// CRUD - ABRIR FORMULARIO
// ============================================================
const abrirFormularioNuevo = () => {
  modoEdicion.value = false
  materiaEditando.value = null
  formMateria.value = {
    nombre: '',
    abreviatura: '',
    tipo: '',
    divisionId: '',
    colorIdentificador: '#88B7F3',
    horasSemanales: 0,
    duracionBloqueHorasMin: 1,
    duracionBloqueHorasMax: 2
  }
  mostrarFormulario.value = true
}

// ============================================================
// CRUD - EDITAR MATERIA
// ============================================================
const editarMateria = (materia) => {
  modoEdicion.value = true
  materiaEditando.value = materia.id
  formMateria.value = {
    nombre: materia.nombre || '',
    abreviatura: materia.abreviatura || '',
    tipo: materia.tipo || '',
    divisionId: materia.divisionId || '',
    colorIdentificador: materia.colorIdentificador || '#88B7F3',
    horasSemanales: materia.horasSemanales || 0,
    duracionBloqueHorasMin: materia.duracionBloqueHorasMin || 1,
    duracionBloqueHorasMax: materia.duracionBloqueHorasMax || 2
  }
  mostrarFormulario.value = true
}

// ============================================================
// CRUD - GUARDAR MATERIA
// ============================================================
const guardarMateria = async () => {
  // Validaciones
  if (!formMateria.value.nombre.trim()) {
    await Swal.fire({
      icon: 'warning',
      title: 'Campo requerido',
      text: 'El nombre de la materia es obligatorio',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#F59E0B',
      width: '450px',
    })
    return
  }

  if (!formMateria.value.tipo) {
    await Swal.fire({
      icon: 'warning',
      title: 'Campo requerido',
      text: 'Debes seleccionar un tipo',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#F59E0B',
      width: '450px',
    })
    return
  }

  if (!formMateria.value.divisionId) {
    await Swal.fire({
      icon: 'warning',
      title: 'Campo requerido',
      text: 'Debes seleccionar una división',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#F59E0B',
      width: '450px',
    })
    return
  }

  cargandoAccion.value = true
  try {
    // ✅ Envía camelCase directamente al backend
    const payload = { ...formMateria.value }

    if (modoEdicion.value) {
      await axios.put(`${API_URL}/${materiaEditando.value}`, payload)
      await Swal.fire({
        icon: 'success',
        title: '¡Actualizado!',
        text: 'Materia actualizada correctamente',
        showConfirmButton: false,
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
    } else {
      await axios.post(API_URL, payload)
      await Swal.fire({
        icon: 'success',
        title: '¡Agregado!',
        text: 'Materia agregada correctamente',
        showConfirmButton: false,
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
    }
    cerrarFormulario()
    await obtenerMaterias()
  } catch (error) {
    console.error('Error al guardar materia:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: error.response?.data?.message || 'Error al guardar materia',
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

// ============================================================
// CRUD - ELIMINAR MATERIA
// ============================================================
const eliminarMateria = async (id) => {
  const confirm = await Swal.fire({
    title: '¿Eliminar materia?',
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
    width: '500px',
  })

  if (confirm.isConfirmed) {
    cargandoAccion.value = true
    try {
      await axios.delete(`${API_URL}/${id}`)
      await Swal.fire({
        icon: 'success',
        title: 'Eliminado',
        text: 'Materia eliminada correctamente',
        showConfirmButton: false,
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
      await obtenerMaterias()
      // Si la página actual queda vacía, retrocede
      if (currentMaterias.value.length === 0 && currentPage.value > 1) {
        currentPage.value--
      }
    } catch (error) {
      console.error('Error al eliminar materia:', error)
      await Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'No se pudo eliminar la materia',
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

// ============================================================
// CRUD - CERRAR FORMULARIO
// ============================================================
const cerrarFormulario = () => {
  mostrarFormulario.value = false
  modoEdicion.value = false
  materiaEditando.value = null
}

// ============================================================
// CICLO DE VIDA
// ============================================================
onMounted(() => {
  obtenerMaterias()
  obtenerDivisiones()
})
</script>

<style scoped>
/* =======================
   LISTA DE MATERIAS - CON BOTÓN REGRESAR
======================= */

.materias-main {
  max-width: 1400px;
  margin: 40px auto;
  padding: 0 20px;
}

/* Header con botón a la izquierda y título centrado */
.materias-header {
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
.materias-card {
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
  font-size: 0.85rem;
}

.styled-table thead tr {
  background-color: #88B7F3;
  color: white;
  text-align: left;
}

.styled-table th,
.styled-table td {
  padding: 12px 10px;
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
  max-width: 750px;
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

.color-pointer {
  width: 100%;
  height: 42px;
  cursor: pointer;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  padding: 5px;
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

/* =======================
   LOADING
======================= */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e2e8f0;
  border-top-color: #3abef9;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.action-loading {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

/* =======================
   EMPTY STATE
======================= */
.empty-state {
  background: white;
  border-radius: 20px;
  padding: 60px 20px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
}

.empty-content i {
  font-size: 4rem;
  color: #cbd5e1;
  margin-bottom: 20px;
}

.empty-content h3 {
  color: #334155;
  margin-bottom: 10px;
}

.empty-content p {
  color: #94a3b8;
}

/* =======================
   RESPONSIVE
======================= */
@media (max-width: 768px) {
  .materias-main {
    padding: 0 15px;
  }
  
  .materias-card {
    padding: 20px;
  }
  
  .form-grid-layout {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .styled-table th,
  .styled-table td {
    padding: 10px 8px;
    font-size: 0.75rem;
  }
  
  .btn-accion {
    margin: 2px;
    font-size: 0.7rem;
    padding: 4px 8px;
  }
}

@media (max-width: 600px) {
  .materias-header {
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