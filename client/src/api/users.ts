import http from './http'
import type { User } from '../types'

export const usersApi = {
  getMe() {
    return http.get<User>('/v1/users/me')
  },
  updateProfile(id: number, data: Partial<User>) {
    return http.put(`/v1/users/${id}/profile`, data)
  },
  changePassword(data: { oldPassword: string; newPassword: string }) {
    return http.put('/v1/users/me/password', data)
  }
}
