import http from './http'
import type { LoginRequest, RegisterRequest, TokenResponse, User } from '../types'

export const authApi = {
  login(data: LoginRequest) {
    return http.post<TokenResponse>('/auth/login', data)
  },
  register(data: RegisterRequest) {
    return http.post<User>('/auth/register', data)
  },
  me() {
    return http.get<User>('/v1/users/me')
  }
}
