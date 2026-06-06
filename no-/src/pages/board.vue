<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { boardService, type BoardItem } from '@/api/boardService'

const router = useRouter()
const store = useAppStore()
const boards = ref<BoardItem[]>([])
const loading = ref(false)
const errorMsg = ref('')
const dialog = ref(false)
const title = ref('')
const content = ref('')
const file = ref<File | null>(null)
const submitting = ref(false)
const modalX = ref(0)
const modalY = ref(0)
const isDragging = ref(false)
const dragStartX = ref(0)
const dragStartY = ref(0)
const likedSet = ref(new Set<number>())
const likingSet = ref(new Set<number>())

async function load() {
  loading.value = true
  try { boards.value = await boardService.getAll() }
  catch { errorMsg.value = '게시글을 불러오지 못했습니다.' }
  finally { loading.value = false }
}
async function submit() {
  if (!file.value) return
  submitting.value = true
  try {
    await boardService.create({ title: title.value, content: content.value, writer: store.user!.nickName, file: file.value })
    dialog.value = false; title.value = ''; content.value = ''; file.value = null
    await load()
  } catch { errorMsg.value = '게시글 작성에 실패했습니다.' }
  finally { submitting.value = false }
}
async function remove(id: number) {
  try { await boardService.remove(id); await load() }
  catch { errorMsg.value = '삭제에 실패했습니다.' }
}
function onFileChange(e: Event) { file.value = (e.target as HTMLInputElement).files?.[0] ?? null }
function openDialog() { modalX.value = 0; modalY.value = 0; dialog.value = true }
function startDrag(e: MouseEvent) {
  isDragging.value = true
  dragStartX.value = e.clientX - modalX.value; dragStartY.value = e.clientY - modalY.value
  document.addEventListener('mousemove', onDrag); document.addEventListener('mouseup', stopDrag)
}
function onDrag(e: MouseEvent) {
  if (!isDragging.value) return
  modalX.value = e.clientX - dragStartX.value; modalY.value = e.clientY - dragStartY.value
}
function stopDrag() {
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag); document.removeEventListener('mouseup', stopDrag)
}
async function toggleLike(id: number, e: MouseEvent) {
  e.stopPropagation()
  if (likingSet.value.has(id)) return
  likingSet.value = new Set(likingSet.value).add(id)
  try {
    await boardService.like(id)
    const next = new Set(likedSet.value)
    next.has(id) ? next.delete(id) : next.add(id)
    likedSet.value = next
  } finally {
    const next = new Set(likingSet.value)
    next.delete(id)
    likingSet.value = next
  }
}
onMounted(load)
onUnmounted(stopDrag)
</script>

<template>
  <div class="board-page">

    <!-- ── Fixed header nav ── -->
    <header class="board-header">
      <div class="header-left">
        <RouterLink to="/" class="ds-logo">
          <span class="g-text">DS</span>
        </RouterLink>
        <span class="header-sep">/</span>
        <span class="header-section">Community</span>
      </div>
      <div class="header-right">
        <span class="user-name">{{ store.user?.nickName }}</span>
        <button class="write-btn btn-pri" @click="openDialog">
          <v-icon size="12" class="mr-1">mdi-pencil-outline</v-icon>
          글쓰기
        </button>
      </div>
    </header>

    <!-- ── Page body ── -->
    <div class="page-inner">

      <!-- Hero -->
      <div class="board-hero">
        <p class="s-label">Community</p>
        <h1 class="board-title"><span class="g-text">Board</span></h1>
        <p class="board-sub">Ideas, projects, and conversations from the community.</p>
      </div>

      <!-- Error -->
      <div v-if="errorMsg" class="error-bar">{{ errorMsg }}</div>

      <!-- Loading -->
      <div v-if="loading" class="state-wrap">
        <v-progress-circular indeterminate color="#5b9cf6" size="32" width="1.5" />
      </div>

      <!-- Empty -->
      <div v-else-if="boards.length === 0 && !errorMsg" class="state-wrap empty">
        <span class="empty-glyph">✦</span>
        <p class="empty-text">아직 게시글이 없습니다</p>
        <button class="btn-pri" @click="openDialog">첫 글 작성하기</button>
      </div>

      <!-- Feed -->
      <div v-else class="feed">

        <!-- Featured (first post) -->
        <div class="featured-card" @click="router.push(`/board/${boards[0].id}`)">
          <div class="featured-thumb">
            <img v-if="boards[0].uploadDir" :src="boards[0].uploadDir" alt="" class="thumb-img" />
            <div v-else class="thumb-placeholder">
              <div class="placeholder-glow" />
              <v-icon size="44" color="rgba(91,156,246,0.25)">mdi-image-outline</v-icon>
            </div>
            <div class="thumb-overlay" />
            <span class="tag tag-blue featured-tag">FEATURED</span>
          </div>
          <div class="featured-body">
            <p class="post-cat">COMMUNITY · FEATURED</p>
            <h2 class="featured-title">{{ boards[0].title }}</h2>
            <p class="featured-excerpt">{{ boards[0].content }}</p>
            <div class="post-footer">
              <span class="author">by {{ boards[0].writer }}</span>
              <div class="post-actions">
                <button
                  class="like-btn"
                  :class="{ liked: likedSet.has(boards[0].id) }"
                  :disabled="likingSet.has(boards[0].id)"
                  @click="toggleLike(boards[0].id, $event)"
                >
                  <v-icon size="14">{{ likedSet.has(boards[0].id) ? 'mdi-heart' : 'mdi-heart-outline' }}</v-icon>
                </button>
                <button
                  v-if="boards[0].writer === store.user?.nickName"
                  class="btn-del"
                  @click.stop="remove(boards[0].id)"
                >삭제</button>
                <span class="read-more">읽기 →</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Article grid -->
        <div v-if="boards.length > 1" class="article-grid stagger">
          <div
            v-for="board in boards.slice(1)" :key="board.id"
            class="article-card reveal-s"
            @click="router.push(`/board/${board.id}`)"
          >
            <div class="article-thumb">
              <img v-if="board.uploadDir" :src="board.uploadDir" alt="" class="thumb-img" />
              <div v-else class="thumb-placeholder">
                <div class="placeholder-glow" />
              </div>
              <div class="thumb-overlay" />
            </div>
            <div class="article-body">
              <p class="post-cat">COMMUNITY</p>
              <h3 class="article-title">{{ board.title }}</h3>
              <p class="article-excerpt">{{ board.content }}</p>
              <div class="post-footer">
                <span class="author">by {{ board.writer }}</span>
                <div class="post-actions">
                  <button
                    class="like-btn"
                    :class="{ liked: likedSet.has(board.id) }"
                    :disabled="likingSet.has(board.id)"
                    @click="toggleLike(board.id, $event)"
                  >
                    <v-icon size="13">{{ likedSet.has(board.id) ? 'mdi-heart' : 'mdi-heart-outline' }}</v-icon>
                  </button>
                  <button
                    v-if="board.writer === store.user?.nickName"
                    class="btn-del"
                    @click.stop="remove(board.id)"
                  >삭제</button>
                </div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>

    <!-- ── Write modal ── -->
    <div v-if="dialog" class="overlay" @click.self="dialog = false">
      <div
        class="write-modal"
        :class="{ dragging: isDragging }"
        :style="{ transform: `translate(${modalX}px, ${modalY}px)` }"
      >
        <div class="drag-handle" @mousedown.prevent="startDrag" />
        <div class="wm-header" @mousedown.prevent="startDrag">
          <div>
            <p class="wm-eyebrow">NEW POST</p>
            <h2 class="wm-title">글쓰기</h2>
          </div>
          <button class="wm-close" @mousedown.stop @click="dialog = false">✕</button>
        </div>
        <div class="wm-body">
          <input v-model="title" type="text" placeholder="제목" class="ds-input" />
          <textarea v-model="content" placeholder="내용을 입력하세요..." class="ds-textarea" rows="6" />
          <label class="file-label">
            <v-icon size="14" color="#5b9cf6">mdi-paperclip</v-icon>
            <span>{{ file ? file.name : '이미지 첨부 (필수)' }}</span>
            <input type="file" class="file-hidden" accept="image/*" @change="onFileChange" />
          </label>
        </div>
        <div class="wm-footer">
          <button class="btn-ghost" @click="dialog = false">취소</button>
          <button class="btn-pri" :disabled="!title || !file || submitting" @click="submit">
            <v-progress-circular v-if="submitting" indeterminate size="13" width="1.5" color="#fff" class="mr-1" />
            등록하기
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>
/* ── Base ─────────────────────────────────────────────────── */
.board-page {
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
  background: rgba(11,11,11,0.85);
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
.header-section { font-size: 13px; color: var(--text-2); font-weight: 400; }
.header-right { display: flex; align-items: center; gap: 16px; }
.user-name { font-size: 12px; color: var(--text-3); }
.write-btn { padding: 8px 18px; font-size: 12px; }

/* ── Inner / Hero ─────────────────────────────────────────── */
.page-inner {
  max-width: 1100px; margin: 0 auto;
  padding: 72px 48px 80px;
}
.board-hero { margin-bottom: 56px; }
.board-title {
  font-family: var(--font-d);
  font-size: clamp(52px, 8vw, 88px);
  font-weight: 700; letter-spacing: -.03em; line-height: 1;
  margin-bottom: 14px;
}
.board-sub {
  font-size: 16px; color: var(--text-2); font-weight: 300; line-height: 1.65; max-width: 480px;
}

/* ── States ───────────────────────────────────────────────── */
.error-bar {
  background: rgba(255,71,87,.08); border: 1px solid rgba(255,71,87,.2);
  border-radius: var(--r-sm); padding: 10px 16px;
  font-size: 13px; color: #ff4757; margin-bottom: 24px;
}
.state-wrap {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; gap: 20px; padding: 100px 0; text-align: center;
}
.empty-glyph { font-size: 32px; color: var(--text-3); }
.empty-text { font-size: 14px; color: var(--text-3); letter-spacing: .1em; }

/* ── Featured card ────────────────────────────────────────── */
.featured-card {
  display: grid; grid-template-columns: 1.15fr 1fr;
  background: var(--glass); border: 1px solid var(--glass-border);
  border-radius: var(--r-lg); overflow: hidden; cursor: pointer;
  transition: border-color .25s var(--ease), transform .25s var(--ease);
  margin-bottom: 28px;
}
.featured-card:hover { border-color: rgba(91,156,246,.4); transform: translateY(-3px); }

.featured-thumb {
  position: relative; overflow: hidden; background: var(--bg-3);
  aspect-ratio: 4/3;
}
.thumb-img { width: 100%; height: 100%; object-fit: cover; display: block; }
.thumb-placeholder {
  width: 100%; height: 100%;
  display: flex; align-items: center; justify-content: center;
  position: relative; overflow: hidden;
}
.placeholder-glow {
  position: absolute; inset: 0;
  background:
    radial-gradient(ellipse at 30% 40%, rgba(91,156,246,.1) 0%, transparent 65%),
    radial-gradient(ellipse at 70% 70%, rgba(167,139,250,.07) 0%, transparent 65%);
}
.thumb-overlay {
  position: absolute; inset: 0;
  background: linear-gradient(to right, transparent 55%, rgba(11,11,11,.65));
}
.featured-tag { position: absolute; top: 16px; left: 16px; }

.featured-body {
  padding: 36px 40px;
  display: flex; flex-direction: column; justify-content: center; gap: 14px;
}
.post-cat {
  font-size: 10px; font-weight: 700; letter-spacing: .22em;
  text-transform: uppercase; color: var(--blue);
}
.featured-title {
  font-family: var(--font-d);
  font-size: clamp(20px, 2.4vw, 28px); font-weight: 700;
  letter-spacing: -.02em; line-height: 1.25; color: var(--text);
}
.featured-excerpt {
  font-size: 14px; color: var(--text-2); line-height: 1.7;
  display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden;
}
.post-footer {
  display: flex; align-items: center; justify-content: space-between;
}
.post-actions { display: flex; align-items: center; gap: 12px; }
.author { font-size: 11px; color: var(--text-3); }
.read-more { font-size: 12px; color: var(--blue); font-weight: 500; }
.btn-del {
  background: none; border: 1px solid rgba(255,71,87,.2);
  color: rgba(255,71,87,.6); font-size: 11px; font-weight: 600;
  padding: 4px 10px; border-radius: 6px; cursor: pointer;
  transition: all .18s var(--ease);
}
.btn-del:hover { background: rgba(255,71,87,.08); border-color: rgba(255,71,87,.4); color: #ff4757; }
.like-btn {
  display: inline-flex; align-items: center;
  background: none; border: 1px solid var(--glass-border);
  color: var(--text-3); border-radius: 6px; padding: 4px 8px;
  cursor: pointer; transition: all .2s var(--ease);
}
.like-btn:hover { border-color: rgba(244,114,182,.35); color: #f472b6; background: rgba(244,114,182,.06); }
.like-btn.liked { border-color: rgba(244,114,182,.5); color: #f472b6; background: rgba(244,114,182,.1); }
.like-btn:disabled { opacity: .45; cursor: not-allowed; }

/* ── Article grid ─────────────────────────────────────────── */
.article-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}
.article-card {
  background: var(--glass); border: 1px solid var(--glass-border);
  border-radius: var(--r); overflow: hidden; cursor: pointer;
  transition: border-color .22s var(--ease), transform .22s var(--ease);
}
.article-card:hover { border-color: rgba(91,156,246,.3); transform: translateY(-2px); }

.article-thumb {
  position: relative; height: 160px; overflow: hidden; background: var(--bg-3);
}
.article-body { padding: 20px; display: flex; flex-direction: column; gap: 8px; }
.article-title {
  font-family: var(--font-d);
  font-size: 15px; font-weight: 700; letter-spacing: -.01em; line-height: 1.35;
  color: var(--text);
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
}
.article-excerpt {
  font-size: 12px; color: var(--text-3); line-height: 1.6;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
}

/* ── Write modal ──────────────────────────────────────────── */
.overlay {
  position: fixed; inset: 0;
  background: rgba(0,0,0,.82); backdrop-filter: blur(20px);
  z-index: 2000; display: flex; align-items: center; justify-content: center;
}
.write-modal {
  background: var(--bg-3); border: 1px solid var(--glass-border);
  border-radius: var(--r-lg);
  width: calc(100% - 48px); max-width: 520px;
  padding: 8px 28px 28px;
  box-shadow: 0 30px 80px rgba(0,0,0,.65);
  user-select: none;
}
.write-modal.dragging { cursor: grabbing; }
.drag-handle {
  width: 36px; height: 3px;
  background: var(--glass-border-hv); border-radius: 2px;
  margin: 12px auto 18px; cursor: grab;
}
.wm-header {
  display: flex; align-items: flex-start; justify-content: space-between;
  margin-bottom: 24px; cursor: grab;
}
.wm-header:active, .write-modal.dragging .wm-header { cursor: grabbing; }
.wm-eyebrow {
  font-size: 10px; font-weight: 700; color: var(--blue);
  letter-spacing: .22em; text-transform: uppercase; margin-bottom: 6px;
}
.wm-title { font-family: var(--font-d); font-size: 22px; font-weight: 700; color: var(--text); }
.wm-close {
  width: 30px; height: 30px;
  background: var(--glass); border: 1px solid var(--glass-border);
  border-radius: 50%; color: var(--text-2);
  font-size: 13px; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all .2s var(--ease);
}
.wm-close:hover { background: var(--glass-hover); color: var(--text); transform: rotate(90deg); }

.wm-body { display: flex; flex-direction: column; gap: 12px; margin-bottom: 24px; }
.ds-input {
  width: 100%; padding: 12px 16px;
  background: var(--glass); border: 1px solid var(--glass-border);
  border-radius: var(--r-sm); font: 14px var(--font); color: var(--text);
  outline: none; box-sizing: border-box; transition: border-color .2s;
}
.ds-input:focus { border-color: rgba(91,156,246,.5); }
.ds-input::placeholder { color: var(--text-3); }
.ds-textarea {
  width: 100%; padding: 12px 16px;
  background: var(--glass); border: 1px solid var(--glass-border);
  border-radius: var(--r-sm); font: 14px/1.65 var(--font); color: var(--text);
  outline: none; resize: none; box-sizing: border-box; transition: border-color .2s;
}
.ds-textarea:focus { border-color: rgba(91,156,246,.5); }
.ds-textarea::placeholder { color: var(--text-3); }
.file-label {
  display: inline-flex; align-items: center; gap: 8px;
  font-size: 12px; color: var(--text-3); cursor: pointer;
  padding: 6px 0; transition: color .2s;
}
.file-label:hover { color: var(--blue); }
.file-hidden { display: none; }

.wm-footer { display: flex; justify-content: flex-end; gap: 10px; }

/* ── Responsive ───────────────────────────────────────────── */
@media (max-width: 768px) {
  .board-header { padding: 14px 20px; }
  .page-inner { padding: 72px 20px 60px; }
  .featured-card { grid-template-columns: 1fr; }
  .featured-thumb { aspect-ratio: 16/9; }
  .thumb-overlay { background: linear-gradient(to bottom, transparent 40%, rgba(11,11,11,.8)); }
  .featured-body { padding: 24px 20px; }
  .article-grid { grid-template-columns: 1fr; }
}
</style>
