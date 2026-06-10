<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { boardService, type BoardItem, type CommentItem } from '@/api/boardService'
import { useAppStore } from '@/stores/app'

const route = useRoute()
const router = useRouter()
const store = useAppStore()
const board = ref<BoardItem | null>(null)
const loading = ref(false)
const errorMsg = ref('')
const liked = ref(false)
const likedCount = ref(0)
const liking = ref(false)
const comments = ref<CommentItem[]>([])
const commentText = ref('')
const submitting = ref(false)
const editingId = ref<number | null>(null)
const editText = ref('')

async function load() {
  loading.value = true
  try {
    const id = Number(route.params.id)
    board.value = await boardService.getOne(id)
    liked.value = board.value.liked
    likedCount.value = board.value.likedCount
    comments.value = await boardService.getComments(id)
  }
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
    likedCount.value += liked.value ? 1 : -1
  } finally {
    liking.value = false
  }
}
function startEdit(c: CommentItem) {
  editingId.value = c.id!
  editText.value = c.content
}
function cancelEdit() {
  editingId.value = null
  editText.value = ''
}
async function saveEdit(commentId: number | undefined) {
  if (!commentId || !editText.value.trim() || !board.value) return
  try {
    await boardService.updateComment(commentId, editText.value.trim())
    comments.value = await boardService.getComments(board.value.id)
    cancelEdit()
  } catch {
    errorMsg.value = '댓글 수정에 실패했습니다.'
  }
}
async function removeComment(commentId: number | undefined) {
  if (!commentId || !board.value) return
  try {
    await boardService.deleteComment(commentId)
    comments.value = await boardService.getComments(board.value.id)
  } catch {
    errorMsg.value = '댓글 삭제에 실패했습니다.'
  }
}
async function submitComment() {
  if (!board.value || !commentText.value.trim() || submitting.value) return
  submitting.value = true
  try {
    await boardService.comment(board.value.id, commentText.value.trim())
    commentText.value = ''
    comments.value = await boardService.getComments(board.value.id)
  } catch {
    errorMsg.value = '댓글 작성에 실패했습니다.'
  } finally {
    submitting.value = false
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
          <img :src="`/board/image/${board.id}`" alt="" class="cover-img" />
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
            <span v-if="likedCount > 0" class="like-count-badge">{{ likedCount }}</span>
          </button>
        </div>

        <!-- 댓글 -->
        <section class="comment-section">
          <h3 class="comment-heading">Comments</h3>

          <div v-if="comments.length > 0" class="comment-list">
            <div v-for="c in comments" :key="c.id" class="comment-item">
              <div class="comment-header">
                <span class="comment-writer">{{ c.nickName }}</span>
                <div v-if="c.nickName === store.user?.nickName && editingId !== c.id" class="comment-actions">
                  <button class="comment-action-btn" @click="startEdit(c)">
                    <v-icon size="13">mdi-pencil-outline</v-icon>
                  </button>
                  <button class="comment-action-btn danger" @click="removeComment(c.id)">
                    <v-icon size="13">mdi-trash-can-outline</v-icon>
                  </button>
                </div>
              </div>

              <!-- 수정 모드 -->
              <template v-if="editingId === c.id">
                <textarea v-model="editText" class="comment-input edit-input" rows="2" />
                <div class="edit-actions">
                  <button class="comment-action-text" @click="cancelEdit">취소</button>
                  <button class="comment-action-text save" @click="saveEdit(c.id)">저장</button>
                </div>
              </template>
              <p v-else class="comment-content">{{ c.content }}</p>
            </div>
          </div>
          <p v-else class="comment-empty">아직 댓글이 없습니다.</p>

          <form class="comment-form" @submit.prevent="submitComment">
            <textarea
              v-model="commentText"
              class="comment-input"
              placeholder="댓글을 입력하세요..."
              rows="3"
              :disabled="submitting"
            />
            <div class="comment-form-footer">
              <span class="comment-char">{{ commentText.length }} / 500</span>
              <button type="submit" class="btn-pri comment-submit" :disabled="submitting || !commentText.trim()">
                <v-icon size="13" class="mr-1">mdi-send</v-icon>
                {{ submitting ? '작성 중...' : '댓글 작성' }}
              </button>
            </div>
          </form>
        </section>

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
.like-count-badge {
  background: rgba(244,114,182,.18); border: 1px solid rgba(244,114,182,.3);
  border-radius: 100px; padding: 2px 9px;
  font-size: 12px; font-weight: 700; color: #f472b6;
}

/* ── Comments ─────────────────────────────────────────────── */
.comment-section {
  border-top: 1px solid var(--glass-border);
  padding-top: 40px;
  margin-bottom: 48px;
}
.comment-heading {
  font-family: var(--font-d); font-size: 15px; font-weight: 700;
  letter-spacing: .06em; color: var(--text); margin-bottom: 24px;
}
.comment-list { display: flex; flex-direction: column; gap: 12px; margin-bottom: 28px; }
.comment-item {
  background: var(--glass); border: 1px solid var(--glass-border);
  border-radius: var(--r-sm); padding: 14px 16px;
}
.comment-header {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 6px;
}
.comment-writer {
  font-size: 11px; font-weight: 700; letter-spacing: .08em;
  text-transform: uppercase; color: var(--blue);
}
.comment-actions { display: flex; gap: 4px; }
.comment-action-btn {
  display: inline-flex; align-items: center; justify-content: center;
  width: 24px; height: 24px; border-radius: 6px;
  background: transparent; border: none;
  color: var(--text-3); cursor: pointer; transition: all .15s;
}
.comment-action-btn:hover { background: var(--glass); color: var(--text-2); }
.comment-action-btn.danger:hover { color: #ff4757; }
.comment-content { font-size: 14px; color: var(--text-2); line-height: 1.6; margin: 0; }
.edit-input { margin-top: 4px; margin-bottom: 8px; }
.edit-actions { display: flex; justify-content: flex-end; gap: 8px; }
.comment-action-text {
  background: none; border: none; cursor: pointer;
  font: 12px var(--font); color: var(--text-3); transition: color .15s; padding: 0;
}
.comment-action-text:hover { color: var(--text-2); }
.comment-action-text.save { color: var(--blue); font-weight: 600; }
.comment-action-text.save:hover { color: #7db4ff; }
.comment-empty {
  font-size: 13px; color: var(--text-3); text-align: center;
  padding: 32px 0; margin-bottom: 28px;
}
.comment-form { display: flex; flex-direction: column; gap: 10px; }
.comment-input {
  width: 100%; background: var(--glass); border: 1px solid var(--glass-border);
  border-radius: var(--r-sm); padding: 12px 14px;
  color: var(--text); font: 14px var(--font); resize: vertical;
  outline: none; transition: border-color .2s;
}
.comment-input::placeholder { color: var(--text-3); }
.comment-input:focus { border-color: rgba(91,156,246,.45); }
.comment-input:disabled { opacity: .5; cursor: not-allowed; }
.comment-form-footer {
  display: flex; align-items: center; justify-content: space-between;
}
.comment-char { font-size: 11px; color: var(--text-3); }
.comment-submit {
  display: inline-flex; align-items: center;
  padding: 8px 20px; font-size: 12px;
}
.comment-submit:disabled { opacity: .4; cursor: not-allowed; }

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
