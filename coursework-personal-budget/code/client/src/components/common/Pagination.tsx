interface PaginationProps {
  page: number
  totalPages: number
  onPage: (p: number) => void
}

export default function Pagination({ page, totalPages, onPage }: PaginationProps) {
  if (totalPages <= 1) return null
  return (
    <div className="flex items-center justify-center gap-2 mt-4">
      <button
        className="btn-secondary px-3 py-1 text-sm disabled:opacity-40"
        disabled={page === 0}
        onClick={() => onPage(page - 1)}
      >‹ Назад</button>
      <span className="text-sm text-gray-600">{page + 1} / {totalPages}</span>
      <button
        className="btn-secondary px-3 py-1 text-sm disabled:opacity-40"
        disabled={page >= totalPages - 1}
        onClick={() => onPage(page + 1)}
      >Вперёд ›</button>
    </div>
  )
}
