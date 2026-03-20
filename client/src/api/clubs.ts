import http from './http'
import type { Club, Category, Event } from '../types'

export const clubsApi = {
  getAll(params?: { categoryId?: number; status?: string }) {
    return http.get<Club[]>('/v1/clubs', { params })
  },
  getById(id: number) {
    return http.get<Club>(`/v1/clubs/${id}`)
  },
  create(data: Partial<Club>) {
    return http.post<Club>('/v1/clubs', data)
  },
  update(id: number, data: Partial<Club>) {
    return http.put<Club>(`/v1/clubs/${id}`, data)
  },
  delete(id: number) {
    return http.delete(`/v1/clubs/${id}`)
  },
  getMyClubs() {
    return http.get<Club[]>('/v1/users/me/clubs')
  },
  getCategories() {
    return http.get<Category[]>('/v1/categories')
  },
  getClubEvents(clubId: number) {
    return http.get<Event[]>(`/v1/clubs/${clubId}/events`)
  }
}
