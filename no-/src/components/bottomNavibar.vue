<script lang="ts" setup>
import { useRoute } from 'vue-router'
import { computed } from 'vue'
const route = useRoute()
const current = computed(() => route.path)
</script>

<template>
  <v-bottom-navigation
    :model-value="current" grow height="60"
    style="border-top:1px solid rgba(0,0,0,0.08); background:rgba(255,255,255,0.95); backdrop-filter:blur(20px);"
    elevation="0"
  >
    <v-btn value="/" to="/" color="#111" style="min-width:0; color:#aaa">
      <v-icon size="21">{{ current === '/' ? 'mdi-home' : 'mdi-home-outline' }}</v-icon>
      <span style="font-size:10px; margin-top:2px">홈</span>
    </v-btn>
    <v-btn value="/board" to="/board" color="#111" style="min-width:0; color:#aaa">
      <v-icon size="21">{{ current === '/board' ? 'mdi-note-text' : 'mdi-note-text-outline' }}</v-icon>
      <span style="font-size:10px; margin-top:2px">게시판</span>
    </v-btn>
    <v-btn value="/market" to="/market" color="#111" style="min-width:0; color:#aaa">
      <v-icon size="21">{{ current === '/market' ? 'mdi-storefront' : 'mdi-storefront-outline' }}</v-icon>
      <span style="font-size:10px; margin-top:2px">마켓</span>
    </v-btn>
    <v-btn value="/inventory" to="/inventory" color="#111" style="min-width:0; color:#aaa">
      <v-icon size="21">{{ current === '/inventory' ? 'mdi-bag-personal' : 'mdi-bag-personal-outline' }}</v-icon>
      <span style="font-size:10px; margin-top:2px">인벤토리</span>
    </v-btn>
  </v-bottom-navigation>
</template>
