import { useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useAuthStore } from '../../store/authStore'
import { queryClient } from '../../api/queryClient'
import PageLoader from '../../components/common/PageLoader'

/**
 * After Google OAuth2 success the backend redirects here with
 * ?accessToken=...&refreshToken=...
 * On error (e.g. blocked account) the backend redirects here with
 * ?error=INACTIVE
 */
export default function OAuth2CallbackPage() {
  const [params] = useSearchParams()
  const { setToken } = useAuthStore()
  const navigate = useNavigate()

  useEffect(() => {
    const errorKey = params.get('error')
    if (errorKey) {
      // Pass the error via navigation state so SignInPage reads it immediately on mount
      const messages: Record<string, string> = {
        INACTIVE: 'Аккаунт заблокирован',
      }
      const errorMessage = messages[errorKey] ?? 'Ошибка входа через Google'
      navigate('/sign-in', { replace: true, state: { oauthError: errorMessage } })
      return
    }

    const accessToken  = params.get('accessToken')
    const refreshToken = params.get('refreshToken')

    if (accessToken && refreshToken) {
      // Decode the JWT payload to get user info
      try {
        queryClient.clear()
        const payload = JSON.parse(atob(accessToken.split('.')[1]))
        setToken({
          type: 'Bearer',
          id: payload.id,
          username: payload.sub,
          role: payload.role,
          active: true,
          accessToken,
          refreshToken,
        })
        navigate('/dashboard', { replace: true })
      } catch {
        navigate('/sign-in', { replace: true })
      }
    } else {
      navigate('/sign-in', { replace: true })
    }
  }, [])

  return <PageLoader />
}
