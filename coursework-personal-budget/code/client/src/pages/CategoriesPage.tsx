import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { categoriesApi } from '../api/categories'
import PageLoader from '../components/common/PageLoader'
import EmptyState from '../components/common/EmptyState'
import Modal from '../components/common/Modal'
import Spinner from '../components/common/Spinner'
import type { CreateCategoryRequest } from '../types'
import { parseApiError } from '../utils/errorUtils'

export default function CategoriesPage() {
  const qc = useQueryClient()
  const [showCreate, setShowCreate] = useState(false)
  const [typeFilter, setTypeFilter] = useState<'INCOME' | 'EXPENSE' | ''>('')

  const { data, isLoading } = useQuery({
    queryKey: ['categories', typeFilter],
    queryFn: () => categoriesApi.getAll(typeFilter || undefined, 0, 200).then(r => r.data),
  })

  const deleteMutation = useMutation({
    mutationFn: categoriesApi.remove,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['categories'] }),
  })

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Категории</h1>
        <button className="btn-primary" onClick={() => setShowCreate(true)}>+ Создать</button>
      </div>

      <div className="flex gap-2 mb-4">
        {(['', 'INCOME', 'EXPENSE'] as const).map(t => (
          <button key={t} className={`btn text-sm ${typeFilter === t ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setTypeFilter(t)}>
            {t === '' ? 'Все' : t === 'INCOME'
              ? <><i className="bi bi-arrow-up me-1" />Доходы</>
              : <><i className="bi bi-arrow-down me-1" />Расходы</>}
          </button>
        ))}
      </div>

      {isLoading ? <PageLoader /> : !data?.content?.length ? (
        <EmptyState message="Категорий нет" action={{ label: '+ Создать', onClick: () => setShowCreate(true) }} />
      ) : (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
          {data.content.map(c => (
            <div key={c.id} className="card flex items-center justify-between p-4">
              <div className="flex items-center gap-3">
                {c.color && <div className="w-4 h-4 rounded-full flex-shrink-0" style={{ backgroundColor: c.color }} />}
                <div>
                  <p className="text-sm font-medium">{c.name}</p>
                  <p className="text-xs text-gray-400">
                    {c.type === 'INCOME'
                      ? <><i className="bi bi-arrow-up me-1" />Доход</>
                      : <><i className="bi bi-arrow-down me-1" />Расход</>}
                  </p>
                </div>
              </div>
              <button className="text-red-400 hover:text-red-600" title="Удалить" onClick={() => deleteMutation.mutate(c.id)}>
                <i className="bi bi-x-lg" />
              </button>
            </div>
          ))}
        </div>
      )}

      {showCreate && (
        <CreateCategoryModal
          onClose={() => setShowCreate(false)}
          onCreated={() => { qc.invalidateQueries({ queryKey: ['categories'] }); setShowCreate(false) }}
        />
      )}
    </div>
  )
}

function CreateCategoryModal({ onClose, onCreated }: { onClose: () => void; onCreated: () => void }) {
  const { register, handleSubmit, watch, setValue } = useForm<CreateCategoryRequest>({
    defaultValues: { type: 'EXPENSE', color: '#3b82f6' },
  })
  const [loading, setLoading] = useState(false)
  const [error, setError]     = useState('')

  const colorValue = watch('color') ?? '#3b82f6'

  const onSubmit = async (data: CreateCategoryRequest) => {
    setLoading(true)
    try { await categoriesApi.create(data); onCreated() }
    catch (e) { setError(parseApiError(e)) }
    finally { setLoading(false) }
  }

  return (
    <Modal title="Новая категория" onClose={onClose}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="label">Название</label>
          <input className="input" type="text" placeholder="Продукты, Зарплата…"
            {...register('name', { required: true })} />
        </div>
        <div>
          <label className="label">Тип</label>
          <select className="input" {...register('type', { required: true })}>
            <option value="EXPENSE">Расход</option>
            <option value="INCOME">Доход</option>
          </select>
        </div>
        <div>
          <label className="label">Цвет (необязательно)</label>
          <div className="flex items-center gap-3">
            <input
              type="color"
              className="h-9 w-16 rounded border border-gray-300 p-1 cursor-pointer"
              value={colorValue}
              onChange={e => setValue('color', e.target.value, { shouldValidate: true })}
            />
            <input
              className="input flex-1"
              type="text"
              placeholder="#3b82f6"
              {...register('color', {
                pattern: { value: /^#[0-9a-fA-F]{6}$/, message: 'Формат: #rrggbb' },
              })}
            />
            <span
              className="w-7 h-7 rounded-full border border-gray-200 flex-shrink-0"
              style={{ backgroundColor: /^#[0-9a-fA-F]{6}$/.test(colorValue) ? colorValue : 'transparent' }}
            />
          </div>
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
