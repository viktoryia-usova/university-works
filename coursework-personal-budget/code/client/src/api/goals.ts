import api from './axios'
import type { GoalResponse, CreateGoalRequest, ContributeToGoalRequest, Page } from '../types'

export const goalsApi = {
  getAll: (status?: string, page = 0, size = 20) =>
    api.get<Page<GoalResponse>>('/goals', { params: { status, page, size } }),

  create: (data: CreateGoalRequest) =>
    api.post<GoalResponse>('/goals', data),

  update: (id: number, data: Partial<CreateGoalRequest>) =>
    api.patch<GoalResponse>(`/goals/${id}`, data),

  remove: (id: number) =>
    api.delete(`/goals/${id}`),

  contribute: (id: number, data: ContributeToGoalRequest) =>
    api.post<GoalResponse>(`/goals/${id}/contribute`, data),
}
