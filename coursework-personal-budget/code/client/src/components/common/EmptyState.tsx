interface EmptyStateProps {
  message: string
  action?: { label: string; onClick: () => void }
}

export default function EmptyState({ message, action }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center py-12 text-center">
      <i className="bi bi-inbox text-4xl text-gray-300 mb-3" />
      <p className="text-gray-500 mb-4">{message}</p>
      {action && (
        <button className="btn-primary text-sm" onClick={action.onClick}>{action.label}</button>
      )}
    </div>
  )
}
