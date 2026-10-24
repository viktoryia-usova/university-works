import api from './axios'
import type { ContactResponse, ProfileSearchResponse, Page } from '../types'

export const contactsApi = {
  getAll: (page = 0, size = 20) =>
    api.get<Page<ContactResponse>>('/contacts', { params: { page, size } }),

  getPending: (page = 0, size = 20) =>
    api.get<Page<ContactResponse>>('/contacts/requests', { params: { page, size } }),

  search: (query: string, page = 0, size = 20) =>
    api.get<Page<ProfileSearchResponse>>('/contacts/search', { params: { query, page, size } }),

  sendRequest: (query: string) =>
    api.post('/contacts/requests', { query }),

  accept: (id: number) =>
    api.post(`/contacts/requests/${id}/accept`),

  decline: (id: number) =>
    api.post(`/contacts/requests/${id}/decline`),

  remove: (id: number) =>
    api.delete(`/contacts/${id}`),

  cancelRequest: (id: number) =>
    api.delete(`/contacts/${id}`),
}
