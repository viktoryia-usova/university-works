import { Outlet } from 'react-router-dom'

export default function AuthLayout() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="text-5xl mb-3 text-blue-600"><i className="bi bi-wallet2" /></div>
          <h1 className="text-3xl font-bold text-gray-900">Личный бюджет</h1>
          <p className="text-gray-500 mt-2">Управляйте финансами с умом</p>
        </div>
        <div className="bg-white rounded-2xl shadow-xl p-8">
          <Outlet />
        </div>
      </div>
    </div>
  )
}
