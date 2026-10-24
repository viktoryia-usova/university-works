import api from './axios'
import type { AnalyticsSummaryResponse, MonthlyEntryResponse } from '../types'

export const analyticsApi = {
  getSummary: (from?: string, to?: string) =>
    api.get<AnalyticsSummaryResponse>('/analytics/summary', { params: { from, to } }),

  getIncome: (from?: string, to?: string) =>
    api.get<AnalyticsSummaryResponse>('/analytics/income', { params: { from, to } }),

  getExpenses: (from?: string, to?: string) =>
    api.get<AnalyticsSummaryResponse>('/analytics/expenses', { params: { from, to } }),

  getMonthly: (from?: string, to?: string) =>
    api.get<MonthlyEntryResponse[]>('/analytics/monthly', { params: { from, to } }),
}
