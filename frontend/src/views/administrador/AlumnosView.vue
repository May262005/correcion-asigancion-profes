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

    <main class="alumnos-main">
      <div class="alumnos-header">
        <button @click="goBack" class="btn-back-icon" title="Volver al Dashboard">
          <i class="fas fa-arrow-left"></i>
        </button>
        <div class="header-text">
          <h1>Lista de Alumnos</h1>
          <p>Gestiona los estudiantes registrados en el sistema</p>
        </div>
        <div class="header-spacer"></div>
      </div>

      <div v-if="cargando" class="loading-container">
        <div class="spinner"></div>
        <p>Cargando información...</p>
      </div>

      <div class="alumnos-card" v-else-if="alumnos.length > 0">
        <div class="table-container">
          <table class="styled-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Correo</th>
                <th>Grupo</th>
                <th>Matrícula</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="alumno in currentAlumnos" :key="alumno.id">
                <td :data-label="'Nombre'">{{ alumno.nombreCompleto }}</td>
                <td :data-label="'Correo'">{{ alumno.correoElectronico }}</td>
                <td :data-label="'Grupo'">{{ obtenerNombreGrupo(alumno) }}</td>
                <td :data-label="'Matrícula'">{{ alumno.matricula }}</td>
                <td :data-label="'Acciones'">
                  <button class="btn-secondary btn-accion" @click="editarAlumno(alumno)">Editar</button>
                  <button class="btn-danger btn-accion" @click="eliminarAlumno(alumno.id)">Eliminar</button>
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
            <i class="fas fa-plus"></i> Agregar Alumno
          </button>
        </div>
      </div>

      <div v-else class="empty-state">
        <div class="empty-content">
          <i class="fas fa-user-graduate"></i>
          <h3>No hay alumnos registrados</h3>
          <p>Comienza agregando tu primer alumno al sistema</p>
        </div>
      </div>
    </main>

    <!-- Modal del formulario -->
    <div v-if="mostrarFormulario" class="form-overlay" @click.self="cerrarFormulario">
      <div class="form-modal">
        <div class="modal-header">
          <h2>{{ modoEdicion ? 'Editar Alumno' : 'Agregar Alumno' }}</h2>
          <button class="close-btn" @click="cerrarFormulario">&times;</button>
        </div>

        <div class="modal-body">
          <form @submit.prevent="guardarAlumno">
            <div class="form-grid-layout">
              <div class="input-group">
                <label>Nombre *</label>
                <input v-model="formAlumno.nombre" type="text" required placeholder="Nombre del alumno" />
              </div>

              <div class="input-group">
                <label>Apellido Paterno *</label>
                <input v-model="formAlumno.apellidoPaterno" type="text" required placeholder="Apellido paterno" />
              </div>

              <div class="input-group">
                <label>Apellido Materno</label>
                <input v-model="formAlumno.apellidoMaterno" type="text" placeholder="Apellido materno" />
              </div>

              <div class="input-group">
                <label>Correo Electrónico *</label>
                <input v-model="formAlumno.correoElectronico" type="email" required placeholder="correo@ejemplo.com" />
              </div>

              <div class="input-group">
                <label>Grupo *</label>
                <select v-model="formAlumno.idGrupo" required>
                  <option disabled value="">Selecciona un grupo</option>
                  <option v-for="g in grupos" :key="g.id" :value="g.id">
                    {{ g.nombre }}
                  </option>
                </select>
              </div>

              <div class="input-group">
                <label>Matrícula *</label>
                <input v-model="formAlumno.matricula" type="text" required placeholder="Número de matrícula" />
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

const API_ESTUDIANTES = '/api/estudiantes'
const API_GRUPOS = '/api/v1/grupos'

const goBack = () => {
  router.back()
}

const alumnos = ref([])
const grupos = ref([])
const cargando = ref(false)
const cargandoAccion = ref(false)
const itemsPerPage = 3
const currentPage = ref(1)
const mostrarFormulario = ref(false)
const modoEdicion = ref(false)
const alumnoEditando = ref(null)

const formAlumno = ref({
  nombre: '',
  apellidoPaterno: '',
  apellidoMaterno: '',
  correoElectronico: '',
  idGrupo: '',
  matricula: '',
})

const totalPages = computed(() => Math.ceil(alumnos.value.length / itemsPerPage))
const indexOfLastAlumno = computed(() => currentPage.value * itemsPerPage)
const indexOfFirstAlumno = computed(() => indexOfLastAlumno.value - itemsPerPage)
const currentAlumnos = computed(() => alumnos.value.slice(indexOfFirstAlumno.value, indexOfLastAlumno.value))

const handlePageChange = (pageNumber) => {
  currentPage.value = pageNumber
}

const obtenerAlumnos = async () => {
  cargando.value = true
  try {
    const res = await axios.get(API_ESTUDIANTES)
    // Mapear los datos para tener la estructura que espera el componente
    alumnos.value = res.data.map(alumno => ({
      ...alumno,
      idGrupo: alumno.grupo?.id || null,
      grupoNombre: alumno.grupo?.nombre || 'Sin grupo',
      grupoAbreviatura: alumno.grupo?.abreviatura || '',
      grupoColor: alumno.grupo?.colorIdentificador || '#6c757d'
    }))
  } catch (error) {
    console.error('Error al obtener alumnos:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'No se pudieron cargar los alumnos',
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

const obtenerGrupos = async () => {
  try {
    const res = await axios.get(API_GRUPOS)
    grupos.value = res.data
  } catch (error) {
    console.error('Error al obtener grupos:', error)
    await Swal.fire({
      icon: 'warning',
      title: 'Aviso',
      text: 'No se pudieron cargar los grupos. Los alumnos se mostrarán sin grupo.',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#F59E0B',
      width: '450px',
    })
  }
}

const obtenerNombreGrupo = (alumno) => {
  // Primero intentamos usar el grupoNombre que mapeamos al obtener los alumnos
  if (alumno.grupoNombre && alumno.grupoNombre !== 'Sin grupo') {
    return alumno.grupoNombre
  }
  
  // Si no tiene grupoNombre, buscamos en la lista de grupos por id
  if (alumno.idGrupo) {
    const grupo = grupos.value.find((g) => g.id === alumno.idGrupo)
    if (grupo) {
      return grupo.nombre
    }
  }
  
  // Si todo falla, usamos el grupo del objeto original si existe
  if (alumno.grupo && alumno.grupo.nombre) {
    return alumno.grupo.nombre
  }
  
  return 'Sin grupo'
}

const abrirFormularioNuevo = () => {
  modoEdicion.value = false
  alumnoEditando.value = null
  formAlumno.value = {
    nombre: '',
    apellidoPaterno: '',
    apellidoMaterno: '',
    correoElectronico: '',
    idGrupo: '',
    matricula: '',
  }
  mostrarFormulario.value = true
}

const editarAlumno = (alumno) => {
  modoEdicion.value = true
  alumnoEditando.value = alumno.id
  
  // Extraer nombre, apellido paterno y materno del nombre completo
  const nombreCompleto = alumno.nombreCompleto || ''
  const partes = nombreCompleto.trim().split(' ')
  
  let nombre = ''
  let apellidoPaterno = ''
  let apellidoMaterno = ''
  
  if (partes.length === 1) {
    nombre = partes[0]
  } else if (partes.length === 2) {
    nombre = partes[0]
    apellidoPaterno = partes[1]
  } else if (partes.length >= 3) {
    nombre = partes[0]
    apellidoPaterno = partes[1]
    apellidoMaterno = partes.slice(2).join(' ')
  }
  
  formAlumno.value = {
    nombre: nombre,
    apellidoPaterno: apellidoPaterno,
    apellidoMaterno: apellidoMaterno,
    correoElectronico: alumno.correoElectronico || '',
    idGrupo: alumno.idGrupo || alumno.grupo?.id || '',
    matricula: alumno.matricula || '',
  }
  mostrarFormulario.value = true
}

const guardarAlumno = async () => {
  cargandoAccion.value = true
  try {
    const payload = {
      nombre: formAlumno.value.nombre.trim(),
      apellidoPaterno: formAlumno.value.apellidoPaterno.trim(),
      apellidoMaterno: formAlumno.value.apellidoMaterno.trim() || '',
      correoElectronico: formAlumno.value.correoElectronico.trim(),
      idGrupo: parseInt(formAlumno.value.idGrupo),
      matricula: formAlumno.value.matricula.trim()
    }

    if (modoEdicion.value) {
      await axios.patch(`${API_ESTUDIANTES}/${alumnoEditando.value}`, payload)
      cargandoAccion.value = false
      await Swal.fire({
        icon: 'success',
        title: '¡Actualizado!',
        text: 'Alumno actualizado correctamente',
        showConfirmButton: false,
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
    } else {
      await axios.post(API_ESTUDIANTES, payload)
      cargandoAccion.value = false
      await Swal.fire({
        icon: 'success',
        title: '¡Agregado!',
        text: 'Alumno agregado correctamente',
        showConfirmButton: false,
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
    }
    cerrarFormulario()
    await obtenerAlumnos()
  } catch (error) {
    cargandoAccion.value = false
    console.error('Error guardando alumno:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error',
      text: error.response?.data?.message || 'Error al guardar el alumno',
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

const eliminarAlumno = async (id) => {
  const confirm = await Swal.fire({
    title: '¿Eliminar alumno?',
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
      await axios.delete(`${API_ESTUDIANTES}/${id}`)
      cargandoAccion.value = false
      await Swal.fire({
        icon: 'success',
        title: 'Eliminado',
        text: 'Alumno eliminado correctamente',
        showConfirmButton: false,
        timer: 2000,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
      await obtenerAlumnos()
      if (currentAlumnos.value.length === 0 && currentPage.value > 1) {
        currentPage.value--
      }
    } catch (error) {
      cargandoAccion.value = false
      console.error('Error al eliminar alumno:', error)
      await Swal.fire({
        icon: 'error',
        title: 'Error',
        text: error.response?.data?.message || 'No se pudo eliminar el alumno',
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
  alumnoEditando.value = null
}

onMounted(() => {
  obtenerAlumnos()
  obtenerGrupos()
})
</script>

<style scoped>
.alumnos-main {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
}

.alumnos-header {
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

.alumnos-card {
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
  .alumnos-main { padding: 0 15px; }
  .alumnos-card { padding: 20px; }
  .form-grid-layout { grid-template-columns: 1fr; gap: 12px; }
  .styled-table th, .styled-table td { padding: 10px 12px; }
  .btn-accion { margin: 2px; }
}

@media (max-width: 600px) {
  .alumnos-header { flex-wrap: wrap; justify-content: center; gap: 10px; }
  .header-spacer { display: none; }
  .btn-back-icon { position: absolute; left: 0; top: 0; }
  .header-text { flex: none; width: 100%; margin-top: 10px; }
  .header-text h1 { font-size: 1.5rem; }
}

@media (max-width: 480px) {
  .styled-table thead { display: none; }
  .styled-table tbody tr { display: block; margin-bottom: 15px; border: 1px solid #e2e8f0; border-radius: 12px; padding: 10px; }
  .styled-table td { display: flex; justify-content: space-between; align-items: center; padding: 8px 10px; border-bottom: 1px solid #e2e8f0; text-align: right; }
  .styled-table td::before { content: attr(data-label); font-weight: 600; color: #334155; text-align: left; }
  .styled-table td:last-child { border-bottom: none; }
  .btn-accion { width: auto; }
  .modal-footer { flex-direction: column-reverse; }
  .modal-footer button { width: 100%; }
}
</style>