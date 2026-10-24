const KEY_MAP: Record<string, string> = {
  // Profile
  PROFILE_NOT_FOUND:              'Профиль не найден',

  // Resource / финграмотность
  DUPLICATE_RESOURCE_CATEGORY:    'Категория с таким названием уже существует',
  RESOURCE_CATEGORY_NOT_FOUND:    'Категория не найдена',
  RESOURCE_NOT_FOUND:             'Материал не найден',
  ATTACHMENT_NOT_FOUND:           'Вложение не найдено',

  // Goals
  GOAL_NOT_FOUND:                 'Цель не найдена',
  GOAL_ACCESS_DENIED:             'Нет доступа к этой цели',

  // Deposits
  DEPOSIT_NOT_FOUND:              'Вклад не найден',
  DEPOSIT_ACCESS_DENIED:          'Нет доступа к этому вкладу',
  DEPOSIT_TYPE_MISMATCH:          'Пополнение доступно только для копилок',
  DEPOSIT_TYPE_EXCEPTION:         'Пополнение доступно только для копилок',

  // Planned expenses
  PLANNED_EXPENSE_NOT_FOUND:      'Запись не найдена',
  PLANNED_EXPENSE_ACCESS_DENIED:  'Нет доступа к этой записи',

  // Contacts
  CONTACT_NOT_FOUND:              'Контакт не найден',
  CONTACT_ALREADY_EXISTS:         'Этот пользователь уже в ваших контактах',
  CONTACT_EXISTS:                 'Заявка или контакт с этим пользователем уже существует',
  CONTACT_REQUEST_NOT_FOUND:      'Заявка не найдена',
  CONTACT_SELF:                   'Нельзя отправить заявку самому себе',
  CONTACT_ACCESS_DENIED:          'Нет доступа к этой заявке',

  // Shared sheets
  SHEET_NOT_FOUND:                'Общий лист не найден',
  SHARED_SHEET_NOT_FOUND:         'Общий лист не найден',
  SHEET_ACCESS_DENIED:            'Только создатель может выполнять это действие',
  SHEET_INACTIVE:                 'Общий лист завершён',
  NOT_MEMBER:                     'Вы не являетесь участником этого листа',
  ALREADY_MEMBER:                 'Пользователь уже является участником листа',
  SHARED_GOAL_NOT_FOUND:          'Общая цель не найдена',
  SHARED_TRANSACTION_NOT_FOUND:   'Операция не найдена',
  CANNOT_LEAVE:                   'Нельзя покинуть лист, в котором уже есть операции',

  // Transactions / categories
  TRANSACTION_NOT_FOUND:          'Операция не найдена',
  TRANSACTION_ACCESS_DENIED:      'Нет доступа к этой операции',
  CATEGORY_NOT_FOUND:             'Категория не найдена',
  CATEGORY_ACCESS_DENIED:         'Нет доступа к этой категории',
  DUPLICATE_CATEGORY:             'Категория с таким названием уже существует',

  // Data integrity (DB unique violations etc.)
  DATA_INTEGRITY_VIOLATION:       'Запись с такими данными уже существует',
  DUPLICATE:                      'Запись с такими данными уже существует',

  // Auth
  INACTIVE:                       'Аккаунт заблокирован',
  ACCESS_DENIED:                  'Нет доступа',
  INVALID_CREDENTIALS:            'Неверный email или пароль',
  CREDENTIALS:                    'Неверный email или пароль',
  PASSWORD:                       'Неверный текущий пароль',
  USERNAME:                       'Аккаунт с таким email уже зарегистрирован',
  ACCOUNT_NOT_FOUND:              'Аккаунт не найден',
  ACCOUNT_ALREADY_EXISTS:         'Аккаунт с таким email уже зарегистрирован',
  EMAIL_NOT_CONFIRMED:            'Email не подтверждён',
  KEY:                            'Неверный или истёкший код подтверждения',
  INVALID_CONFIRMATION_CODE:      'Неверный или истёкший код подтверждения',
  REFRESH_TOKEN:                  'Сессия истекла, войдите снова',

  // Generic
  UNHANDLED:                      'Внутренняя ошибка сервера. Попробуйте позже',
  UNSUPPORTED:                    'Операция не поддерживается',
}

const STATUS_MAP: Record<number, string> = {
  400: 'Проверьте правильность введённых данных',
  401: 'Необходима авторизация',
  403: 'Нет доступа',
  404: 'Запись не найдена',
  409: 'Запись с такими данными уже существует',
  422: 'Некорректные данные',
  500: 'Внутренняя ошибка сервера. Попробуйте позже',
  503: 'Сервис временно недоступен. Попробуйте позже',
}

export function parseApiError(e: unknown, fallback = 'Произошла ошибка. Попробуйте позже'): string {
  const err = e as any
  if (!err?.response) return 'Ошибка сети. Проверьте подключение'

  const key: string | undefined = err.response.data?.key
  if (key && KEY_MAP[key]) return KEY_MAP[key]

  const status: number = err.response.status
  return STATUS_MAP[status] ?? fallback
}
