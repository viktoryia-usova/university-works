import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { JwtInfo } from '../types'

interface AuthState {
  token: JwtInfo | null
  setToken: (token: JwtInfo) => void
  clearToken: () => void
  isAuthenticated: () => boolean
  isAdmin: () => boolean
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      setToken: (token) => set({ token }),
      clearToken: () => set({ token: null }),
      isAuthenticated: () => !!get().token?.accessToken,
      isAdmin: () => get().token?.role === 'ADMIN',
    }),
    { name: 'auth' }
  )
)
