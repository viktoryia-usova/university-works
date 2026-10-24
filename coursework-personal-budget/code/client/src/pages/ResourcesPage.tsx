import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { resourcesApi } from '../api/resources'
import { useAuthStore } from '../store/authStore'
import PageLoader from '../components/common/PageLoader'
import EmptyState from '../components/common/EmptyState'
import Pagination from '../components/common/Pagination'
import type { FinancialResourceResponse } from '../types'
import { parseApiError } from '../utils/errorUtils'

interface ResourceForm { categoryId: number; title: string; content: string }
interface CategoryForm { name: string }

type Tab = 'all' | 'favorites'

export default function ResourcesPage() {
  const qc = useQueryClient()
  const { isAdmin } = useAuthStore()
  const admin = isAdmin()

  const [tab, setTab]                  = useState<Tab>('all')
  const [page, setPage]                = useState(0)
  const [favPage, setFavPage]          = useState(0)
  const [categoryId, setCategoryId]    = useState<number | undefined>()
  const [titleSearch, setTitleSearch]  = useState('')
  const [selectedId, setSelectedId]    = useState<number | null>(null)
  const [editTarget, setEditTarget]    = useState<FinancialResourceResponse | null>(null)
  const [showCreate, setShowCreate]    = useState(false)
  const [showCatMgr, setShowCatMgr]   = useState(false)

  const { data: categories } = useQuery({
    queryKey: ['resource-categories'],
    queryFn: () => resourcesApi.getCategories().then(r => r.data),
  })

  const { data, isLoading } = useQuery({
    queryKey: ['resources', page, categoryId, titleSearch],
    queryFn: () => resourcesApi.getAll({ categoryId, title: titleSearch || undefined, page, size: 12 }).then(r => r.data),
    enabled: tab === 'all',
  })

  const { data: favData, isLoading: loadFav } = useQuery({
    queryKey: ['resources', 'favorites', favPage],
    queryFn: () => resourcesApi.getFavorites({ page: favPage, size: 12 }).then(r => r.data),
    enabled: tab === 'favorites',
  })

  const { data: detail, isLoading: loadDetail } = useQuery({
    queryKey: ['resource', selectedId],
    queryFn: () => resourcesApi.getById(selectedId!).then(r => r.data),
    enabled: selectedId !== null,
  })

  const invalidateAll = () => {
    qc.invalidateQueries({ queryKey: ['resources'] })
    qc.invalidateQueries({ queryKey: ['resource-categories'] })
  }

  const invalidateOne = (id: number) => {
    qc.invalidateQueries({ queryKey: ['resource', id] })
    qc.invalidateQueries({ queryKey: ['resources'] })
  }

  const [mutError, setMutError] = useState('')

  const createMut = useMutation({
    mutationFn: resourcesApi.create,
    onSuccess: () => { invalidateAll(); setShowCreate(false); setMutError('') },
    onError: (e) => setMutError(parseApiError(e)),
  })
  const updateMut = useMutation({
    mutationFn: ({ id, data }: { id: number; data: any }) => resourcesApi.update(id, data),
    onSuccess: (_, vars) => { invalidateAll(); setEditTarget(null); qc.invalidateQueries({ queryKey: ['resource', vars.id] }); setMutError('') },
    onError: (e) => setMutError(parseApiError(e)),
  })
  const removeMut = useMutation({
    mutationFn: resourcesApi.remove,
    onSuccess: invalidateAll,
    onError: (e) => setMutError(parseApiError(e)),
  })
  const createCat = useMutation({
    mutationFn: (name: string) => resourcesApi.createCategory(name),
    onSuccess: invalidateAll,
  })
  const removeCat = useMutation({
    mutationFn: resourcesApi.removeCategory,
    onSuccess: invalidateAll,
  })

  // Favorite toggle — optimistically updates the card; invalidates both lists
  const favMut = useMutation({
    mutationFn: ({ id, isFavorite }: { id: number; isFavorite: boolean }) =>
      isFavorite ? resourcesApi.removeFavorite(id) : resourcesApi.addFavorite(id),
    onSuccess: (_, vars) => invalidateOne(vars.id),
  })

  const handleFavToggle = (e: React.MouseEvent, r: FinancialResourceResponse) => {
    e.stopPropagation()
    favMut.mutate({ id: r.id, isFavorite: r.isFavorite })
  }

  // ---- Detail view ----
  if (selectedId !== null) {
    if (loadDetail || !detail) return <PageLoader />
    return (
      <div>
        <button
          className="flex items-center gap-2 text-sm text-gray-500 hover:text-gray-900 mb-4"
          onClick={() => { setSelectedId(null); setEditTarget(null) }}
        >
          <i className="bi bi-arrow-left me-1" /> Назад к списку
        </button>

        {admin && editTarget?.id === detail.id ? (
          <ResourceForm
            categories={categories ?? []}
            initial={detail}
            loading={updateMut.isPending}
            onSave={data => updateMut.mutate({ id: detail.id, data })}
            onCancel={() => setEditTarget(null)}
          />
        ) : (
          <div className="max-w-3xl">
            <div className="flex items-start justify-between mb-3">
              <span className="badge badge-blue">{detail.categoryName}</span>
              <button
                className={`flex items-center gap-1.5 text-sm font-medium transition-colors ${
                  detail.isFavorite
                    ? 'text-yellow-500 hover:text-yellow-700'
                    : 'text-gray-400 hover:text-yellow-500'
                }`}
                onClick={() => favMut.mutate({ id: detail.id, isFavorite: detail.isFavorite })}
                disabled={favMut.isPending}
                title={detail.isFavorite ? 'Убрать из избранного' : 'Добавить в избранное'}
              >
                <i className={`bi ${detail.isFavorite ? 'bi-star-fill' : 'bi-star'} text-lg`} />
                {detail.isFavorite ? 'В избранном' : 'В избранное'}
              </button>
            </div>

            {admin && (
              <div className="flex gap-2 mb-3">
                <button className="btn-secondary text-sm" onClick={() => setEditTarget(detail)}>
                  <i className="bi bi-pencil me-1" /> Редактировать
                </button>
                <button
                  className="btn-danger text-sm"
                  onClick={() => { removeMut.mutate(detail.id); setSelectedId(null) }}
                >
                  <i className="bi bi-trash me-1" /> Удалить
                </button>
              </div>
            )}

            <h1 className="text-2xl font-bold mb-4">{detail.title}</h1>
            <div className="card">
              <div className="prose prose-sm max-w-none text-gray-700 whitespace-pre-wrap">{detail.content}</div>
            </div>

            {detail.attachments?.length > 0 && (
              <div className="card mt-4">
                <h3 className="text-sm font-semibold mb-3 text-gray-700">Вложения</h3>
                <div className="space-y-2">
                  {detail.attachments.map(a => (
                    <div key={a.id} className="flex items-center justify-between">
                      <a
                        href={`/api/file/${a.fileUrl}`}
                        target="_blank"
                        rel="noopener"
                        className="flex items-center gap-2 text-sm text-blue-600 hover:underline"
                      >
                        {a.fileType === 'IMAGE'
                          ? <i className="bi bi-image text-blue-500" />
                          : <i className="bi bi-file-earmark-text text-gray-500" />}
                        {a.fileName}
                      </a>
                      {admin && (
                        <button
                          className="text-xs text-red-500 hover:text-red-700"
                          onClick={() => resourcesApi.deleteAttachment(a.id).then(() => qc.invalidateQueries({ queryKey: ['resource', detail.id] }))}
                        >
                          Удалить
                        </button>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    )
  }

  // ---- List view ----
  const activeList   = tab === 'all'       ? data    : favData
  const activeLoad   = tab === 'all'       ? isLoading : loadFav
  const activePage   = tab === 'all'       ? page    : favPage
  const setActivePage = tab === 'all'      ? setPage : setFavPage

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Финансовая грамотность</h1>
        {admin && (
          <div className="flex gap-2">
            <button className="btn-secondary text-sm" onClick={() => setShowCatMgr(v => !v)}>
              <i className="bi bi-tags me-1" /> Категории
            </button>
            <button className="btn-primary text-sm" onClick={() => setShowCreate(true)}>
              + Новый материал
            </button>
          </div>
        )}
      </div>

      {/* Tabs */}
      <div className="flex gap-2 mb-6">
        <button
          className={`btn text-sm ${tab === 'all' ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => setTab('all')}
        >
          <i className="bi bi-collection me-1" /> Все материалы
        </button>
        <button
          className={`btn text-sm ${tab === 'favorites' ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => setTab('favorites')}
        >
          <i className="bi bi-star me-1" /> Избранное
        </button>
      </div>

      {/* Category manager (admin, all tab only) */}
      {admin && tab === 'all' && showCatMgr && (
        <CategoryManager
          categories={categories ?? []}
          onCreate={name => createCat.mutate(name)}
          onDelete={id => removeCat.mutate(id)}
          loading={createCat.isPending || removeCat.isPending}
          createError={createCat.error ? parseApiError(createCat.error) : ''}
        />
      )}

      {/* Create form (admin, all tab only) */}
      {admin && tab === 'all' && showCreate && (
        <div className="card mb-6">
          <h2 className="text-base font-semibold mb-4">Новый материал</h2>
          {mutError && <p className="error-text mb-3">{mutError}</p>}
          <ResourceForm
            categories={categories ?? []}
            loading={createMut.isPending}
            onSave={data => createMut.mutate(data as any)}
            onCancel={() => setShowCreate(false)}
          />
        </div>
      )}

      {/* Filters (all tab only) */}
      {tab === 'all' && (
        <div className="flex flex-wrap gap-3 mb-6">
          <input
            className="input w-64"
            type="text"
            placeholder="Поиск по заголовку…"
            value={titleSearch}
            onChange={e => { setTitleSearch(e.target.value); setPage(0) }}
          />
          <select
            className="input w-auto"
            value={categoryId ?? ''}
            onChange={e => { setCategoryId(e.target.value ? +e.target.value : undefined); setPage(0) }}
          >
            <option value="">Все категории</option>
            {categories?.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
        </div>
      )}

      {/* Grid */}
      {activeLoad ? <PageLoader /> : !activeList?.content?.length ? (
        <EmptyState
          message={tab === 'favorites' ? 'Нет избранных материалов. Нажмите ★ на карточке, чтобы добавить.' : 'Материалов не найдено'}
        />
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {activeList.content.map(r => (
              <div
                key={r.id}
                className="card cursor-pointer hover:shadow-md transition-shadow relative"
                onClick={() => setSelectedId(r.id)}
              >
                {/* Admin controls */}
                {admin && tab === 'all' && (
                  <div className="absolute top-2 right-10 flex gap-1" onClick={e => e.stopPropagation()}>
                    <button
                      className="p-1 text-gray-400 hover:text-blue-600"
                      title="Редактировать"
                      onClick={() => { setSelectedId(r.id); setEditTarget(r) }}
                    >
                      <i className="bi bi-pencil" />
                    </button>
                    <button
                      className="p-1 text-gray-400 hover:text-red-600"
                      title="Удалить"
                      onClick={() => removeMut.mutate(r.id)}
                    >
                      <i className="bi bi-trash" />
                    </button>
                  </div>
                )}

                {/* Favorite star */}
                <button
                  className={`absolute top-2 right-2 p-1 transition-colors ${
                    r.isFavorite ? 'text-yellow-400 hover:text-yellow-600' : 'text-gray-300 hover:text-yellow-400'
                  }`}
                  title={r.isFavorite ? 'Убрать из избранного' : 'Добавить в избранное'}
                  onClick={e => handleFavToggle(e, r)}
                  disabled={favMut.isPending}
                >
                  <i className={`bi ${r.isFavorite ? 'bi-star-fill' : 'bi-star'}`} />
                </button>

                <span className="badge badge-blue mb-2 text-xs">{r.categoryName}</span>
                <h3 className="font-semibold text-gray-900 mb-2 line-clamp-2 pr-8">{r.title}</h3>
                <p className="text-sm text-gray-500 line-clamp-3">{r.content}</p>
                {r.attachments?.length > 0 && (
                  <p className="text-xs text-gray-400 mt-2">
                    <i className="bi bi-paperclip me-1" />{r.attachments.length} вложений
                  </p>
                )}
              </div>
            ))}
          </div>
          <Pagination page={activePage} totalPages={activeList.page.totalPages} onPage={setActivePage} />
        </>
      )}
    </div>
  )
}

// ---- Sub-components ----

function ResourceForm({ categories, initial, loading, onSave, onCancel }: {
  categories: { id: number; name: string }[]
  initial?: FinancialResourceResponse
  loading: boolean
  onSave: (data: ResourceForm) => void
  onCancel: () => void
}) {
  const { register, handleSubmit, formState: { errors } } = useForm<ResourceForm>({
    defaultValues: initial
      ? { categoryId: initial.categoryId, title: initial.title, content: initial.content }
      : {},
  })
  return (
    <form onSubmit={handleSubmit(onSave)} className="space-y-3">
      <div>
        <label className="label">Категория</label>
        <select className="input" {...register('categoryId', { required: true, valueAsNumber: true })}>
          <option value="">— выберите —</option>
          {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
        </select>
        {errors.categoryId && <p className="error-text">Выберите категорию</p>}
      </div>
      <div>
        <label className="label">Заголовок</label>
        <input className="input" {...register('title', { required: 'Введите заголовок' })} />
        {errors.title && <p className="error-text">{errors.title.message}</p>}
      </div>
      <div>
        <label className="label">Текст</label>
        <textarea className="input min-h-[180px]" {...register('content', { required: 'Введите текст' })} />
        {errors.content && <p className="error-text">{errors.content.message}</p>}
      </div>
      <div className="flex gap-2">
        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Сохранение…' : 'Сохранить'}
        </button>
        <button type="button" className="btn-secondary" onClick={onCancel}>Отмена</button>
      </div>
    </form>
  )
}

function CategoryManager({ categories, onCreate, onDelete, loading, createError }: {
  categories: { id: number; name: string }[]
  onCreate: (name: string) => void
  onDelete: (id: number) => void
  loading: boolean
  createError?: string
}) {
  const { register, handleSubmit, reset } = useForm<CategoryForm>()
  return (
    <div className="card mb-6">
      <h2 className="text-base font-semibold mb-4">Управление категориями</h2>
      <form onSubmit={handleSubmit(d => { onCreate(d.name); reset() })} className="flex gap-2 mb-4">
        <input
          className="input flex-1"
          placeholder="Название новой категории"
          {...register('name', { required: true })}
        />
        <button type="submit" className="btn-primary" disabled={loading}>Добавить</button>
      </form>
      {createError && <p className="error-text mb-2">{createError}</p>}
      <div className="space-y-2">
        {categories.map(c => (
          <div key={c.id} className="flex items-center justify-between text-sm">
            <span>{c.name}</span>
            <button className="text-red-500 hover:text-red-700 text-xs" onClick={() => onDelete(c.id)}>
              Удалить
            </button>
          </div>
        ))}
      </div>
    </div>
  )
}
