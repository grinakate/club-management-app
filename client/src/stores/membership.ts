import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { membershipApi } from '../api/membership'
import type { MembershipApplication, ClubMembership } from '../types'

export const useMembershipStore = defineStore('membership', () => {
  const applications = ref<MembershipApplication[]>([])
  const members = ref<ClubMembership[]>([])
  const loadingApplications = ref(false)
  const loadingMembers = ref(false)
  const loading = computed(() => loadingApplications.value || loadingMembers.value)

  async function fetchApplications(clubId: number) {
    loadingApplications.value = true
    try {
      const { data } = await membershipApi.getApplications(clubId)
      applications.value = data
    } finally {
      loadingApplications.value = false
    }
  }

  async function applyToClub(clubId: number, comment?: string | null): Promise<MembershipApplication> {
    const { data } = await membershipApi.apply(clubId, { comment })
    return data
  }

  async function reviewApplication(id: number, decision: 'APPROVED' | 'REJECTED', comment?: string | null): Promise<MembershipApplication> {
    const { data } = await membershipApi.reviewApplication(id, { decision, comment })
    const idx = applications.value.findIndex(a => a.id === id)
    if (idx !== -1) applications.value[idx] = data
    return data
  }

  async function fetchMembers(clubId: number) {
    loadingMembers.value = true
    try {
      const { data } = await membershipApi.getMembers(clubId)
      members.value = data
    } finally {
      loadingMembers.value = false
    }
  }

  async function updateRole(id: number, role: 'MEMBER' | 'MODERATOR' | 'ADMIN'): Promise<ClubMembership> {
    const { data } = await membershipApi.updateRole(id, { role })
    const idx = members.value.findIndex(m => m.id === id)
    if (idx !== -1) members.value[idx] = data
    return data
  }

  async function removeMember(id: number) {
    await membershipApi.removeMember(id)
    members.value = members.value.filter(m => m.id !== id)
  }

  async function leaveClub(clubId: number) {
    await membershipApi.leaveClub(clubId)
  }

  return {
    applications,
    members,
    loading,
    loadingApplications,
    loadingMembers,
    fetchApplications,
    applyToClub,
    reviewApplication,
    fetchMembers,
    updateRole,
    removeMember,
    leaveClub
  }
})
