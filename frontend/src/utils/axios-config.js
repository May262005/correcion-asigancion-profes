import axios from 'axios'

axios.defaults.baseURL = 'http://localhost:8080'

axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('access_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('id_usuario')
      localStorage.removeItem('nombre')
      localStorage.removeItem('rol')
    }
    return Promise.reject(error)
  }
)

export default axios