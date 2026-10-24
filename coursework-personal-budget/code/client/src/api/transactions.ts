import api from './axios'
import type {
  TransactionResponse, CreateTransactionRequest, UpdateTransactionRequest, Page,
} from '../types'

export const transactionsApi = {
  getAll: (params?: {
    type?: string; categoryId?: number; dateFrom?: string; dateTo?: string
    currency?: string; page?: number; size?: number; sort?: string
  }) =>
    api.get<Page<TransactionResponse>>('/transactions', { params }),

  getById: (id: number) =>
    api.get<TransactionResponse>(`/transactions/${id}`),

  create: (data: CreateTransactionRequest) =>
    api.post<TransactionResponse>('/transactions', data),

  update: (id: number, data: UpdateTransactionRequest) =>
    api.patch<TransactionResponse>(`/transactions/${id}`, data),

  remove: (id: number) =>
    api.delete(`/transactions/${id}`),
}
