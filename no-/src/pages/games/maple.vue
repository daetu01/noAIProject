<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { mapleService, type CharacterInfo } from '@/api/mapleService'

const router = useRouter()
const query = ref('')
const character = ref<CharacterInfo | null>(null)
const loading = ref(false)
const errorMsg = ref('')

async function search() {
  if (!query.value.trim()) return
  loading.value = true; errorMsg.value = ''; character.value = null
  try { character.value = await mapleService.getCharacter(query.value.trim()) }
  catch { errorMsg.value = '캐릭터를 찾을 수 없습니다.' }
  finally { loading.value = false }
}
</script>

<template>
  <div class="maple-page">

    <!-- 배경 파티클 -->
    <div class="bg-orb bg-orb1" />
    <div class="bg-orb bg-orb2" />
    <div class="bg-grid" />

    <!-- 헤더 -->
    <header class="maple-header">
      <button class="btn-back" @click="router.push('/games')">
        <span>←</span>
        <span>Gaming Hub</span>
      </button>
      <div class="maple-header-center">
        <p class="maple-eyebrow">NEXON · MAPLESTORY</p>
        <h1 class="maple-title">캐릭터 검색</h1>
      </div>
      <div style="width:130px" />
    </header>

    <!-- 검색 영역 -->
    <main class="maple-main">
      <div class="search-section">
        <div class="search-card">
          <div class="search-card-top">
            <span class="search-icon-wrap">🍁</span>
            <div>
              <p class="search-card-title">캐릭터 이름으로 검색</p>
              <p class="search-card-sub">메이플스토리 캐릭터명을 입력하세요</p>
            </div>
          </div>
          <div class="search-row">
            <input
              v-model="query"
              type="text"
              placeholder="캐릭터명 입력..."
              class="search-input"
              @keyup.enter="search"
              autocomplete="off"
            />
            <button class="btn-search" :disabled="loading || !query.trim()" @click="search">
              <span v-if="loading" class="spinner" />
              <span v-else>검색</span>
            </button>
          </div>
          <div v-if="errorMsg" class="error-bar">
            <span class="error-icon">⚠</span>
            {{ errorMsg }}
          </div>
        </div>
      </div>

      <!-- 검색 결과 -->
      <transition name="result-fade">
        <div v-if="character" class="result-grid">

          <!-- 캐릭터 카드 -->
          <div class="char-card">
            <div class="char-image-wrap">
              <div class="char-image-bg" />
              <img :src="character.character_image" :alt="character.character_name" class="char-image" />
            </div>

            <div class="char-identity">
              <p class="char-class-label">{{ character.character_class }}</p>
              <h2 class="char-name">{{ character.character_name }}</h2>
              <p class="char-world">
                {{ character.world_name }}
                <span v-if="character.character_guild_name" class="char-guild">
                  &lt;{{ character.character_guild_name }}&gt;
                </span>
              </p>
            </div>

            <div class="char-badges">
              <div class="char-badge primary">
                <span class="badge-label">LEVEL</span>
                <span class="badge-val">{{ character.character_level }}</span>
              </div>
              <div class="char-badge secondary">
                <span class="badge-label">인기도</span>
                <span class="badge-val">{{ character.popularity }}</span>
              </div>
              <div class="char-badge" :class="character.remain_ap > 0 ? 'warning' : 'secondary'">
                <span class="badge-label">남은 AP</span>
                <span class="badge-val">{{ character.remain_ap }}</span>
              </div>
            </div>

            <!-- EXP Bar -->
            <div v-if="character.character_exp_rate" class="exp-section">
              <div class="exp-header">
                <span class="exp-label">EXP</span>
                <span class="exp-val">{{ character.character_exp_rate }}%</span>
              </div>
              <div class="exp-track">
                <div class="exp-fill" :style="{ width: character.character_exp_rate + '%' }" />
                <div class="exp-glow" :style="{ width: character.character_exp_rate + '%' }" />
              </div>
            </div>
          </div>

          <!-- 오른쪽 패널 -->
          <div class="detail-panels">

            <!-- 기본 정보 -->
            <div class="info-panel">
              <p class="panel-label">기본 정보</p>
              <div class="info-grid">
                <div class="info-cell">
                  <span class="info-key">성별</span>
                  <span class="info-val">{{ character.character_gender }}</span>
                </div>
                <div class="info-cell">
                  <span class="info-key">직업 차수</span>
                  <span class="info-val">{{ character.character_class_level }}차</span>
                </div>
                <div class="info-cell">
                  <span class="info-key">직업</span>
                  <span class="info-val">{{ character.character_class }}</span>
                </div>
                <div class="info-cell">
                  <span class="info-key">경험치</span>
                  <span class="info-val">{{ character.character_exp_rate }}%</span>
                </div>
              </div>
            </div>

            <!-- 상세 스탯 -->
            <div class="stat-panel">
              <p class="panel-label">파이널 스탯</p>
              <div class="stat-list">
                <div v-for="stat in character.final_stat" :key="stat.stat_name" class="stat-row">
                  <span class="stat-name">{{ stat.stat_name }}</span>
                  <span class="stat-val">{{ stat.stat_value }}</span>
                </div>
              </div>
            </div>

          </div>
        </div>
      </transition>

      <!-- 빈 상태 -->
      <div v-if="!character && !loading && !errorMsg" class="empty-state">
        <div class="empty-icon">🍁</div>
        <p class="empty-title">캐릭터를 검색해보세요</p>
        <p class="empty-sub">메이플스토리 캐릭터명을 입력하면<br>실시간으로 정보를 불러옵니다</p>
      </div>

    </main>
  </div>
</template>

<style scoped>
/* ─── Layout ────────────────────────────────────────────── */
.maple-page {
  min-height: 100vh;
  background: #0b0b0f;
  color: #d0d0e8;
  font-family: 'Inter', -apple-system, sans-serif;
  -webkit-font-smoothing: antialiased;
  position: relative;
  overflow-x: hidden;
  margin-top: -56px;
  padding-bottom: 80px;
}

/* ─── Background ─────────────────────────────────────────── */
.bg-orb {
  position: fixed; border-radius: 50%;
  filter: blur(80px); pointer-events: none; z-index: 0;
}
.bg-orb1 {
  width: 500px; height: 500px;
  background: rgba(167,139,250,.06);
  top: -100px; right: -100px;
}
.bg-orb2 {
  width: 400px; height: 400px;
  background: rgba(91,156,246,.05);
  bottom: 0; left: -100px;
}
.bg-grid {
  position: fixed; inset: 0; z-index: 0;
  background-image:
    linear-gradient(rgba(167,139,250,.025) 1px, transparent 1px),
    linear-gradient(90deg, rgba(167,139,250,.025) 1px, transparent 1px);
  background-size: 40px 40px;
  pointer-events: none;
}

/* ─── Header ─────────────────────────────────────────────── */
.maple-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 18px 32px;
  background: rgba(11,11,15,0.88);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255,255,255,0.06);
  position: sticky; top: 0; z-index: 100;
}
.maple-header-center { text-align: center; }

.btn-back {
  display: inline-flex; align-items: center; gap: 8px;
  padding: 7px 14px;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 10px;
  color: #7070a0; font-size: 12px; font-weight: 600;
  font-family: inherit; cursor: pointer;
  transition: all .2s ease; width: 130px;
}
.btn-back:hover { background: rgba(167,139,250,.1); border-color: rgba(167,139,250,.3); color: #a78bfa; }
.btn-back span:first-child { transition: transform .2s ease; }
.btn-back:hover span:first-child { transform: translateX(-3px); }

.maple-eyebrow {
  font-size: 8px; font-weight: 700; color: #a78bfa;
  letter-spacing: .25em; text-transform: uppercase; margin-bottom: 3px;
}
.maple-title {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 20px; font-weight: 700; color: #f0f0ff; letter-spacing: -.02em;
}

/* ─── Main ───────────────────────────────────────────────── */
.maple-main {
  position: relative; z-index: 1;
  max-width: 1000px; margin: 0 auto;
  padding: 48px 32px 0;
}

/* ─── Search ─────────────────────────────────────────────── */
.search-section { margin-bottom: 40px; }
.search-card {
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 20px;
  padding: 28px 32px;
  backdrop-filter: blur(12px);
}
.search-card-top { display: flex; align-items: center; gap: 14px; margin-bottom: 20px; }
.search-icon-wrap { font-size: 32px; filter: drop-shadow(0 0 12px rgba(167,139,250,.7)); }
.search-card-title {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 16px; font-weight: 700; color: #f0f0ff; margin-bottom: 3px;
}
.search-card-sub { font-size: 12px; color: #5060a0; }

.search-row { display: flex; gap: 10px; }
.search-input {
  flex: 1; padding: 13px 18px;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 12px;
  font-size: 14px; color: #f0f0ff; font-family: inherit;
  outline: none; transition: all .2s ease;
}
.search-input::placeholder { color: #3a3a5a; }
.search-input:focus {
  background: rgba(255,255,255,0.07);
  border-color: rgba(167,139,250,0.4);
  box-shadow: 0 0 0 3px rgba(167,139,250,0.1);
}
.btn-search {
  padding: 13px 28px;
  background: linear-gradient(135deg, #5b9cf6 0%, #a78bfa 100%);
  border: none; border-radius: 12px;
  font-size: 14px; font-weight: 700; color: #fff; font-family: inherit;
  cursor: pointer; transition: all .2s ease;
  display: flex; align-items: center; gap: 8px; white-space: nowrap;
  min-width: 88px; justify-content: center;
}
.btn-search:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(91,156,246,.4); }
.btn-search:disabled { opacity: 0.4; cursor: not-allowed; }

.spinner {
  width: 16px; height: 16px; border-radius: 50%;
  border: 2px solid rgba(255,255,255,.3);
  border-top-color: #fff;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.error-bar {
  display: flex; align-items: center; gap: 8px;
  margin-top: 14px;
  padding: 10px 16px;
  background: rgba(255,71,87,0.08);
  border: 1px solid rgba(255,71,87,0.2);
  border-radius: 10px;
  font-size: 13px; color: #ff6b7a;
}
.error-icon { font-size: 14px; }

/* ─── Result grid ────────────────────────────────────────── */
.result-grid {
  display: grid; grid-template-columns: 300px 1fr;
  gap: 16px; align-items: start;
}

/* ─── Character card ─────────────────────────────────────── */
.char-card {
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(167,139,250,0.15);
  border-radius: 20px; overflow: hidden;
  backdrop-filter: blur(12px);
}

.char-image-wrap {
  position: relative; height: 240px;
  background: linear-gradient(160deg, #1a0b2e 0%, #0e0818 100%);
  display: flex; align-items: flex-end; justify-content: center;
  overflow: hidden;
}
.char-image-bg {
  position: absolute; inset: 0;
  background: radial-gradient(circle at 50% 60%, rgba(167,139,250,.2) 0%, transparent 65%);
}
.char-image {
  width: 200px; height: 210px;
  object-fit: contain; image-rendering: pixelated;
  position: relative; z-index: 1;
  filter: drop-shadow(0 0 20px rgba(167,139,250,.5));
}

.char-identity {
  padding: 20px 20px 0;
  text-align: center;
}
.char-class-label {
  font-size: 9px; font-weight: 700; color: #a78bfa;
  letter-spacing: .2em; text-transform: uppercase; margin-bottom: 6px;
}
.char-name {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 22px; font-weight: 700; color: #f0f0ff;
  letter-spacing: -.02em; margin-bottom: 6px;
}
.char-world { font-size: 12px; color: #5060a0; }
.char-guild { color: #a78bfa; margin-left: 6px; }

.char-badges {
  display: flex; justify-content: center; gap: 8px;
  padding: 16px 20px;
}
.char-badge {
  flex: 1; text-align: center;
  padding: 10px 8px;
  border-radius: 10px; border: 1px solid;
}
.char-badge.primary {
  background: rgba(91,156,246,.1); border-color: rgba(91,156,246,.25);
}
.char-badge.secondary {
  background: rgba(255,255,255,.03); border-color: rgba(255,255,255,.07);
}
.char-badge.warning {
  background: rgba(251,146,60,.1); border-color: rgba(251,146,60,.25);
}
.badge-label {
  display: block; font-size: 8px; font-weight: 700; color: #5060a0;
  letter-spacing: .1em; text-transform: uppercase; margin-bottom: 4px;
}
.char-badge.primary .badge-label { color: #5b9cf6; }
.char-badge.warning .badge-label { color: #fb923c; }
.badge-val {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 18px; font-weight: 800; color: #f0f0ff;
}
.char-badge.primary .badge-val { color: #5b9cf6; }
.char-badge.warning .badge-val { color: #fb923c; }

/* ─── EXP ────────────────────────────────────────────────── */
.exp-section { padding: 0 20px 20px; }
.exp-header {
  display: flex; justify-content: space-between;
  font-size: 10px; font-weight: 700; margin-bottom: 8px;
}
.exp-label { color: #a78bfa; letter-spacing: .1em; }
.exp-val { color: #d0d0e8; }
.exp-track {
  height: 5px; background: rgba(255,255,255,.06);
  border-radius: 3px; overflow: hidden; position: relative;
}
.exp-fill {
  height: 100%; border-radius: 3px;
  background: linear-gradient(90deg, #5b9cf6, #a78bfa);
  transition: width .6s ease;
  position: relative; z-index: 1;
}
.exp-glow {
  position: absolute; top: 0; left: 0; height: 100%;
  border-radius: 3px;
  background: linear-gradient(90deg, #5b9cf6, #a78bfa);
  filter: blur(4px); opacity: .5;
  transition: width .6s ease;
}

/* ─── Detail panels ──────────────────────────────────────── */
.detail-panels { display: flex; flex-direction: column; gap: 12px; }

.info-panel, .stat-panel {
  background: rgba(255,255,255,0.02);
  border: 1px solid rgba(255,255,255,0.07);
  border-radius: 16px; padding: 20px 22px;
  backdrop-filter: blur(8px);
}
.panel-label {
  font-size: 9px; font-weight: 700; color: #a78bfa;
  letter-spacing: .2em; text-transform: uppercase; margin-bottom: 16px;
}

.info-grid {
  display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px;
}
.info-cell {
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 10px; padding: 12px 14px;
}
.info-key {
  display: block; font-size: 9px; font-weight: 700; color: #4a4a6a;
  letter-spacing: .08em; text-transform: uppercase; margin-bottom: 5px;
}
.info-val { font-size: 15px; font-weight: 700; color: #f0f0ff; }

.stat-list {
  display: grid; grid-template-columns: repeat(2, 1fr);
  gap: 1px;
  background: rgba(255,255,255,0.04);
  border-radius: 10px; overflow: hidden;
}
.stat-row {
  display: flex; justify-content: space-between; align-items: center;
  padding: 10px 14px;
  background: rgba(11,11,15,0.8);
  transition: background .1s;
}
.stat-row:hover { background: rgba(167,139,250,0.05); }
.stat-name { font-size: 11px; color: #6060a0; }
.stat-val { font-size: 12px; font-weight: 700; color: #d0d0e8; font-family: 'SF Mono', 'Fira Code', monospace; }

/* ─── Empty state ────────────────────────────────────────── */
.empty-state {
  text-align: center; padding: 80px 0;
}
.empty-icon {
  font-size: 56px; margin-bottom: 20px;
  filter: drop-shadow(0 0 20px rgba(167,139,250,.4));
  opacity: .6;
}
.empty-title {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 20px; font-weight: 700; color: #3a3a6a; margin-bottom: 10px;
}
.empty-sub { font-size: 13px; color: #2a2a4a; line-height: 1.7; }

/* ─── Animation ──────────────────────────────────────────── */
.result-fade-enter-active { transition: opacity .4s ease, transform .4s ease; }
.result-fade-enter-from { opacity: 0; transform: translateY(16px); }

@media (max-width: 768px) {
  .result-grid { grid-template-columns: 1fr; }
  .maple-main { padding: 32px 20px 0; }
}
</style>
