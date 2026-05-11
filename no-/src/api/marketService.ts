import client from './client'

export interface MarketItem {
  id: number
  itemName: string
  price: number
  sellerNickName: string
}

export interface RegisterRequest {
  id: number
  name: string
  price: number
}

export const marketService = {
  getAll(): Promise<MarketItem[]> {
    return client.get<{ data: MarketItem[] }>('/market').then(res => res.data.data)
  },

  register(data: RegisterRequest): Promise<void> {
    return client.post('/market/register', data)
  },

  buy(data: RegisterRequest): Promise<void> {
    return client.post('/trade/buy', data)
  },
}
