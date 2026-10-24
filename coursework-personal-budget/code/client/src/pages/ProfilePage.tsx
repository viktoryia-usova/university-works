import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { profileApi } from '../api/profile'
import { currenciesApi } from '../api/currencies'
import { accountApi } from '../api/account'
import { useAuthStore } from '../store/authStore'
import PageLoader from '../components/common/PageLoader'
import Spinner from '../components/common/Spinner'
import PasswordRequirements, { pwdStrong } from '../components/common/PasswordRequirements'
import type { UpdateProfileRequest } from '../types'
import { useState } from 'react'
import { parseApiError } from '../utils/errorUtils'

type SavedSection = 'profile' | 'notif' | 'privacy' | 'password' | null

export default function ProfilePage() {
  const qc = useQueryClient()
  const { token } = useAuthStore()
  const { data: profile, isLoading } = useQuery({
    queryKey: ['profile'],
    queryFn: () => profileApi.getMe().then(r => r.data),
  })

  const { data: privacy } = useQuery({
    queryKey: ['privacy'],
    queryFn: () => profileApi.getPrivacy().then(r => r.data),
  })

  const { data: notif } = useQuery({
    queryKey: ['notifications'],
    queryFn: () => profileApi.getNotifications().then(r => r.data),
  })

  const { data: supported } = useQuery({
    queryKey: ['currencies', 'supported'],
    queryFn: () => currenciesApi.getSupported().then(r => r.data),
  })

  const {
    register,
    handleSubmit,
    setError,
    formState: { errors: profileErrors },
  } = useForm<UpdateProfileRequest>()
  const { register: regPwd, handleSubmit: handlePwd, reset: resetPwd, watch: watchPwd, formState: { errors: pwdErrors } } = useForm<{
    oldPassword: string; newPassword: string; confirmPassword: string
  }>()
  const newPwd = watchPwd('newPassword', '')
  const [savedSection, setSavedSection] = useState<SavedSection>(null)
  const [profileError, setProfileError] = useState('')
  const [pwdError, setPwdError] = useState('')
  const [pwdLoading, setPwdLoading] = useState(false)

  const flash = (section: SavedSection) => {
    setSavedSection(section)
    setTimeout(() => setSavedSection(null), 2500)
  }

  const PROFILE_KEY_TO_FIELD: Record<string, keyof UpdateProfileRequest> = {
    FIRST_NAME:       'firstName',
    LAST_NAME:        'lastName',
    PHONE:            'phone',
    BIRTH_DATE:       'birthDate',
    DEFAULT_CURRENCY: 'defaultCurrency',
  }

  const updateProfile = useMutation({
    mutationFn: (data: UpdateProfileRequest) => profileApi.updateMe(data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['profile'] })
      flash('profile')
      setProfileError('')
    },
    onError: (e: any) => {
      const body = e?.response?.data
      const field = body?.key && PROFILE_KEY_TO_FIELD[body.key]
      if (field) {
        setError(field, { message: body.message })
      } else {
        setProfileError(parseApiError(e))
      }
    },
  })

  const updateNotif = useMutation({
    mutationFn: (data: any) => profileApi.updateNotifications(data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['notifications'] }); flash('notif') },
    onError: (e) => setProfileError(parseApiError(e)),
  })

  const updatePrivacy = useMutation({
    mutationFn: (data: any) => profileApi.updatePrivacy(data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['privacy'] }); flash('privacy') },
    onError: (e) => setProfileError(parseApiError(e)),
  })

  const handleChangePassword = async (data: { oldPassword: string; newPassword: string; confirmPassword: string }) => {
    if (!token?.id) return
    setPwdLoading(true)
    setPwdError('')
    try {
      await accountApi.changePassword(token.id, data.oldPassword, data.newPassword)
      flash('password')
      resetPwd()
    } catch (e) {
      setPwdError(parseApiError(e))
    } finally {
      setPwdLoading(false)
    }
  }

  if (isLoading || !profile) return <PageLoader />

  return (
    <div className="max-w-2xl">
      <h1 className="text-2xl font-bold mb-6">Профиль</h1>

      {/* Profile info */}
      <div className="card mb-6">
        <h2 className="text-base font-semibold mb-4">Личные данные</h2>
        <form onSubmit={handleSubmit(data => updateProfile.mutate(data))} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Имя</label>
              <input
                className={`input ${profileErrors.firstName ? 'input-error' : ''}`}
                defaultValue={profile.firstName}
                {...register('firstName', {
                  required: 'Имя обязательно для заполнения',
                  maxLength: { value: 100, message: 'Имя не должно превышать 100 символов' },
                })}
              />
              {profileErrors.firstName && (
                <p className="error-text">{profileErrors.firstName.message}</p>
              )}
            </div>
            <div>
              <label className="label">Фамилия</label>
              <input
                className={`input ${profileErrors.lastName ? 'input-error' : ''}`}
                defaultValue={profile.lastName}
                {...register('lastName', {
                  required: 'Фамилия обязательна для заполнения',
                  maxLength: { value: 100, message: 'Фамилия не должна превышать 100 символов' },
                })}
              />
              {profileErrors.lastName && (
                <p className="error-text">{profileErrors.lastName.message}</p>
              )}
            </div>
          </div>
          <div>
            <label className="label">Email</label>
            <input className="input bg-gray-50" value={profile.email} disabled />
          </div>
          <div>
            <label className="label">Телефон</label>
            <input
              className={`input ${profileErrors.phone ? 'input-error' : ''}`}
              defaultValue={profile.phone ?? ''}
              placeholder="+375XXXXXXXXX"
              {...register('phone', {
                validate: v =>
                  !v || /^\+375\d{9}$/.test(v) || 'Формат: +375XXXXXXXXX (9 цифр после кода страны)',
              })}
            />
            {profileErrors.phone && (
              <p className="error-text">{profileErrors.phone.message}</p>
            )}
          </div>
          <div>
            <label className="label">Дата рождения</label>
            <input
              className={`input ${profileErrors.birthDate ? 'input-error' : ''}`}
              type="date"
              defaultValue={profile.birthDate ?? ''}
              {...register('birthDate', {
                validate: v => {
                  if (!v) return true
                  return new Date(v) < new Date() || 'Дата рождения должна быть в прошлом'
                },
              })}
            />
            {profileErrors.birthDate && (
              <p className="error-text">{profileErrors.birthDate.message}</p>
            )}
          </div>
          <div>
            <label className="label">Валюта по умолчанию</label>
            <select
              className={`input w-36 ${profileErrors.defaultCurrency ? 'input-error' : ''}`}
              defaultValue={profile.defaultCurrency}
              {...register('defaultCurrency', { required: 'Выберите валюту' })}
            >
              {(supported ?? [profile.defaultCurrency]).map(cur => (
                <option key={cur} value={cur}>{cur}</option>
              ))}
            </select>
            {profileErrors.defaultCurrency && (
              <p className="error-text">{profileErrors.defaultCurrency.message}</p>
            )}
          </div>
          {profileError && <p className="error-text">{profileError}</p>}
          <div className="flex items-center gap-3">
            <button type="submit" className="btn-primary" disabled={updateProfile.isPending}>
              {updateProfile.isPending ? <Spinner size="sm" /> : 'Сохранить'}
            </button>
            {savedSection === 'profile' && (
              <span className="text-sm text-green-600 flex items-center gap-1">
                <i className="bi bi-check-lg" /> Сохранено
              </span>
            )}
          </div>
        </form>
        <div className="mt-4 p-3 bg-gray-50 rounded-lg text-sm text-gray-500">
          Код друга: <span className="font-mono font-semibold text-gray-800">{profile.friendCode}</span>
        </div>
      </div>

      {/* Notification settings */}
      {notif && (
        <div className="card mb-6">
          <div className="flex items-center gap-3 mb-4">
            <h2 className="text-base font-semibold">Уведомления на почту</h2>
            {savedSection === 'notif' && (
              <span className="text-sm text-green-600 flex items-center gap-1">
                <i className="bi bi-check-lg" /> Сохранено
              </span>
            )}
          </div>
          <div key={JSON.stringify(notif)} className="space-y-3">
            {([
              ['onGoalReached',       'Цель достигнута'],
              ['onBudgetExceeded',    'Бюджет превышен'],
              ['onSharedTransaction', 'Операция в общем листе'],
              ['onContactRequest',    'Запрос в контакты'],
              ['onDepositMatured',    'Вклад закрыт'],
            ] as const).map(([key, label]) => (
              <label key={key} className="flex items-center justify-between">
                <span className="text-sm">{label}</span>
                <input
                  type="checkbox"
                  className="h-4 w-4 rounded border-gray-300 text-blue-600"
                  checked={!!notif[key]}
                  onChange={e => {
                    const { id: _, ...rest } = notif
                    updateNotif.mutate({ ...rest, [key]: e.target.checked })
                  }}
                />
              </label>
            ))}
          </div>
        </div>
      )}

      {/* Privacy settings */}
      {privacy && (
        <div className="card mb-6">
          <div className="flex items-center gap-3 mb-4">
            <h2 className="text-base font-semibold">Настройки приватности</h2>
            {savedSection === 'privacy' && (
              <span className="text-sm text-green-600 flex items-center gap-1">
                <i className="bi bi-check-lg" /> Сохранено
              </span>
            )}
          </div>
          <div key={JSON.stringify(privacy)} className="space-y-3">
            {([
              ['searchableByEmail',      'Искать по email'],
              ['searchableByPhone',      'Искать по телефону'],
              ['searchableByFriendCode', 'Искать по коду друга'],
              ['showFullName',           'Показывать полное имя'],
            ] as const).map(([key, label]) => (
              <label key={key} className="flex items-center justify-between">
                <span className="text-sm">{label}</span>
                <input
                  type="checkbox"
                  className="h-4 w-4 rounded border-gray-300 text-blue-600"
                  checked={!!privacy[key]}
                  onChange={e => {
                    const { id: _, ...rest } = privacy
                    updatePrivacy.mutate({ ...rest, [key]: e.target.checked })
                  }}
                />
              </label>
            ))}
          </div>
        </div>
      )}

      {/* Password change */}
      <div className="card">
        <div className="flex items-center gap-3 mb-4">
          <h2 className="text-base font-semibold">Смена пароля</h2>
          {savedSection === 'password' && (
            <span className="text-sm text-green-600 flex items-center gap-1">
              <i className="bi bi-check-lg" /> Пароль изменён
            </span>
          )}
        </div>
        <form onSubmit={handlePwd(handleChangePassword)} className="space-y-4">
          <div>
            <label className="label">Текущий пароль</label>
            <input
              className={`input ${pwdErrors.oldPassword ? 'input-error' : ''}`}
              type="password"
              autoComplete="current-password"
              {...regPwd('oldPassword', { required: 'Введите текущий пароль' })}
            />
            {pwdErrors.oldPassword && (
              <p className="error-text">{pwdErrors.oldPassword.message}</p>
            )}
          </div>
          <div>
            <label className="label">Новый пароль</label>
            <input
              className={`input ${pwdErrors.newPassword ? 'input-error' : ''}`}
              type="password"
              autoComplete="new-password"
              {...regPwd('newPassword', {
                required: 'Введите новый пароль',
                validate: v => pwdStrong(v) || 'Пароль не соответствует требованиям безопасности',
              })}
            />
            {pwdErrors.newPassword && (
              <p className="error-text">{pwdErrors.newPassword.message}</p>
            )}
            <PasswordRequirements value={newPwd} />
          </div>
          <div>
            <label className="label">Повторите новый пароль</label>
            <input
              className={`input ${pwdErrors.confirmPassword ? 'input-error' : ''}`}
              type="password"
              autoComplete="new-password"
              {...regPwd('confirmPassword', {
                required: 'Подтвердите новый пароль',
                validate: v => v === watchPwd('newPassword') || 'Пароли не совпадают',
              })}
            />
            {pwdErrors.confirmPassword && (
              <p className="error-text">{pwdErrors.confirmPassword.message}</p>
            )}
          </div>
          {pwdError && <p className="error-text">{pwdError}</p>}
          <div className="flex items-center gap-3">
            <button type="submit" className="btn-primary" disabled={pwdLoading}>
              {pwdLoading ? <Spinner size="sm" /> : 'Изменить пароль'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
