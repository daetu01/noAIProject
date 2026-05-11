import client from './client'

export interface LoginRequest {
  email: string
  password: string
}

export interface SignupRequest {
  nickName: string
  email: string
  password: string
}

export const authService = {
  login(data: LoginRequest): Promise<string> {
    return client.post<string>('/user/login', data).then((res) => res.data)
  },

  signup(data: SignupRequest): Promise<number> {
    return client.post<number>('/user/signup', data).then((res) => res.data)
  },
}
