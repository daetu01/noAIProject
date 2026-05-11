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
  <v-app-bar flat height="52" class="apple-glass px-2">
    <v-app-bar-nav-icon
      variant="text"
      size="small"
      color="#1D1D1F"
      @click.stop="drawer = !drawer"
    />

    <v-toolbar-title
      class="font-weight-semibold"
      style="font-size:17px; color:#1D1D1F; letter-spacing:-0.2px"
    >
      NoAI Market
    </v-toolbar-title>

    <v-spacer />

    <template v-if="store.isLoggedIn">
      <span class="mr-3" style="font-size:14px; color:#6E6E73">
        {{ store.user?.nickName }}
      </span>
      <v-btn
        variant="text"
        size="small"
        style="font-size:14px; color:#0071E3; font-weight:500"
        @click="logout"
      >
        로그아웃
      </v-btn>
    </template>
    <template v-else>
      <v-btn
        variant="text"
        size="small"
        style="font-size:14px; color:#0071E3; font-weight:500"
        to="/login"
      >
        로그인
      </v-btn>
    </template>
  </v-app-bar>

  <v-navigation-drawer v-model="drawer" temporary width="260">
    <div class="pa-6 pb-4">
      <p style="font-size:13px; color:#6E6E73; font-weight:500; text-transform:uppercase; letter-spacing:0.6px">
        메뉴
      </p>
    </div>
    <v-list nav density="compact" class="px-3">
      <v-list-item
        prepend-icon="mdi-home-outline"
        title="홈"
        to="/"
        rounded="lg"
        active-color="primary"
      />
      <v-list-item
        prepend-icon="mdi-note-text-outline"
        title="게시판"
        to="/board"
        rounded="lg"
        active-color="primary"
      />
      <v-list-item
        prepend-icon="mdi-storefront-outline"
        title="마켓플레이스"
        to="/market"
        rounded="lg"
        active-color="primary"
      />
      <v-list-item
        prepend-icon="mdi-bag-personal-outline"
        title="인벤토리"
        to="/inventory"
        rounded="lg"
        active-color="primary"
      />
      <v-list-item
        v-if="store.isAdmin"
        prepend-icon="mdi-shield-crown-outline"
        title="관리자"
        to="/admin"
        rounded="lg"
        active-color="primary"
      />
    </v-list>
  </v-navigation-drawer>
</template>
