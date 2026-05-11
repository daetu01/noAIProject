<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { inventoryService, type InventoryItem } from '@/api/inventoryService'

const items = ref<InventoryItem[]>([])
const loading = ref(false)
const errorMsg = ref('')

async function load() {
  loading.value = true
  try { items.value = await inventoryService.getMyInventory() }
  catch { errorMsg.value = '인벤토리를 불러오지 못했습니다.' }
  finally { loading.value = false }
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h1 class="page-title">인벤토리</h1>
        <p class="page-subtitle">보유 아이템 {{ items.length }}개</p>
      </div>
    </div>

    <div v-if="errorMsg" class="error-bar">{{ errorMsg }}</div>

    <div v-if="loading" class="loading-wrap">
      <v-progress-circular indeterminate color="#0071E3" size="32" width="2.5" />
    </div>

    <div v-else-if="items.length === 0" class="empty">
      <v-icon size="40" color="#AEAEB2">mdi-bag-personal-outline</v-icon>
      <p>보유 아이템이 없습니다</p>
      <router-link to="/market" class="empty-link">마켓에서 구매하기</router-link>
    </div>

    <div v-else class="list-card">
      <div
        v-for="(item, i) in items"
        :key="item.itemId"
        class="list-row"
        :class="{ last: i === items.length - 1 }"
      >
        <div class="item-icon">
          <v-icon size="22" color="#FF9500">mdi-sword</v-icon>
        </div>
        <div class="item-body">
          <p class="item-name">{{ item.itemName }}</p>
          <p class="item-desc">{{ item.description || '설명 없음' }}</p>
        </div>
        <div class="item-qty">
          <span class="qty-num">{{ item.quantity }}</span>
          <span class="qty-label">개</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { min-height: 100vh; background: #F5F5F7; padding: 24px 16px; }
.page-header { margin-bottom: 20px; }
.page-title { font-size: 26px; font-weight: 700; color: #1D1D1F; letter-spacing: -0.4px; }
.page-subtitle { font-size: 13px; color: #6E6E73; margin-top: 2px; }

.error-bar { background: rgba(255,59,48,0.08); border: 1px solid rgba(255,59,48,0.15); border-radius: 10px; padding: 10px 14px; font-size: 13px; color: #FF3B30; margin-bottom: 12px; }
.loading-wrap { display: flex; justify-content: center; padding: 60px 0; }
.empty { display: flex; flex-direction: column; align-items: center; gap: 10px; padding: 60px 0; color: #AEAEB2; font-size: 14px; }
.empty-link { font-size: 14px; color: #0071E3; font-weight: 500; text-decoration: none; }

.list-card { background: #FFFFFF; border-radius: 14px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.06); }
.list-row { display: flex; align-items: center; gap: 14px; padding: 14px 16px; border-bottom: 1px solid rgba(0,0,0,0.06); }
.list-row.last { border-bottom: none; }

.item-icon { width: 44px; height: 44px; background: #FFF6E5; border-radius: 11px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.item-body { flex: 1; min-width: 0; }
.item-name { font-size: 15px; font-weight: 600; color: #1D1D1F; }
.item-desc { font-size: 13px; color: #6E6E73; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.item-qty { text-align: right; flex-shrink: 0; }
.qty-num { font-size: 20px; font-weight: 700; color: #1D1D1F; }
.qty-label { font-size: 12px; color: #6E6E73; margin-left: 2px; }
</style>
