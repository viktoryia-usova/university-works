export const PWD_MIN = 8

export const hasUpper   = (v: string) => /[A-Z]/.test(v)
export const hasLower   = (v: string) => /[a-z]/.test(v)
export const hasSpecial = (v: string) => /[^A-Za-z0-9]/.test(v)
export const pwdStrong  = (v: string) =>
  v.length >= PWD_MIN && hasUpper(v) && hasLower(v) && hasSpecial(v)

interface Props { value: string }

export default function PasswordRequirements({ value }: Props) {
  const reqs = [
    { ok: value.length >= PWD_MIN, label: `Не менее ${PWD_MIN} символов` },
    { ok: hasUpper(value),         label: 'Заглавная буква (A–Z)' },
    { ok: hasLower(value),         label: 'Строчная буква (a–z)' },
    { ok: hasSpecial(value),       label: 'Спецсимвол (!@#$…)' },
  ]
  return (
    <ul className="mt-2 space-y-1">
      {reqs.map(r => (
        <li
          key={r.label}
          className={`text-xs flex items-center gap-1.5 transition-colors ${
            r.ok ? 'text-green-600' : 'text-gray-400'
          }`}
        >
          <i className={`bi ${r.ok ? 'bi-check-circle-fill' : 'bi-circle'}`} />
          {r.label}
        </li>
      ))}
    </ul>
  )
}
