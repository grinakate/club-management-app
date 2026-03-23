import http from './http'
import type { Notification } from '../types'

export const notificationsApi = {
  getAll() {
    return http.get<Notification[]>('/v1/notifications')
  },
  getUnreadCount() {
    return http.get<{ count: number }>('/v1/notifications/unread-count')
  },
  markAsRead(id: number) {
    return http.put<Notification>(`/v1/notifications/${id}/read`)
  },
  markAllAsRead() {
    return http.put('/v1/notifications/read-all')
  }
}
