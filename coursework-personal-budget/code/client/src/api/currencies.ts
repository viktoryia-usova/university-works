import api from './axios'
import type { CurrencyRate } from '../types'

export const currenciesApi = {
  getRates: () =>
    api.get<CurrencyRate[]>('/currencies/rates'),

  getSupported: () =>
    api.get<string[]>('/currencies/supported'),
}
