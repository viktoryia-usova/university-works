import api from './axios'
import type { CategoryResponse, CreateCategoryRequest, UpdateCategoryRequest, Page } from '../types'

export const categoriesApi = {
  getAll: (type?: string, page = 0, size = 100) =>
    api.get<Page<CategoryResponse>>('/categories', { params: { type, page, size } }),

  create: (data: CreateCategoryRequest) =>
    api.post<CategoryResponse>('/categories', data),

  update: (id: number, data: UpdateCategoryRequest) =>
    api.patch<CategoryResponse>(`/categories/${id}`, data),

  remove: (id: number) =>
    api.delete(`/categories/${id}`),
}
