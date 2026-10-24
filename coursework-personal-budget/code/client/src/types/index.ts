// ---- Auth ----
export interface JwtInfo {
  type: string
  id: number
  username: string
  role: 'USER' | 'ADMIN'
  active: boolean
  accessToken: string
  refreshToken: string
}

// ---- Profile ----
export interface ProfileResponse {
  id: number
  accountId: number
  email: string
  firstName: string
  lastName: string
  phone?: string
  birthDate?: string
  friendCode: string
  defaultCurrency: string
  createdAt: string
}

export interface UpdateProfileRequest {
  firstName?: string
  lastName?: string
  phone?: string
  birthDate?: string
  defaultCurrency?: string
}

export interface PrivacySettingsResponse {
  id: number
  searchableByEmail: boolean
  searchableByPhone: boolean
  searchableByFriendCode: boolean
  showFullName: boolean
}

export interface NotificationSettingsResponse {
  id: number
  onGoalReached: boolean
  onBudgetExceeded: boolean
  onSharedTransaction: boolean
  onContactRequest: boolean
  onDepositMatured: boolean
}

// ---- Categories ----
export type CategoryType = 'INCOME' | 'EXPENSE'

export interface CategoryResponse {
  id: number
  name: string
  color?: string
  type: CategoryType
}

export interface CreateCategoryRequest {
  name: string
  color?: string
  type: CategoryType
}

export interface UpdateCategoryRequest {
  name?: string
  color?: string
}

// ---- Transactions ----
export type TransactionType = 'INCOME' | 'EXPENSE'

export interface TransactionResponse {
  id: number
  categoryId?: number
  categoryName?: string
  categoryColor?: string
  amount: number
  currency: string
  exchangeRateToByn: number
  amountByn: number
  type: TransactionType
  date: string
  description?: string
  createdAt: string
}

export interface CreateTransactionRequest {
  categoryId?: number
  amount: number
  currency: string
  type: TransactionType
  date: string
  description?: string
}

export interface UpdateTransactionRequest {
  categoryId?: number
  amount?: number
  currency?: string
  type?: TransactionType
  date?: string
  description?: string
}

// ---- Goals ----
export type GoalStatus = 'ACTIVE' | 'COMPLETED' | 'CANCELLED'

export interface GoalResponse {
  id: number
  name: string
  targetAmount: number
  currency: string
  currentAmount: number
  deadline?: string
  status: GoalStatus
  progressPercent: number
  createdAt: string
}

export interface CreateGoalRequest {
  name: string
  targetAmount: number
  currency: string
  deadline?: string
}

export interface ContributeToGoalRequest {
  amount: number
  currency: string
}

// ---- Planned Expenses ----
export type PlannedExpenseType = 'BUDGET_LIMIT' | 'PLANNED_PURCHASE'
export type PeriodType = 'MONTHLY' | 'QUARTERLY' | 'YEARLY'

export interface PlannedExpenseResponse {
  id: number
  categoryId?: number
  categoryName?: string
  name: string
  amount: number
  currency: string
  type: PlannedExpenseType
  periodType?: PeriodType
  plannedDate?: string
  isFulfilled: boolean
  createdAt: string
}

export interface CreatePlannedExpenseRequest {
  categoryId?: number
  name: string
  amount: number
  currency: string
  type: PlannedExpenseType
  periodType?: PeriodType
  plannedDate?: string
}

// ---- Deposits ----
export type DepositType = 'DEPOSIT' | 'PIGGY_BANK'
export type DepositStatus = 'ACTIVE' | 'COMPLETED'

export interface DepositResponse {
  id: number
  name: string
  type: DepositType
  initialAmount: number
  currency: string
  interestRate?: number
  startDate: string
  endDate?: string
  currentAmount: number
  status: DepositStatus
  projectedIncome: number
  createdAt: string
}

export interface CreateDepositRequest {
  name: string
  type: DepositType
  initialAmount: number
  currency: string
  interestRate?: number
  startDate: string
  endDate?: string
}

export interface AddFundsRequest {
  amount: number
}

// ---- Contacts ----
export type ContactStatus = 'PENDING' | 'ACCEPTED' | 'DECLINED'

export interface ContactResponse {
  id: number
  contactId: number
  profileId: number       // the OTHER person's profile id
  email: string
  firstName: string
  lastName: string
  friendCode: string
  status: ContactStatus
  isRequester: boolean
  createdAt: string
}

export interface ProfileSearchResponse {
  profileId: number
  email: string
  firstName: string
  lastName: string
  friendCode: string
}

export interface SendContactRequestRequest {
  profileId: number
}

// ---- Shared Sheets ----
export type SheetStatus = 'ACTIVE' | 'COMPLETED'
export type SharedTxType = 'EXPENSE' | 'CONTRIBUTION'

export interface SharedSheetMemberResponse {
  id: number
  profileId: number
  email: string
  firstName: string
  lastName: string
  joinedAt: string
}

export interface SharedSheetResponse {
  id: number
  name: string
  description?: string
  status: SheetStatus
  creatorProfileId: number
  creatorEmail: string
  members: SharedSheetMemberResponse[]
  completedAt?: string
  createdAt: string
}

export interface SharedTransactionResponse {
  id: number
  sheetId: number
  profileId: number
  authorEmail: string
  authorFirstName: string
  amount: number
  currency: string
  amountByn: number
  type: SharedTxType
  description?: string
  date: string
  createdAt: string
}

export interface SharedGoalResponse {
  id: number
  sheetId: number
  name: string
  targetAmount: number
  currency: string
  currentAmount: number
  deadline?: string
  status: GoalStatus
  progressPercent: number
}

export interface MemberContributionResponse {
  profileId: number
  email: string
  firstName: string
  contributedByn: number
  spentByn: number
  netByn: number
}

export interface SharedSheetAnalyticsResponse {
  sheetId: number
  sheetName: string
  totalContributedByn: number
  totalSpentByn: number
  balanceByn: number
  equalShareByn: number
  memberCount: number
  memberContributions: MemberContributionResponse[]
}

// ---- Analytics ----
export interface AnalyticsEntryResponse {
  label: string
  amountByn: number
  percent: number
}

export interface AnalyticsSummaryResponse {
  totalIncomeByn: number
  totalExpenseByn: number
  balanceByn: number
  entries: AnalyticsEntryResponse[]
}

export interface MonthlyEntryResponse {
  year: number
  month: number
  label: string
  incomeByn: number
  expenseByn: number
  balanceByn: number
}

// ---- Resources ----
export interface ResourceCategoryResponse {
  id: number
  name: string
}

export interface FinancialResourceResponse {
  id: number
  title: string
  content: string
  categoryId: number
  categoryName: string
  attachments: AttachmentResponse[]
  createdAt: string
  isFavorite: boolean
}

export interface AttachmentResponse {
  id: number
  fileName: string
  fileUrl: string
  fileType: 'IMAGE' | 'DOCUMENT'
}

// ---- Admin ----
export interface UserResponse {
  id: number
  username: string        // account email / login
  role: string
  active: boolean
  createdAt: string
  profileId?: number
  firstName?: string
  lastName?: string
  phone?: string
  friendCode?: string
}

// ---- Currency ----
export interface CurrencyRate {
  Cur_Abbreviation: string
  Cur_Scale: number
  Cur_Name: string
  Cur_OfficialRate: number
}

// ---- Pagination ----
// Spring Data PagedModel (VIA_DTO mode): pagination meta is nested under "page"
export interface Page<T> {
  content: T[]
  page: {
    size: number
    totalElements: number
    totalPages: number
    number: number
  }
}
