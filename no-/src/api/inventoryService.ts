import client from './client'

export interface InventoryItem {
  itemId: number
  itemName: string
  quantity: number
  description: string
}

export const inventoryService = {
  getMyInventory(): Promise<InventoryItem[]> {
    return client.get<{ data: InventoryItem[] }>('/inventory').then(res => res.data.data)
  },
}
