export interface User {
  id: number
  fullName: string
  email: string | null
  phone: string | null
  city: string | null
  interests: string | null
  role: 'PARTICIPANT' | 'ADMIN' | 'MANAGER'
  status: 'ACTIVE' | 'BLOCKED' | 'PENDING'
  birthDate: string
  createdAt: string
}

export interface Club {
  id: number
  name: string
  description: string | null
  categoryId: number
  categoryName?: string
  ageLimitMin: number | null
  ageLimitMax: number | null
  membershipFee: number | null
  ownerUserId: number
  ownerFullName: string
  status: 'DRAFT' | 'ACTIVE' | 'ARCHIVED'
  createdAt: string
}

export interface Category {
  id: number
  name: string
}

export interface Event {
  id: number
  clubId: number
  clubName?: string
  title: string
  description: string | null
  startAt: string
  endAt: string
  participantLimit: number | null
  price: number | null
  status: 'DRAFT' | 'PUBLISHED' | 'CANCELLED' | 'COMPLETED'
  createdBy: number
}

export interface EventRegistration {
  id: number
  eventId: number
  userId: number
  userFullName?: string
  registeredAt: string
  status: 'REGISTERED' | 'CANCELLED' | 'WAITLIST' | 'ATTENDED'
  attendanceMark: boolean | null
}

export interface MembershipApplication {
  id: number
  clubId: number
  clubName: string
  userId: number
  userFullName: string
  appliedAt: string
  status: 'NEW' | 'APPROVED' | 'REJECTED' | 'CANCELLED'
  reviewedByFullName: string | null
  reviewedAt: string | null
  comment: string | null
}

export interface ClubMembership {
  id: number
  clubId: number
  clubName: string
  userId: number
  userFullName: string
  joinedAt: string
  memberRole: 'MEMBER' | 'MODERATOR' | 'ADMIN'
  status: 'ACTIVE' | 'SUSPENDED' | 'LEFT'
}

export interface Notification {
  id: number
  userId: number
  eventId: number | null
  clubId: number | null
  subject: string
  messageText: string | null
  channel: 'IN_APP' | 'EMAIL' | 'TELEGRAM'
  scheduledAt: string
  sentAt: string | null
  deliveryStatus: 'QUEUED' | 'SENT' | 'DELIVERED' | 'FAILED'
  isRead: boolean
}

export interface LoginRequest {
  login: string
  password: string
}

export interface RegisterRequest {
  fullName: string
  email: string | null
  phone: string | null
  password: string
  city: string | null
  interests: string | null
  birthDate: string
}

export interface TokenResponse {
  token: string
}
