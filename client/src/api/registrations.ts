import http from './http'
import type { EventRegistration } from '../types'

export const registrationsApi = {
  register(eventId: number) {
    return http.post<EventRegistration>(`/v1/events/${eventId}/registrations`)
  },
  cancel(eventId: number) {
    return http.delete(`/v1/events/${eventId}/registrations`)
  },
  getByEvent(eventId: number) {
    return http.get<EventRegistration[]>(`/v1/events/${eventId}/registrations`)
  },
  markAttendance(registrationId: number) {
    return http.put(`/v1/registrations/${registrationId}/attendance`)
  },
  getMyRegistrations() {
    return http.get<EventRegistration[]>('/v1/users/me/registrations')
  }
}
