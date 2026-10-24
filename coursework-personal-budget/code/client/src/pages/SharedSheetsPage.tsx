import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { sharedSheetsApi } from '../api/sharedSheets'
import PageLoader from '../components/common/PageLoader'
import EmptyState from '../components/common/EmptyState'
import Modal from '../components/common/Modal'
import Spinner from '../components/common/Spinner'
import type { SharedSheetResponse } from '../types'
import SharedSheetDetail from './SharedSheetDetail'
import { parseApiError } from '../utils/errorUtils'

export default function SharedSheetsPage() {
  const qc = useQueryClient()
  const [showCreate, setShowCreate] = useState(false)
  const [selectedId, setSelectedId] = useState<number | null>(null)

  const { data, isLoading } = useQuery({
    queryKey: ['shared-sheets'],
    queryFn: () => sharedSheetsApi.getAll().then(r => r.data),
  })

  const [name, setName] = useState('')
  const [desc, setDesc] = useState('')
  const [creating, setCreating] = useState(false)
  const [createError, setCreateError] = useState('')

  const handleCreate = async () => {
    if (!name.trim()) return
    setCreating(true)
    setCreateError('')
    try {
      await sharedSheetsApi.create(name, desc)
      qc.invalidateQueries({ queryKey: ['shared-sheets'] })
      setShowCreate(false)
      setName('')
      setDesc('')
    } catch (e) {
      setCreateError(parseApiError(e))
    } finally {
      setCreating(false)
    }
  }

  if (selectedId !== null) {
    return <SharedSheetDetail sheetId={selectedId} onBack={() => setSelectedId(null)} />
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Общие листы</h1>
        <button className="btn-primary" onClick={() => setShowCreate(true)}>+ Создать</button>
      </div>

      {isLoading ? <PageLoader /> : !data?.content?.length ? (
        <EmptyState message="Общих листов нет" action={{ label: '+ Создать', onClick: () => setShowCreate(true) }} />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {data.content.map(s => (
            <div key={s.id} className="card cursor-pointer hover:shadow-md transition-shadow" onClick={() => setSelectedId(s.id)}>
              <div className="flex items-start justify-between mb-2">
                <h3 className="font-semibold">{s.name}</h3>
                <span className={`badge ${s.status === 'ACTIVE' ? 'badge-green' : 'badge-gray'}`}>
                  {s.status === 'ACTIVE' ? 'Активен' : 'Завершён'}
                </span>
              </div>
              {s.description && <p className="text-sm text-gray-500 mb-3">{s.description}</p>}
              <p className="text-xs text-gray-400">{s.members.length} участников · {s.createdAt.slice(0, 10)}</p>
            </div>
          ))}
        </div>
      )}

      {showCreate && (
        <Modal title="Новый общий лист" onClose={() => setShowCreate(false)}>
          <div className="space-y-4">
            <div>
              <label className="label">Название</label>
              <input className="input" value={name} onChange={e => setName(e.target.value)} placeholder="Отпуск, совместный бюджет…" />
            </div>
            <div>
              <label className="label">Описание (необязательно)</label>
              <textarea className="input" rows={2} value={desc} onChange={e => setDesc(e.target.value)} />
            </div>
            {createError && <p className="error-text">{createError}</p>}
            <div className="flex gap-3 pt-2">
              <button className="btn-secondary flex-1" onClick={() => setShowCreate(false)}>Отмена</button>
              <button className="btn-primary flex-1" disabled={creating} onClick={handleCreate}>{creating ? <Spinner size="sm" /> : 'Создать'}</button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  )
}
