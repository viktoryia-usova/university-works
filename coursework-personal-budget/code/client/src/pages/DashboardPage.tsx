import { useQuery } from '@tanstack/react-query'
import { analyticsApi } from '../api/analytics'
import { profileApi } from '../api/profile'
import { transactionsApi } from '../api/transactions'
import PageLoader from '../components/common/PageLoader'
import { format, startOfMonth, endOfMonth } from 'date-fns'
import { ru } from 'date-fns/locale'

const fmt = (n: number) =>
  new Intl.NumberFormat('ru-BY', { style: 'currency', currency: 'BYN', maximumFractionDigits: 2 }).format(n)

const today = new Date()
const from  = format(startOfMonth(today), 'yyyy-MM-dd')
const to    = format(endOfMonth(today), 'yyyy-MM-dd')

export default function DashboardPage() {
  const { data: profile } = useQuery({
    queryKey: ['profile'],
    queryFn: () => profileApi.getMe().then(r => r.data),
  })

  const { data: summary, isLoading } = useQuery({
    queryKey: ['analytics', 'summary', from, to],
    queryFn: () => analyticsApi.getSummary(from, to).then(r => r.data),
  })

  const { data: recentTx } = useQuery({
    queryKey: ['transactions', 'recent'],
    queryFn: () => transactionsApi.getAll({ size: 5, sort: 'date,desc' }).then(r => r.data),
  })

  if (isLoading) return <PageLoader />

  const name = profile?.firstName ? `${profile.firstName}!` : ''

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-1">
        Привет{name ? `, ${name}` : '!'}
      </h1>
      <p className="text-gray-500 mb-8">
        {format(today, 'LLLL yyyy', { locale: ru })} — обзор вашего бюджета
      </p>

      {/* KPI cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
        <div className="card border-l-4 border-green-500">
          <p className="text-sm text-gray-500 mb-1">Доходы (месяц)</p>
          <p className="text-2xl font-bold text-green-600">{fmt(summary?.totalIncomeByn ?? 0)}</p>
        </div>
        <div className="card border-l-4 border-red-500">
          <p className="text-sm text-gray-500 mb-1">Расходы (месяц)</p>
          <p className="text-2xl font-bold text-red-600">{fmt(summary?.totalExpenseByn ?? 0)}</p>
        </div>
        <div className={`card border-l-4 ${(summary?.balanceByn ?? 0) >= 0 ? 'border-blue-500' : 'border-orange-500'}`}>
          <p className="text-sm text-gray-500 mb-1">Баланс</p>
          <p className={`text-2xl font-bold ${(summary?.balanceByn ?? 0) >= 0 ? 'text-blue-600' : 'text-orange-600'}`}>
            {fmt(summary?.balanceByn ?? 0)}
          </p>
        </div>
      </div>

      {/* Recent transactions */}
      <div className="card">
        <h2 className="text-lg font-semibold mb-4">Последние операции</h2>
        {!recentTx?.content?.length ? (
          <p className="text-gray-400 text-sm">Операций пока нет</p>
        ) : (
          <div className="divide-y divide-gray-50">
            {recentTx.content.map(tx => (
              <div key={tx.id} className="flex items-center justify-between py-3">
                <div className="flex items-center gap-3">
                  <span className={`w-8 h-8 rounded-full flex items-center justify-center text-sm ${
                    tx.type === 'INCOME' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                  }`}>
                    <i className={`bi ${tx.type === 'INCOME' ? 'bi-arrow-up' : 'bi-arrow-down'}`} />
                  </span>
                  <div>
                    <p className="text-sm font-medium">{tx.description || tx.categoryName || '—'}</p>
                    <p className="text-xs text-gray-400">{tx.date}</p>
                  </div>
                </div>
                <span className={`font-semibold text-sm ${tx.type === 'INCOME' ? 'text-green-600' : 'text-red-600'}`}>
                  {tx.type === 'INCOME' ? '+' : '-'}{fmt(tx.amountByn)}
                </span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
