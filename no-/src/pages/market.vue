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
    selected.value = null; await load()
  } catch { errorMsg.value = '구매 실패. 잔액을 확인해주세요.' }
  finally { buying.value = false }
}
onMounted(load)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <p class="eyebrow">TRADE</p>
      <div class="header-row"><h1 class="page-title">마켓플레이스</h1><span class="count">{{ items.length }}개</span></div>
    </div>
    <div class="header-divider" />

    <div v-if="errorMsg" class="msg-bar error">{{ errorMsg }}</div>
    <div v-if="successMsg" class="msg-bar success">{{ successMsg }}</div>
    <div v-if="loading" class="loading-wrap"><v-progress-circular indeterminate color="#111" size="28" width="1.5" /></div>
    <div v-else-if="items.length === 0" class="empty">— 등록된 상품이 없습니다 —</div>

    <div v-else class="item-grid">
      <div v-for="item in items" :key="item.id" class="item-card" @click="selected = item">
        <div class="item-top">
          <div class="item-icon"><v-icon size="20" color="#bbb">mdi-sword</v-icon></div>
          <p class="item-seller">{{ item.sellerNickName }}</p>
        </div>
        <p class="item-name">{{ item.itemName }}</p>
        <div class="item-footer">
          <span class="item-price">{{ item.price.toLocaleString() }}<span class="gold">G</span></span>
          <span class="item-buy">구매 →</span>
        </div>
      </div>
    </div>

    <div v-if="selected" class="overlay" @click.self="selected = null">
      <div class="sheet">
        <div class="sheet-handle" />
        <p class="eyebrow" style="margin-bottom:16px">CONFIRM PURCHASE</p>
        <div class="sheet-item">
          <div class="sheet-icon"><v-icon size="20" color="#bbb">mdi-sword</v-icon></div>
          <div class="sheet-info"><p class="sheet-name">{{ selected.itemName }}</p><p class="sheet-seller">{{ selected.sellerNickName }}</p></div>
          <span class="sheet-price">{{ selected.price.toLocaleString() }}G</span>
        </div>
        <button class="btn-buy" :disabled="buying" @click="confirmBuy">
          <v-progress-circular v-if="buying" indeterminate size="16" width="2" color="#fff" class="mr-1" />
          {{ selected.price.toLocaleString() }}G 구매 확정
        </button>
        <button class="btn-cancel" @click="selected = null">취소</button>
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
.msg-bar { border-radius: 8px; padding: 10px 14px; font-size: 13px; margin-bottom: 16px; }
.msg-bar.error { background: #FFF0F0; border: 1px solid rgba(220,50,50,0.2); color: #cc3333; }
.msg-bar.success { background: #F0FFF4; border: 1px solid rgba(0,150,80,0.2); color: #009650; }
.loading-wrap { display: flex; justify-content: center; padding: 60px 0; }
.empty { text-align: center; padding: 60px 0; font-size: 13px; color: #bbb; letter-spacing: 2px; }

.item-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.item-card { background: #fff; border: 1px solid rgba(0,0,0,0.07); border-radius: 12px; padding: 16px; cursor: pointer; transition: box-shadow 0.15s; }
.item-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.08); }
.item-top { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.item-icon { width: 38px; height: 38px; background: #F5F5F5; border-radius: 8px; display: flex; align-items: center; justify-content: center; }
.item-seller { font-size: 11px; color: #bbb; }
.item-name { font-size: 14px; font-weight: 700; color: #111; margin-bottom: 14px; line-height: 1.3; }
.item-footer { display: flex; align-items: center; justify-content: space-between; }
.item-price { font-size: 15px; font-weight: 800; color: #111; }
.gold { font-size: 11px; color: #999; margin-left: 2px; }
.item-buy { font-size: 11px; color: #aaa; }

.overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 2000; display: flex; align-items: flex-end; justify-content: center; }
.sheet { background: #fff; border-top: 1px solid rgba(0,0,0,0.08); border-radius: 16px 16px 0 0; width: 100%; max-width: 600px; padding: 14px 24px 48px; }
.sheet-handle { width: 32px; height: 3px; background: #eee; border-radius: 2px; margin: 0 auto 24px; }
.sheet-item { display: flex; align-items: center; gap: 14px; background: #F5F5F5; border-radius: 10px; padding: 14px; margin-bottom: 20px; }
.sheet-icon { width: 40px; height: 40px; background: #fff; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.sheet-info { flex: 1; }
.sheet-name { font-size: 15px; font-weight: 700; color: #111; }
.sheet-seller { font-size: 12px; color: #aaa; margin-top: 2px; }
.sheet-price { font-size: 18px; font-weight: 800; color: #111; }
.btn-buy { width: 100%; padding: 16px; background: #111; color: #fff; border: none; border-radius: 10px; font-size: 15px; font-weight: 800; cursor: pointer; margin-bottom: 10px; display: flex; align-items: center; justify-content: center; transition: opacity 0.15s; }
.btn-buy:hover:not(:disabled) { opacity: 0.85; }
.btn-buy:disabled { opacity: 0.3; cursor: not-allowed; }
.btn-cancel { width: 100%; padding: 15px; background: #F5F5F5; color: #888; border: none; border-radius: 10px; font-size: 14px; cursor: pointer; }
</style>
