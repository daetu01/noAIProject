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
      <p class="eyebrow">STORAGE</p>
      <div class="header-row"><h1 class="page-title">인벤토리</h1><span class="count">{{ items.length }}개</span></div>
    </div>
    <div class="header-divider" />

    <div v-if="errorMsg" class="error-bar">{{ errorMsg }}</div>
    <div v-if="loading" class="loading-wrap"><v-progress-circular indeterminate color="#111" size="28" width="1.5" /></div>
    <div v-else-if="items.length === 0" class="empty">
      <p>— 보유 아이템이 없습니다 —</p>
      <router-link to="/market" class="empty-link">마켓에서 구매하기 →</router-link>
    </div>

    <div v-else class="item-list">
      <div v-for="(item, i) in items" :key="item.itemId" class="item-row" :class="{ last: i === items.length - 1 }">
        <div class="item-icon"><v-icon size="18" color="#bbb">mdi-sword</v-icon></div>
        <div class="item-body">
          <p class="item-name">{{ item.itemName }}</p>
          <p class="item-desc">{{ item.description || '설명 없음' }}</p>
        </div>
        <div class="item-qty">
          <span class="qty-num">{{ item.quantity }}</span>
          <span class="qty-unit">ea</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { min-height: 100vh; background: #F5F5F5; padding: 32px 20px 80px; }
.page-header { margin-bottom: 16px; }
.eyebrow { font-size: 10px; font-weight: 700; color: #aaa; letter-spacing: 3px; margin-bottom: 8px; }
.header-row { display: flex; align-items: flex-end; gap: 10px; }
.page-title { font-size: 36px; font-weight: 900; color: #111; letter-spacing: -0.5px; }
.count { font-size: 12px; color: #aaa; margin-bottom: 5px; }
.header-divider { height: 1px; background: rgba(0,0,0,0.08); margin-bottom: 28px; }
.error-bar { background: #FFF0F0; border: 1px solid rgba(220,50,50,0.2); border-radius: 8px; padding: 10px 14px; font-size: 13px; color: #cc3333; margin-bottom: 16px; }
.loading-wrap { display: flex; justify-content: center; padding: 60px 0; }
.empty { display: flex; flex-direction: column; align-items: center; gap: 12px; padding: 60px 0; font-size: 13px; color: #bbb; letter-spacing: 2px; }
.empty-link { font-size: 13px; color: #999; text-decoration: none; transition: color 0.15s; letter-spacing: 0; }
.empty-link:hover { color: #111; }
.item-list { background: #fff; border: 1px solid rgba(0,0,0,0.07); border-radius: 12px; overflow: hidden; }
.item-row { display: flex; align-items: center; gap: 14px; padding: 16px 18px; border-bottom: 1px solid rgba(0,0,0,0.06); transition: background 0.12s; }
.item-row:hover { background: #FAFAFA; }
.item-row.last { border-bottom: none; }
.item-icon { width: 38px; height: 38px; background: #F5F5F5; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.item-body { flex: 1; min-width: 0; }
.item-name { font-size: 14px; font-weight: 700; color: #111; margin-bottom: 2px; }
.item-desc { font-size: 12px; color: #aaa; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.item-qty { text-align: right; flex-shrink: 0; }
.qty-num { font-size: 22px; font-weight: 900; color: #111; }
.qty-unit { font-size: 11px; color: #bbb; margin-left: 2px; }
</style>
