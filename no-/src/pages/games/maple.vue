<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { mapleService, type CharacterInfo } from '@/api/mapleService'

const router = useRouter()
const activeTab = ref<'search'>('search')
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
  <div class="page">
    <button class="btn-back" @click="router.push('/games')">
      <v-icon size="14">mdi-chevron-left</v-icon>게임 검색
    </button>

    <div class="page-header">
      <p class="eyebrow">NEXON</p>
      <h1 class="page-title">메이플스토리</h1>
    </div>
    <div class="header-divider" />

    <div class="tab-bar">
      <button class="tab-item" :class="{ active: activeTab === 'search' }" @click="activeTab = 'search'">전적 검색</button>
    </div>

    <div v-if="activeTab === 'search'">
      <div class="search-wrap">
        <input v-model="query" type="text" placeholder="캐릭터명 입력" class="search-input" @keyup.enter="search" />
        <button class="btn-search" :disabled="loading || !query.trim()" @click="search">
          <v-progress-circular v-if="loading" indeterminate size="14" width="1.5" color="#fff" class="mr-1" />
          검색
        </button>
      </div>

      <div v-if="errorMsg" class="error-bar">{{ errorMsg }}</div>

      <div v-if="character" class="result">

        <!-- 캐릭터 이미지 -->
        <div class="char-image-section">
          <img :src="character.character_image" :alt="character.character_name" class="char-image" />
        </div>

        <div class="char-header">
          <p class="char-class">{{ character.character_class }}</p>
          <h2 class="char-name">{{ character.character_name }}</h2>
          <p class="char-world">{{ character.world_name }}<span v-if="character.character_guild_name"> · &lt;{{ character.character_guild_name }}&gt;</span></p>
          <div class="badge-row">
            <span class="badge">Lv. {{ character.character_level }}</span>
            <span class="badge muted">인기도 {{ character.popularity }}</span>
          </div>
        </div>

        <div v-if="character.character_exp_rate" class="exp-wrap">
          <div class="exp-label-row">
            <span class="exp-label">EXP</span>
            <span class="exp-val">{{ character.character_exp_rate }}%</span>
          </div>
          <div class="exp-bg"><div class="exp-fill" :style="{ width: character.character_exp_rate + '%' }" /></div>
        </div>

        <div class="section-divider" />
        <p class="section-label">기본 정보</p>
        <div class="info-grid">
          <div class="info-item"><span class="info-key">성별</span><span class="info-val">{{ character.character_gender }}</span></div>
          <div class="info-item"><span class="info-key">직업 차수</span><span class="info-val">{{ character.character_class_level }}차</span></div>
          <div class="info-item"><span class="info-key">남은 AP</span><span class="info-val" :class="{ highlight: character.remain_ap > 0 }">{{ character.remain_ap }}</span></div>
          <div class="info-item"><span class="info-key">경험치</span><span class="info-val">{{ character.character_exp_rate }}%</span></div>
        </div>

        <div class="section-divider" />
        <p class="section-label">상세 스탯</p>
        <div class="stat-list">
          <div v-for="stat in character.final_stat" :key="stat.stat_name" class="stat-row">
            <span class="stat-name">{{ stat.stat_name }}</span>
            <span class="stat-val">{{ stat.stat_value }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { min-height: 100vh; background: #F5F5F5; padding: 28px 20px 80px; }
.btn-back { display: inline-flex; align-items: center; gap: 2px; background: none; border: none; cursor: pointer; font-size: 12px; font-weight: 600; color: #aaa; padding: 4px 0; margin-bottom: 28px; transition: color 0.15s; }
.btn-back:hover { color: #111; }
.page-header { margin-bottom: 16px; }
.eyebrow { font-size: 10px; font-weight: 700; color: #aaa; letter-spacing: 3px; margin-bottom: 8px; }
.page-title { font-size: 36px; font-weight: 900; color: #111; letter-spacing: -0.5px; }
.header-divider { height: 1px; background: rgba(0,0,0,0.08); margin-bottom: 24px; }

.tab-bar { display: flex; border-bottom: 1px solid rgba(0,0,0,0.08); margin-bottom: 24px; }
.tab-item { padding: 10px 0; margin-right: 24px; background: none; border: none; font-size: 13px; font-weight: 600; color: #bbb; cursor: pointer; border-bottom: 2px solid transparent; transition: all 0.15s; }
.tab-item.active { color: #111; border-bottom-color: #111; }

.search-wrap { display: flex; gap: 8px; margin-bottom: 20px; }
.search-input { flex: 1; padding: 13px 16px; background: #fff; border: 1px solid rgba(0,0,0,0.1); border-radius: 8px; font-size: 14px; color: #111; outline: none; transition: border-color 0.15s; }
.search-input::placeholder { color: #ccc; }
.search-input:focus { border-color: rgba(0,0,0,0.25); }
.btn-search { display: inline-flex; align-items: center; gap: 4px; background: #111; color: #fff; border: none; border-radius: 8px; padding: 13px 22px; font-size: 13px; font-weight: 800; cursor: pointer; transition: opacity 0.15s; }
.btn-search:hover:not(:disabled) { opacity: 0.8; }
.btn-search:disabled { opacity: 0.3; cursor: not-allowed; }
.error-bar { background: #FFF0F0; border: 1px solid rgba(220,50,50,0.2); border-radius: 8px; padding: 10px 14px; font-size: 13px; color: #cc3333; margin-bottom: 20px; }

/* 캐릭터 이미지 크게 */
.char-image-section { display: flex; justify-content: center; background: #fff; border: 1px solid rgba(0,0,0,0.07); border-radius: 12px; padding: 20px 0 0; margin-bottom: 20px; overflow: hidden; }
.char-image { width: 280px; height: 280px; object-fit: contain; image-rendering: pixelated; display: block; }

.char-header { text-align: center; margin-bottom: 20px; }
.char-class { font-size: 10px; font-weight: 700; color: #bbb; letter-spacing: 2px; margin-bottom: 6px; }
.char-name { font-size: 28px; font-weight: 900; color: #111; letter-spacing: -0.5px; margin-bottom: 6px; }
.char-world { font-size: 13px; color: #999; margin-bottom: 14px; }
.badge-row { display: flex; justify-content: center; gap: 8px; }
.badge { font-size: 12px; font-weight: 700; color: #111; background: rgba(0,0,0,0.07); border-radius: 4px; padding: 4px 12px; }
.badge.muted { color: #999; background: rgba(0,0,0,0.04); }

.exp-wrap { margin-bottom: 20px; }
.exp-label-row { display: flex; justify-content: space-between; margin-bottom: 6px; }
.exp-label { font-size: 10px; font-weight: 700; color: #bbb; letter-spacing: 2px; }
.exp-val { font-size: 11px; color: #999; }
.exp-bg { height: 4px; background: rgba(0,0,0,0.08); border-radius: 2px; overflow: hidden; }
.exp-fill { height: 100%; background: #111; border-radius: 2px; transition: width 0.5s ease; }

.section-divider { height: 1px; background: rgba(0,0,0,0.07); margin: 20px 0; }
.section-label { font-size: 10px; font-weight: 700; color: #aaa; letter-spacing: 3px; margin-bottom: 14px; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin-bottom: 4px; }
.info-item { background: #fff; border: 1px solid rgba(0,0,0,0.07); border-radius: 10px; padding: 14px 16px; }
.info-key { font-size: 11px; color: #bbb; margin-bottom: 4px; display: block; }
.info-val { font-size: 16px; font-weight: 800; color: #111; }
.info-val.highlight { color: #16A34A; }

.stat-list { background: #fff; border: 1px solid rgba(0,0,0,0.07); border-radius: 10px; overflow: hidden; }
.stat-row { display: flex; justify-content: space-between; align-items: center; padding: 11px 16px; border-bottom: 1px solid rgba(0,0,0,0.05); }
.stat-row:last-child { border-bottom: none; }
.stat-name { font-size: 13px; color: #888; }
.stat-val { font-size: 13px; font-weight: 700; color: #111; }
</style>
