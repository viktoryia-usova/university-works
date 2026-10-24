import { useState, useEffect } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { transactionsApi } from '../api/transactions'
import { categoriesApi } from '../api/categories'
import { currenciesApi } from '../api/currencies'
import { profileApi } from '../api/profile'
import PageLoader from '../components/common/PageLoader'
import EmptyState from '../components/common/EmptyState'
import Pagination from '../components/common/Pagination'
import Modal from '../components/common/Modal'
import Spinner from '../components/common/Spinner'
import type { CreateTransactionRequest } from '../types'
import { parseApiError } from '../utils/errorUtils'

const fmt = (n: number, cur = 'BYN') =>
  new Intl.NumberFormat('ru-BY', { style: 'currency', currency: cur, maximumFractionDigits: 2 }).format(n)

interface Filters {
  type?: string; categoryId?: number; dateFrom?: string; dateTo?: string; currency?: string
}

const SORT_OPTIONS = [
  { value: 'date,desc',      label: 'Дата ↓' },
  { value: 'date,asc',       label: 'Дата ↑' },
  { value: 'amountByn,desc', label: 'Сумма ↓' },
  { value: 'amountByn,asc',  label: 'Сумма ↑' },
  { value: 'type,asc',       label: 'Тип' },
]

export default function TransactionsPage() {
  const qc = useQueryClient()
  const [page, setPage] = useState(0)
  const [filters, setFilters] = useState<Filters>({})
  const [sort, setSort] = useState('date,desc')
  const [showCreate, setShowCreate] = useState(false)

  const { data, isLoading } = useQuery({
    queryKey: ['transactions', page, filters, sort],
    queryFn: () => transactionsApi.getAll({ ...filters, page, size: 20, sort }).then(r => r.data),
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
    mutationFn: transactionsApi.remove,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['transactions'] })
      qc.invalidateQueries({ queryKey: ['analytics'] })
    },
  })

  // When type filter changes, reset category (it might belong to the other type)
  const setTypeFilter = (type: string) => {
    setFilters(f => ({ ...f, type: type || undefined, categoryId: undefined }))
    setPage(0)
  }

  const filteredCategories = (categories?.content ?? []).filter(
    c => !filters.type || c.type === filters.type
  )

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Операции</h1>
        <button className="btn-primary" onClick={() => setShowCreate(true)}>+ Добавить</button>
      </div>

      {/* Filters */}
      <div className="card mb-4 p-4">
        <div className="flex flex-wrap gap-3">
          <select
            className="input w-auto"
            value={filters.type ?? ''}
            onChange={e => setTypeFilter(e.target.value)}
          >
            <option value="">Все типы</option>
            <option value="INCOME">Доход</option>
            <option value="EXPENSE">Расход</option>
          </select>

          <select
            className="input w-auto"
            value={filters.categoryId ?? ''}
            onChange={e => { setFilters(f => ({ ...f, categoryId: e.target.value ? +e.target.value : undefined })); setPage(0) }}
          >
            <option value="">Все категории</option>
            {filteredCategories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>

          <input className="input w-36" type="date" value={filters.dateFrom ?? ''}
            onChange={e => { setFilters(f => ({ ...f, dateFrom: e.target.value || undefined })); setPage(0) }} />
          <input className="input w-36" type="date" value={filters.dateTo ?? ''}
            onChange={e => { setFilters(f => ({ ...f, dateTo: e.target.value || undefined })); setPage(0) }} />

          <select className="input w-auto" value={sort} onChange={e => { setSort(e.target.value); setPage(0) }}>
            {SORT_OPTIONS.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
          </select>

          <button className="btn-secondary text-sm" onClick={() => { setFilters({}); setSort('date,desc'); setPage(0) }}>
            Сбросить
          </button>
        </div>
      </div>

      {isLoading ? <PageLoader /> : !data?.content?.length ? (
        <EmptyState message="Операций нет" action={{ label: '+ Добавить', onClick: () => setShowCreate(true) }} />
      ) : (
        <>
          <div className="card p-0 overflow-hidden">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
                <tr>
                  <th className="px-4 py-3 text-left">Дата</th>
                  <th className="px-4 py-3 text-left">Тип</th>
                  <th className="px-4 py-3 text-left">Категория</th>
                  <th className="px-4 py-3 text-left">Описание</th>
                  <th className="px-4 py-3 text-right">Сумма</th>
                  <th className="px-4 py-3 text-right">Сумма BYN</th>
                  <th className="px-4 py-3" />
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {data.content.map(tx => (
                  <tr key={tx.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3 text-gray-500">{tx.date}</td>
                    <td className="px-4 py-3">
                      <span className={`badge ${tx.type === 'INCOME' ? 'badge-green' : 'badge-red'}`}>
                        <i className={`bi ${tx.type === 'INCOME' ? 'bi-arrow-up' : 'bi-arrow-down'} me-1`} />
                        {tx.type === 'INCOME' ? 'Доход' : 'Расход'}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-2">
                        {tx.categoryColor && (
                          <span className="w-3 h-3 rounded-full flex-shrink-0 inline-block"
                            style={{ backgroundColor: tx.categoryColor }} />
                        )}
                        <span className="text-gray-700">{tx.categoryName || '—'}</span>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-gray-500 max-w-xs truncate">{tx.description || '—'}</td>
                    <td className="px-4 py-3 text-right font-medium">{fmt(tx.amount, tx.currency)}</td>
                    <td className={`px-4 py-3 text-right font-semibold ${tx.type === 'INCOME' ? 'text-green-600' : 'text-red-600'}`}>
                      {tx.type === 'INCOME' ? '+' : '-'}{fmt(tx.amountByn)}
                    </td>
                    <td className="px-4 py-3 text-right">
                      <button className="text-red-400 hover:text-red-600" title="Удалить"
                        onClick={() => deleteMutation.mutate(tx.id)}>
                        <i className="bi bi-trash" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <Pagination page={page} totalPages={data.page.totalPages} onPage={setPage} />
        </>
      )}

      {showCreate && (
        <CreateTransactionModal
          categories={categories?.content ?? []}
          supported={supported ?? ['BYN']}
          defaultCurrency={profile?.defaultCurrency ?? 'BYN'}
          onClose={() => setShowCreate(false)}
          onCreated={() => {
            qc.invalidateQueries({ queryKey: ['transactions'] })
            qc.invalidateQueries({ queryKey: ['analytics'] })
            setShowCreate(false)
          }}
        />
      )}
    </div>
  )
}

function CreateTransactionModal({ categories, supported, defaultCurrency, onClose, onCreated }: {
  categories: any[]; supported: string[]; defaultCurrency: string; onClose: () => void; onCreated: () => void
}) {
  const { register, handleSubmit, watch, setValue } = useForm<CreateTransactionRequest>({
    defaultValues: { currency: defaultCurrency, type: 'EXPENSE', date: new Date().toLocaleDateString('en-CA') }
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const selectedType = watch('type')

  useEffect(() => {
    setValue('categoryId', undefined as any)
  }, [selectedType, setValue])

  const filteredCategories = categories.filter(c => c.type === selectedType)

  const onSubmit = async (data: CreateTransactionRequest) => {
    setLoading(true)
    try {
      await transactionsApi.create(data)
      onCreated()
    } catch (e) {
      setError(parseApiError(e))
    } finally {
      setLoading(false)
    }
  }

  return (
    <Modal title="Новая операция" onClose={onClose}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="label">Тип</label>
          <select className="input" {...register('type', { required: true })}>
            <option value="EXPENSE">Расход</option>
            <option value="INCOME">Доход</option>
          </select>
        </div>
        <div>
          <label className="label">Категория</label>
          <select className="input" {...register('categoryId', { valueAsNumber: true })}>
            <option value="">— Без категории —</option>
            {filteredCategories.map(c => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>
          {filteredCategories.length === 0 && (
            <p className="text-xs text-gray-400 mt-1">
              Нет категорий типа «{selectedType === 'INCOME' ? 'Доход' : 'Расход'}» — создайте их в разделе Категории
            </p>
          )}
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="label">Сумма</label>
            <input className="input" type="number" step="0.01" min="0"
              {...register('amount', { required: true, valueAsNumber: true })} />
          </div>
          <div>
            <label className="label">Валюта</label>
            <select className="input" {...register('currency', { required: true })}>
              {supported.map(c => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>
        </div>
        <div>
          <label className="label">Дата</label>
          <input className="input" type="date" {...register('date', { required: true })} />
        </div>
        <div>
          <label className="label">Описание</label>
          <input className="input" type="text" placeholder="Необязательно" {...register('description')} />
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
