import api from './axios'
import type { JwtInfo } from '../types'

export const authApi = {
  signUp: (username: string, password: string) =>
    api.post('/auth/sign-up', { username, password }),

  confirm: (username: string, key: string, password: string) =>
    api.post<JwtInfo>('/auth/confirm', { username, key, password }),

  signIn: (username: string, password: string) =>
    api.post<JwtInfo>('/auth/sign-in', { username, password }),

  refresh: (refreshToken: string) =>
    api.post<JwtInfo>('/auth/refresh', { refreshToken }),

  signOut: () =>
    api.post('/auth/sign-out'),
}
