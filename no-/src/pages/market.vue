<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { marketService, type MarketItem } from '@/api/marketService'

const items = ref<MarketItem[]>([])
const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const selected = ref<MarketItem | null>(null)
const buying = ref(false)

async function load() {
  loading.value = true
  try { items.value = await marketService.getAll() }
  catch { errorMsg.value = '마켓 목록을 불러오지 못했습니다.' }
  finally { loading.value = false }
}

async function confirmBuy() {
  if (!selected.value) return
  buying.value = true
  try {
    await marketService.buy({ id: selected.value.id, name: selected.value.itemName, price: selected.value.price })
    successMsg.value = `${selected.value.itemName} 구매 완료!`
    selected.value = null
    await load()
  } catch { errorMsg.value = '구매 실패. 잔액을 확인해주세요.' }
  finally { buying.value = false }
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h1 class="page-title">마켓플레이스</h1>
        <p class="page-subtitle">{{ items.length }}개 상품</p>
      </div>
    </div>

    <div v-if="errorMsg" class="error-bar">{{ errorMsg }}</div>
    <div v-if="successMsg" class="success-bar">{{ successMsg }}</div>

    <div v-if="loading" class="loading-wrap">
      <v-progress-circular indeterminate color="#0071E3" size="32" width="2.5" />
    </div>

    <div v-else-if="items.length === 0" class="empty">
      <v-icon size="40" color="#AEAEB2">mdi-storefront-outline</v-icon>
      <p>등록된 상품이 없습니다</p>
    </div>

    <div v-else class="item-grid">
      <div v-for="item in items" :key="item.id" class="item-card" @click="selected = item">
        <div class="item-icon">
          <v-icon size="28" color="#0071E3">mdi-sword</v-icon>
        </div>
        <p class="item-name">{{ item.itemName }}</p>
        <p class="item-seller">{{ item.sellerNickName }}</p>
        <div class="item-footer">
          <span class="item-price">{{ item.price.toLocaleString() }}G</span>
          <span class="item-buy">구매</span>
        </div>
      </div>
    </div>

    <!-- 구매 확인 바텀시트 -->
    <div v-if="selected" class="overlay" @click.self="selected = null">
      <div class="sheet">
        <div class="sheet-handle" />
        <p class="sheet-title">구매 확인</p>
        <div class="sheet-item">
          <div class="sheet-icon">
            <v-icon size="28" color="#0071E3">mdi-sword</v-icon>
          </div>
          <div>
            <p class="sheet-item-name">{{ selected.itemName }}</p>
            <p class="sheet-item-seller">{{ selected.sellerNickName }}</p>
          </div>
          <span class="sheet-item-price">{{ selected.price.toLocaleString() }}G</span>
        </div>
        <button class="btn-buy" :disabled="buying" @click="confirmBuy">
          <v-progress-circular v-if="buying" indeterminate size="16" width="2" color="white" class="mr-1" />
          {{ selected.price.toLocaleString() }}G로 구매하기
        </button>
        <button class="btn-sheet-cancel" @click="selected = null">취소</button>
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
.success-bar { background: rgba(48,209,88,0.1); border: 1px solid rgba(48,209,88,0.2); border-radius: 10px; padding: 10px 14px; font-size: 13px; color: #28A745; margin-bottom: 12px; }
.loading-wrap { display: flex; justify-content: center; padding: 60px 0; }
.empty { display: flex; flex-direction: column; align-items: center; gap: 10px; padding: 60px 0; color: #AEAEB2; font-size: 14px; }

.item-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }

.item-card {
  background: #FFFFFF; border-radius: 14px;
  padding: 16px 14px 14px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: transform 0.12s ease;
}
.item-card:active { transform: scale(0.96); }

.item-icon { width: 48px; height: 48px; background: #EBF3FF; border-radius: 12px; display: flex; align-items: center; justify-content: center; margin-bottom: 10px; }
.item-name { font-size: 14px; font-weight: 600; color: #1D1D1F; margin-bottom: 2px; }
.item-seller { font-size: 12px; color: #6E6E73; margin-bottom: 10px; }
.item-footer { display: flex; align-items: center; justify-content: space-between; }
.item-price { font-size: 14px; font-weight: 700; color: #1D1D1F; }
.item-buy { font-size: 12px; font-weight: 600; color: #0071E3; }

/* Bottom sheet */
.overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 100; display: flex; align-items: flex-end; justify-content: center; }
.sheet { background: #FFFFFF; border-radius: 20px 20px 0 0; width: 100%; max-width: 600px; padding: 12px 20px 40px; }
.sheet-handle { width: 36px; height: 4px; background: #D2D2D7; border-radius: 2px; margin: 0 auto 20px; }
.sheet-title { font-size: 17px; font-weight: 700; color: #1D1D1F; margin-bottom: 16px; }
.sheet-item { display: flex; align-items: center; gap: 12px; background: #F5F5F7; border-radius: 12px; padding: 14px; margin-bottom: 20px; }
.sheet-icon { width: 44px; height: 44px; background: #EBF3FF; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.sheet-item-name { font-size: 15px; font-weight: 600; color: #1D1D1F; }
.sheet-item-seller { font-size: 13px; color: #6E6E73; }
.sheet-item-price { margin-left: auto; font-size: 16px; font-weight: 700; color: #1D1D1F; }
.btn-buy { width: 100%; padding: 15px; background: #0071E3; color: white; border: none; border-radius: 12px; font-size: 15px; font-weight: 600; cursor: pointer; margin-bottom: 10px; font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif; transition: background 0.15s; display: flex; align-items: center; justify-content: center; }
.btn-buy:hover:not(:disabled) { background: #0077ED; }
.btn-buy:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-sheet-cancel { width: 100%; padding: 15px; background: #F5F5F7; color: #1D1D1F; border: none; border-radius: 12px; font-size: 15px; font-weight: 500; cursor: pointer; font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif; }
</style>
