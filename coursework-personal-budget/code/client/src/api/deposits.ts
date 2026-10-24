import api from './axios'
import type { DepositResponse, CreateDepositRequest, AddFundsRequest, Page } from '../types'

export const depositsApi = {
  getAll: (params?: { status?: string; type?: string; page?: number; size?: number }) =>
    api.get<Page<DepositResponse>>('/deposits', { params }),

  create: (data: CreateDepositRequest) =>
    api.post<DepositResponse>('/deposits', data),

  update: (id: number, data: Partial<CreateDepositRequest>) =>
    api.patch<DepositResponse>(`/deposits/${id}`, data),

  remove: (id: number) =>
    api.delete(`/deposits/${id}`),

  addFunds: (id: number, data: AddFundsRequest) =>
    api.post<DepositResponse>(`/deposits/${id}/add-funds`, data),
}
