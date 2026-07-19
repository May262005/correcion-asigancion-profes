<template>
  <div class="login-wrapper animate-bg">
    <div class="bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>

    <div class="login-container-card">
      <div class="logo-section">
        <img src="../../imagenes/logo1.png" class="img-lo-reg animate-pop" alt="Logo Cronos" />
        <h2 class="welcome-text">¡Bienvenido!</h2>
      </div>

      <form @submit="handleSubmit" class="form-lg">
        <div class="input-group">
          <i class="fas fa-envelope"></i>
          <input v-model="correoElectronico" type="email" placeholder="Correo electrónico" required />
        </div>

        <div class="input-group">
          <i class="fas fa-lock"></i>
          <input v-model="contrasena" type="password" placeholder="Contraseña" required />
        </div>

        <div class="btn-container">
          <button type="submit" class="btn-primary-login">
            <span>Iniciar Sesión</span>
            <i class="fas fa-arrow-right"></i>
          </button>
        </div>
      </form>

      <p class="regresar-l">
        ¿No tienes cuenta?
        <router-link :to="{ name: 'registro' }" class="nav-to">Regístrate aquí</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import axios from '../../utils/axios-config'
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'

const router = useRouter()
const correoElectronico = ref('')
const contrasena = ref('')

const handleSubmit = (event) => {
  event.preventDefault()
  login()
  return false
}

const login = async () => {
  if (!correoElectronico.value || !contrasena.value) {
    Swal.fire({
      icon: 'error',
      title: 'Campos incompletos',
      text: 'Por favor, ingresa tu correo y contraseña.',
      confirmButtonText: 'OK',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#E54848',
      width: '400px',
      allowOutsideClick: false
    })
    return
  }

  try {
    // ✅ Login con camelCase (Spring Boot)
    const res = await axios.post('/api/auth/login', {
      correoElectronico: correoElectronico.value,
      contrasena: contrasena.value
    })

    // ✅ Guardar datos en localStorage (con camelCase)
    localStorage.setItem('access_token', res.data.token)
    localStorage.setItem('id', res.data.id)
    localStorage.setItem('idRol', res.data.idRol || '')
    localStorage.setItem('nombre', res.data.nombre)
    localStorage.setItem('rol', res.data.rol)

    await Swal.fire({
      icon: 'success',
      title: '¡Bienvenido!',
      text: `Hola ${res.data.nombre}`,
      showConfirmButton: false,
      timer: 2000,
      background: '#ffffff',
      color: '#213547',
      iconColor: '#3ABEF9',
      width: '400px'
    })

    // ✅ Redirigir según rol
    switch (res.data.rol) {
      case 'estudiante': router.push({ name: 'alumno-home' }); break
      case 'admin': router.push({ name: 'dashboard' }); break
      case 'profesor': router.push({ name: 'profesor-home' }); break
      default: router.push({ name: 'login' })
    }

  } catch (err) {
    console.error('Error en login:', err)
    let mensajeError = 'Error al iniciar sesión'
    
    if (err.response) {
      const backendMessage = err.response.data?.message || err.response.data?.error
      if (backendMessage) {
        if (backendMessage.toLowerCase().includes('usuario')) mensajeError = 'Usuario no encontrado'
        else if (backendMessage.toLowerCase().includes('contraseña') || backendMessage.toLowerCase().includes('password')) mensajeError = 'Contraseña incorrecta'
        else mensajeError = backendMessage
      }
    }
    
    Swal.fire({
      icon: 'error',
      title: 'Error de inicio de sesión',
      text: mensajeError,
      confirmButtonText: 'OK',
      confirmButtonColor: '#3ABEF9',
      background: '#ffffff',
      color: '#213547',
      iconColor: '#E54848',
      width: '400px',
      allowOutsideClick: false
    })
  }
}
</script>

<style scoped>
/* --- ESTILOS BASE (Mantenidos) --- */
.login-wrapper {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: linear-gradient(-45deg, #e3f2fd, #bbdefb, #e1f5fe, #3abef9);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
  padding: 20px;
  /* Espacio para que en móvil no toque los bordes */
}

@keyframes gradientBG {
  0% {
    background-position: 0% 50%;
  }

  50% {
    background-position: 100% 50%;
  }

  100% {
    background-position: 0% 50%;
  }
}

/* Formas decorativas */
.bg-shapes .shape {
  position: absolute;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  filter: blur(50px);
  z-index: 0;
}

.shape-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation: float 8s infinite;
}

.shape-2 {
  width: 400px;
  height: 400px;
  bottom: -150px;
  right: -100px;
  animation: float 10s infinite reverse;
}

.shape-3 {
  width: 200px;
  height: 200px;
  top: 20%;
  right: 10%;
  animation: float 12s infinite;
}

@keyframes float {

  0%,
  100% {
    transform: translateY(0) scale(1);
  }

  50% {
    transform: translateY(30px) scale(1.1);
  }
}

/* Tarjeta de Login */
.login-container-card {
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  padding: 40px;
  border-radius: 28px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  animation: slideUp 0.8s ease-out;
}

.logo-section {
  text-align: center;
  margin-bottom: 30px;
}

.img-lo-reg {
  width: 120px;
  margin-bottom: 15px;
}

.welcome-text {
  color: #1e293b;
  font-size: 1.5rem;
  font-weight: 800;
}

/* Inputs Modernos */
.input-group {
  position: relative;
  margin-bottom: 20px;
}

.input-group i {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
}

.input-group input {
  width: 100%;
  padding: 14px 15px 14px 45px;
  border: 2px solid #f1f5f9;
  border-radius: 14px;
  font-size: 1rem;
  transition: all 0.3s;
  background: #f8fafc;
}

.input-group input:focus {
  outline: none;
  border-color: #3abef9;
  background: white;
  box-shadow: 0 0 0 4px rgba(58, 190, 249, 0.1);
}

/* Botón */
.btn-primary-login {
  width: 100%;
  background: #1e293b;
  color: white;
  border: none;
  padding: 16px;
  border-radius: 14px;
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.3s;
}

.btn-primary-login:hover {
  background: #3abef9;
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(58, 190, 249, 0.3);
}

.regresar-l {
  margin-top: 25px;
  color: #64748b;
  font-size: 0.9rem;
  text-align: center;
}

.nav-to {
  color: #3abef9;
  text-decoration: none;
  font-weight: 700;
}

/* --- AJUSTES PARA MÓVIL --- */
@media (max-width: 480px) {
  .login-container-card {
    padding: 30px 20px;
    /* Reducimos padding interno */
    border-radius: 20px;
    /* Bordes un poco menos redondeados para que quepa mejor */
  }

  .img-lo-reg {
    width: 100px;
    /* Logo un poco más pequeño */
  }

  .welcome-text {
    font-size: 1.25rem;
    /* Texto un poco más pequeño */
  }

  .btn-primary-login {
    padding: 14px;
    /* Botón un poco más compacto */
  }

  /* Ocultamos algunas formas de fondo si estorban visualmente en pantallas muy pequeñas */
  .shape-3 {
    display: none;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-pop {
  animation: pop 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes pop {
  0% {
    transform: scale(0);
  }

  100% {
    transform: scale(1);
  }
}
</style>, haber intenten con este este ya me funcionaba