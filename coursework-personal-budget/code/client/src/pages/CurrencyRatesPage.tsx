import { useState, useMemo } from 'react'
import { useQuery } from '@tanstack/react-query'
import { currenciesApi } from '../api/currencies'
import PageLoader from '../components/common/PageLoader'

type SortKey = 'code-asc' | 'code-desc' | 'rate-asc' | 'rate-desc'

const fmt = (n: number) =>
  new Intl.NumberFormat('ru-BY', { minimumFractionDigits: 4, maximumFractionDigits: 4 }).format(n)

export default function CurrencyRatesPage() {
  const { data: rates, isLoading, refetch, isFetching } = useQuery({
    queryKey: ['currencies', 'rates'],
    queryFn: () => currenciesApi.getRates().then(r => r.data),
    staleTime: 5 * 60 * 1000,
  })

  const [search, setSearch] = useState('')
  const [sort, setSort] = useState<SortKey>('code-asc')

  const displayed = useMemo(() => {
    if (!rates) return []
    const q = search.trim().toLowerCase()
    let result = q
      ? rates.filter(r =>
          r.Cur_Abbreviation.toLowerCase().includes(q) ||
          r.Cur_Name.toLowerCase().includes(q)
        )
      : [...rates]

    result.sort((a, b) => {
      switch (sort) {
        case 'code-asc':  return a.Cur_Abbreviation.localeCompare(b.Cur_Abbreviation)
        case 'code-desc': return b.Cur_Abbreviation.localeCompare(a.Cur_Abbreviation)
        case 'rate-asc':  return a.Cur_OfficialRate / a.Cur_Scale - b.Cur_OfficialRate / b.Cur_Scale
        case 'rate-desc': return b.Cur_OfficialRate / b.Cur_Scale - a.Cur_OfficialRate / a.Cur_Scale
      }
    })
    return result
  }, [rates, search, sort])

  const SortIcon = ({ asc, desc }: { asc: SortKey; desc: SortKey }) => {
    if (sort === asc)  return <i className="bi bi-sort-up ms-1 text-blue-500" />
    if (sort === desc) return <i className="bi bi-sort-down ms-1 text-blue-500" />
    return <i className="bi bi-arrow-down-up ms-1 text-gray-300" />
  }

  const toggleSort = (asc: SortKey, desc: SortKey) =>
    setSort(prev => (prev === asc ? desc : asc))

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Курсы валют</h1>
        <button className="btn-secondary flex items-center gap-2" onClick={() => refetch()} disabled={isFetching}>
          <i className={`bi bi-arrow-clockwise ${isFetching ? 'animate-spin' : ''}`} />
          Обновить
        </button>
      </div>

      <p className="text-sm text-gray-400 mb-4">
        Официальные курсы Национального банка Республики Беларусь (к белорусскому рублю)
      </p>

      {/* Search + sort controls */}
      <div className="flex gap-3 mb-4">
        <div className="relative flex-1 max-w-xs">
          <i className="bi bi-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm pointer-events-none" />
          <input
            className="input pl-8"
            placeholder="Код или наименование…"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
        <select
          className="input w-52"
          value={sort}
          onChange={e => setSort(e.target.value as SortKey)}
        >
          <option value="code-asc">Код A → Z</option>
          <option value="code-desc">Код Z → A</option>
          <option value="rate-asc">Курс по возрастанию</option>
          <option value="rate-desc">Курс по убыванию</option>
        </select>
      </div>

      {isLoading ? <PageLoader /> : (
        <div className="card p-0 overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
              <tr>
                <th
                  className="px-4 py-3 text-left cursor-pointer select-none hover:text-gray-700"
                  onClick={() => toggleSort('code-asc', 'code-desc')}
                >
                  Код <SortIcon asc="code-asc" desc="code-desc" />
                </th>
                <th className="px-4 py-3 text-left">Наименование</th>
                <th className="px-4 py-3 text-right">Единиц</th>
                <th
                  className="px-4 py-3 text-right cursor-pointer select-none hover:text-gray-700"
                  onClick={() => toggleSort('rate-asc', 'rate-desc')}
                >
                  Курс (BYN) <SortIcon asc="rate-asc" desc="rate-desc" />
                </th>
                <th className="px-4 py-3 text-right">За 1 ед.</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-50">
              {displayed.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-4 py-8 text-center text-gray-400">
                    Ничего не найдено
                  </td>
                </tr>
              ) : displayed.map(r => (
                <tr key={r.Cur_Abbreviation} className="hover:bg-gray-50">
                  <td className="px-4 py-3 font-mono font-semibold text-blue-600">{r.Cur_Abbreviation}</td>
                  <td className="px-4 py-3 text-gray-700">{r.Cur_Name}</td>
                  <td className="px-4 py-3 text-right text-gray-500">{r.Cur_Scale}</td>
                  <td className="px-4 py-3 text-right font-medium">{fmt(r.Cur_OfficialRate)} BYN</td>
                  <td className="px-4 py-3 text-right text-gray-500">
                    {fmt(r.Cur_OfficialRate / r.Cur_Scale)} BYN
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
