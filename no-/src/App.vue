<template>
  <v-app>
    <Navbar v-if="showLayout" />
    <v-main>
      <router-view />
    </v-main>
    <BottomNavibar v-if="showLayout" />
  </v-app>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from './components/navbar.vue'
import BottomNavibar from './components/bottomNavibar.vue'

const route = useRoute()
const noLayoutRoutes = ['/login', '/signup']
const showLayout = computed(() => !noLayoutRoutes.includes(route.path))
</script>

<style>
.v-main {
  padding-bottom: 72px !important;
}
</style>
