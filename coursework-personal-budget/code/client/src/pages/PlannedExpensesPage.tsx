import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { plannedExpensesApi } from '../api/plannedExpenses'
import { categoriesApi } from '../api/categories'
import { currenciesApi } from '../api/currencies'
import { profileApi } from '../api/profile'
import PageLoader from '../components/common/PageLoader'
import EmptyState from '../components/common/EmptyState'
import Modal from '../components/common/Modal'
import Spinner from '../components/common/Spinner'
import type { CreatePlannedExpenseRequest } from '../types'
import { parseApiError } from '../utils/errorUtils'

const fmt = (n: number, cur = 'BYN') =>
  new Intl.NumberFormat('ru-BY', { style: 'currency', currency: cur, maximumFractionDigits: 2 }).format(n)

const typeLabel = (t: string) => t === 'BUDGET_LIMIT' ? 'Лимит бюджета' : 'Запланированная покупка'
const periodLabel = (p?: string) => p === 'MONTHLY' ? 'в месяц' : p === 'QUARTERLY' ? 'в квартал' : p === 'YEARLY' ? 'в год' : ''

export default function PlannedExpensesPage() {
  const qc = useQueryClient()
  const [showCreate, setShowCreate] = useState(false)

  const { data, isLoading } = useQuery({
    queryKey: ['planned-expenses'],
    queryFn: () => plannedExpensesApi.getAll(0, 50).then(r => r.data),
  })

  const { data: categories } = useQuery({
    queryKey: ['categories'],
    queryFn: () => categoriesApi.getAll(undefined, 0, 200).then(r => r.data),
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
    mutationFn: plannedExpensesApi.remove,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['planned-expenses'] }),
  })

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Бюджет и планы</h1>
        <button className="btn-primary" onClick={() => setShowCreate(true)}>+ Создать</button>
      </div>

      {isLoading ? <PageLoader /> : !data?.content?.length ? (
        <EmptyState message="Записей нет" action={{ label: '+ Создать', onClick: () => setShowCreate(true) }} />
      ) : (
        <div className="space-y-3">
          {data.content.map(e => (
            <div key={e.id} className="card flex items-center justify-between p-4">
              <div className="flex items-center gap-4">
                <div className={`w-2 h-10 rounded-full ${e.type === 'BUDGET_LIMIT' ? 'bg-orange-400' : 'bg-purple-400'}`} />
                <div>
                  <p className="font-medium">{e.name}</p>
                  <p className="text-xs text-gray-400">
                    {typeLabel(e.type)}
                    {e.categoryName && ` · ${e.categoryName}`}
                    {e.periodType && ` · ${periodLabel(e.periodType)}`}
                    {e.plannedDate && ` · до ${e.plannedDate}`}
                  </p>
                </div>
              </div>
              <div className="flex items-center gap-4">
                <div className="text-right">
                  <p className="font-semibold">{fmt(e.amount, e.currency)}</p>
                  {e.isFulfilled && (
                    <span className="badge badge-green text-xs">
                      <i className="bi bi-check-lg me-1" />Выполнено
                    </span>
                  )}
                </div>
                <button className="text-red-400 hover:text-red-600" title="Удалить" onClick={() => deleteMutation.mutate(e.id)}>
                  <i className="bi bi-trash" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {showCreate && (
        <CreateModal
          categories={categories?.content ?? []}
          supported={supported ?? ['BYN']}
          defaultCurrency={profile?.defaultCurrency ?? 'BYN'}
          onClose={() => setShowCreate(false)}
          onCreated={() => { qc.invalidateQueries({ queryKey: ['planned-expenses'] }); setShowCreate(false) }}
        />
      )}
    </div>
  )
}

function CreateModal({ categories, supported, defaultCurrency, onClose, onCreated }: { categories: any[]; supported: string[]; defaultCurrency: string; onClose: () => void; onCreated: () => void }) {
  const { register, handleSubmit, watch } = useForm<CreatePlannedExpenseRequest>({ defaultValues: { type: 'BUDGET_LIMIT', currency: defaultCurrency } })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const type = watch('type')

  const onSubmit = async (data: any) => {
    setLoading(true)
    try { await plannedExpensesApi.create({ ...data, amount: Number(data.amount) }); onCreated() }
    catch (e) { setError(parseApiError(e)) }
    finally { setLoading(false) }
  }

  return (
    <Modal title="Новая запись" onClose={onClose}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="label">Тип</label>
          <select className="input" {...register('type')}>
            <option value="BUDGET_LIMIT">Лимит бюджета</option>
            <option value="PLANNED_PURCHASE">Запланированная покупка</option>
          </select>
        </div>
        <div>
          <label className="label">Название</label>
          <input className="input" {...register('name', { required: true })} />
        </div>
        <div>
          <label className="label">Категория</label>
          <select className="input" {...register('categoryId', { valueAsNumber: true })}>
            <option value="">Без категории</option>
            {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="label">Сумма</label>
            <input className="input" type="number" step="0.01" min="0" {...register('amount', { required: true })} />
          </div>
          <div>
            <label className="label">Валюта</label>
            <select className="input" {...register('currency')}>
              {supported.map(c => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>
        </div>
        {type === 'BUDGET_LIMIT' && (
          <div>
            <label className="label">Период</label>
            <select className="input" {...register('periodType')}>
              <option value="MONTHLY">Ежемесячно</option>
              <option value="QUARTERLY">Ежеквартально</option>
              <option value="YEARLY">Ежегодно</option>
            </select>
          </div>
        )}
        {type === 'PLANNED_PURCHASE' && (
          <div>
            <label className="label">Дата покупки</label>
            <input className="input" type="date" {...register('plannedDate')} />
          </div>
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
