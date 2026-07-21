<template>
  <div>
    <nav class="nav">
      <div class="nav-container">
        <router-link to="/alumno" class="nav-figure">
          <img src="../../imagenes/logo.png" class="nav-logo" alt="Logo Cronos" />
        </router-link>

        <label class="nav-toggle" for="menu-input">
          <input type="checkbox" id="menu-input" class="nav-input" />
        </label>

        <ul class="nav-list">
          <li class="nav-item">
            <router-link to="/alumno" class="nav-link">Inicio</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/alumno/perfil" class="nav-link">Perfil</router-link>
          </li>
        </ul>
      </div>
    </nav>

    <main class="perfil-main">
      <!-- HEADER CON BOTÓN REGRESAR -->
      <div class="perfil-header">
        <button @click="goBack" class="btn-back-icon" title="Volver al Inicio">
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
          <div class="perfil-avatar">
            <span class="avatar-text">{{ obtenerIniciales(datos.usuario) }}</span>
          </div>
          <h2 class="user-full-name">
            {{ datos.usuario.nombre }} {{ datos.usuario.apellido_paterno }}
          </h2>
          <span class="user-role-badge">Estudiante</span>
        </div>

        <hr class="divider" />

        <div class="info-grid">
          <div class="info-item">
            <div class="info-icon"><i class="fas fa-envelope"></i></div>
            <div class="info-details">
              <span class="info-label">Correo Electrónico</span>
              <span class="info-value">{{ datos.usuario.correo_electronico || 'N/A' }}</span>
            </div>
          </div>

          <div class="info-item">
            <div class="info-icon"><i class="fas fa-lock"></i></div>
            <div class="info-details">
              <span class="info-label">Contraseña</span>
              <span class="info-value">{{ datos.usuario.passwordVisible || '********' }}</span>
            </div>
          </div>

          <template v-if="datos.extra">
            <div class="info-item">
              <div class="info-icon"><i class="fas fa-id-card"></i></div>
              <div class="info-details">
                <span class="info-label">Matrícula</span>
                <span class="info-value">{{ datos.extra.matricula || 'N/A' }}</span>
              </div>
            </div>

            <div class="info-item">
              <div class="info-icon"><i class="fas fa-users"></i></div>
              <div class="info-details">
                <span class="info-label">Grupo</span>
                <span class="info-value">{{ datos.extra.grupo?.nombre || 'Sin grupo' }}</span>
              </div>
            </div>
          </template>
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
                <label>Nombre(s)</label>
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
                <div class="password-wrapper">
                  <input v-model="formData.password" type="password" placeholder="Mínimo 8 caracteres" />
                </div>
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

const goBack = () => {
  router.push('/alumno')
}

const id = localStorage.getItem("id")
const datos = ref({
  usuario: null,
  extra: null
})
const cargando = ref(true)
const mostrarFormulario = ref(false)
const guardando = ref(false)

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
  const nombres = usuario.nombre?.split(' ')[0] || ''
  const apellido = usuario.apellido_paterno?.split(' ')[0] || ''
  return (nombres[0] || '?') + (apellido[0] || '')
}

onMounted(async () => {
  try {
    const res = await axios.get(`/api/usuario/perfil/${id}`)
    datos.value = res.data
  } catch (e) {
    console.error('Error al cargar perfil:', e)
    Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'No se pudo cargar el perfil',
      confirmButtonText: 'Aceptar',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#E54848',
      customClass: { confirmButton: 'btn-primary' },
      buttonsStyling: false
    })
  } finally {
    cargando.value = false
  }
})

const cerrarSesion = async () => {
  const confirm = await Swal.fire({
    title: '¿Deseas cerrar sesión?',
    text: "Tendrás que volver a ingresar tus credenciales.",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Sí, salir',
    cancelButtonText: 'Cancelar',
    background: '#ffffff',
    color: '#213547',
    iconColor: '#f39c12',
    customClass: { confirmButton: 'btn-primary', cancelButton: 'btn-secondary' },
    buttonsStyling: false
  })

  if (confirm.isConfirmed) {
    localStorage.clear()
    router.push({ name: 'landin-page' })
  }
}

const editarPerfil = () => {
  if (!datos.value.usuario) return
  formData.value = {
    nombre: datos.value.usuario.nombre || '',
    apellido_paterno: datos.value.usuario.apellido_paterno || '',
    apellido_materno: datos.value.usuario.apellido_materno || '',
    correo_electronico: datos.value.usuario.correo_electronico || '',
    password: '',
    confirmarPassword: ''
  }
  mostrarFormulario.value = true
}

const validarFormulario = () => {
  let valido = true
  errors.value = { nombre: false, apellido_paterno: false, correo_electronico: false, confirmarPassword: false }
  
  if (!formData.value.nombre.trim()) { errors.value.nombre = true; valido = false }
  if (!formData.value.apellido_paterno.trim()) { errors.value.apellido_paterno = true; valido = false }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!formData.value.correo_electronico.trim() || !emailRegex.test(formData.value.correo_electronico)) {
    errors.value.correo_electronico = true
    valido = false
  }
  
  if (formData.value.password && formData.value.password !== formData.value.confirmarPassword) {
    errors.value.confirmarPassword = true
    valido = false
    Swal.fire({ icon: 'error', title: 'Error', text: 'Las contraseñas no coinciden' })
  }
  return valido
}

const guardarCambios = async () => {
  if (!validarFormulario()) return
  guardando.value = true
  try {
    const datosActualizar = {
      nombre: formData.value.nombre,
      apellido_paterno: formData.value.apellido_paterno,
      apellido_materno: formData.value.apellido_materno,
      correo_electronico: formData.value.correo_electronico
    }
    if (formData.value.password) datosActualizar.password = formData.value.password
    
    await axios.put(`/api/usuario/perfil/${id}`, datosActualizar)
    const res = await axios.get(`/api/usuario/perfil/${id}`)
    datos.value = res.data
    mostrarFormulario.value = false
    Swal.fire({ icon: 'success', title: '¡Actualizado!', showConfirmButton: false, timer: 1500 })
  } catch (error) {
    Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo actualizar' })
  } finally {
    guardando.value = false
  }
}

const cancelarEdicion = () => { mostrarFormulario.value = false }
</script>

<style scoped>
/* =======================
   PERFIL ALUMNO - CON BOTÓN REGRESAR
======================= */

.perfil-main {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 20px;
  text-align: center;
}

/* Header con botón a la izquierda y título centrado */
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

/* CARD DE PERFIL */
.perfil-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.08);
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
}

.avatar-text {
  font-size: 2.2rem;
  color: white;
  font-weight: bold;
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

/* GRID DE INFO */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
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

/* BOTONES */
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
  transition: all 0.2s;
}

.btn-primary:hover {
  background: #29a9e0;
}

.btn-danger {
  background: #ffeded;
  color: #ef4444;
  padding: 12px 25px;
  border-radius: 10px;
  border: none;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-danger:hover {
  background: #fee2e2;
}

/* MODAL DE EDICIÓN */
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

.modal-header h2 { font-size: 1.25rem; margin: 0; }

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #64748b;
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

.input-group.full-width { grid-column: span 2; }

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
  font-size: 0.95rem;
  transition: all 0.2s;
}

.input-group input:focus {
  outline: none;
  border-color: #3abef9;
  box-shadow: 0 0 0 3px rgba(58, 190, 249, 0.1);
}

.input-group input.error { border-color: #ef4444; }

.modal-footer {
  padding: 20px 25px;
  display: flex;
  flex-direction: row-reverse;
  gap: 10px;
  background: #f8fafc;
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


/* TRANSICIONES */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* Responsive */
@media (max-width: 600px) {
  .perfil-header {
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
  
  .form-grid-layout { 
    grid-template-columns: 1fr; 
  }
  
  .input-group.full-width { 
    grid-column: span 1; 
  }
  
  .perfil-actions { 
    flex-direction: column; 
  }
  
  .modal-footer { 
    flex-direction: column; 
  }
  
  .modal-footer button {
    width: 100%;
  }
}
</style>