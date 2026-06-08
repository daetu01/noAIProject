import axios from 'axios'

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '',
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000,
})

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      // Redirect only if not already on login page to avoid loops
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login?reason=session_expired'
      }
    }
    return Promise.reject(error)
  }
)

export default client
