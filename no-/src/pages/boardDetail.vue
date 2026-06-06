<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { boardService, type BoardItem } from '@/api/boardService'
import { useAppStore } from '@/stores/app'

const route = useRoute()
const router = useRouter()
const store = useAppStore()
const board = ref<BoardItem | null>(null)
const loading = ref(false)
const errorMsg = ref('')
const liked = ref(false)
const liking = ref(false)

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
async function toggleLike() {
  if (!board.value || liking.value) return
  liking.value = true
  try {
    await boardService.like(board.value.id)
    liked.value = !liked.value
  } finally {
    liking.value = false
  }
}
onMounted(load)
</script>

<template>
  <div class="detail-page">

    <!-- ── Fixed header ── -->
    <header class="board-header">
      <div class="header-left">
        <RouterLink to="/" class="ds-logo">
          <span class="g-text">DS</span>
        </RouterLink>
        <span class="header-sep">/</span>
        <RouterLink to="/board" class="header-crumb">Community</RouterLink>
        <span class="header-sep">/</span>
        <span class="header-section">Post</span>
      </div>
      <button class="btn-ghost back-btn" @click="router.push('/board')">
        <v-icon size="13" class="mr-1">mdi-arrow-left</v-icon>
        목록으로
      </button>
    </header>

    <!-- ── Content ── -->
    <div class="page-inner">

      <div v-if="errorMsg" class="error-bar">{{ errorMsg }}</div>

      <div v-if="loading" class="state-wrap">
        <v-progress-circular indeterminate color="#5b9cf6" size="32" width="1.5" />
      </div>

      <article v-else-if="board" class="article">
        <header class="article-header">
          <p class="post-cat">COMMUNITY</p>
          <h1 class="article-title">{{ board.title }}</h1>
          <div class="article-meta">
            <span class="author">by {{ board.writer }}</span>
          </div>
          <div class="header-divider" />
        </header>

        <div v-if="board.uploadDir" class="cover-wrap">
          <img :src="board.uploadDir" alt="" class="cover-img" />
        </div>

        <p class="article-content">{{ board.content }}</p>

        <!-- 좋아요 -->
        <div class="like-section">
          <button
            class="like-big-btn"
            :class="{ liked }"
            :disabled="liking"
            @click="toggleLike"
          >
            <v-icon :size="liked ? 20 : 18" class="like-icon">
              {{ liked ? 'mdi-heart' : 'mdi-heart-outline' }}
            </v-icon>
            <span>{{ liked ? '좋아요 취소' : '좋아요' }}</span>
          </button>
        </div>

        <footer class="article-footer">
          <div class="footer-divider" />
          <div class="footer-row">
            <span class="author">by {{ board.writer }}</span>
            <button
              v-if="board.writer === store.user?.nickName"
              class="btn-ghost delete-btn"
              @click="remove"
            >
              <v-icon size="13" class="mr-1">mdi-trash-can-outline</v-icon>
              삭제
            </button>
          </div>
        </footer>
      </article>

    </div>
  </div>
</template>

<style scoped>
/* ── Base ─────────────────────────────────────────────────── */
.detail-page {
  min-height: 100vh;
  background: var(--bg);
  color: var(--text);
  font-family: var(--font);
}

/* ── Header ───────────────────────────────────────────────── */
.board-header {
  position: fixed; top: 0; left: 0; right: 0; z-index: 100;
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px 48px;
  background: rgba(11,11,11,.85);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border-bottom: 1px solid var(--glass-border);
}
.header-left { display: flex; align-items: center; gap: 10px; }
.ds-logo {
  font-family: var(--font-d); font-size: 18px; font-weight: 700;
  letter-spacing: .12em; text-decoration: none;
}
.header-sep { color: var(--text-3); font-size: 13px; }
.header-crumb {
  font-size: 13px; color: var(--text-2); font-weight: 400;
  text-decoration: none; transition: color .2s;
}
.header-crumb:hover { color: var(--text); }
.header-section { font-size: 13px; color: var(--text-3); font-weight: 400; }
.back-btn {
  display: inline-flex; align-items: center;
  padding: 8px 16px; font-size: 12px;
}

/* ── Inner ────────────────────────────────────────────────── */
.page-inner {
  max-width: 720px; margin: 0 auto;
  padding: 72px 48px 80px;
}

/* ── States ───────────────────────────────────────────────── */
.error-bar {
  background: rgba(255,71,87,.08); border: 1px solid rgba(255,71,87,.2);
  border-radius: var(--r-sm); padding: 10px 16px;
  font-size: 13px; color: #ff4757; margin-bottom: 24px;
}
.state-wrap {
  display: flex; justify-content: center; padding: 80px 0;
}

/* ── Article ──────────────────────────────────────────────── */
.article-header { margin-bottom: 32px; }
.post-cat {
  font-size: 10px; font-weight: 700; letter-spacing: .22em;
  text-transform: uppercase; color: var(--blue); margin-bottom: 18px;
}
.article-title {
  font-family: var(--font-d);
  font-size: clamp(28px, 5vw, 44px); font-weight: 700;
  letter-spacing: -.03em; line-height: 1.15;
  color: var(--text); margin-bottom: 16px;
}
.article-meta { margin-bottom: 24px; }
.author { font-size: 12px; color: var(--text-3); }
.header-divider { height: 1px; background: var(--glass-border); }

.cover-wrap {
  border-radius: var(--r); overflow: hidden;
  border: 1px solid var(--glass-border); margin-bottom: 36px;
}
.cover-img { width: 100%; display: block; object-fit: cover; }

.article-content {
  font-size: 16px; color: var(--text-2); line-height: 1.9;
  white-space: pre-wrap; margin-bottom: 60px;
}

/* ── Like ─────────────────────────────────────────────────── */
.like-section {
  display: flex; justify-content: center;
  padding: 32px 0 48px;
}
.like-big-btn {
  display: inline-flex; align-items: center; gap: 10px;
  padding: 13px 32px;
  background: var(--glass); border: 1px solid var(--glass-border);
  border-radius: 100px; color: var(--text-3);
  font: 500 14px var(--font); cursor: pointer;
  transition: all .25s var(--ease);
}
.like-big-btn:hover {
  border-color: rgba(244,114,182,.4);
  color: #f472b6;
  background: rgba(244,114,182,.07);
  transform: translateY(-2px);
  box-shadow: 0 8px 28px rgba(244,114,182,.15);
}
.like-big-btn.liked {
  border-color: rgba(244,114,182,.55);
  color: #f472b6;
  background: rgba(244,114,182,.12);
}
.like-big-btn.liked:hover {
  background: rgba(244,114,182,.18);
  box-shadow: 0 8px 28px rgba(244,114,182,.2);
}
.like-big-btn:disabled { opacity: .45; cursor: not-allowed; transform: none; }
.like-icon { transition: transform .2s var(--ease); }
.like-big-btn.liked .like-icon { transform: scale(1.15); }

/* ── Footer ───────────────────────────────────────────────── */
.footer-divider { height: 1px; background: var(--glass-border); margin-bottom: 20px; }
.footer-row {
  display: flex; align-items: center; justify-content: space-between;
}
.delete-btn {
  display: inline-flex; align-items: center;
  padding: 8px 14px; font-size: 12px;
  border-color: rgba(255,71,87,.25) !important;
  color: #ff4757 !important;
}
.delete-btn:hover {
  background: rgba(255,71,87,.08) !important;
  border-color: rgba(255,71,87,.45) !important;
}

/* ── Responsive ───────────────────────────────────────────── */
@media (max-width: 768px) {
  .board-header { padding: 14px 20px; }
  .page-inner { padding: 72px 20px 60px; }
}
</style>
