<script lang="ts" setup>
import { useRoute } from 'vue-router'
import { computed } from 'vue'

const route = useRoute()
const current = computed(() => route.path)
</script>

<template>
  <v-bottom-navigation
    :model-value="current"
    grow
    height="60"
    style="border-top: 1px solid rgba(0,0,0,0.08); background: rgba(255,255,255,0.92); backdrop-filter: saturate(180%) blur(20px); -webkit-backdrop-filter: saturate(180%) blur(20px);"
    elevation="0"
  >
    <v-btn value="/" to="/" :active="current === ''" style="min-width:0">
      <v-icon size="22">{{ current === '/' ? 'mdi-home' : 'mdi-home-outline' }}</v-icon>
      <span style="font-size:10px; margin-top:2px">홈</span>
    </v-btn>
    <v-btn value="/board" to="/board" style="min-width:0">
      <v-icon size="22">{{ current === '/board' ? 'mdi-note-text' : 'mdi-note-text-outline' }}</v-icon>
      <span style="font-size:10px; margin-top:2px">게시판</span>
    </v-btn>
    <v-btn value="/market" to="/market" style="min-width:0">
      <v-icon size="22">{{ current === '/market' ? 'mdi-storefront' : 'mdi-storefront-outline' }}</v-icon>
      <span style="font-size:10px; margin-top:2px">마켓</span>
    </v-btn>
    <v-btn value="/inventory" to="/inventory" style="min-width:0">
      <v-icon size="22">{{ current === '/inventory' ? 'mdi-bag-personal' : 'mdi-bag-personal-outline' }}</v-icon>
      <span style="font-size:10px; margin-top:2px">인벤토리</span>
    </v-btn>
  </v-bottom-navigation>
</template>
