import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { contactsApi } from '../api/contacts'
import PageLoader from '../components/common/PageLoader'
import EmptyState from '../components/common/EmptyState'
import Spinner from '../components/common/Spinner'
import { parseApiError } from '../utils/errorUtils'

export default function ContactsPage() {
  const qc = useQueryClient()
  const [tab, setTab] = useState<'contacts' | 'pending' | 'search'>('contacts')
  const [searchQuery, setSearchQuery] = useState('')

  // Track per-row send state: profileId → 'pending' | 'sent' | 'error'
  const [sendState, setSendState] = useState<Record<number, 'pending' | 'sent' | 'error'>>({})
  const [sendError, setSendError] = useState('')

  const { data: contacts, isLoading: loadContacts } = useQuery({
    queryKey: ['contacts'],
    queryFn: () => contactsApi.getAll().then(r => r.data),
    enabled: tab === 'contacts',
  })

  const { data: pending, isLoading: loadPending } = useQuery({
    queryKey: ['contacts', 'pending'],
    queryFn: () => contactsApi.getPending().then(r => r.data),
    enabled: tab === 'pending',
  })

  const { data: searchResults, isLoading: loadSearch } = useQuery({
    queryKey: ['contacts', 'search', searchQuery],
    queryFn: () => contactsApi.search(searchQuery).then(r => r.data),
    enabled: tab === 'search' && searchQuery.length >= 2,
  })

  const invalidate = () => qc.invalidateQueries({ queryKey: ['contacts'] })

  const acceptMutation   = useMutation({ mutationFn: contactsApi.accept,        onSuccess: invalidate })
  const declineMutation  = useMutation({ mutationFn: contactsApi.decline,       onSuccess: invalidate })
  const removeMutation   = useMutation({ mutationFn: contactsApi.remove,        onSuccess: invalidate })
  const cancelMutation   = useMutation({ mutationFn: contactsApi.cancelRequest, onSuccess: invalidate })

  const handleSend = async (profileId: number, email: string) => {
    setSendState(s => ({ ...s, [profileId]: 'pending' }))
    setSendError('')
    try {
      await contactsApi.sendRequest(email)
      setSendState(s => ({ ...s, [profileId]: 'sent' }))
      invalidate()
    } catch (e) {
      setSendState(s => ({ ...s, [profileId]: 'error' }))
      setSendError(parseApiError(e))
    }
  }

  const incoming = pending?.content?.filter(c => !c.isRequester) ?? []
  const outgoing = pending?.content?.filter(c => c.isRequester) ?? []
  const incomingCount = incoming.length

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Контакты</h1>
      </div>

      <div className="flex gap-2 mb-6">
        {(['contacts', 'pending', 'search'] as const).map(t => (
          <button
            key={t}
            className={`btn text-sm relative ${tab === t ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setTab(t)}
          >
            {t === 'contacts' ? 'Мои контакты'
              : t === 'pending' ? (
                <>
                  Заявки
                  {incomingCount > 0 && (
                    <span className="ml-2 inline-flex items-center justify-center min-w-[18px] h-[18px] rounded-full bg-red-500 text-white text-[10px] font-bold px-1 leading-none">
                      {incomingCount > 99 ? '99+' : incomingCount}
                    </span>
                  )}
                </>
              )
              : <><i className="bi bi-search me-1" />Поиск</>}
          </button>
        ))}
      </div>

      {/* ---- My contacts ---- */}
      {tab === 'contacts' && (
        loadContacts ? <PageLoader /> : !contacts?.content?.length ? (
          <EmptyState message="Контактов нет. Найдите людей через поиск." action={{ label: 'Найти', onClick: () => setTab('search') }} />
        ) : (
          <div className="space-y-2">
            {contacts.content.map(c => (
              <div key={c.id} className="card flex items-center justify-between p-4">
                <div>
                  <p className="font-medium">{c.firstName} {c.lastName}</p>
                  <p className="text-sm text-gray-400">{c.email}</p>
                </div>
                <button
                  className="btn-secondary text-xs"
                  onClick={() => removeMutation.mutate(c.id)}
                  disabled={removeMutation.isPending}
                >
                  Удалить
                </button>
              </div>
            ))}
          </div>
        )
      )}

      {/* ---- Pending requests ---- */}
      {tab === 'pending' && (
        loadPending ? <PageLoader /> : !pending?.content?.length ? (
          <EmptyState message="Заявок нет" />
        ) : (
          <div className="space-y-4">
            {/* Incoming */}
            {incoming.length > 0 && (
              <div>
                <p className="text-xs font-semibold text-gray-400 uppercase tracking-wide mb-2">Входящие</p>
                <div className="space-y-2">
                  {incoming.map(c => (
                    <div key={c.id} className="card flex items-center justify-between p-4">
                      <div>
                        <p className="font-medium">
                          {c.firstName || c.lastName
                            ? `${c.firstName ?? ''} ${c.lastName ?? ''}`.trim()
                            : c.email}
                        </p>
                        <p className="text-sm text-gray-400">{c.email}</p>
                      </div>
                      <div className="flex gap-2">
                        <button
                          className="btn-primary text-xs"
                          onClick={() => acceptMutation.mutate(c.id)}
                          disabled={acceptMutation.isPending}
                        >
                          Принять
                        </button>
                        <button
                          className="btn-secondary text-xs"
                          onClick={() => declineMutation.mutate(c.id)}
                          disabled={declineMutation.isPending}
                        >
                          Отклонить
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Outgoing */}
            {outgoing.length > 0 && (
              <div>
                <p className="text-xs font-semibold text-gray-400 uppercase tracking-wide mb-2">Исходящие</p>
                <div className="space-y-2">
                  {outgoing.map(c => (
                    <div key={c.id} className="card flex items-center justify-between p-4">
                      <div>
                        <p className="font-medium">
                          {c.firstName || c.lastName
                            ? `${c.firstName ?? ''} ${c.lastName ?? ''}`.trim()
                            : c.email}
                        </p>
                        <p className="text-sm text-gray-400">{c.email}</p>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className="badge badge-yellow">Ожидает ответа</span>
                        <button
                          className="text-xs text-red-400 hover:text-red-600"
                          onClick={() => cancelMutation.mutate(c.id)}
                          disabled={cancelMutation.isPending}
                        >
                          Отменить
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )
      )}

      {/* ---- Search ---- */}
      {tab === 'search' && (
        <div>
          <div className="flex gap-3 mb-4">
            <input
              className="input"
              type="text"
              placeholder="Имя, email или код друга…"
              value={searchQuery}
              onChange={e => { setSearchQuery(e.target.value); setSendError('') }}
            />
          </div>
          {sendError && <p className="error-text mb-3">{sendError}</p>}
          {loadSearch ? <PageLoader /> : !searchResults?.content?.length ? (
            searchQuery.length >= 2
              ? <EmptyState message="Никого не найдено" />
              : <p className="text-gray-400 text-sm">Введите минимум 2 символа</p>
          ) : (
            <div className="space-y-2">
              {searchResults.content.map(p => {
                const state = sendState[p.profileId]
                const isSent = state === 'sent'
                const isPending = state === 'pending'
                return (
                  <div key={p.profileId} className="card flex items-center justify-between p-4">
                    <div>
                      <p className="font-medium">{p.firstName} {p.lastName}</p>
                      <p className="text-sm text-gray-400">{p.email}</p>
                    </div>
                    {isSent ? (
                      <span className="badge badge-green text-xs">
                        <i className="bi bi-check-lg me-1" />Заявка отправлена
                      </span>
                    ) : (
                      <button
                        className="btn-primary text-xs"
                        onClick={() => handleSend(p.profileId, p.email)}
                        disabled={isPending}
                      >
                        {isPending ? <Spinner size="sm" /> : 'Добавить'}
                      </button>
                    )}
                  </div>
                )
              })}
            </div>
          )}
        </div>
      )}
    </div>
  )
}
