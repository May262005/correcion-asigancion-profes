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

    <main class="profesores-main">
      <div class="profesores-header">
        <button @click="goBack" class="btn-back-icon" title="Volver al Dashboard">
          <i class="fas fa-arrow-left"></i>
        </button>
        <div class="header-text">
          <h1>Lista de {{ tipoVista === 'todos' ? 'Profesores' : tipoVista === 'profesores' ? 'Profesores' : 'Psicólogos' }}</h1>
          <p>Gestiona el personal docente y psicólogos de la institución</p>
        </div>
        <div class="header-spacer"></div>
      </div>

      <!-- FILTROS -->
      <div class="filtros-container">
        <div class="filtros-buttons">
          <button @click="cambiarFiltro('todos')" :class="['filtro-btn', { active: tipoVista === 'todos' }]">
            <i class="fas fa-users"></i> Todos
          </button>
          <button @click="cambiarFiltro('profesores')" :class="['filtro-btn', { active: tipoVista === 'profesores' }]">
            <i class="fas fa-chalkboard-user"></i> Solo Profesores
          </button>
          <button @click="cambiarFiltro('psicologos')" :class="['filtro-btn', { active: tipoVista === 'psicologos' }]">
            <i class="fas fa-brain"></i> Solo Psicólogos
          </button>
        </div>
        
        <div class="filtro-busqueda">
          <i class="fas fa-search"></i>
          <input v-model="terminoBusqueda" type="text" placeholder="Buscar por nombre o correo..." class="busqueda-input" />
          <button v-if="terminoBusqueda" @click="terminoBusqueda = ''" class="limpiar-busqueda">
            <i class="fas fa-times"></i>
          </button>
        </div>
      </div>

      <div v-if="cargando" class="loading-container">
        <div class="spinner"></div>
        <p>Cargando información...</p>
      </div>

      <div class="profesores-card" v-else-if="profesoresFiltrados.length > 0">
        <div class="table-container">
          <table class="styled-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Correo</th>
                <th>Teléfono</th>
                <th>Título</th>
                <th>Tipo</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="profesor in currentProfesores" :key="profesor.id">
                <td :data-label="'Nombre'">
                  <div class="nombre-info">
                    <span v-if="profesor.esPsicologo" class="psicologo-badge">🧠</span>
                    {{ profesor.nombreCompleto }}
                  </div>
                </td>
                <td :data-label="'Correo'">{{ profesor.correoElectronico || '-' }}</td>
                <td :data-label="'Teléfono'">{{ profesor.telefono || '-' }}</td>
                <td :data-label="'Título'">{{ profesor.titulo || '-' }}</td>
                <td :data-label="'Tipo'">
                  <span :class="['tipo-badge', profesor.esPsicologo ? 'psicologo' : 'profesor']">
                    {{ profesor.esPsicologo ? 'Psicólogo' : 'Profesor' }}
                  </span>
                </td>
                <td :data-label="'Acciones'">
                  <button class="btn-secondary btn-accion" @click="editarProfesor(profesor)">Editar</button>
                  <button class="btn-danger btn-accion" @click="eliminarProfesor(profesor.id)">Eliminar</button>
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
            <i class="fas fa-plus"></i> Agregar {{ tipoVista === 'psicologos' ? 'Psicólogo' : 'Profesor' }}
          </button>
        </div>
      </div>

      <div v-else class="empty-state">
        <div class="empty-content">
          <i class="fas fa-chalkboard-user"></i>
          <h3>No hay {{ tipoVista === 'psicologos' ? 'psicólogos' : 'profesores' }} registrados</h3>
          <p>Comienza agregando {{ tipoVista === 'psicologos' ? 'tu primer psicólogo' : 'tu primer profesor' }} al sistema</p>
        </div>
      </div>
    </main>

    <!-- Modal del formulario -->
    <div v-if="mostrarFormulario" class="form-overlay" @click.self="cerrarFormulario">
      <div class="form-modal">
        <div class="modal-header">
          <h2>{{ modoEdicion ? 'Editar' : 'Agregar' }} {{ formProfesor.esPsicologo ? 'Psicólogo' : 'Profesor' }}</h2>
          <button class="close-btn" @click="cerrarFormulario">&times;</button>
        </div>

        <div class="modal-body">
          <form @submit.prevent="guardarProfesor">
            <div class="form-grid-layout">
              <div class="input-group full-width">
                <label class="checkbox-label">
                  <input type="checkbox" v-model="formProfesor.esPsicologo" :disabled="modoEdicion" />
                  <span>Marcar como Psicólogo</span>
                  <small v-if="modoEdicion" class="disabled-note">(No se puede cambiar el tipo después de crear)</small>
                </label>
              </div>

              <div class="input-group">
                <label>Nombre *</label>
                <input v-model="formProfesor.nombre" type="text" placeholder="Nombre" required />
              </div>

              <div class="input-group">
                <label>Apellido Paterno *</label>
                <input v-model="formProfesor.apellidoPaterno" type="text" placeholder="Apellido Paterno" required />
              </div>

              <div class="input-group">
                <label>Apellido Materno</label>
                <input v-model="formProfesor.apellidoMaterno" type="text" placeholder="Apellido Materno" />
              </div>

              <div class="input-group">
                <label>Correo Electrónico *</label>
                <input v-model="formProfesor.correoElectronico" type="email" placeholder="Correo" required />
              </div>

              <div class="input-group">
                <label>Teléfono</label>
                <input v-model="formProfesor.telefono" type="tel" placeholder="Teléfono (10 dígitos)" maxlength="10" />
              </div>

              <div class="input-group">
                <label>Título</label>
                <input v-model="formProfesor.titulo" type="text" placeholder="Título (ej: Ing., Lic., Dr., Psic.)" maxlength="100" />
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
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../../utils/axios-config'
import Swal from 'sweetalert2'
import '../../assets/styles.css'

const router = useRouter()

// ============================================================
// NAVEGACIÓN
// ============================================================
const goBack = () => router.back()

// ============================================================
// CONFIGURACIÓN DE API - Gateway
// ============================================================
const API_URL = '/api/profesores'

// ============================================================
// ESTADO
// ============================================================
const profesores = ref([])
const itemsPerPage = 5
const currentPage = ref(1)
const mostrarFormulario = ref(false)
const modoEdicion = ref(false)
const profesorEditando = ref(null)
const tipoVista = ref('todos')
const terminoBusqueda = ref('')
const cargando = ref(false)
const cargandoAccion = ref(false)

// ============================================================
// FORMULARIO
// ============================================================
const formProfesor = ref({
  nombre: '',
  apellidoPaterno: '',
  apellidoMaterno: '',
  correoElectronico: '',
  telefono: '',
  titulo: '',
  esPsicologo: false,
})

// ============================================================
// COMPUTADAS
// ============================================================
const profesoresFiltrados = computed(() => {
  let filtrados = profesores.value
  
  if (tipoVista.value === 'profesores') {
    filtrados = filtrados.filter(p => !p.esPsicologo)
  } else if (tipoVista.value === 'psicologos') {
    filtrados = filtrados.filter(p => p.esPsicologo)
  }
  
  if (terminoBusqueda.value.trim()) {
    const busqueda = terminoBusqueda.value.toLowerCase()
    filtrados = filtrados.filter(p => 
      p.nombreCompleto?.toLowerCase().includes(busqueda) ||
      p.correoElectronico?.toLowerCase().includes(busqueda)
    )
  }
  
  return filtrados
})

const totalPages = computed(() => Math.ceil(profesoresFiltrados.value.length / itemsPerPage))
const indexOfLastProfesor = computed(() => currentPage.value * itemsPerPage)
const indexOfFirstProfesor = computed(() => indexOfLastProfesor.value - itemsPerPage)
const currentProfesores = computed(() => profesoresFiltrados.value.slice(indexOfFirstProfesor.value, indexOfLastProfesor.value))

// ============================================================
// MÉTODOS
// ============================================================
const handlePageChange = (pageNumber) => { 
  currentPage.value = pageNumber 
}

const cambiarFiltro = (tipo) => { 
  tipoVista.value = tipo
  currentPage.value = 1
}

watch(terminoBusqueda, () => { 
  currentPage.value = 1 
})

// ============================================================
// CRUD - OBTENER PROFESORES
// ============================================================
const obtenerProfesores = async () => {
  cargando.value = true
  try {
    const res = await axios.get(API_URL)
    profesores.value = res.data
    console.log('Profesores cargados:', profesores.value)
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
  } finally {
    cargando.value = false
  }
}

// ============================================================
// CRUD - ABRIR FORMULARIO
// ============================================================
const abrirFormularioNuevo = () => {
  modoEdicion.value = false
  profesorEditando.value = null
  formProfesor.value = {
    nombre: '',
    apellidoPaterno: '',
    apellidoMaterno: '',
    correoElectronico: '',
    telefono: '',
    titulo: '',
    esPsicologo: tipoVista.value === 'psicologos',
  }
  mostrarFormulario.value = true
}

// ============================================================
// CRUD - EDITAR PROFESOR
// ============================================================
const editarProfesor = (profesor) => {
  modoEdicion.value = true
  profesorEditando.value = profesor.id
  
  // Dividir nombre completo en partes
  const nombreCompleto = profesor.nombreCompleto || ''
  const partes = nombreCompleto.split(' ')
  
  formProfesor.value = {
    nombre: partes[0] || '',
    apellidoPaterno: partes.slice(1, partes.length - 1).join(' ') || '',
    apellidoMaterno: partes[partes.length - 1] || '',
    correoElectronico: profesor.correoElectronico || '',
    telefono: profesor.telefono || '',
    titulo: profesor.titulo || '',
    esPsicologo: profesor.esPsicologo || false,
  }
  mostrarFormulario.value = true
}

// ============================================================
// CRUD - GUARDAR PROFESOR
// ============================================================
const guardarProfesor = async () => {
  // Validaciones
  if (!formProfesor.value.nombre.trim()) {
    await Swal.fire({
      icon: 'warning',
      title: 'Campo requerido',
      text: 'El nombre es obligatorio',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#F59E0B',
      width: '450px',
    })
    return
  }

  if (!formProfesor.value.apellidoPaterno.trim()) {
    await Swal.fire({
      icon: 'warning',
      title: 'Campo requerido',
      text: 'El apellido paterno es obligatorio',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#F59E0B',
      width: '450px',
    })
    return
  }

  if (!formProfesor.value.correoElectronico.trim()) {
    await Swal.fire({
      icon: 'warning',
      title: 'Campo requerido',
      text: 'El correo electrónico es obligatorio',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#F59E0B',
      width: '450px',
    })
    return
  }

  // Validar formato de teléfono (10 dígitos)
  if (formProfesor.value.telefono && !/^\d{10}$/.test(formProfesor.value.telefono)) {
    await Swal.fire({
      icon: 'warning',
      title: 'Formato inválido',
      text: 'El teléfono debe tener 10 dígitos numéricos',
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
    const payload = {
      nombre: formProfesor.value.nombre.trim(),
      apellidoPaterno: formProfesor.value.apellidoPaterno.trim(),
      apellidoMaterno: formProfesor.value.apellidoMaterno.trim() || '',
      correoElectronico: formProfesor.value.correoElectronico.trim(),
      telefono: formProfesor.value.telefono.trim() || '',
      titulo: formProfesor.value.titulo.trim() || '',
      esPsicologo: formProfesor.value.esPsicologo || false
    }

    if (modoEdicion.value) {
      await axios.patch(`${API_URL}/${profesorEditando.value}`, payload)
      await Swal.fire({
        icon: 'success',
        title: '¡Actualizado!',
        text: 'Profesor actualizado correctamente',
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
        text: 'Profesor agregado correctamente',
        showConfirmButton: false,
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
    }
    cerrarFormulario()
    await obtenerProfesores()
  } catch (error) {
    console.error('Error al guardar profesor:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: error.response?.data?.message || 'Error al guardar el profesor',
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

// ============================================================
// CRUD - ELIMINAR PROFESOR
// ============================================================
const eliminarProfesor = async (id) => {
  const profesor = profesores.value.find(p => p.id === id)
  const tipo = profesor?.esPsicologo ? 'psicólogo' : 'profesor'
  
  const confirm = await Swal.fire({
    title: `¿Eliminar ${tipo}?`,
    text: 'Esto también eliminará su usuario. Esta acción no se puede deshacer.',
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
        text: `${tipo} eliminado correctamente`,
        showConfirmButton: false,
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
      await obtenerProfesores()
      
      if (currentProfesores.value.length === 0 && currentPage.value > 1) {
        currentPage.value--
      }
    } catch (error) {
      console.error('Error al eliminar:', error)
      await Swal.fire({
        icon: 'error',
        title: 'Error',
        text: error.response?.data?.message || 'No se pudo eliminar el profesor',
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

// ============================================================
// CRUD - CERRAR FORMULARIO
// ============================================================
const cerrarFormulario = () => {
  mostrarFormulario.value = false
  modoEdicion.value = false
  profesorEditando.value = null
}

// ============================================================
// CICLO DE VIDA
// ============================================================
onMounted(() => {
  obtenerProfesores()
})
</script>

<style scoped>
/* =======================
   LISTA DE PROFESORES - CON BOTÓN REGRESAR
======================= */

.profesores-main {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
}

/* Header con botón a la izquierda y título centrado */
.profesores-header {
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

/* Estilos para filtros */
.filtros-container {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f0f0;
}

.filtros-buttons {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.filtro-btn {
  padding: 10px 20px;
  border: 2px solid #e2e8f0;
  background: white;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
}

.filtro-btn i {
  font-size: 1rem;
}

.filtro-btn:hover {
  border-color: #3abef9;
  color: #3abef9;
  transform: translateY(-2px);
}

.filtro-btn.active {
  background: #3abef9;
  border-color: #3abef9;
  color: white;
  box-shadow: 0 4px 12px rgba(58, 190, 249, 0.3);
}

.filtro-busqueda {
  position: relative;
  display: flex;
  align-items: center;
}

.filtro-busqueda i {
  position: absolute;
  left: 15px;
  color: #94a3b8;
}

.busqueda-input {
  width: 100%;
  padding: 12px 40px 12px 40px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.busqueda-input:focus {
  outline: none;
  border-color: #3abef9;
  box-shadow: 0 0 0 3px rgba(58, 190, 249, 0.1);
}

.limpiar-busqueda {
  position: absolute;
  right: 10px;
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  padding: 5px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.limpiar-busqueda:hover {
  background: #f1f5f9;
  color: #64748b;
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

/* Tarjeta blanca */
.profesores-card {
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

.nombre-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.psicologo-badge {
  font-size: 1.1rem;
}

.tipo-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-align: center;
}

.tipo-badge.profesor {
  background: #e0f2fe;
  color: #0284c7;
}

.tipo-badge.psicologo {
  background: #fae8ff;
  color: #a21caf;
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

.form-grid-layout .full-width {
  grid-column: 1 / -1;
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

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 10px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.checkbox-label input {
  width: auto;
  cursor: pointer;
}

.checkbox-label span {
  font-weight: 500;
  color: #334155;
}

.disabled-note {
  color: #94a3b8;
  font-size: 0.7rem;
  margin-left: auto;
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
  .profesores-main {
    padding: 0 15px;
  }
  
  .profesores-card {
    padding: 20px;
  }
  
  .form-grid-layout {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .styled-table th,
  .styled-table td {
    padding: 10px 12px;
  }
  
  .btn-accion {
    margin: 2px;
  }
  
  .filtros-buttons {
    flex-direction: column;
  }
  
  .filtro-btn {
    justify-content: center;
  }
}

@media (max-width: 600px) {
  .profesores-header {
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