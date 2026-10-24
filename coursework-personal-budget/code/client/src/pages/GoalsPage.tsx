import { useState, useMemo } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { goalsApi } from '../api/goals'
import { currenciesApi } from '../api/currencies'
import { profileApi } from '../api/profile'
import PageLoader from '../components/common/PageLoader'
import EmptyState from '../components/common/EmptyState'
import Modal from '../components/common/Modal'
import Spinner from '../components/common/Spinner'
import type { GoalResponse, GoalStatus } from '../types'
import { parseApiError } from '../utils/errorUtils'

// ---- currency conversion helpers (client-side preview) ----
interface NbrbRate { Cur_Abbreviation: string; Cur_OfficialRate: number; Cur_Scale: number }

function getRateToByn(rates: NbrbRate[], code: string): number {
  if (code === 'BYN') return 1
  const r = rates.find(r => r.Cur_Abbreviation === code)
  return r ? r.Cur_OfficialRate / r.Cur_Scale : 0
}

function convertAmount(amount: number, from: string, to: string, rates: NbrbRate[]): number | null {
  if (!amount || from === to) return amount
  const fromRate = getRateToByn(rates, from)
  const toRate   = getRateToByn(rates, to)
  if (!fromRate || !toRate) return null
  return (amount * fromRate) / toRate
}

// ---- formatters ----
const fmt = (n: number, cur = 'BYN') =>
  new Intl.NumberFormat('ru-BY', { style: 'currency', currency: cur, maximumFractionDigits: 2 }).format(n)

const fmtPreview = (n: number | null, cur: string) =>
  n == null ? '…' : fmt(n, cur)

// ================================================================
export default function GoalsPage() {
  const qc = useQueryClient()
  const [showCreate, setShowCreate]             = useState(false)
  const [contributeGoal, setContributeGoal]     = useState<GoalResponse | null>(null)
  const [editGoal, setEditGoal]                 = useState<GoalResponse | null>(null)
  const [statusFilter, setStatusFilter]         = useState<GoalStatus | ''>('')

  const { data, isLoading } = useQuery({
    queryKey: ['goals', statusFilter],
    queryFn: () => goalsApi.getAll(statusFilter || undefined, 0, 50).then(r => r.data),
  })

  const { data: supported } = useQuery({
    queryKey: ['currencies', 'supported'],
    queryFn: () => currenciesApi.getSupported().then(r => r.data),
  })

  const { data: rates } = useQuery({
    queryKey: ['currencies', 'rates'],
    queryFn: () => currenciesApi.getRates().then(r => r.data),
    staleTime: 5 * 60_000,
  })

  const { data: profile } = useQuery({
    queryKey: ['profile'],
    queryFn: () => profileApi.getMe().then(r => r.data),
  })

  const deleteMutation = useMutation({
    mutationFn: goalsApi.remove,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['goals'] }),
  })

  const statusLabel = (s: GoalStatus) =>
    s === 'ACTIVE' ? 'Активна' : s === 'COMPLETED' ? 'Выполнена' : 'Отменена'
  const statusClass = (s: GoalStatus) =>
    s === 'ACTIVE' ? 'badge-blue' : s === 'COMPLETED' ? 'badge-green' : 'badge-gray'

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Цели накопления</h1>
        <button className="btn-primary" onClick={() => setShowCreate(true)}>+ Создать цель</button>
      </div>

      <div className="flex gap-2 mb-4">
        {(['', 'ACTIVE', 'COMPLETED'] as const).map(s => (
          <button key={s} className={`btn ${statusFilter === s ? 'btn-primary' : 'btn-secondary'} text-sm`}
            onClick={() => setStatusFilter(s)}>
            {s === '' ? 'Все' : statusLabel(s as GoalStatus)}
          </button>
        ))}
      </div>

      {isLoading ? <PageLoader /> : !data?.content?.length ? (
        <EmptyState message="Целей нет" action={{ label: '+ Создать цель', onClick: () => setShowCreate(true) }} />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {data.content.map(goal => (
            <div key={goal.id} className="card">
              <div className="flex items-start justify-between mb-3">
                <div>
                  <h3 className="font-semibold text-gray-900">{goal.name}</h3>
                  {goal.deadline && <p className="text-xs text-gray-400 mt-0.5">до {goal.deadline}</p>}
                </div>
                <span className={`badge ${statusClass(goal.status)}`}>{statusLabel(goal.status)}</span>
              </div>

              <div className="mb-3">
                <div className="flex justify-between text-sm mb-1">
                  <span className="text-gray-500">{fmt(goal.currentAmount, goal.currency)}</span>
                  <span className="text-gray-700 font-medium">{fmt(goal.targetAmount, goal.currency)}</span>
                </div>
                <div className="w-full bg-gray-100 rounded-full h-2">
                  <div
                    className="bg-blue-500 h-2 rounded-full transition-all"
                    style={{ width: `${Math.min(goal.progressPercent, 100)}%` }}
                  />
                </div>
                <p className="text-xs text-gray-400 mt-1">{goal.progressPercent.toFixed(1)}%</p>
              </div>

              <div className="flex gap-2">
                {goal.status === 'ACTIVE' && (
                  <button className="btn-primary text-sm flex-1" onClick={() => setContributeGoal(goal)}>
                    Пополнить
                  </button>
                )}
                <button
                  className="btn-secondary text-sm px-3"
                  title="Редактировать"
                  onClick={() => setEditGoal(goal)}
                >
                  <i className="bi bi-pencil" />
                </button>
                {(goal.status === 'ACTIVE' || goal.status === 'COMPLETED') && (
                  <button
                    className="text-red-400 hover:text-red-600 px-2"
                    title="Удалить"
                    onClick={() => deleteMutation.mutate(goal.id)}
                  >
                    <i className="bi bi-trash" />
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {showCreate && (
        <CreateGoalModal
          supported={supported ?? ['BYN']}
          defaultCurrency={profile?.defaultCurrency ?? 'BYN'}
          onClose={() => setShowCreate(false)}
          onCreated={() => { qc.invalidateQueries({ queryKey: ['goals'] }); setShowCreate(false) }}
        />
      )}

      {editGoal && (
        <EditGoalModal
          goal={editGoal}
          supported={supported ?? ['BYN']}
          rates={rates ?? []}
          onClose={() => setEditGoal(null)}
          onSaved={() => { qc.invalidateQueries({ queryKey: ['goals'] }); setEditGoal(null) }}
        />
      )}

      {contributeGoal !== null && (
        <ContributeModal
          goal={contributeGoal}
          supported={supported ?? ['BYN']}
          rates={rates ?? []}
          onClose={() => setContributeGoal(null)}
          onDone={() => { qc.invalidateQueries({ queryKey: ['goals'] }); setContributeGoal(null) }}
        />
      )}
    </div>
  )
}

// ================================================================
function CreateGoalModal({ supported, defaultCurrency, onClose, onCreated }: {
  supported: string[]; defaultCurrency: string; onClose: () => void; onCreated: () => void
}) {
  const { register, handleSubmit } = useForm({ defaultValues: { currency: defaultCurrency } })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const onSubmit = async (data: any) => {
    setLoading(true)
    try {
      await goalsApi.create({ ...data, targetAmount: Number(data.targetAmount) })
      onCreated()
    } catch (e) { setError(parseApiError(e)) }
    finally { setLoading(false) }
  }

  return (
    <Modal title="Новая цель" onClose={onClose}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="label">Название</label>
          <input className="input" type="text" {...register('name', { required: true })} />
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="label">Целевая сумма</label>
            <input className="input" type="number" step="0.01" min="0" {...register('targetAmount', { required: true })} />
          </div>
          <div>
            <label className="label">Валюта цели</label>
            <select className="input" {...register('currency', { required: true })}>
              {supported.map(c => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>
        </div>
        <div>
          <label className="label">Срок (необязательно)</label>
          <input className="input" type="date" {...register('deadline')} />
        </div>
        {error && <p className="error-text">{error}</p>}
        <div className="flex gap-3 pt-2">
          <button type="button" className="btn-secondary flex-1" onClick={onClose}>Отмена</button>
          <button type="submit" className="btn-primary flex-1" disabled={loading}>
            {loading ? <Spinner size="sm" /> : 'Создать'}
          </button>
        </div>
      </form>
    </Modal>
  )
}

// ================================================================
function EditGoalModal({ goal, supported, rates, onClose, onSaved }: {
  goal: GoalResponse; supported: string[]; rates: NbrbRate[];
  onClose: () => void; onSaved: () => void
}) {
  const { register, handleSubmit, watch } = useForm({
    defaultValues: {
      name:         goal.name,
      targetAmount: goal.targetAmount,
      currency:     goal.currency,
      deadline:     goal.deadline ?? '',
    },
  })
  const [loading, setLoading] = useState(false)
  const [error, setError]     = useState('')

  const selectedCurrency = watch('currency')
  const currencyChanged  = selectedCurrency !== goal.currency

  // Live preview: what currentAmount becomes after currency conversion
  const convertedCurrent = useMemo(() => {
    if (!currencyChanged || !rates.length) return null
    return convertAmount(goal.currentAmount, goal.currency, selectedCurrency, rates)
  }, [selectedCurrency, currencyChanged, goal.currentAmount, goal.currency, rates])

  const onSubmit = async (data: any) => {
    setLoading(true)
    try {
      await goalsApi.update(goal.id, {
        ...data,
        targetAmount: Number(data.targetAmount),
        deadline: data.deadline || undefined,
      })
      onSaved()
    } catch (e) { setError(parseApiError(e)) }
    finally { setLoading(false) }
  }

  return (
    <Modal title="Редактировать цель" onClose={onClose}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="label">Название</label>
          <input className="input" type="text" {...register('name', { required: true })} />
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="label">Целевая сумма</label>
            <input className="input" type="number" step="0.01" min="0" {...register('targetAmount', { required: true })} />
          </div>
          <div>
            <label className="label">Валюта цели</label>
            <select className="input" {...register('currency', { required: true })}>
              {supported.map(c => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>
        </div>

        {/* Conversion preview when currency changes */}
        {currencyChanged && goal.currentAmount > 0 && (
          <div className="rounded-lg bg-amber-50 border border-amber-200 px-3 py-2 text-sm text-amber-800 flex items-start gap-2">
            <i className="bi bi-arrow-left-right mt-0.5 flex-shrink-0" />
            <span>
              Накоплено{' '}
              <strong>{fmt(goal.currentAmount, goal.currency)}</strong>
              {' '}будет пересчитано в{' '}
              <strong>{fmtPreview(convertedCurrent, selectedCurrency)}</strong>
            </span>
          </div>
        )}

        <div>
          <label className="label">Срок (необязательно)</label>
          <input className="input" type="date" {...register('deadline')} />
        </div>
        {error && <p className="error-text">{error}</p>}
        <div className="flex gap-3 pt-2">
          <button type="button" className="btn-secondary flex-1" onClick={onClose}>Отмена</button>
          <button type="submit" className="btn-primary flex-1" disabled={loading}>
            {loading ? <Spinner size="sm" /> : 'Сохранить'}
          </button>
        </div>
      </form>
    </Modal>
  )
}

// ================================================================
function ContributeModal({ goal, supported, rates, onClose, onDone }: {
  goal: GoalResponse; supported: string[]; rates: NbrbRate[];
  onClose: () => void; onDone: () => void
}) {
  const [amount, setAmount]     = useState('')
  const [currency, setCurrency] = useState(goal.currency)
  const [loading, setLoading]   = useState(false)
  const [error, setError]       = useState('')

  const needsConversion = currency !== goal.currency
  const numAmount = parseFloat(amount) || 0

  const convertedPreview = useMemo(() => {
    if (!needsConversion || !numAmount || !rates.length) return null
    return convertAmount(numAmount, currency, goal.currency, rates)
  }, [amount, currency, goal.currency, needsConversion, rates, numAmount])

  const handleSubmit = async () => {
    if (!numAmount) return
    setLoading(true)
    setError('')
    try {
      await goalsApi.contribute(goal.id, { amount: numAmount, currency })
      onDone()
    } catch (e) { setError(parseApiError(e)) }
    finally { setLoading(false) }
  }

  return (
    <Modal title="Пополнить цель" onClose={onClose}>
      <div className="space-y-4">
        {/* Amount + currency row */}
        <div>
          <label className="label">Сумма пополнения</label>
          <div className="flex gap-2">
            <input
              className="input flex-1"
              type="number"
              step="0.01"
              min="0"
              placeholder="0.00"
              value={amount}
              onChange={e => setAmount(e.target.value)}
            />
            <select
              className="input w-28"
              value={currency}
              onChange={e => setCurrency(e.target.value)}
            >
              {supported.map(c => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>
        </div>

        {/* Conversion preview */}
        {needsConversion && numAmount > 0 && (
          <div className="rounded-lg bg-blue-50 border border-blue-200 px-3 py-2 text-sm text-blue-800 flex items-start gap-2">
            <i className="bi bi-arrow-left-right mt-0.5 flex-shrink-0" />
            <span>
              {fmt(numAmount, currency)}
              {' → '}
              <strong>{fmtPreview(convertedPreview, goal.currency)}</strong>
              {' '}будет зачислено в цель
            </span>
          </div>
        )}

        <div className="text-xs text-gray-400">
          Цель: <strong>{goal.name}</strong> ·{' '}
          Накоплено: <strong>{fmt(goal.currentAmount, goal.currency)}</strong>{' '}
          из <strong>{fmt(goal.targetAmount, goal.currency)}</strong>
        </div>

        {error && <p className="error-text">{error}</p>}
        <div className="flex gap-3 pt-2">
          <button className="btn-secondary flex-1" onClick={onClose}>Отмена</button>
          <button
            className="btn-primary flex-1"
            disabled={loading || !numAmount}
            onClick={handleSubmit}
          >
            {loading ? <Spinner size="sm" /> : 'Пополнить'}
          </button>
        </div>
      </div>
    </Modal>
  )
}
