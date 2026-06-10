<script lang="ts" setup>
import { useAppStore } from '@/stores/app'
const store = useAppStore()
</script>

<template>
  <div class="home">
    <div class="hero">
      <p class="eyebrow">WELCOME BACK</p>
      <h1 class="hero-title">{{ store.user?.nickName }}</h1>
      <p class="hero-sub">아이템을 거래하고 커뮤니티와 소통하세요</p>
    </div>
    <div class="divider" />
    <div class="cards">
      <router-link to="/music" class="card">
        <div><p class="card-category">MUSIC</p><p class="card-title">뮤직</p><p class="card-desc">트랙 업로드 · 등록</p></div>
        <v-icon color="#ccc" size="18">mdi-arrow-right</v-icon>
      </router-link>
      <router-link to="/board" class="card">
        <div><p class="card-category">COMMUNITY</p><p class="card-title">게시판</p><p class="card-desc">커뮤니티 글 읽기 · 작성</p></div>
        <v-icon color="#ccc" size="18">mdi-arrow-right</v-icon>
      </router-link>
      <router-link to="/games" class="card">
        <div><p class="card-category">GAMING</p><p class="card-title">게임 검색</p><p class="card-desc">캐릭터 전적 · 스탯 조회</p></div>
        <v-icon color="#ccc" size="18">mdi-arrow-right</v-icon>
      </router-link>
      <router-link to="/market" class="card">
        <div><p class="card-category">TRADE</p><p class="card-title">마켓플레이스</p><p class="card-desc">아이템 사고팔기</p></div>
        <v-icon color="#ccc" size="18">mdi-arrow-right</v-icon>
      </router-link>
      <router-link to="/inventory" class="card">
        <div><p class="card-category">STORAGE</p><p class="card-title">인벤토리</p><p class="card-desc">보유 아이템 확인</p></div>
        <v-icon color="#ccc" size="18">mdi-arrow-right</v-icon>
      </router-link>
      <router-link v-if="store.isAdmin" to="/admin" class="card">
        <div><p class="card-category">ADMIN</p><p class="card-title">관리자</p><p class="card-desc">아이템 등록 · 관리</p></div>
        <v-icon color="#ccc" size="18">mdi-arrow-right</v-icon>
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.home { min-height: 100vh; background: #F5F5F5; padding-bottom: 60px; }
.hero { padding: 40px 24px 32px; background: #fff; border-bottom: 1px solid rgba(0,0,0,0.07); }
.eyebrow { font-size: 10px; font-weight: 700; color: #aaa; letter-spacing: 3px; margin-bottom: 12px; }
.hero-title { font-size: 40px; font-weight: 900; color: #111; letter-spacing: -1px; line-height: 1.1; margin-bottom: 10px; }
.hero-sub { font-size: 14px; color: #999; }
.divider { height: 1px; background: rgba(0,0,0,0.07); }
.cards { padding: 8px 0; background: #fff; }
.card { display: flex; align-items: center; justify-content: space-between; padding: 22px 24px; text-decoration: none; border-bottom: 1px solid rgba(0,0,0,0.06); transition: background 0.15s; }
.card:hover { background: #FAFAFA; }
.card-category { font-size: 10px; font-weight: 700; color: #bbb; letter-spacing: 2px; margin-bottom: 5px; }
.card-title { font-size: 18px; font-weight: 700; color: #111; margin-bottom: 3px; }
.card-desc { font-size: 13px; color: #aaa; }
</style>
