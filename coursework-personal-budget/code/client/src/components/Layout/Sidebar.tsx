import { NavLink, useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { useAuthStore } from '../../store/authStore'
import { authApi } from '../../api/auth'
import { contactsApi } from '../../api/contacts'
import { queryClient } from '../../api/queryClient'

const nav = [
  { to: '/dashboard',        label: 'Главная',         icon: 'bi-house' },
  { to: '/transactions',     label: 'Операции',        icon: 'bi-credit-card' },
  { to: '/categories',       label: 'Категории',       icon: 'bi-tags' },
  { to: '/goals',            label: 'Цели',            icon: 'bi-bullseye' },
  { to: '/planned-expenses', label: 'Бюджет',          icon: 'bi-clipboard-check' },
  { to: '/deposits',         label: 'Вклады',          icon: 'bi-bank' },
  { to: '/analytics',        label: 'Аналитика',       icon: 'bi-bar-chart' },
  { to: '/currencies',       label: 'Курсы валют',     icon: 'bi-currency-exchange' },
  { to: '/contacts',         label: 'Контакты',        icon: 'bi-people' },
  { to: '/shared-sheets',    label: 'Общие листы',     icon: 'bi-collection' },
  { to: '/resources',        label: 'Финграмотность',  icon: 'bi-book' },
  { to: '/profile',          label: 'Профиль',         icon: 'bi-gear' },
]

export default function Sidebar() {
  const { isAdmin, clearToken } = useAuthStore()
  const navigate = useNavigate()

  // Fetch pending contact requests — same cache key as ContactsPage,
  // so no extra network request when the user is already on that page.
  const { data: pendingContacts } = useQuery({
    queryKey: ['contacts', 'pending'],
    queryFn: () => contactsApi.getPending().then(r => r.data),
    staleTime: 30_000,
    refetchInterval: 60_000,
  })

  const incomingCount =
    pendingContacts?.content?.filter(c => !c.isRequester).length ?? 0

  const handleSignOut = async () => {
    try { await authApi.signOut() } catch {}
    queryClient.clear()
    clearToken()
    navigate('/sign-in')
  }

  return (
    <aside className="w-60 flex-shrink-0 bg-white border-r border-gray-200 flex flex-col h-screen sticky top-0">
      <div className="px-6 py-5 border-b border-gray-100">
        <span className="text-xl font-bold text-blue-600">
          <i className="bi bi-wallet2 me-2" />Бюджет
        </span>
      </div>

      <nav className="flex-1 overflow-y-auto py-4 px-3">
        {nav.map(({ to, label, icon }) => {
          const isContacts = to === '/contacts'
          const showBadge  = isContacts && incomingCount > 0

          return (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium mb-0.5 transition-colors ${
                  isActive
                    ? 'bg-blue-50 text-blue-700'
                    : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                }`
              }
            >
              <i className={`bi ${icon} text-base`} />
              {label}
              {showBadge && (
                <span className="ml-auto inline-flex items-center justify-center min-w-[18px] h-[18px] rounded-full bg-red-500 text-white text-[10px] font-bold px-1 leading-none">
                  {incomingCount > 99 ? '99+' : incomingCount}
                </span>
              )}
            </NavLink>
          )
        })}

        {isAdmin() && (
          <NavLink
            to="/admin"
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium mb-0.5 transition-colors mt-2 ${
                isActive
                  ? 'bg-red-50 text-red-700'
                  : 'text-red-600 hover:bg-red-50'
              }`
            }
          >
            <i className="bi bi-shield text-base" />
            Администрация
          </NavLink>
        )}
      </nav>

      <div className="p-3 border-t border-gray-100">
        <button
          onClick={handleSignOut}
          className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium text-gray-600 hover:bg-gray-50 hover:text-gray-900 transition-colors"
        >
          <i className="bi bi-box-arrow-right text-base" /> Выйти
        </button>
      </div>
    </aside>
  )
}
