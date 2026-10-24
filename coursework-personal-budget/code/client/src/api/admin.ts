import api from './axios'
import type { UserResponse, Page } from '../types'

export const adminApi = {
  getUsers: (page = 0, size = 20) =>
    api.get<Page<UserResponse>>('/admin/users', { params: { page, size } }),

  blockUser: (id: number) =>
    api.post(`/admin/users/${id}/block`),

  unblockUser: (id: number) =>
    api.post(`/admin/users/${id}/unblock`),
}
