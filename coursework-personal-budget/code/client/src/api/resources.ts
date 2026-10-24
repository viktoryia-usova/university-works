import api from './axios'
import type {
  ResourceCategoryResponse, FinancialResourceResponse, Page,
} from '../types'

interface CreateFinancialResourceRequest {
  categoryId: number
  title: string
  content: string
}

export const resourcesApi = {
  getCategories: () =>
    api.get<ResourceCategoryResponse[]>('/resources/categories'),

  createCategory: (name: string) =>
    api.post<ResourceCategoryResponse>('/resources/categories', { name }),

  removeCategory: (id: number) =>
    api.delete(`/resources/categories/${id}`),

  getAll: (params?: { categoryId?: number; title?: string; page?: number; size?: number }) =>
    api.get<Page<FinancialResourceResponse>>('/resources', { params }),

  getById: (id: number) =>
    api.get<FinancialResourceResponse>(`/resources/${id}`),

  create: (data: CreateFinancialResourceRequest) =>
    api.post<FinancialResourceResponse>('/resources', data),

  update: (id: number, data: Partial<CreateFinancialResourceRequest>) =>
    api.patch<FinancialResourceResponse>(`/resources/${id}`, data),

  remove: (id: number) =>
    api.delete(`/resources/${id}`),

  addAttachment: (id: number, file: File, fileType: 'IMAGE' | 'DOCUMENT') => {
    const form = new FormData()
    form.append('file', file)
    form.append('fileType', fileType)
    return api.post(`/resources/${id}/attachments`, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },

  deleteAttachment: (attachmentId: number) =>
    api.delete(`/resources/attachments/${attachmentId}`),

  // ---- Favorites ----
  getFavorites: (params?: { page?: number; size?: number }) =>
    api.get<Page<FinancialResourceResponse>>('/resources/favorites', { params }),

  addFavorite: (id: number) =>
    api.post<FinancialResourceResponse>(`/resources/${id}/favorite`),

  removeFavorite: (id: number) =>
    api.delete<FinancialResourceResponse>(`/resources/${id}/favorite`),
}
