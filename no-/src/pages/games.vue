<script lang="ts" setup>
import { ref } from 'vue'
import { mapleService, type CharacterInfo } from '@/api/mapleService'

const activeTab = ref<'maple' | 'wow'>('maple')

const mapleQuery = ref('')
const mapleCharacter = ref<CharacterInfo | null>(null)
const mapleLoading = ref(false)
const mapleError = ref('')

async function searchMaple() {
  if (!mapleQuery.value.trim()) return
  mapleLoading.value = true
  mapleError.value = ''
  mapleCharacter.value = null
  try {
    mapleCharacter.value = await mapleService.getCharacter(mapleQuery.value.trim())
  } catch {
    mapleError.value = '캐릭터를 찾을 수 없습니다.'
  } finally {
    mapleLoading.value = false
  }
}

function switchTab(tab: 'maple' | 'wow') {
  activeTab.value = tab
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">게임 검색</h1>
      <p class="page-subtitle">게임별 캐릭터 정보를 확인하세요</p>
    </div>

    <!-- 탭 -->
    <div class="tab-bar">
      <button
        class="tab-item"
        :class="{ active: activeTab === 'maple' }"
        @click="switchTab('maple')"
      >
        <span class="tab-icon">🍁</span>
        메이플스토리
      </button>
      <button
        class="tab-item"
        :class="{ active: activeTab === 'wow' }"
        @click="switchTab('wow')"
      >
        <span class="tab-icon">⚔️</span>
        와우
      </button>
    </div>

    <!-- 메이플스토리 -->
    <div v-if="activeTab === 'maple'">
      <div class="search-wrap">
        <input
          v-model="mapleQuery"
          type="text"
          placeholder="캐릭터명 입력"
          class="search-input"
          @keyup.enter="searchMaple"
        />
        <button class="btn-primary" :disabled="mapleLoading || !mapleQuery.trim()" @click="searchMaple">
          <v-progress-circular v-if="mapleLoading" indeterminate size="14" width="2" color="white" class="mr-1" />
          검색
        </button>
      </div>

      <div v-if="mapleError" class="error-bar">{{ mapleError }}</div>

      <div v-if="mapleCharacter" class="character-card">
        <div class="character-image-wrap">
          <img :src="mapleCharacter.character_image" :alt="mapleCharacter.character_name" class="character-image" />
        </div>
        <div class="character-info">
          <h2 class="character-name">{{ mapleCharacter.character_name }}</h2>
          <p class="character-world">
            {{ mapleCharacter.world_name }}
            <span v-if="mapleCharacter.character_guild_name"> · {{ mapleCharacter.character_guild_name }}</span>
          </p>
          <div class="stat-list">
            <div class="stat-row">
              <span class="stat-label">직업</span>
              <span class="stat-value">{{ mapleCharacter.character_class }}</span>
            </div>
            <div class="stat-row">
              <span class="stat-label">레벨</span>
              <span class="stat-value">Lv. {{ mapleCharacter.character_level }}</span>
            </div>
            <div v-if="mapleCharacter.character_exp_rate" class="stat-row">
              <span class="stat-label">경험치</span>
              <span class="stat-value">{{ mapleCharacter.character_exp_rate }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 와우 (준비 중) -->
    <div v-if="activeTab === 'wow'" class="coming-soon">
      <v-icon size="48" color="#AEAEB2">mdi-sword-cross</v-icon>
      <p class="coming-soon-title">와우</p>
      <p class="coming-soon-sub">준비 중입니다</p>
    </div>
  </div>
</template>

<style scoped>
.page { min-height: 100vh; background: #F5F5F7; padding: 24px 16px; }
.page-header { margin-bottom: 20px; }
.page-title { font-size: 26px; font-weight: 700; color: #1D1D1F; letter-spacing: -0.4px; }
.page-subtitle { font-size: 13px; color: #6E6E73; margin-top: 2px; }

/* 탭 */
.tab-bar {
  display: flex; gap: 8px;
  background: #FFFFFF; border-radius: 14px;
  padding: 6px; margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.tab-item {
  flex: 1; display: flex; align-items: center; justify-content: center; gap: 6px;
  padding: 10px 0; border: none; border-radius: 10px;
  font-size: 14px; font-weight: 500; color: #6E6E73;
  background: transparent; cursor: pointer;
  transition: background 0.15s, color 0.15s;
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif;
}
.tab-item.active { background: #0071E3; color: #FFFFFF; font-weight: 600; }
.tab-icon { font-size: 16px; }

/* 검색 */
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

/* 캐릭터 카드 */
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
.stat-list { display: flex; flex-direction: column; background: #F5F5F7; border-radius: 12px; overflow: hidden; }
.stat-row { display: flex; justify-content: space-between; align-items: center; padding: 13px 16px; border-bottom: 1px solid rgba(0,0,0,0.06); }
.stat-row:last-child { border-bottom: none; }
.stat-label { font-size: 14px; color: #6E6E73; }
.stat-value { font-size: 14px; font-weight: 600; color: #1D1D1F; }

/* 준비 중 */
.coming-soon {
  display: flex; flex-direction: column; align-items: center;
  gap: 10px; padding: 80px 0; color: #AEAEB2;
}
.coming-soon-title { font-size: 17px; font-weight: 600; color: #3A3A3C; }
.coming-soon-sub { font-size: 14px; color: #AEAEB2; }
</style>
