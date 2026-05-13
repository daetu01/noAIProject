<script lang="ts" setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<template>
  <div class="page">
    <div class="page-header">
      <p class="eyebrow">GAMING</p>
      <h1 class="page-title">게임 검색</h1>
    </div>
    <div class="header-divider" />

    <div class="game-row" @click="router.push('/games/maple')">
      <div>
        <p class="game-category">NEXON</p>
        <p class="game-title">메이플스토리</p>
        <p class="game-desc">전적 검색 · 캐릭터 정보 · 스탯</p>
      </div>
      <div class="game-right">
        <span class="badge-live">● LIVE</span>
        <v-icon color="#ccc" size="18">mdi-arrow-right</v-icon>
      </div>
    </div>

    <div class="game-row disabled">
      <div>
        <p class="game-category">BLIZZARD</p>
        <p class="game-title">World of Warcraft</p>
        <p class="game-desc">준비 중</p>
      </div>
      <span class="badge-soon">SOON</span>
    </div>
  </div>
</template>

<style scoped>
.page { min-height: 100vh; background: #F5F5F5; padding: 32px 20px 80px; }
.page-header { margin-bottom: 16px; }
.eyebrow { font-size: 10px; font-weight: 700; color: #aaa; letter-spacing: 3px; margin-bottom: 8px; }
.page-title { font-size: 36px; font-weight: 900; color: #111; letter-spacing: -0.5px; }
.header-divider { height: 1px; background: rgba(0,0,0,0.08); margin-bottom: 8px; }

.game-row { display: flex; align-items: center; justify-content: space-between; padding: 26px 0; border-bottom: 1px solid rgba(0,0,0,0.07); cursor: pointer; transition: opacity 0.15s; }
.game-row:hover:not(.disabled) { opacity: 0.7; }
.game-row.disabled { cursor: default; opacity: 0.35; }
.game-category { font-size: 10px; font-weight: 700; color: #bbb; letter-spacing: 2px; margin-bottom: 6px; }
.game-title { font-size: 22px; font-weight: 800; color: #111; margin-bottom: 4px; letter-spacing: -0.3px; }
.game-desc { font-size: 13px; color: #999; }
.game-right { display: flex; align-items: center; gap: 12px; }
.badge-live { font-size: 11px; font-weight: 700; color: #22C55E; letter-spacing: 1px; }
.badge-soon { font-size: 11px; font-weight: 700; color: #ccc; letter-spacing: 1px; }
</style>
