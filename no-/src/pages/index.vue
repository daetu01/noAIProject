<script lang="ts" setup>
import { useAppStore } from '@/stores/app'

const store = useAppStore()
</script>

<template>
  <div class="home">
    <div class="hero">
      <p class="hero-eyebrow">NoAI Market</p>
      <h1 class="hero-title">안녕하세요,<br>{{ store.user?.nickName }}님</h1>
      <p class="hero-sub">아이템을 거래하고 커뮤니티와 소통하세요</p>
    </div>

    <div class="cards">
      <router-link to="/board" class="feature-card">
        <div class="card-icon" style="background: #EBF3FF">
          <v-icon color="#0071E3" size="26">mdi-note-text-outline</v-icon>
        </div>
        <div class="card-body">
          <p class="card-title">게시판</p>
          <p class="card-desc">커뮤니티 글 읽기 · 작성</p>
        </div>
        <v-icon color="#AEAEB2" size="18">mdi-chevron-right</v-icon>
      </router-link>

      <router-link to="/market" class="feature-card">
        <div class="card-icon" style="background: #EAFAF0">
          <v-icon color="#30D158" size="26">mdi-storefront-outline</v-icon>
        </div>
        <div class="card-body">
          <p class="card-title">마켓플레이스</p>
          <p class="card-desc">아이템 사고팔기</p>
        </div>
        <v-icon color="#AEAEB2" size="18">mdi-chevron-right</v-icon>
      </router-link>

      <router-link to="/inventory" class="feature-card">
        <div class="card-icon" style="background: #FFF6E5">
          <v-icon color="#FF9500" size="26">mdi-bag-personal-outline</v-icon>
        </div>
        <div class="card-body">
          <p class="card-title">인벤토리</p>
          <p class="card-desc">보유 아이템 확인</p>
        </div>
        <v-icon color="#AEAEB2" size="18">mdi-chevron-right</v-icon>
      </router-link>

      <router-link v-if="store.isAdmin" to="/admin" class="feature-card">
        <div class="card-icon" style="background: #F5EEFF">
          <v-icon color="#BF5AF2" size="26">mdi-shield-crown-outline</v-icon>
        </div>
        <div class="card-body">
          <p class="card-title">관리자</p>
          <p class="card-desc">아이템 등록 · 관리</p>
        </div>
        <v-icon color="#AEAEB2" size="18">mdi-chevron-right</v-icon>
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.home {
  min-height: 100vh;
  background: #F5F5F7;
  padding: 0 0 40px;
}

.hero {
  background: #FFFFFF;
  padding: 36px 24px 28px;
  border-bottom: 1px solid rgba(0,0,0,0.06);
}

.hero-eyebrow {
  font-size: 13px;
  font-weight: 500;
  color: #0071E3;
  letter-spacing: 0.2px;
  margin-bottom: 6px;
}

.hero-title {
  font-size: 30px;
  font-weight: 700;
  color: #1D1D1F;
  letter-spacing: -0.6px;
  line-height: 1.2;
  margin-bottom: 8px;
}

.hero-sub {
  font-size: 15px;
  color: #6E6E73;
}

.cards {
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.feature-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #FFFFFF;
  border-radius: 14px;
  padding: 16px 16px;
  text-decoration: none;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  transition: transform 0.12s ease, box-shadow 0.12s ease;
}

.feature-card:active {
  transform: scale(0.98);
  box-shadow: 0 1px 2px rgba(0,0,0,0.04);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.card-body {
  flex: 1;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #1D1D1F;
  margin-bottom: 2px;
}

.card-desc {
  font-size: 13px;
  color: #6E6E73;
}
</style>
