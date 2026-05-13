<script lang="ts" setup>
import { ref } from 'vue'
import { mapleService, type CharacterInfo } from '@/api/mapleService'

const query = ref('')
const character = ref<CharacterInfo | null>(null)
const loading = ref(false)
const errorMsg = ref('')

async function search() {
  if (!query.value.trim()) return
  loading.value = true
  errorMsg.value = ''
  character.value = null
  try {
    character.value = await mapleService.getCharacter(query.value.trim())
  } catch {
    errorMsg.value = '캐릭터를 찾을 수 없습니다.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">캐릭터 검색</h1>
      <p class="page-subtitle">메이플스토리 캐릭터 정보</p>
    </div>

    <div class="search-wrap">
      <input
        v-model="query"
        type="text"
        placeholder="캐릭터명 입력"
        class="search-input"
        @keyup.enter="search"
      />
      <button class="btn-primary" :disabled="loading || !query.trim()" @click="search">
        <v-progress-circular v-if="loading" indeterminate size="14" width="2" color="white" class="mr-1" />
        검색
      </button>
    </div>

    <div v-if="errorMsg" class="error-bar">{{ errorMsg }}</div>

    <div v-if="character" class="character-card">
      <div class="character-image-wrap">
        <img :src="character.character_image" :alt="character.character_name" class="character-image" />
      </div>
      <div class="character-info">
        <h2 class="character-name">{{ character.character_name }}</h2>
        <p class="character-world">{{ character.world_name }}<span v-if="character.character_guild_name"> · {{ character.character_guild_name }}</span></p>

        <div class="stat-list">
          <div class="stat-row">
            <span class="stat-label">직업</span>
            <span class="stat-value">{{ character.character_class }}</span>
          </div>
          <div class="stat-row">
            <span class="stat-label">레벨</span>
            <span class="stat-value">Lv. {{ character.character_level }}</span>
          </div>
          <div v-if="character.character_exp_rate" class="stat-row">
            <span class="stat-label">경험치</span>
            <span class="stat-value">{{ character.character_exp_rate }}%</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { min-height: 100vh; background: #F5F5F7; padding: 24px 16px; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 26px; font-weight: 700; color: #1D1D1F; letter-spacing: -0.4px; }
.page-subtitle { font-size: 13px; color: #6E6E73; margin-top: 2px; }

.search-wrap { display: flex; gap: 10px; margin-bottom: 20px; }
.search-input {
  flex: 1; padding: 13px 14px;
  background: #FFFFFF; border: 1px solid rgba(0,0,0,0.1);
  border-radius: 10px; font-size: 15px; color: #1D1D1F; outline: none;
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif;
  transition: border-color 0.15s;
}
.search-input:focus { border-color: #0071E3; }

.btn-primary {
  display: inline-flex; align-items: center;
  background: #0071E3; color: white; border: none;
  border-radius: 10px; padding: 9px 20px; font-size: 14px; font-weight: 600;
  cursor: pointer; transition: background 0.15s; white-space: nowrap;
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif;
}
.btn-primary:hover:not(:disabled) { background: #0077ED; }
.btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }

.error-bar { background: rgba(255,59,48,0.08); border: 1px solid rgba(255,59,48,0.15); border-radius: 10px; padding: 10px 14px; font-size: 13px; color: #FF3B30; margin-bottom: 16px; }

.character-card {
  background: #FFFFFF; border-radius: 16px;
  padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  display: flex; flex-direction: column; align-items: center; gap: 20px;
}

.character-image-wrap {
  background: #F5F5F7; border-radius: 12px;
  padding: 16px; display: flex; align-items: center; justify-content: center;
}
.character-image { width: 400px; height: 400px; object-fit: contain; image-rendering: pixelated; }

.character-info { width: 100%; }
.character-name { font-size: 22px; font-weight: 700; color: #1D1D1F; letter-spacing: -0.4px; margin-bottom: 4px; text-align: center; }
.character-world { font-size: 13px; color: #6E6E73; text-align: center; margin-bottom: 20px; }

.stat-list { display: flex; flex-direction: column; gap: 0; background: #F5F5F7; border-radius: 12px; overflow: hidden; }
.stat-row {
  display: flex; justify-content: space-between; align-items: center;
  padding: 13px 16px; border-bottom: 1px solid rgba(0,0,0,0.06);
}
.stat-row:last-child { border-bottom: none; }
.stat-label { font-size: 14px; color: #6E6E73; }
.stat-value { font-size: 14px; font-weight: 600; color: #1D1D1F; }
</style>