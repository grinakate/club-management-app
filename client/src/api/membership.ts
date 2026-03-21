import http from './http'
import type { MembershipApplication, ClubMembership } from '../types'

export const membershipApi = {
  apply(clubId: number, data: { comment?: string | null }) {
    return http.post<MembershipApplication>(`/v1/clubs/${clubId}/applications`, data)
  },
  getApplications(clubId: number) {
    return http.get<MembershipApplication[]>(`/v1/clubs/${clubId}/applications`)
  },
  reviewApplication(id: number, data: { decision: 'APPROVED' | 'REJECTED'; comment?: string | null }) {
    return http.put<MembershipApplication>(`/v1/applications/${id}/review`, data)
  },
  getMembers(clubId: number) {
    return http.get<ClubMembership[]>(`/v1/clubs/${clubId}/members`)
  },
  updateRole(id: number, data: { role: 'MEMBER' | 'MODERATOR' | 'ADMIN' }) {
    return http.put<ClubMembership>(`/v1/memberships/${id}/role`, data)
  },
  removeMember(id: number) {
    return http.delete(`/v1/memberships/${id}`)
  },
  leaveClub(clubId: number) {
    return http.post(`/v1/clubs/${clubId}/leave`)
  }
}
