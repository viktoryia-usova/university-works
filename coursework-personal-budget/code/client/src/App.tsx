import { Routes, Route, Navigate } from 'react-router-dom'
import AuthLayout from './components/Layout/AuthLayout'
import AppLayout from './components/Layout/AppLayout'
import ProtectedRoute from './components/Layout/ProtectedRoute'

import SignInPage         from './pages/auth/SignInPage'
import SignUpPage         from './pages/auth/SignUpPage'
import ConfirmPage        from './pages/auth/ConfirmPage'
import OAuth2CallbackPage from './pages/auth/OAuth2CallbackPage'

import DashboardPage      from './pages/DashboardPage'
import TransactionsPage   from './pages/TransactionsPage'
import CategoriesPage     from './pages/CategoriesPage'
import GoalsPage          from './pages/GoalsPage'
import PlannedExpensesPage from './pages/PlannedExpensesPage'
import DepositsPage       from './pages/DepositsPage'
import AnalyticsPage      from './pages/AnalyticsPage'
import ContactsPage       from './pages/ContactsPage'
import SharedSheetsPage   from './pages/SharedSheetsPage'
import ResourcesPage      from './pages/ResourcesPage'
import ProfilePage        from './pages/ProfilePage'
import AdminPage          from './pages/AdminPage'
import CurrencyRatesPage  from './pages/CurrencyRatesPage'

export default function App() {
  return (
    <Routes>
      {/* Public auth routes */}
      <Route element={<AuthLayout />}>
        <Route path="/sign-in"  element={<SignInPage />} />
        <Route path="/sign-up"  element={<SignUpPage />} />
        <Route path="/confirm"  element={<ConfirmPage />} />
      </Route>

      {/* OAuth2 callback — outside auth layout */}
      <Route path="/oauth2/callback" element={<OAuth2CallbackPage />} />

      {/* Protected app routes */}
      <Route element={<ProtectedRoute><AppLayout /></ProtectedRoute>}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard"        element={<DashboardPage />} />
        <Route path="/transactions"     element={<TransactionsPage />} />
        <Route path="/categories"       element={<CategoriesPage />} />
        <Route path="/goals"            element={<GoalsPage />} />
        <Route path="/planned-expenses" element={<PlannedExpensesPage />} />
        <Route path="/deposits"         element={<DepositsPage />} />
        <Route path="/analytics"        element={<AnalyticsPage />} />
        <Route path="/currencies"       element={<CurrencyRatesPage />} />
        <Route path="/contacts"         element={<ContactsPage />} />
        <Route path="/shared-sheets"    element={<SharedSheetsPage />} />
        <Route path="/resources"        element={<ResourcesPage />} />
        <Route path="/profile"          element={<ProfilePage />} />
        <Route path="/admin"            element={
          <ProtectedRoute adminOnly><AdminPage /></ProtectedRoute>
        } />
      </Route>

      {/* Catch-all */}
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  )
}
