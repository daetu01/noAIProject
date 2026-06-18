<template>
  <!-- Studio landing: no Vuetify layout wrapper -->
  <div v-if="isStudio" id="studio-root">
    <RouterView />
  </div>

  <!-- Service app: original Vuetify layout -->
  <v-app v-else theme="dark">
    <Navbar v-if="showServiceNav" />
    <v-main>
      <RouterView />
    </v-main>
    <BottomNavibar v-if="showServiceNav" />
    <ChatBot v-if="showServiceNav" />
  </v-app>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from './components/navbar.vue'
import BottomNavibar from './components/bottomNavibar.vue'
import ChatBot from './components/ChatBot.vue'

const route = useRoute()

// Studio layout: landing + auth pages (no Vuetify wrapper)
const studioRoutes = ['/', '/login', '/signup', '/music']
const isStudio = computed(() => studioRoutes.includes(route.path))

// Service app auth pages don't show nav
const noNavRoutes = ['/login', '/signup', '/city', '/games', '/board']
const showServiceNav = computed(() =>
  !noNavRoutes.some(r => route.path === r || route.path.startsWith(r + '/'))
)
</script>

<style>
/* Service app layout — only active when v-app is rendered */
.v-main {
  padding-top: 56px !important;
  padding-bottom: 72px !important;
}
</style>
