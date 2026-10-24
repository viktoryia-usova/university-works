import { Navigate } from 'react-router-dom'
import { useAuthStore } from '../../store/authStore'
import type { ReactNode } from 'react'

interface Props {
  children: ReactNode
  adminOnly?: boolean
}

export default function ProtectedRoute({ children, adminOnly = false }: Props) {
  const { isAuthenticated, isAdmin } = useAuthStore()

  if (!isAuthenticated()) return <Navigate to="/sign-in" replace />
  if (adminOnly && !isAdmin()) return <Navigate to="/dashboard" replace />

  return <>{children}</>
}
