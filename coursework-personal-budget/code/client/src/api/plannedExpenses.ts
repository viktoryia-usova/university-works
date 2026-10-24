import api from './axios'
import type {
  PlannedExpenseResponse, CreatePlannedExpenseRequest, Page,
} from '../types'

export const plannedExpensesApi = {
  getAll: (page = 0, size = 20) =>
    api.get<Page<PlannedExpenseResponse>>('/planned-expenses', { params: { page, size } }),

  create: (data: CreatePlannedExpenseRequest) =>
    api.post<PlannedExpenseResponse>('/planned-expenses', data),

  update: (id: number, data: Partial<CreatePlannedExpenseRequest>) =>
    api.patch<PlannedExpenseResponse>(`/planned-expenses/${id}`, data),

  remove: (id: number) =>
    api.delete(`/planned-expenses/${id}`),
}
