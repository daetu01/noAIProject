<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'

const store = useAppStore()
const router = useRouter()
const drawer = ref(false)

function logout() {
  store.logout()
  router.push('/login')
}
</script>

<template>
  <v-app-bar flat height="52" class="apple-glass px-3">
    <v-app-bar-nav-icon variant="text" size="small" color="#333" @click.stop="drawer = !drawer" />
    <v-toolbar-title style="font-size:15px; font-weight:800; color:#111; letter-spacing:0.3px">NoAI</v-toolbar-title>
    <v-spacer />
    <template v-if="store.isLoggedIn">
      <span class="mr-3" style="font-size:12px; color:#888">{{ store.user?.nickName }}</span>
      <v-btn variant="text" size="small" style="font-size:12px; color:#888; font-weight:500" @click="logout">로그아웃</v-btn>
    </template>
    <template v-else>
      <v-btn variant="text" size="small" style="font-size:12px; color:#111; font-weight:700" to="/login">로그인</v-btn>
    </template>
  </v-app-bar>

  <v-navigation-drawer v-model="drawer" temporary width="260" class="light-drawer">
    <div style="padding:28px 24px 16px">
      <p style="font-size:20px; font-weight:900; color:#111">NoAI</p>
      <p style="font-size:12px; color:#aaa; margin-top:4px">{{ store.user?.nickName }}</p>
    </div>
    <div style="height:1px; background:rgba(0,0,0,0.08); margin:0 24px 12px" />
    <v-list nav density="compact" class="px-3">
      <v-list-item prepend-icon="mdi-home-outline"         title="홈"           to="/"          rounded="lg" base-color="#111" />
      <v-list-item prepend-icon="mdi-city"               title="도시 모니터링"  to="/city"      rounded="lg" base-color="#111" />
      <v-list-item prepend-icon="mdi-note-text-outline"    title="게시판"        to="/board"     rounded="lg" base-color="#111" />
      <v-list-item prepend-icon="mdi-sword"                title="게임 검색"     to="/games"     rounded="lg" base-color="#111" />
      <v-list-item prepend-icon="mdi-storefront-outline"   title="마켓플레이스"  to="/market"    rounded="lg" base-color="#111" />
      <v-list-item prepend-icon="mdi-bag-personal-outline" title="인벤토리"      to="/inventory" rounded="lg" base-color="#111" />
      <v-list-item v-if="store.isAdmin" prepend-icon="mdi-shield-crown-outline" title="관리자" to="/admin" rounded="lg" base-color="#111" />
    </v-list>
  </v-navigation-drawer>
</template>

<style>
.light-drawer { background: #fff !important; border-right: 1px solid rgba(0,0,0,0.08) !important; }
.light-drawer .v-list-item { color: #888 !important; }
.light-drawer .v-list-item__prepend .v-icon { color: #bbb !important; }
.light-drawer .v-list-item:hover { background: rgba(0,0,0,0.04) !important; color: #333 !important; }
.light-drawer .v-list-item:hover .v-icon { color: #666 !important; }
.light-drawer .v-list-item--active { background: rgba(0,0,0,0.06) !important; color: #111 !important; }
.light-drawer .v-list-item--active .v-icon { color: #111 !important; }
</style>
