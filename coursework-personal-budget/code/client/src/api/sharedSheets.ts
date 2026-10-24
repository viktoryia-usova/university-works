import api from './axios'
import type {
  SharedSheetResponse, SharedTransactionResponse, SharedGoalResponse,
  SharedSheetAnalyticsResponse, Page,
} from '../types'

export const sharedSheetsApi = {
  // sheets
  getAll: (page = 0, size = 20) =>
    api.get<Page<SharedSheetResponse>>('/shared-sheets', { params: { page, size } }),

  getById: (id: number) =>
    api.get<SharedSheetResponse>(`/shared-sheets/${id}`),

  create: (name: string, description?: string) =>
    api.post<SharedSheetResponse>('/shared-sheets', { name, description }),

  update: (id: number, name?: string, description?: string) =>
    api.patch<SharedSheetResponse>(`/shared-sheets/${id}`, { name, description }),

  complete: (id: number) =>
    api.post<SharedSheetResponse>(`/shared-sheets/${id}/complete`),

  remove: (id: number) =>
    api.delete(`/shared-sheets/${id}`),

  // members
  invite: (sheetId: number, profileId: number) =>
    api.post(`/shared-sheets/${sheetId}/members`, { profileId }),

  leave: (sheetId: number) =>
    api.delete(`/shared-sheets/${sheetId}/members/me`),

  // transactions
  getTransactions: (sheetId: number, page = 0, size = 20) =>
    api.get<Page<SharedTransactionResponse>>(`/shared-sheets/${sheetId}/transactions`, {
      params: { page, size },
    }),

  addTransaction: (sheetId: number, data: {
    amount: number; currency: string; type: string; description?: string; date: string
  }) =>
    api.post<SharedTransactionResponse>(`/shared-sheets/${sheetId}/transactions`, data),

  deleteTransaction: (sheetId: number, txId: number) =>
    api.delete(`/shared-sheets/${sheetId}/transactions/${txId}`),

  // goals
  getGoals: (sheetId: number, page = 0, size = 20) =>
    api.get<Page<SharedGoalResponse>>(`/shared-sheets/${sheetId}/goals`, {
      params: { page, size },
    }),

  createGoal: (sheetId: number, data: {
    name: string; targetAmount: number; currency: string; deadline?: string
  }) =>
    api.post<SharedGoalResponse>(`/shared-sheets/${sheetId}/goals`, data),

  contributeToGoal: (sheetId: number, goalId: number, amount: number) =>
    api.post<SharedGoalResponse>(`/shared-sheets/${sheetId}/goals/${goalId}/contribute`, { amount }),

  deleteGoal: (sheetId: number, goalId: number) =>
    api.delete(`/shared-sheets/${sheetId}/goals/${goalId}`),

  // analytics
  getAnalytics: (sheetId: number) =>
    api.get<SharedSheetAnalyticsResponse>(`/shared-sheets/${sheetId}/analytics`),
}
