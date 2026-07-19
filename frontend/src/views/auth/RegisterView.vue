<template>
  <div class="register-wrapper animate-bg">
    <div class="bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>

    <div class="register-container-card">
      <div class="logo-section">
        <img src="../../imagenes/logo1.png" class="img-lo-reg animate-pop" alt="Logo Cronos" />
        <h2 class="welcome-text">Crea tu cuenta</h2>
        <p class="subtitle-text">Únete a la plataforma académica Cronos</p>
      </div>

      <form @submit.prevent="register" class="form-lg">
        <div class="input-grid">
          <div class="input-group">
            <i class="fas fa-user"></i>
            <input v-model="nombre" type="text" placeholder="Nombre" required />
          </div>

          <div class="input-group">
            <i class="fas fa-id-card"></i>
            <input v-model="apellidoPaterno" type="text" placeholder="Ap. Paterno" required />
          </div>

          <div class="input-group full-width-mobile">
            <i class="fas fa-id-card"></i>
            <input v-model="apellidoMaterno" type="text" placeholder="Ap. Materno" required />
          </div>
        </div>

        <div class="input-group">
          <i class="fas fa-envelope"></i>
          <input v-model="correoElectronico" type="email" placeholder="Correo institucional" @input="detectarRol" required />
        </div>

        <div class="input-group">
          <i class="fas fa-lock"></i>
          <input v-model="contrasena" type="password" placeholder="Contraseña" required @input="mostrarInfo = true" />
        </div>

        <transition name="fade">
          <div v-if="mostrarInfo && !esContrasenaSegura" class="password-status-card">
            <p><i class="fas fa-shield-alt"></i> Seguridad requerida:</p>
            <ul class="status-list">
              <li :class="{ 'valid': longitudValida }"><i class="fas" :class="longitudValida ? 'fa-check' : 'fa-times'"></i> 8+ carac.</li>
              <li :class="{ 'valid': tieneMayuscula }"><i class="fas" :class="tieneMayuscula ? 'fa-check' : 'fa-times'"></i> Mayúscula</li>
              <li :class="{ 'valid': tieneNumero }"><i class="fas" :class="tieneNumero ? 'fa-check' : 'fa-times'"></i> Número</li>
              <li :class="{ 'valid': tieneEspecial }"><i class="fas" :class="tieneEspecial ? 'fa-check' : 'fa-times'"></i> Símbolo</li>
            </ul>
          </div>
        </transition>

        <!-- ✅ CAMPOS PARA PROFESOR -->
        <transition name="scale">
          <div v-if="esProfesor" class="dynamic-fields">
            <div class="input-group">
              <i class="fas fa-phone"></i>
              <input v-model="telefono" type="tel" placeholder="Teléfono (10 dígitos)" pattern="[0-9]{10}" maxlength="10" required />
            </div>
            <div class="input-group">
              <i class="fas fa-graduation-cap"></i>
              <input v-model="titulo" type="text" placeholder="Título (ej: Ing., Lic.)" maxlength="100" required />
            </div>
            <!-- ✅ CHECKBOX PARA PSICÓLOGO -->
            <div class="input-group checkbox-group">
              <label class="checkbox-label">
                <input type="checkbox" v-model="esPsicologo" />
                <span><i class="fas fa-brain"></i> Marcar como Psicólogo</span>
              </label>
            </div>
          </div>
        </transition>

        <!-- ✅ CAMPOS PARA ESTUDIANTE -->
        <transition name="scale">
          <div v-if="!esProfesor && correoElectronico" class="dynamic-fields">
            <div class="input-group select-group">
              <i class="fas fa-users"></i>
              <select v-model="idGrupo" required>
                <option value="" disabled selected>Selecciona tu grupo</option>
                <option v-for="g in grupos" :key="g.id" :value="g.id">
                  {{ g.grado }}° {{ g.abreviatura }}
                </option>
              </select>
            </div>
            <div class="input-group">
              <i class="fas fa-id-card"></i>
              <input v-model="matricula" type="text" placeholder="Matrícula" required />
            </div>
          </div>
        </transition>

        <div class="btn-container">
          <button type="submit" class="btn-primary-register" :disabled="procesando">
            <span>{{ procesando ? 'Registrando...' : 'Finalizar Registro' }}</span>
            <i v-if="!procesando" class="fas fa-user-plus"></i>
          </button>
        </div>

        <p class="regresar-l">
          ¿Ya tienes cuenta?
          <router-link to="/inicio-sesion" class="nav-to">Inicia sesión</router-link>
        </p>
      </form>
    </div>
  </div>
</template>

<script setup>
import axios from '../../utils/axios-config'
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'

const router = useRouter()

// ============================================================
// ESTADO
// ============================================================
const nombre = ref('')
const apellidoPaterno = ref('')
const apellidoMaterno = ref('')
const correoElectronico = ref('')
const contrasena = ref('')
const telefono = ref('')
const titulo = ref('')
const esProfesor = ref(false)
const esPsicologo = ref(false)
const mostrarInfo = ref(false)
const procesando = ref(false)
const grupos = ref([])
const idGrupo = ref('')
const matricula = ref('')

// ============================================================
// OBTENER GRUPOS
// ============================================================
const obtenerGrupos = async () => {
  try {
    const res = await axios.get('/api/v1/grupos')
    grupos.value = res.data
  } catch (error) {
    console.error('Error cargando grupos:', error)
  }
}

// ============================================================
// DETECTAR ROL POR CORREO
// ============================================================
const detectarRol = () => {
  const emailPart = correoElectronico.value.split('@')[0]
  const soloNumeros = /^\d+$/.test(emailPart)
  esProfesor.value = !soloNumeros && correoElectronico.value.length > 0
  
  if (!esProfesor.value && correoElectronico.value.length > 0) {
    obtenerGrupos()
  }
}

// ============================================================
// VALIDACIONES DE CONTRASEÑA
// ============================================================
const tieneMinuscula = computed(() => /[a-z]/.test(contrasena.value))
const tieneMayuscula = computed(() => /[A-Z]/.test(contrasena.value))
const tieneNumero = computed(() => /\d/.test(contrasena.value))
const tieneEspecial = computed(() => /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(contrasena.value))
const longitudValida = computed(() => contrasena.value.length >= 8)

const esContrasenaSegura = computed(() =>
  tieneMinuscula.value && tieneMayuscula.value && tieneNumero.value && tieneEspecial.value && longitudValida.value
)

// ============================================================
// REGISTRAR USUARIO
// ============================================================
const register = async () => {
  if (procesando.value) return

  if (!esContrasenaSegura.value) {
    await Swal.fire({
      icon: 'info',
      title: 'Contraseña no segura',
      text: 'Asegúrate de cumplir los requisitos de seguridad.',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#3ABEF9',
      width: '450px',
    })
    return
  }

  procesando.value = true

  try {
    if (esProfesor.value) {
      // ============================================================
      // ✅ REGISTRO DE PROFESOR (con contraseña del usuario)
      // ============================================================
      const payload = {
        nombre: nombre.value.trim(),
        apellidoPaterno: apellidoPaterno.value.trim(),
        apellidoMaterno: apellidoMaterno.value.trim(),
        correoElectronico: correoElectronico.value.trim(),
        telefono: telefono.value.trim() || '',
        titulo: titulo.value.trim() || '',
        esPsicologo: esPsicologo.value,
        contrasena: contrasena.value  // ✅ ENVÍA LA CONTRASEÑA
      }

      await axios.post('/api/profesores', payload)

      await Swal.fire({
        icon: 'success',
        title: '¡Registro exitoso!',
        text: `Profesor${esPsicologo.value ? ' (Psicólogo)' : ''} registrado correctamente`,
        showConfirmButton: false,
        timer: 1500,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })

    } else {
      // ============================================================
      // ✅ REGISTRO DE ESTUDIANTE (con contraseña del usuario)
      // ============================================================
      if (!idGrupo.value) {
        await Swal.fire({
          icon: 'warning',
          title: 'Grupo requerido',
          text: 'Selecciona un grupo para continuar',
          confirmButtonColor: '#3ABEF9',
          background: '#ffffff',
          color: '#213547',
          iconColor: '#F59E0B',
          width: '450px',
        })
        procesando.value = false
        return
      }

      if (!matricula.value.trim()) {
        await Swal.fire({
          icon: 'warning',
          title: 'Matrícula requerida',
          text: 'Ingresa tu número de matrícula',
          confirmButtonColor: '#3ABEF9',
          background: '#ffffff',
          color: '#213547',
          iconColor: '#F59E0B',
          width: '450px',
        })
        procesando.value = false
        return
      }

      const payload = {
        nombre: nombre.value.trim(),
        apellidoPaterno: apellidoPaterno.value.trim(),
        apellidoMaterno: apellidoMaterno.value.trim(),
        correoElectronico: correoElectronico.value.trim(),
        idGrupo: parseInt(idGrupo.value),
        matricula: matricula.value.trim(),
        contrasena: contrasena.value  // ✅ ENVÍA LA CONTRASEÑA
      }

      await axios.post('/api/estudiantes', payload)

      await Swal.fire({
        icon: 'success',
        title: '¡Registro exitoso!',
        text: 'Estudiante registrado correctamente',
        showConfirmButton: false,
        timer: 1500,
        background: '#ffffff',
        color: '#213547',
        iconColor: '#3ABEF9',
        width: '450px',
      })
    }

    // Mostrar aviso de privacidad
    await Swal.fire({
      title: 'Aviso de Privacidad',
      html: `<div style="text-align: justify; font-size: 14px;">Tus datos serán utilizados únicamente para fines académicos y administrativos, garantizando la confidencialidad.</div>`,
      icon: 'info',
      confirmButtonText: 'Aceptar',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#3ABEF9',
      allowOutsideClick: false,
      width: '450px',
    })

    router.push({ name: 'login' })

  } catch (error) {
    console.error('Error al registrar:', error)
    await Swal.fire({
      icon: 'error',
      title: 'Error al registrar',
      text: error.response?.data?.message || 'Error en el servidor',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#E54848',
      width: '500px',
    })
  } finally {
    procesando.value = false
  }
}

// ============================================================
// CICLO DE VIDA
// ============================================================
onMounted(() => {
  obtenerGrupos()
})
</script>

<style scoped>
/* Fondo animado */
.register-wrapper {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(-45deg, #e3f2fd, #bbdefb, #e1f5fe, #3abef9);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
  padding: 40px 20px;
}

@keyframes gradientBG {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* Shapes decorativas */
.bg-shapes .shape {
  position: absolute;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  filter: blur(60px);
  z-index: 0;
}

.shape-1 {
  width: 350px;
  height: 350px;
  top: -100px;
  left: -100px;
  animation: float 10s infinite;
}

.shape-2 {
  width: 400px;
  height: 400px;
  bottom: -150px;
  right: -100px;
  animation: float 8s infinite reverse;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(20px); }
}

/* Tarjeta Principal */
.register-container-card {
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(15px);
  padding: 35px;
  border-radius: 28px;
  width: 100%;
  max-width: 520px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.1);
  animation: slideIn 0.6s ease-out;
}

.logo-section {
  text-align: center;
  margin-bottom: 25px;
}

.img-lo-reg {
  width: 100px;
  margin-bottom: 10px;
}

.welcome-text {
  color: #1e293b;
  font-size: 1.6rem;
  font-weight: 800;
  margin: 0;
}

.subtitle-text {
  color: #64748b;
  font-size: 0.9rem;
}

/* Grid de Inputs */
.input-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-bottom: 15px;
}

.input-group {
  position: relative;
  margin-bottom: 15px;
}

.input-group i {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
  font-size: 0.9rem;
  z-index: 2;
}

.input-group input,
.input-group select {
  width: 100%;
  padding: 12px 15px 12px 42px;
  border: 2px solid #f1f5f9;
  border-radius: 12px;
  background: #f8fafc;
  font-size: 0.95rem;
  transition: 0.3s;
}

.input-group input:focus,
.input-group select:focus {
  border-color: #3abef9;
  outline: none;
  background: white;
  box-shadow: 0 0 0 4px rgba(58, 190, 249, 0.1);
}

/* ============================================================
   ✅ CHECKBOX PARA PSICÓLOGO
   ============================================================ */
.checkbox-group {
  position: relative;
  margin-bottom: 15px;
}

.checkbox-group i {
  display: none;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 12px 18px;
  background: #f8fafc;
  border-radius: 12px;
  border: 2px solid #f1f5f9;
  transition: 0.3s;
  width: 100%;
  box-sizing: border-box;
}

.checkbox-label:hover {
  border-color: #3abef9;
  background: #f0f9ff;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #3abef9;
  flex-shrink: 0;
  margin: 0;
}

.checkbox-label span {
  font-weight: 500;
  color: #334155;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  gap: 8px;
}

.checkbox-label span i {
  display: inline-block !important;
  position: static !important;
  transform: none !important;
  color: #3abef9;
  font-size: 1rem;
}

/* ============================================================
   PASSWORD STATUS
   ============================================================ */
.password-status-card {
  background: #fffafa;
  border: 1px solid #fee2e2;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 15px;
}

.password-status-card p {
  font-size: 0.75rem;
  font-weight: 800;
  color: #991b1b;
  margin-bottom: 8px;
}

.status-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 5px;
  list-style: none;
  padding: 0;
  margin: 0;
}

.status-list li {
  font-size: 0.7rem;
  color: #ef4444;
  display: flex;
  align-items: center;
  gap: 5px;
}

.status-list li.valid {
  color: #10b981;
}

/* Botón Moderno */
.btn-primary-register {
  width: 100%;
  background: #1e293b;
  color: white;
  border: none;
  padding: 15px;
  border-radius: 14px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: 0.3s;
}

.btn-primary-register:hover:not(:disabled) {
  background: #3abef9;
  transform: translateY(-2px);
  box-shadow: 0 10px 15px rgba(58, 190, 249, 0.3);
}

.btn-primary-register:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.regresar-l {
  text-align: center;
  margin-top: 20px;
  color: #64748b;
  font-size: 0.9rem;
}

.nav-to {
  color: #3abef9;
  text-decoration: none;
  font-weight: 700;
}

/* Transiciones */
.scale-enter-active,
.scale-leave-active {
  transition: all 0.3s ease;
}

.scale-enter-from,
.scale-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* ============================================================
   RESPONSIVE
   ============================================================ */
@media (max-width: 550px) {
  .input-grid {
    grid-template-columns: 1fr;
    gap: 0;
  }

  .full-width-mobile {
    grid-column: 1 / -1;
  }

  .register-container-card {
    padding: 25px 20px;
    border-radius: 20px;
  }

  .welcome-text {
    font-size: 1.3rem;
  }

  .bg-shapes .shape-3 {
    display: none;
  }

  .status-list {
    grid-template-columns: 1fr;
  }

  .checkbox-label {
    padding: 10px 14px;
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-pop {
  animation: pop 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes pop {
  from {
    transform: scale(0.8);
  }
  to {
    transform: scale(1);
  }
}
</style>