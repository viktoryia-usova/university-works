import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { sharedSheetsApi } from '../api/sharedSheets'
import { currenciesApi } from '../api/currencies'
import { contactsApi } from '../api/contacts'
import { profileApi } from '../api/profile'
import PageLoader from '../components/common/PageLoader'
import Modal from '../components/common/Modal'
import Spinner from '../components/common/Spinner'
import { parseApiError } from '../utils/errorUtils'

const fmt = (n: number, cur = 'BYN') =>
  new Intl.NumberFormat('ru-BY', { style: 'currency', currency: cur, maximumFractionDigits: 2 }).format(n)

type Tab = 'transactions' | 'goals' | 'analytics' | 'members'

export default function SharedSheetDetail({ sheetId, onBack }: { sheetId: number; onBack: () => void }) {
  const qc = useQueryClient()
  const [tab, setTab] = useState<Tab>('transactions')
  const [showAddTx, setShowAddTx] = useState(false)
  const [showAddGoal, setShowAddGoal] = useState(false)
  const [showInvite, setShowInvite] = useState(false)
  const [showEdit, setShowEdit] = useState(false)

  const { data: sheet, isLoading } = useQuery({
    queryKey: ['shared-sheet', sheetId],
    queryFn: () => sharedSheetsApi.getById(sheetId).then(r => r.data),
  })

  const { data: transactions } = useQuery({
    queryKey: ['shared-sheet', sheetId, 'transactions'],
    queryFn: () => sharedSheetsApi.getTransactions(sheetId).then(r => r.data),
    enabled: tab === 'transactions',
  })

  const { data: goals } = useQuery({
    queryKey: ['shared-sheet', sheetId, 'goals'],
    queryFn: () => sharedSheetsApi.getGoals(sheetId).then(r => r.data),
    enabled: tab === 'goals',
  })

  const { data: analytics } = useQuery({
    queryKey: ['shared-sheet', sheetId, 'analytics'],
    queryFn: () => sharedSheetsApi.getAnalytics(sheetId).then(r => r.data),
    enabled: tab === 'analytics',
  })

  const { data: supported } = useQuery({
    queryKey: ['currencies', 'supported'],
    queryFn: () => currenciesApi.getSupported().then(r => r.data),
  })

  const { data: myProfile } = useQuery({
    queryKey: ['profile'],
    queryFn: () => profileApi.getMe().then(r => r.data),
  })

  const deleteTx = useMutation({
    mutationFn: (txId: number) => sharedSheetsApi.deleteTransaction(sheetId, txId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['shared-sheet', sheetId, 'transactions'] }),
  })

  const contributeGoal = useMutation({
    mutationFn: ({ goalId, amount }: { goalId: number; amount: number }) =>
      sharedSheetsApi.contributeToGoal(sheetId, goalId, amount),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['shared-sheet', sheetId, 'goals'] }),
  })

  const [leaveError, setLeaveError] = useState('')

  const leaveMutation = useMutation({
    mutationFn: () => sharedSheetsApi.leave(sheetId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['shared-sheets'] })
      onBack()
    },
    onError: (e) => setLeaveError(parseApiError(e)),
  })

  const deleteMutation = useMutation({
    mutationFn: () => sharedSheetsApi.remove(sheetId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['shared-sheets'] })
      onBack()
    },
    onError: (e) => setLeaveError(parseApiError(e)),
  })

  const updateMutation = useMutation({
    mutationFn: (data: { name: string; description?: string }) =>
      sharedSheetsApi.update(sheetId, data.name, data.description),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['shared-sheet', sheetId] })
      qc.invalidateQueries({ queryKey: ['shared-sheets'] })
      setShowEdit(false)
    },
  })

  if (isLoading || !sheet) return <PageLoader />

  const isCreator = !!myProfile && myProfile.id === sheet.creatorProfileId

  return (
    <div>
      <button className="flex items-center gap-2 text-sm text-gray-500 hover:text-gray-900 mb-4" onClick={onBack}>
        <i className="bi bi-arrow-left" /> Назад
      </button>

      <div className="flex items-start justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold">{sheet.name}</h1>
          {sheet.description && <p className="text-gray-500 text-sm mt-1">{sheet.description}</p>}
        </div>
        <div className="flex gap-2 items-center flex-wrap justify-end">
          <span className={`badge ${sheet.status === 'ACTIVE' ? 'badge-green' : 'badge-gray'}`}>
            {sheet.status === 'ACTIVE' ? 'Активен' : 'Завершён'}
          </span>
          {isCreator && (
            <button className="btn-secondary text-xs" onClick={() => setShowEdit(true)}>
              <i className="bi bi-pencil me-1" />Редактировать
            </button>
          )}
          {isCreator ? (
            <button
              className="btn-secondary text-xs text-red-500 hover:text-red-700"
              onClick={() => { setLeaveError(''); deleteMutation.mutate() }}
              disabled={deleteMutation.isPending}
            >
              <i className="bi bi-trash me-1" />Удалить лист
            </button>
          ) : sheet.status === 'ACTIVE' && (
            <button className="btn-secondary text-xs" onClick={() => { setLeaveError(''); leaveMutation.mutate() }}
              disabled={leaveMutation.isPending}>
              Покинуть
            </button>
          )}
        </div>
      </div>
      {leaveError && <p className="error-text mb-4">{leaveError}</p>}

      <div className="flex gap-2 mb-6 border-b border-gray-200 pb-0">
        {(['transactions', 'goals', 'analytics', 'members'] as Tab[]).map(t => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`px-4 py-2 text-sm font-medium border-b-2 -mb-px transition-colors ${
              tab === t ? 'border-blue-600 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            {t === 'transactions' ? 'Операции' : t === 'goals' ? 'Цели' : t === 'analytics' ? 'Аналитика' : 'Участники'}
          </button>
        ))}
      </div>

      {/* Transactions */}
      {tab === 'transactions' && (
        <div>
          <div className="flex justify-end mb-4">
            {sheet.status === 'ACTIVE' && (
              <button className="btn-primary text-sm" onClick={() => setShowAddTx(true)}>+ Операция</button>
            )}
          </div>
          {!transactions?.content?.length ? (
            <p className="text-gray-400 text-sm text-center py-8">Операций нет</p>
          ) : (
            <div className="card p-0 overflow-hidden">
              <table className="w-full text-sm">
                <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
                  <tr>
                    <th className="px-4 py-3 text-left">Дата</th>
                    <th className="px-4 py-3 text-left">Автор</th>
                    <th className="px-4 py-3 text-left">Тип</th>
                    <th className="px-4 py-3 text-left">Описание</th>
                    <th className="px-4 py-3 text-right">Сумма BYN</th>
                    <th className="px-4 py-3" />
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-50">
                  {transactions.content.map(tx => (
                    <tr key={tx.id} className="hover:bg-gray-50">
                      <td className="px-4 py-3 text-gray-500">{tx.date}</td>
                      <td className="px-4 py-3">{tx.authorFirstName}</td>
                      <td className="px-4 py-3">
                        <span className={`badge ${tx.type === 'CONTRIBUTION' ? 'badge-green' : 'badge-red'}`}>
                          <i className={`bi ${tx.type === 'CONTRIBUTION' ? 'bi-arrow-up' : 'bi-arrow-down'} me-1`} />
                          {tx.type === 'CONTRIBUTION' ? 'Взнос' : 'Расход'}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-gray-500">{tx.description || '—'}</td>
                      <td className={`px-4 py-3 text-right font-semibold ${tx.type === 'CONTRIBUTION' ? 'text-green-600' : 'text-red-600'}`}>
                        {tx.type === 'CONTRIBUTION' ? '+' : '-'}{fmt(tx.amountByn)}
                      </td>
                      <td className="px-4 py-3 text-right">
                        <button className="text-red-400 hover:text-red-600" title="Удалить" onClick={() => deleteTx.mutate(tx.id)}>
                          <i className="bi bi-x-lg" />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Goals */}
      {tab === 'goals' && (
        <div>
          <div className="flex justify-end mb-4">
            {sheet.status === 'ACTIVE' && (
              <button className="btn-primary text-sm" onClick={() => setShowAddGoal(true)}>+ Цель</button>
            )}
          </div>
          {!goals?.content?.length ? (
            <p className="text-gray-400 text-sm text-center py-8">Целей нет</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {goals.content.map(g => (
                <div key={g.id} className="card">
                  <div className="flex justify-between items-start mb-3">
                    <h3 className="font-semibold">{g.name}</h3>
                    <span className={`badge ${g.status === 'ACTIVE' ? 'badge-blue' : 'badge-green'}`}>
                      {g.status === 'ACTIVE' ? 'Активна' : 'Выполнена'}
                    </span>
                  </div>
                  <div className="flex justify-between text-sm mb-1">
                    <span className="text-gray-500">{fmt(g.currentAmount, g.currency)}</span>
                    <span className="font-medium">{fmt(g.targetAmount, g.currency)}</span>
                  </div>
                  <div className="w-full bg-gray-100 rounded-full h-2 mb-3">
                    <div className="bg-blue-500 h-2 rounded-full" style={{ width: `${Math.min(g.progressPercent, 100)}%` }} />
                  </div>
                  {g.status === 'ACTIVE' && sheet.status === 'ACTIVE' && (
                    <ContributeInline
                      onContribute={amt => contributeGoal.mutate({ goalId: g.id, amount: amt })}
                    />
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Analytics */}
      {tab === 'analytics' && (
        <div>
          {!analytics ? <PageLoader /> : (() => {
            const n = analytics.memberCount || 1
            const spentPerPerson       = analytics.totalSpentByn       / n
            const contributedPerPerson = analytics.totalContributedByn / n
            const balance              = analytics.balanceByn
            return (
              <>
              <div className="grid grid-cols-5 gap-4">
                <div className="card border-l-4 border-green-500">
                  <p className="text-xs text-gray-500 mb-1">Всего внесено</p>
                  <p className="text-2xl font-bold text-green-600">{fmt(analytics.totalContributedByn)}</p>
                  <p className="text-xs text-gray-400 mt-1">{analytics.memberCount} участн.</p>
                </div>
                <div className="card border-l-4 border-red-500">
                  <p className="text-xs text-gray-500 mb-1">Всего потрачено</p>
                  <p className="text-2xl font-bold text-red-600">{fmt(analytics.totalSpentByn)}</p>
                  <p className="text-xs text-gray-400 mt-1">{analytics.memberCount} участн.</p>
                </div>
                <div className={`card border-l-4 ${balance >= 0 ? 'border-blue-500' : 'border-orange-500'}`}>
                  <p className="text-xs text-gray-500 mb-1">Баланс</p>
                  <p className={`text-2xl font-bold ${balance >= 0 ? 'text-blue-600' : 'text-orange-600'}`}>
                    {fmt(balance)}
                  </p>
                  <p className="text-xs text-gray-400 mt-1">внесено − потрачено</p>
                </div>
                <div className="card border-l-4 border-red-300">
                  <p className="text-xs text-gray-500 mb-1">Доля расходов на человека</p>
                  <p className="text-2xl font-bold text-red-500">{fmt(spentPerPerson)}</p>
                  <p className="text-xs text-gray-400 mt-1">потрачено ÷ {analytics.memberCount}</p>
                </div>
                <div className="card border-l-4 border-green-300">
                  <p className="text-xs text-gray-500 mb-1">Доля внесений на человека</p>
                  <p className="text-2xl font-bold text-green-500">{fmt(contributedPerPerson)}</p>
                  <p className="text-xs text-gray-400 mt-1">внесено ÷ {analytics.memberCount}</p>
                </div>
              </div>

              {analytics.memberContributions?.length > 0 && (
                <div className="card mt-4">
                  <h2 className="text-base font-semibold mb-4">Вклад участников</h2>
                  <table className="w-full text-sm">
                    <thead className="text-gray-400 text-xs uppercase border-b">
                      <tr>
                        <th className="pb-2 text-left">Участник</th>
                        <th className="pb-2 text-right">Внёс</th>
                        <th className="pb-2 text-right">Потратил</th>
                        <th className="pb-2 text-right">Баланс</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-50">
                      {analytics.memberContributions.map(m => (
                        <tr key={m.profileId}>
                          <td className="py-2">{m.firstName} <span className="text-gray-400 text-xs">{m.email}</span></td>
                          <td className="py-2 text-right text-green-600">{fmt(m.contributedByn)}</td>
                          <td className="py-2 text-right text-red-600">{fmt(m.spentByn)}</td>
                          <td className={`py-2 text-right font-semibold ${m.netByn >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                            {m.netByn >= 0 ? '+' : ''}{fmt(m.netByn)}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
              </>
            )
          })()}
        </div>
      )}

      {/* Members */}
      {tab === 'members' && (
        <div>
          {sheet.status === 'ACTIVE' && (
            <div className="flex justify-end mb-4">
              <button className="btn-primary text-sm" onClick={() => setShowInvite(true)}>+ Пригласить</button>
            </div>
          )}
          <div className="space-y-2">
            {sheet.members.map(m => (
              <div key={m.id} className="card flex items-center justify-between p-4">
                <div>
                  <p className="font-medium">{m.firstName} {m.lastName}</p>
                  <p className="text-sm text-gray-400">{m.email}</p>
                </div>
                {m.profileId === sheet.creatorProfileId && (
                  <span className="badge badge-blue">Создатель</span>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {showAddTx && (
        <AddTransactionModal
          supported={supported ?? ['BYN']}
          defaultCurrency={myProfile?.defaultCurrency ?? 'BYN'}
          onClose={() => setShowAddTx(false)}
          onDone={() => { qc.invalidateQueries({ queryKey: ['shared-sheet', sheetId, 'transactions'] }); setShowAddTx(false) }}
          sheetId={sheetId}
        />
      )}

      {showAddGoal && (
        <AddGoalModal
          supported={supported ?? ['BYN']}
          defaultCurrency={myProfile?.defaultCurrency ?? 'BYN'}
          onClose={() => setShowAddGoal(false)}
          onDone={() => { qc.invalidateQueries({ queryKey: ['shared-sheet', sheetId, 'goals'] }); setShowAddGoal(false) }}
          sheetId={sheetId}
        />
      )}

      {showInvite && (
        <InviteMemberModal
          sheetId={sheetId}
          existingProfileIds={sheet.members.map(m => m.profileId)}
          onClose={() => setShowInvite(false)}
          onDone={() => { qc.invalidateQueries({ queryKey: ['shared-sheet', sheetId] }); setShowInvite(false) }}
        />
      )}

      {showEdit && (
        <EditSheetModal
          currentName={sheet.name}
          currentDescription={sheet.description}
          onClose={() => setShowEdit(false)}
          onSave={(name, description) => updateMutation.mutate({ name, description })}
          loading={updateMutation.isPending}
          error={updateMutation.isError ? parseApiError(updateMutation.error) : ''}
        />
      )}
    </div>
  )
}

function ContributeInline({ onContribute }: { onContribute: (amt: number) => void }) {
  const [val, setVal] = useState('')
  return (
    <div className="flex gap-2">
      <input className="input text-sm" type="number" step="0.01" min="0" placeholder="Сумма" value={val} onChange={e => setVal(e.target.value)} />
      <button className="btn-primary text-sm px-3" onClick={() => { if (val) { onContribute(Number(val)); setVal('') } }}>+</button>
    </div>
  )
}

function AddTransactionModal({ supported, defaultCurrency, onClose, onDone, sheetId }: { supported: string[]; defaultCurrency: string; onClose: () => void; onDone: () => void; sheetId: number }) {
  const { register, handleSubmit } = useForm({
    defaultValues: { currency: defaultCurrency, type: 'EXPENSE', date: new Date().toLocaleDateString('en-CA') }
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const onSubmit = async (data: any) => {
    setLoading(true)
    try { await sharedSheetsApi.addTransaction(sheetId, { ...data, amount: Number(data.amount) }); onDone() }
    catch (e) { setError(parseApiError(e)) }
    finally { setLoading(false) }
  }

  return (
    <Modal title="Новая операция" onClose={onClose}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="label">Тип</label>
          <select className="input" {...register('type')}>
            <option value="EXPENSE">Расход</option>
            <option value="CONTRIBUTION">Взнос</option>
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
        <div>
          <label className="label">Дата</label>
          <input className="input" type="date" {...register('date', { required: true })} />
        </div>
        <div>
          <label className="label">Описание</label>
          <input className="input" type="text" {...register('description')} />
        </div>
        {error && <p className="error-text">{error}</p>}
        <div className="flex gap-3 pt-2">
          <button type="button" className="btn-secondary flex-1" onClick={onClose}>Отмена</button>
          <button type="submit" className="btn-primary flex-1" disabled={loading}>{loading ? <Spinner size="sm" /> : 'Добавить'}</button>
        </div>
      </form>
    </Modal>
  )
}

function InviteMemberModal({ sheetId, existingProfileIds, onClose, onDone }: {
  sheetId: number
  existingProfileIds: number[]
  onClose: () => void
  onDone: () => void
}) {
  const { data: contacts, isLoading } = useQuery({
    queryKey: ['contacts'],
    queryFn: () => contactsApi.getAll(0, 100).then(r => r.data),
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const available = contacts?.content?.filter(c => !existingProfileIds.includes(c.profileId)) ?? []

  const invite = async (profileId: number) => {
    setLoading(true)
    setError('')
    try { await sharedSheetsApi.invite(sheetId, profileId); onDone() }
    catch (e) { setError(parseApiError(e)) }
    finally { setLoading(false) }
  }

  return (
    <Modal title="Пригласить участника" onClose={onClose}>
      {isLoading ? <PageLoader /> : !available.length ? (
        <p className="text-gray-400 text-sm text-center py-4">Нет доступных контактов для приглашения</p>
      ) : (
        <div className="space-y-2">
          {available.map(c => (
            <div key={c.id} className="flex items-center justify-between p-3 rounded-lg border border-gray-100 hover:bg-gray-50">
              <div>
                <p className="font-medium text-sm">{c.firstName} {c.lastName}</p>
                <p className="text-xs text-gray-400">{c.email}</p>
              </div>
              <button className="btn-primary text-xs px-3" disabled={loading} onClick={() => invite(c.profileId)}>
                {loading ? <Spinner size="sm" /> : 'Пригласить'}
              </button>
            </div>
          ))}
        </div>
      )}
      {error && <p className="error-text mt-3">{error}</p>}
      <div className="flex justify-end mt-4">
        <button className="btn-secondary text-sm" onClick={onClose}>Закрыть</button>
      </div>
    </Modal>
  )
}

function EditSheetModal({ currentName, currentDescription, onClose, onSave, loading, error }: {
  currentName: string
  currentDescription?: string
  onClose: () => void
  onSave: (name: string, description?: string) => void
  loading: boolean
  error: string
}) {
  const { register, handleSubmit } = useForm({
    defaultValues: { name: currentName, description: currentDescription ?? '' },
  })

  const onSubmit = (data: { name: string; description?: string }) => {
    onSave(data.name, data.description || undefined)
  }

  return (
    <Modal title="Редактировать лист" onClose={onClose}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="label">Название</label>
          <input className="input" type="text" {...register('name', { required: true })} />
        </div>
        <div>
          <label className="label">Описание</label>
          <input className="input" type="text" placeholder="Необязательно" {...register('description')} />
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

function AddGoalModal({ supported, defaultCurrency, onClose, onDone, sheetId }: { supported: string[]; defaultCurrency: string; onClose: () => void; onDone: () => void; sheetId: number }) {
  const { register, handleSubmit } = useForm({ defaultValues: { currency: defaultCurrency } })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const onSubmit = async (data: any) => {
    setLoading(true)
    try { await sharedSheetsApi.createGoal(sheetId, { ...data, targetAmount: Number(data.targetAmount) }); onDone() }
    catch (e) { setError(parseApiError(e)) }
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
            <label className="label">Сумма</label>
            <input className="input" type="number" step="0.01" min="0" {...register('targetAmount', { required: true })} />
          </div>
          <div>
            <label className="label">Валюта</label>
            <select className="input" {...register('currency')}>
              {supported.map(c => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>
        </div>
        <div>
          <label className="label">Срок</label>
          <input className="input" type="date" {...register('deadline')} />
        </div>
        {error && <p className="error-text">{error}</p>}
        <div className="flex gap-3 pt-2">
          <button type="button" className="btn-secondary flex-1" onClick={onClose}>Отмена</button>
          <button type="submit" className="btn-primary flex-1" disabled={loading}>{loading ? <Spinner size="sm" /> : 'Создать'}</button>
        </div>
      </form>
    </Modal>
  )
}
