import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminApi } from '../api/admin'
import PageLoader from '../components/common/PageLoader'
import Pagination from '../components/common/Pagination'

export default function AdminPage() {
  const qc = useQueryClient()
  const [page, setPage] = useState(0)

  const { data, isLoading } = useQuery({
    queryKey: ['admin', 'users', page],
    queryFn: () => adminApi.getUsers(page, 20).then(r => r.data),
  })

  const blockMutation = useMutation({
    mutationFn: adminApi.blockUser,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin', 'users'] }),
  })

  const unblockMutation = useMutation({
    mutationFn: adminApi.unblockUser,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin', 'users'] }),
  })

  return (
    <div>
      <h1 className="text-2xl font-bold mb-2">Администрирование</h1>
      <p className="text-gray-500 text-sm mb-6">Управление пользователями системы</p>

      {isLoading ? <PageLoader /> : (
        <>
          <div className="card p-0 overflow-hidden">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
                <tr>
                  <th className="px-4 py-3 text-left">ID</th>
                  <th className="px-4 py-3 text-left">Email</th>
                  <th className="px-4 py-3 text-left">Имя</th>
                  <th className="px-4 py-3 text-left">Роль</th>
                  <th className="px-4 py-3 text-left">Статус</th>
                  <th className="px-4 py-3 text-left">Дата</th>
                  <th className="px-4 py-3" />
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {data?.content?.map(u => (
                  <tr key={u.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3 text-gray-400">{u.id}</td>
                    <td className="px-4 py-3 font-medium">{u.username}</td>
                    <td className="px-4 py-3 text-gray-600">
                      {u.firstName || u.lastName ? `${u.firstName ?? ''} ${u.lastName ?? ''}`.trim() : '—'}
                    </td>
                    <td className="px-4 py-3">
                      <span className={`badge ${u.role === 'ADMIN' ? 'badge-red' : 'badge-blue'}`}>{u.role}</span>
                    </td>
                    <td className="px-4 py-3">
                      <span className={`badge ${u.active ? 'badge-green' : 'badge-gray'}`}>
                        {u.active ? 'Активен' : 'Заблокирован'}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-gray-400">{u.createdAt.slice(0, 10)}</td>
                    <td className="px-4 py-3 text-right">
                      {u.active ? (
                        <button
                          className="text-red-500 hover:text-red-700 text-xs"
                          onClick={() => blockMutation.mutate(u.id)}
                          disabled={blockMutation.isPending}
                        >
                          Заблокировать
                        </button>
                      ) : (
                        <button
                          className="text-green-600 hover:text-green-700 text-xs"
                          onClick={() => unblockMutation.mutate(u.id)}
                          disabled={unblockMutation.isPending}
                        >
                          Разблокировать
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div className="flex items-center justify-between mt-4 text-sm text-gray-500">
            <span>Всего: {data?.page?.totalElements ?? 0} пользователей</span>
            <Pagination page={page} totalPages={data?.page?.totalPages ?? 1} onPage={setPage} />
          </div>
        </>
      )}
    </div>
  )
}
