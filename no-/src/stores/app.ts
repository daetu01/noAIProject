import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { authService, type LoginRequest, type SignupRequest } from '@/api/authService'

interface JwtPayload {
  userId: number
  email: string
  nickName: string
  role: 'USER' | 'ADMIN'
}

function parseJwt(token: string): JwtPayload {
  const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
  return JSON.parse(atob(base64))
}

export const useAppStore = defineStore('app', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<JwtPayload | null>(token.value ? parseJwt(token.value) : null)

  const isLoggedIn = computed(() => token.value !== null)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  async function login(data: LoginRequest) {
    const jwt = await authService.login(data)
    token.value = jwt
    user.value = parseJwt(jwt)
    localStorage.setItem('token', jwt)
  }

  async function signup(data: SignupRequest) {
    await authService.signup(data)
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
  }

  return { token, user, isLoggedIn, isAdmin, login, signup, logout }
})
