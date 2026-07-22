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

    <main class="perfil-main">
      <!-- HEADER CON BOTÓN REGRESAR -->
      <div class="perfil-header">
        <button @click="goBack" class="btn-back-icon" title="Volver al Dashboard">
          <i class="fas fa-arrow-left"></i>
        </button>
        <div class="header-text">
          <h1>Mi Perfil</h1>
          <p>Gestiona tu información personal y académica</p>
        </div>
        <div class="header-spacer"></div>
      </div>

      <div v-if="cargando" class="loading-container">
        <div class="spinner"></div>
        <p>Cargando información...</p>
      </div>

      <div v-else-if="datos.usuario" class="perfil-card">
        <div class="avatar-section">
          <div class="perfil-avatar" @click="mostrarSelectorAvatar = true">
            <img v-if="avatarActual" :src="avatarActual.imagen" :alt="avatarActual.nombre" class="avatar-img" />
            <span v-else class="avatar-text">{{ obtenerIniciales(datos.usuario) }}</span>
            <div class="avatar-overlay">Cambiar</div>
          </div>

          <!-- Modal selector -->
          <div v-if="mostrarSelectorAvatar" class="avatar-modal-overlay" @click.self="mostrarSelectorAvatar = false">
            <div class="avatar-modal">
              <h3>Elige tu avatar</h3>
              <div class="avatar-grid">
                <img
                  v-for="av in catalogoAvatares"
                  :key="av.id"
                  :src="av.imagen"
                  :alt="av.nombre"
                  :class="['avatar-option', { seleccionado: avatarActual?.id === av.id }]"
                  @click="seleccionarAvatar(av)"
                />
              </div>
              <button @click="mostrarSelectorAvatar = false" class="btn-cerrar">Cerrar</button>
            </div>
          </div>

          <h2 class="user-full-name">
            {{ datos.usuario.nombre }} {{ datos.usuario.apellidoPaterno }}
          </h2>
          <span class="user-role-badge">Administrador</span>
        </div>

        <hr class="divider" />

        <div class="info-grid">
          <div class="info-item">
            <div class="info-icon"><i class="fas fa-envelope"></i></div>
            <div class="info-details">
              <span class="info-label">Correo Electrónico</span>
              <span class="info-value">{{ datos.usuario.correoElectronico || 'N/A' }}</span>
            </div>
          </div>

          <div class="info-item">
            <div class="info-icon"><i class="fas fa-lock"></i></div>
            <div class="info-details">
              <span class="info-label">Contraseña</span>
              <span class="info-value">{{ datos.usuario.passwordVisible || '********' }}</span>
            </div>
          </div>

          <div class="info-item full-width">
            <div class="info-icon"><i class="fas fa-user"></i></div>
            <div class="info-details">
              <span class="info-label">Nombre Completo</span>
              <span class="info-value">{{ datos.usuario.nombre }} {{ datos.usuario.apellidoPaterno }} {{ datos.usuario.apellidoMaterno }}</span>
            </div>
          </div>
        </div>

        <div class="perfil-actions">
          <button @click="editarPerfil" class="btn-primary">
            <i class="fas fa-edit"></i> Editar Perfil
          </button>
          <button @click="cerrarSesion" class="btn-danger">
            <i class="fas fa-sign-out-alt"></i> Cerrar Sesión
          </button>
        </div>
      </div>

      <div v-else class="empty-state">
        <div class="empty-content">
          <i class="fas fa-user-slash"></i>
          <h3>No se encontró información</h3>
          <p>Por favor, intenta recargar la página</p>
        </div>
      </div>
    </main>

    <!-- MODAL EDITAR PERFIL -->
    <Transition name="fade">
      <div v-if="mostrarFormulario" class="form-overlay" @click.self="cancelarEdicion">
        <div class="form-modal">
          <div class="modal-header">
            <h2>Actualizar Datos</h2>
            <button class="close-btn" @click="cancelarEdicion">&times;</button>
          </div>

          <div class="modal-body">
            <div class="form-grid-layout">
              <div class="input-group">
                <label>Nombre</label>
                <input v-model="formData.nombre" type="text" :class="{ error: errors.nombre }" />
              </div>

              <div class="input-group">
                <label>Apellido Paterno</label>
                <input v-model="formData.apellido_paterno" type="text" :class="{ error: errors.apellido_paterno }" />
              </div>

              <div class="input-group">
                <label>Apellido Materno</label>
                <input v-model="formData.apellido_materno" type="text" />
              </div>

              <div class="input-group">
                <label>Correo Institucional</label>
                <input v-model="formData.correo_electronico" type="email" :class="{ error: errors.correo_electronico }" />
              </div>

              <div class="input-group full-width">
                <label>Nueva Contraseña (opcional)</label>
                <input v-model="formData.password" type="password" placeholder="Mínimo 6 caracteres" />
              </div>

              <div v-if="formData.password" class="input-group full-width">
                <label>Confirmar Nueva Contraseña</label>
                <input v-model="formData.confirmarPassword" type="password" :class="{ error: errors.confirmarPassword }" />
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button @click="guardarCambios" class="btn-primary" :disabled="guardando">
              {{ guardando ? 'Guardando...' : 'Guardar Cambios' }}
            </button>
            <button @click="cancelarEdicion" class="btn-secondary">Cancelar</button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import axios from "@/utils/axios-config"
import Swal from "sweetalert2"

const router = useRouter()
const id = localStorage.getItem("id")
const datos = ref({ usuario: null })
const cargando = ref(true)
const cargandoAccion = ref(false)
const mostrarFormulario = ref(false)
const guardando = ref(false)

// Estados para el selector de avatar
const mostrarSelectorAvatar = ref(false)
const catalogoAvatares = ref([])
const avatarActual = ref(null)

const goBack = () => {
  router.back()
}

const formData = ref({
  nombre: '',
  apellido_paterno: '',
  apellido_materno: '',
  correo_electronico: '',
  password: '',
  confirmarPassword: ''
})

const errors = ref({
  nombre: false,
  apellido_paterno: false,
  correo_electronico: false,
  confirmarPassword: false
})

const obtenerIniciales = (usuario) => {
  if (!usuario) return '?'
  const n = usuario.nombre?.charAt(0) || ''
  const a = usuario.apellidoPaterno?.charAt(0) || ''
  return (n + a).toUpperCase()
}

// Carga el catálogo y detecta automáticamente el avatar activo
const cargarCatalogoAvatares = async () => {
  try {
    const res = await axios.get('/api/v1/profiles/avatares')
    catalogoAvatares.value = res.data

    // Detectar idAvatar independientemente de la estructura enviada por el microservicio
    const idAvatar = datos.value.usuario?.idAvatar || datos.value.usuario?.avatar?.id
    if (idAvatar) {
      avatarActual.value = catalogoAvatares.value.find(a => a.id === idAvatar) || null
    }
  } catch (e) {
    console.warn('No se pudo cargar el catálogo de avatares', e)
  }
}

const seleccionarAvatar = async (avatar) => {
  try {
    // 1. Enviar el cambio al backend
    await axios.patch(`/api/v1/profiles/${id}/avatar/${avatar.id}`)
    
    // 2. Actualizar estado local inmediato
    avatarActual.value = avatar
    if (datos.value.usuario) {
      datos.value.usuario.idAvatar = avatar.id
    }

    // 3. Re-consultar perfil para garantizar consistencia con M2
    const res = await axios.get(`/api/usuario/perfil/${id}`)
    datos.value = res.data

    mostrarSelectorAvatar.value = false
    Swal.fire({ icon: 'success', title: 'Avatar actualizado', showConfirmButton: false, timer: 1200 })
  } catch (e) {
    console.error('Error al cambiar avatar:', e)
    Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo guardar el avatar en el servidor' })
  }
}

onMounted(async () => {
  try {
    const res = await axios.get(`/api/usuario/perfil/${id}`)
    datos.value = res.data
    await cargarCatalogoAvatares()
  } catch (e) {
    Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'No se pudo cargar el perfil',
      confirmButtonColor: '#3abef9'
    })
  } finally {
    cargando.value = false
  }
})

const cerrarSesion = async () => {
  const confirm = await Swal.fire({
    title: '¿Cerrar sesión?',
    text: "Tendrás que volver a ingresar tus credenciales.",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Sí, salir',
    cancelButtonText: 'Cancelar',
    confirmButtonColor: '#3abef9',
  })
  if (confirm.isConfirmed) {
    localStorage.clear()
    router.push({ name: 'landin-page' })
  }
}

const editarPerfil = () => {
  formData.value = {
    nombre: datos.value.usuario?.nombre || '',
    apellido_paterno: datos.value.usuario?.apellidoPaterno || '',
    apellido_materno: datos.value.usuario?.apellidoMaterno || '',
    correo_electronico: datos.value.usuario?.correoElectronico || '',
    password: '',
    confirmarPassword: ''
  }
  errors.value = { nombre: false, apellido_paterno: false, correo_electronico: false, confirmarPassword: false }
  mostrarFormulario.value = true
}

const validarFormulario = () => {
  let valido = true
  errors.value = { nombre: false, apellido_paterno: false, correo_electronico: false, confirmarPassword: false }

  if (!formData.value.nombre.trim()) { errors.value.nombre = true; valido = false; }
  if (!formData.value.apellido_paterno.trim()) { errors.value.apellido_paterno = true; valido = false; }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!formData.value.correo_electronico.trim() || !emailRegex.test(formData.value.correo_electronico)) {
    errors.value.correo_electronico = true; valido = false;
  }
  if (formData.value.password && formData.value.password !== formData.value.confirmarPassword) {
    errors.value.confirmarPassword = true; valido = false;
  }
  return valido
}

const guardarCambios = async () => {
  if (!validarFormulario()) return
  guardando.value = true
  cargandoAccion.value = true
  try {
    const datosActualizar = {
      nombre: formData.value.nombre,
      apellidoPaterno: formData.value.apellido_paterno,
      apellidoMaterno: formData.value.apellido_materno,
      correoElectronico: formData.value.correo_electronico,
    }
    if (formData.value.password) {
      datosActualizar.password = formData.value.password
    }

    await axios.put(`/api/usuario/perfil/${id}`, datosActualizar)
    const res = await axios.get(`/api/usuario/perfil/${id}`)
    datos.value = res.data
    mostrarFormulario.value = false
    Swal.fire({ icon: 'success', title: 'Perfil actualizado', showConfirmButton: false, timer: 1500 })
  } catch (error) {
    Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo actualizar' })
  } finally {
    guardando.value = false
    cargandoAccion.value = false
  }
}

const cancelarEdicion = () => { mostrarFormulario.value = false }
</script>

<style scoped>
/* =======================
   PERFIL ADMIN - CON BOTÓN REGRESAR
======================= */

.perfil-main {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 20px;
  text-align: center;
}

.perfil-header {
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

.perfil-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  padding: 40px;
  border: 1px solid #f0f0f0;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
}

.perfil-avatar {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #3abef9, #1e90ff);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 15px;
  box-shadow: 0 5px 15px rgba(58, 190, 249, 0.4);
  position: relative;
  cursor: pointer;
}

.avatar-text {
  font-size: 2.2rem;
  color: white;
  font-weight: bold;
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  font-size: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}

.perfil-avatar:hover .avatar-overlay {
  opacity: 1;
}

.avatar-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.avatar-modal {
  background: white;
  padding: 24px;
  border-radius: 12px;
  max-width: 400px;
  width: 90%;
}

.avatar-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin: 16px 0;
}

.avatar-option {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  cursor: pointer;
  border: 3px solid transparent;
  object-fit: cover;
}

.avatar-option.seleccionado {
  border-color: #3abef9;
}

.btn-cerrar {
  background: #e2e8f0;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  color: #475569;
}

.btn-cerrar:hover {
  background: #cbd5e1;
}

.user-full-name {
  font-size: 1.5rem;
  margin: 0;
  color: #2c3e50;
}

.user-role-badge {
  background: #e1f5fe;
  color: #039be5;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: bold;
  margin-top: 8px;
}

.divider {
  border: 0;
  border-top: 1px solid #eee;
  margin: 30px 0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 15px;
  text-align: left;
  padding: 15px;
  background: #fcfcfc;
  border-radius: 12px;
}

.info-item.full-width {
  grid-column: span 2;
}

.info-icon {
  width: 40px;
  height: 40px;
  background: #f0f7ff;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #3abef9;
}

.info-details {
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: 0.75rem;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-value {
  font-weight: 600;
  color: #334155;
}

.perfil-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.btn-primary {
  background: #3abef9;
  color: white;
  padding: 12px 25px;
  border-radius: 10px;
  border: none;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-danger {
  background: #ffeded;
  color: #ef4444;
  padding: 12px 25px;
  border-radius: 10px;
  border: none;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-danger:hover {
  background: #fee2e2;
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

.input-group input.error {
  border-color: #ef4444;
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

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .perfil-main { padding: 0 15px; }
  .perfil-card { padding: 25px; }
  .info-grid { grid-template-columns: 1fr; }
  .info-item.full-width { grid-column: span 1; }
  .form-grid-layout { grid-template-columns: 1fr; gap: 12px; }
  .input-group.full-width { grid-column: span 1; }
  .perfil-actions { flex-direction: column; }
  .perfil-actions button { width: 100%; justify-content: center; }
  .modal-footer { flex-direction: column-reverse; }
  .modal-footer button { width: 100%; }
}

@media (max-width: 600px) {
  .perfil-header { flex-wrap: wrap; justify-content: center; gap: 10px; }
  .header-spacer { display: none; }
  .btn-back-icon { position: absolute; left: 0; top: 0; }
  .header-text { flex: none; width: 100%; margin-top: 10px; }
  .header-text h1 { font-size: 1.5rem; }
}
</style>