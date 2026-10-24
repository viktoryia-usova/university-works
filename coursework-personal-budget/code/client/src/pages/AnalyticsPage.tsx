import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import {
  PieChart, Pie, Cell, Tooltip, ResponsiveContainer,
  ComposedChart, Bar, Line, XAxis, YAxis, CartesianGrid, Legend,
} from 'recharts'
import { analyticsApi } from '../api/analytics'
import { format, startOfMonth, endOfMonth, subMonths } from 'date-fns'
import PageLoader from '../components/common/PageLoader'

const COLORS = ['#3b82f6','#10b981','#f59e0b','#ef4444','#8b5cf6','#ec4899','#14b8a6','#f97316']

const fmt = (n: number) =>
  new Intl.NumberFormat('ru-BY', { style: 'currency', currency: 'BYN', maximumFractionDigits: 2 }).format(n)

const fmtShort = (n: number) =>
  new Intl.NumberFormat('ru-BY', { maximumFractionDigits: 0 }).format(n)

export default function AnalyticsPage() {
  const today = new Date()
  const [from, setFrom] = useState(format(startOfMonth(today), 'yyyy-MM-dd'))
  const [to, setTo]     = useState(format(endOfMonth(today), 'yyyy-MM-dd'))

  const { data: summary, isLoading: loadSum } = useQuery({
    queryKey: ['analytics', 'summary', from, to],
    queryFn: () => analyticsApi.getSummary(from, to).then(r => r.data),
  })

  const { data: income } = useQuery({
    queryKey: ['analytics', 'income', from, to],
    queryFn: () => analyticsApi.getIncome(from, to).then(r => r.data),
  })

  const { data: expenses } = useQuery({
    queryKey: ['analytics', 'expenses', from, to],
    queryFn: () => analyticsApi.getExpenses(from, to).then(r => r.data),
  })

  const { data: monthly } = useQuery({
    queryKey: ['analytics', 'monthly', from, to],
    queryFn: () => analyticsApi.getMonthly(from, to).then(r => r.data),
  })

  const setPreset = (months: number) => {
    const d = subMonths(today, months - 1)
    setFrom(format(startOfMonth(d), 'yyyy-MM-dd'))
    setTo(format(endOfMonth(today), 'yyyy-MM-dd'))
  }

  return (
    <div>
      <h1 className="text-2xl font-bold mb-6">Аналитика</h1>

      {/* Date range */}
      <div className="card mb-6 p-4">
        <div className="flex flex-wrap items-center gap-3">
          <input className="input w-36" type="date" value={from} onChange={e => setFrom(e.target.value)} />
          <span className="text-gray-400">—</span>
          <input className="input w-36" type="date" value={to} onChange={e => setTo(e.target.value)} />
          <div className="flex gap-2 ml-auto">
            {[1, 3, 6, 12].map(m => (
              <button key={m} className="btn-secondary text-xs" onClick={() => setPreset(m)}>
                {m === 1 ? 'Месяц' : m === 3 ? '3 мес' : m === 6 ? '6 мес' : 'Год'}
              </button>
            ))}
          </div>
        </div>
      </div>

      {loadSum ? <PageLoader /> : (
        <>
          {/* KPI */}
          <div className="grid grid-cols-3 gap-4 mb-6">
            <div className="card border-l-4 border-green-500">
              <p className="text-sm text-gray-500">Доходы</p>
              <p className="text-2xl font-bold text-green-600">{fmt(summary?.totalIncomeByn ?? 0)}</p>
            </div>
            <div className="card border-l-4 border-red-500">
              <p className="text-sm text-gray-500">Расходы</p>
              <p className="text-2xl font-bold text-red-600">{fmt(summary?.totalExpenseByn ?? 0)}</p>
            </div>
            <div className={`card border-l-4 ${(summary?.balanceByn ?? 0) >= 0 ? 'border-blue-500' : 'border-orange-500'}`}>
              <p className="text-sm text-gray-500">Баланс</p>
              <p className={`text-2xl font-bold ${(summary?.balanceByn ?? 0) >= 0 ? 'text-blue-600' : 'text-orange-600'}`}>
                {fmt(summary?.balanceByn ?? 0)}
              </p>
            </div>
          </div>

          {/* Pie charts */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
            {/* Expense pie */}
            <div className="card">
              <h2 className="text-base font-semibold mb-4">Расходы по категориям</h2>
              {!expenses?.entries?.length ? (
                <p className="text-gray-400 text-sm text-center py-8">Нет данных</p>
              ) : (
                <>
                  <ResponsiveContainer width="100%" height={200}>
                    <PieChart>
                      <Pie
                        data={expenses.entries.map(e => ({ name: e.label, value: e.amountByn }))}
                        dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={75}
                        label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                        labelLine={false}
                      >
                        {expenses.entries.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                      </Pie>
                      <Tooltip formatter={(v: number) => fmt(v)} />
                    </PieChart>
                  </ResponsiveContainer>
                  <div className="divide-y mt-2">
                    {expenses.entries.map((e, i) => (
                      <div key={i} className="flex items-center justify-between py-1.5">
                        <div className="flex items-center gap-2">
                          <div className="w-2.5 h-2.5 rounded-full flex-shrink-0" style={{ backgroundColor: COLORS[i % COLORS.length] }} />
                          <span className="text-sm text-gray-700">{e.label}</span>
                        </div>
                        <div className="flex items-center gap-3">
                          <span className="text-xs text-gray-400">{e.percent?.toFixed(1)}%</span>
                          <span className="text-sm font-medium text-red-600">{fmt(e.amountByn)}</span>
                        </div>
                      </div>
                    ))}
                  </div>
                </>
              )}
            </div>

            {/* Income pie */}
            <div className="card">
              <h2 className="text-base font-semibold mb-4">Доходы по категориям</h2>
              {!income?.entries?.length ? (
                <p className="text-gray-400 text-sm text-center py-8">Нет данных</p>
              ) : (
                <>
                  <ResponsiveContainer width="100%" height={200}>
                    <PieChart>
                      <Pie
                        data={income.entries.map(e => ({ name: e.label, value: e.amountByn }))}
                        dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={75}
                        label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                        labelLine={false}
                      >
                        {income.entries.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                      </Pie>
                      <Tooltip formatter={(v: number) => fmt(v)} />
                    </PieChart>
                  </ResponsiveContainer>
                  <div className="divide-y mt-2">
                    {income.entries.map((e, i) => (
                      <div key={i} className="flex items-center justify-between py-1.5">
                        <div className="flex items-center gap-2">
                          <div className="w-2.5 h-2.5 rounded-full flex-shrink-0" style={{ backgroundColor: COLORS[i % COLORS.length] }} />
                          <span className="text-sm text-gray-700">{e.label}</span>
                        </div>
                        <div className="flex items-center gap-3">
                          <span className="text-xs text-gray-400">{e.percent?.toFixed(1)}%</span>
                          <span className="text-sm font-medium text-green-600">{fmt(e.amountByn)}</span>
                        </div>
                      </div>
                    ))}
                  </div>
                </>
              )}
            </div>
          </div>

          {/* Monthly dynamics */}
          {monthly && monthly.length > 0 && (
            <div className="card">
              <h2 className="text-base font-semibold mb-4">Динамика по месяцам</h2>
              <ResponsiveContainer width="100%" height={280}>
                <ComposedChart data={monthly.map(m => ({
                  name: m.label,
                  income: m.incomeByn,
                  expense: m.expenseByn,
                  balance: m.balanceByn,
                }))}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                  <XAxis dataKey="name" tick={{ fontSize: 11 }} />
                  <YAxis tick={{ fontSize: 11 }} tickFormatter={fmtShort} />
                  <Tooltip
                    formatter={(value: number, name: string) => [
                      fmt(value),
                      name === 'income' ? 'Доходы' : name === 'expense' ? 'Расходы' : 'Баланс',
                    ]}
                  />
                  <Legend
                    formatter={(value) =>
                      value === 'income' ? 'Доходы' : value === 'expense' ? 'Расходы' : 'Баланс'
                    }
                  />
                  <Bar dataKey="income"  name="income"  fill="#10b981" radius={[3,3,0,0]} maxBarSize={40} />
                  <Bar dataKey="expense" name="expense" fill="#ef4444" radius={[3,3,0,0]} maxBarSize={40} />
                  <Line dataKey="balance" name="balance" type="monotone" stroke="#3b82f6" strokeWidth={2} dot={{ r: 3 }} />
                </ComposedChart>
              </ResponsiveContainer>
            </div>
          )}
        </>
      )}
    </div>
  )
}
