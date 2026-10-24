import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { depositsApi } from '../api/deposits'
import { currenciesApi } from '../api/currencies'
import { profileApi } from '../api/profile'
import PageLoader from '../components/common/PageLoader'
import EmptyState from '../components/common/EmptyState'
import Modal from '../components/common/Modal'
import Spinner from '../components/common/Spinner'
import { parseApiError } from '../utils/errorUtils'

const fmt = (n: number, cur = 'BYN') =>
  new Intl.NumberFormat('ru-BY', { style: 'currency', currency: cur, maximumFractionDigits: 2 }).format(n)

export default function DepositsPage() {
  const qc = useQueryClient()
  const [showCreate, setShowCreate] = useState(false)
  const [addFundsId, setAddFundsId] = useState<number | null>(null)

  const { data, isLoading } = useQuery({
    queryKey: ['deposits'],
    queryFn: () => depositsApi.getAll({ size: 50 }).then(r => r.data),
  })

  const { data: supported } = useQuery({
    queryKey: ['currencies', 'supported'],
    queryFn: () => currenciesApi.getSupported().then(r => r.data),
  })

  const { data: profile } = useQuery({
    queryKey: ['profile'],
    queryFn: () => profileApi.getMe().then(r => r.data),
  })

  const deleteMutation = useMutation({
    mutationFn: depositsApi.remove,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['deposits'] }),
  })

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Вклады и копилки</h1>
        <button className="btn-primary" onClick={() => setShowCreate(true)}>+ Создать</button>
      </div>

      {isLoading ? <PageLoader /> : !data?.content?.length ? (
        <EmptyState message="Вкладов нет" action={{ label: '+ Создать', onClick: () => setShowCreate(true) }} />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {data.content.map(d => (
            <div key={d.id} className="card">
              <div className="flex justify-between items-start mb-3">
                <div>
                  <h3 className="font-semibold">{d.name}</h3>
                  <span className={`badge mt-1 ${d.type === 'DEPOSIT' ? 'badge-blue' : 'badge-yellow'}`}>
                    {d.type === 'DEPOSIT'
                      ? <><i className="bi bi-bank me-1" />Вклад</>
                      : <><i className="bi bi-piggy-bank me-1" />Копилка</>}
                  </span>
                </div>
                <span className={`badge ${d.status === 'ACTIVE' ? 'badge-green' : 'badge-gray'}`}>
                  {d.status === 'ACTIVE' ? 'Активен' : 'Завершён'}
                </span>
              </div>

              <div className="space-y-1 text-sm text-gray-600 mb-4">
                <div className="flex justify-between">
                  <span>Текущая сумма</span>
                  <span className="font-semibold text-gray-900">{fmt(d.currentAmount, d.currency)}</span>
                </div>
                {d.type === 'DEPOSIT' && (
                  <>
                    <div className="flex justify-between">
                      <span>Ставка</span>
                      <span>{d.interestRate}% годовых</span>
                    </div>
                    <div className="flex justify-between">
                      <span>Прогнозируемый доход</span>
                      <span className="text-green-600 font-medium">{fmt(d.projectedIncome, d.currency)}</span>
                    </div>
                    <div className="flex justify-between">
                      <span>Срок</span>
                      <span>{d.startDate} — {d.endDate ?? '∞'}</span>
                    </div>
                  </>
                )}
              </div>

              <div className="flex gap-2">
                {d.status === 'ACTIVE' && d.type === 'PIGGY_BANK' && (
                  <button className="btn-primary text-sm flex-1" onClick={() => setAddFundsId(d.id)}>
                    Пополнить
                  </button>
                )}
                <button
                  className="btn-secondary text-sm flex-1"
                  onClick={() => deleteMutation.mutate(d.id)}
                  disabled={deleteMutation.isPending}
                >
                  Удалить
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {showCreate && (
        <CreateDepositModal
          supported={supported ?? ['BYN']}
          defaultCurrency={profile?.defaultCurrency ?? 'BYN'}
          onClose={() => setShowCreate(false)}
          onCreated={() => { qc.invalidateQueries({ queryKey: ['deposits'] }); setShowCreate(false) }}
        />
      )}

      {addFundsId !== null && (
        <AddFundsModal
          depositId={addFundsId}
          onClose={() => setAddFundsId(null)}
          onDone={() => { qc.invalidateQueries({ queryKey: ['deposits'] }); setAddFundsId(null) }}
        />
      )}
    </div>
  )
}

function CreateDepositModal({ supported, defaultCurrency, onClose, onCreated }: { supported: string[]; defaultCurrency: string; onClose: () => void; onCreated: () => void }) {
  const { register, handleSubmit, watch } = useForm({ defaultValues: { type: 'PIGGY_BANK', currency: defaultCurrency, startDate: new Date().toLocaleDateString('en-CA') } })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const type = watch('type')

  const onSubmit = async (data: any) => {
    setLoading(true)
    try {
      await depositsApi.create({ ...data, initialAmount: Number(data.initialAmount), interestRate: data.interestRate ? Number(data.interestRate) : undefined })
      onCreated()
    } catch (e) { setError(parseApiError(e)) }
    finally { setLoading(false) }
  }

  return (
    <Modal title="Новый вклад / копилка" onClose={onClose}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="label">Тип</label>
          <select className="input" {...register('type')}>
            <option value="PIGGY_BANK">Копилка</option>
            <option value="DEPOSIT">Вклад</option>
          </select>
        </div>
        <div>
          <label className="label">Название</label>
          <input className="input" type="text" {...register('name', { required: true })} />
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="label">Начальная сумма</label>
            <input className="input" type="number" step="0.01" min="0" {...register('initialAmount', { required: true })} />
          </div>
          <div>
            <label className="label">Валюта</label>
            <select className="input" {...register('currency')}>
              {supported.map(c => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>
        </div>
        <div>
          <label className="label">Дата открытия</label>
          <input className="input" type="date" {...register('startDate', { required: true })} />
        </div>
        {type === 'DEPOSIT' && (
          <>
            <div>
              <label className="label">Дата закрытия</label>
              <input className="input" type="date" {...register('endDate')} />
            </div>
            <div>
              <label className="label">Процентная ставка (%)</label>
              <input className="input" type="number" step="0.01" min="0" {...register('interestRate')} />
            </div>
          </>
        )}
        {error && <p className="error-text">{error}</p>}
        <div className="flex gap-3 pt-2">
          <button type="button" className="btn-secondary flex-1" onClick={onClose}>Отмена</button>
          <button type="submit" className="btn-primary flex-1" disabled={loading}>{loading ? <Spinner size="sm" /> : 'Создать'}</button>
        </div>
      </form>
    </Modal>
  )
}

function AddFundsModal({ depositId, onClose, onDone }: { depositId: number; onClose: () => void; onDone: () => void }) {
  const [amount, setAmount] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async () => {
    setLoading(true)
    try { await depositsApi.addFunds(depositId, { amount: Number(amount) }); onDone() }
    catch (e) { setError(parseApiError(e)) }
    finally { setLoading(false) }
  }

  return (
    <Modal title="Пополнить копилку" onClose={onClose}>
      <div className="space-y-4">
        <div>
          <label className="label">Сумма</label>
          <input className="input" type="number" step="0.01" min="0" value={amount} onChange={e => setAmount(e.target.value)} />
        </div>
        {error && <p className="error-text">{error}</p>}
        <div className="flex gap-3 pt-2">
          <button className="btn-secondary flex-1" onClick={onClose}>Отмена</button>
          <button className="btn-primary flex-1" disabled={loading} onClick={handleSubmit}>{loading ? <Spinner size="sm" /> : 'Пополнить'}</button>
        </div>
      </div>
    </Modal>
  )
}
