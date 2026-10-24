import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { authApi } from '../../api/auth'
import Spinner from '../../components/common/Spinner'
import PasswordRequirements, { pwdStrong } from '../../components/common/PasswordRequirements'
import { parseApiError } from '../../utils/errorUtils'

interface Fields { username: string; password: string; confirm: string }

export default function SignUpPage() {
  const { register, handleSubmit, watch, formState: { errors } } = useForm<Fields>()
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const pwd = watch('password', '')

  const onSubmit = async (data: Fields) => {
    setError('')
    setLoading(true)
    try {
      await authApi.signUp(data.username, data.password)
      navigate('/confirm', { state: { username: data.username, password: data.password } })
    } catch (e: any) {
      setError(parseApiError(e, 'Ошибка регистрации'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      <h2 className="text-2xl font-bold text-gray-900 mb-6">Регистрация</h2>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="label">Email</label>
          <input
            className={`input ${errors.username ? 'input-error' : ''}`}
            type="email"
            placeholder="you@example.com"
            {...register('username', { required: 'Введите email' })}
          />
          {errors.username && <p className="error-text">{errors.username.message}</p>}
        </div>

        <div>
          <label className="label">Пароль</label>
          <input
            className={`input ${errors.password ? 'input-error' : ''}`}
            type="password"
            placeholder="Минимум 8 символов"
            {...register('password', {
              required: 'Введите пароль',
              validate: v => pwdStrong(v) || 'Пароль не соответствует требованиям безопасности',
            })}
          />
          {errors.password && <p className="error-text">{errors.password.message}</p>}
          <PasswordRequirements value={pwd} />
        </div>

        <div>
          <label className="label">Подтверждение пароля</label>
          <input
            className={`input ${errors.confirm ? 'input-error' : ''}`}
            type="password"
            placeholder="Повторите пароль"
            {...register('confirm', {
              required: 'Подтвердите пароль',
              validate: v => v === watch('password') || 'Пароли не совпадают',
            })}
          />
          {errors.confirm && <p className="error-text">{errors.confirm.message}</p>}
        </div>

        {error && <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2">{error}</p>}

        <button type="submit" className="btn-primary w-full" disabled={loading}>
          {loading ? <Spinner size="sm" /> : 'Отправить код'}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-gray-500">
        Уже есть аккаунт?{' '}
        <Link to="/sign-in" className="text-blue-600 font-medium hover:underline">Войти</Link>
      </p>
    </>
  )
}
