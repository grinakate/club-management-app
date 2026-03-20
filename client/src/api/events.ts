import http from './http'
import type { Event } from '../types'

export interface EventCreatePayload {
  clubId: number
  title: string
  description: string | null
  startAt: string
  endAt: string
  participantLimit: number | null
  price: number | null
}

export interface EventUpdatePayload {
  title?: string
  description?: string | null
  startAt?: string
  endAt?: string
  participantLimit?: number | null
  price?: number | null
  status?: string
}

export const eventsApi = {
  getAll(params?: { clubId?: number; status?: string }) {
    return http.get<Event[]>('/v1/events', { params })
  },
  getById(id: number) {
    return http.get<Event>(`/v1/events/${id}`)
  },
  create(data: EventCreatePayload) {
    return http.post<Event>('/v1/events', data)
  },
  update(id: number, data: EventUpdatePayload) {
    return http.put<Event>(`/v1/events/${id}`, data)
  },
  delete(id: number) {
    return http.delete(`/v1/events/${id}`)
  }
}
