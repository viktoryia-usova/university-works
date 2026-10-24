import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { authApi } from '../../api/auth'
import { useAuthStore } from '../../store/authStore'
import { queryClient } from '../../api/queryClient'
import Spinner from '../../components/common/Spinner'
import { parseApiError } from '../../utils/errorUtils'

interface Fields { key: string; }

export default function ConfirmPage() {
  const { state } = useLocation() as { state?: { username: string, password: string } }
  const { register, handleSubmit, watch, formState: { errors } } = useForm<Fields>()
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { setToken } = useAuthStore()
  const navigate = useNavigate()

  const username = state?.username ?? ''
  const password = state?.password ?? ''

  const onSubmit = async (data: Fields) => {
    setError('')
    setLoading(true)
    try {
      queryClient.clear()
      const res = await authApi.confirm(username, data.key, password)
      setToken(res.data)
      navigate('/dashboard')
    } catch (e: any) {
      setError(parseApiError(e, 'Неверный или истёкший код'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      <h2 className="text-2xl font-bold text-gray-900 mb-2">Подтверждение</h2>
      <p className="text-sm text-gray-500 mb-6">
        Код отправлен на <strong>{username || 'ваш email'}</strong>
      </p>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="label">Код из письма</label>
          <input
            className={`input text-center text-xl tracking-widest ${errors.key ? 'input-error' : ''}`}
            type="text"
            placeholder="• • • • • • • •"
            maxLength={8}
            {...register('key', { required: 'Введите код' })}
          />
          {errors.key && <p className="error-text">{errors.key.message}</p>}
        </div>

        {error && <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2">{error}</p>}

        <button type="submit" className="btn-primary w-full" disabled={loading}>
          {loading ? <Spinner size="sm" /> : 'Создать аккаунт'}
        </button>
      </form>
    </>
  )
}
