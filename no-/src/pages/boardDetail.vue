<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { boardService, type BoardItem } from '@/api/boardService'
import { useAppStore } from '@/stores/app'

const route = useRoute()
const router = useRouter()
const store = useAppStore()
const board = ref<BoardItem | null>(null)
const loading = ref(false)
const errorMsg = ref('')

async function load() {
  loading.value = true
  try { board.value = await boardService.getOne(Number(route.params.id)) }
  catch { errorMsg.value = '게시글을 불러오지 못했습니다.' }
  finally { loading.value = false }
}
async function remove() {
  if (!board.value) return
  try { await boardService.remove(board.value.id); router.push('/board') }
  catch { errorMsg.value = '삭제에 실패했습니다.' }
}
onMounted(load)
</script>

<template>
  <div class="page">
    <button class="btn-back" @click="router.push('/board')">
      <v-icon size="14">mdi-chevron-left</v-icon>게시판
    </button>

    <div v-if="errorMsg" class="error-bar">{{ errorMsg }}</div>
    <div v-if="loading" class="loading-wrap">
      <v-progress-circular indeterminate color="#111" size="28" width="1.5" />
    </div>

    <article v-else-if="board">
      <header class="article-header">
        <p class="article-category">COMMUNITY</p>
        <h1 class="article-title">{{ board.title }}</h1>
        <span class="author">by {{ board.writer }}</span>
      </header>
      <div class="header-divider" />
      <div v-if="board.uploadDir" class="cover-wrap">
        <img :src="board.uploadDir" alt="" class="cover-img" />
      </div>
      <p class="article-content">{{ board.content }}</p>
      <footer class="article-footer">
        <div class="footer-divider" />
        <div class="footer-row">
          <span class="footer-author">by {{ board.writer }}</span>
          <button v-if="board.writer === store.user?.nickName" class="btn-delete" @click="remove">삭제</button>
        </div>
      </footer>
    </article>
  </div>
</template>

<style scoped>
.page { min-height: 100vh; background: #F5F5F5; padding: 28px 20px 80px; max-width: 680px; margin: 0 auto; }
.btn-back { display: inline-flex; align-items: center; gap: 2px; background: none; border: none; cursor: pointer; font-size: 12px; font-weight: 600; color: #aaa; padding: 4px 0; margin-bottom: 32px; transition: color 0.15s; }
.btn-back:hover { color: #111; }
.error-bar { background: #FFF0F0; border: 1px solid rgba(220,50,50,0.2); border-radius: 8px; padding: 10px 14px; font-size: 13px; color: #cc3333; margin-bottom: 20px; }
.loading-wrap { display: flex; justify-content: center; padding: 60px 0; }

.article-header { margin-bottom: 20px; }
.article-category { font-size: 10px; font-weight: 700; color: #bbb; letter-spacing: 3px; margin-bottom: 14px; }
.article-title { font-size: 30px; font-weight: 900; color: #111; letter-spacing: -0.5px; line-height: 1.25; margin-bottom: 12px; }
.author { font-size: 12px; color: #aaa; }
.header-divider { height: 1px; background: rgba(0,0,0,0.08); margin-bottom: 24px; }
.cover-wrap { width: 100%; border-radius: 10px; overflow: hidden; margin-bottom: 24px; }
.cover-img { width: 100%; display: block; object-fit: cover; }
.article-content { font-size: 16px; color: #444; line-height: 1.85; white-space: pre-wrap; margin-bottom: 48px; }
.footer-divider { height: 1px; background: rgba(0,0,0,0.07); margin-bottom: 16px; }
.footer-row { display: flex; justify-content: space-between; align-items: center; }
.footer-author { font-size: 11px; color: #bbb; }
.btn-delete { background: none; border: 1px solid rgba(220,50,50,0.25); color: #cc3333; font-size: 11px; font-weight: 700; cursor: pointer; padding: 6px 12px; border-radius: 6px; transition: all 0.15s; }
.btn-delete:hover { background: rgba(220,50,50,0.06); }
</style>
