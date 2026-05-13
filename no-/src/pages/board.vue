<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
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
onMounted(load)
onUnmounted(stopDrag)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <div><p class="eyebrow">COMMUNITY</p><h1 class="page-title">게시판</h1></div>
      <button class="btn-write" @click="openDialog"><v-icon size="14" class="mr-1">mdi-pencil-outline</v-icon>글쓰기</button>
    </div>
    <div class="header-divider" />

    <div v-if="errorMsg" class="error-bar">{{ errorMsg }}</div>
    <div v-if="loading" class="loading-wrap"><v-progress-circular indeterminate color="#111" size="28" width="1.5" /></div>
    <div v-else-if="boards.length === 0" class="empty">— 아직 게시글이 없습니다 —</div>

    <div v-else class="feed">
      <div class="featured" @click="router.push(`/board/${boards[0].id}`)">
        <div class="featured-thumb">
          <img v-if="boards[0].uploadDir" :src="boards[0].uploadDir" alt="" class="thumb-img" />
          <div v-else class="thumb-empty"><v-icon size="28" color="#ccc">mdi-image-outline</v-icon></div>
          <span class="chip">COMMUNITY</span>
        </div>
        <h2 class="featured-title">{{ boards[0].title }}</h2>
        <p class="featured-excerpt">{{ boards[0].content }}</p>
        <div class="meta">
          <span class="author">by {{ boards[0].writer }}</span>
          <button v-if="boards[0].writer === store.user?.nickName" class="btn-del" @click.stop="remove(boards[0].id)">삭제</button>
        </div>
      </div>

      <div class="article-list">
        <div v-for="board in boards.slice(1)" :key="board.id" class="article-row" @click="router.push(`/board/${board.id}`)">
          <div class="article-thumb">
            <img v-if="board.uploadDir" :src="board.uploadDir" alt="" class="thumb-img" />
            <div v-else class="thumb-empty small"><v-icon size="18" color="#ccc">mdi-image-outline</v-icon></div>
          </div>
          <div class="article-body">
            <p class="article-category">COMMUNITY</p>
            <h3 class="article-title">{{ board.title }}</h3>
            <p class="article-excerpt">{{ board.content }}</p>
            <div class="meta">
              <span class="author">by {{ board.writer }}</span>
              <button v-if="board.writer === store.user?.nickName" class="btn-del" @click.stop="remove(board.id)">삭제</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="dialog" class="overlay" @click.self="dialog = false">
      <div class="modal" :class="{ dragging: isDragging }" :style="{ transform: `translate(${modalX}px, ${modalY}px)` }">
        <div class="drag-bar" />
        <div class="modal-header" @mousedown.prevent="startDrag">
          <div><p class="modal-eyebrow">NEW POST</p><h2 class="modal-title">글쓰기</h2></div>
          <button class="btn-icon" @mousedown.stop @click="dialog = false"><v-icon size="18" color="#aaa">mdi-close</v-icon></button>
        </div>
        <div class="modal-body">
          <input v-model="title" type="text" placeholder="제목" class="modal-input" />
          <textarea v-model="content" placeholder="내용을 입력하세요..." class="modal-textarea" rows="5" />
          <label class="file-label"><v-icon size="15" color="#aaa" class="mr-1">mdi-paperclip</v-icon><span>{{ file ? file.name : '이미지 첨부' }}</span><input type="file" class="file-hidden" @change="onFileChange" /></label>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="dialog = false">취소</button>
          <button class="btn-submit" :disabled="!file || submitting" @click="submit">
            <v-progress-circular v-if="submitting" indeterminate size="14" width="2" color="#fff" class="mr-1" />등록하기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { min-height: 100vh; background: #F5F5F5; padding: 32px 20px 80px; }
.page-header { display: flex; align-items: flex-end; justify-content: space-between; margin-bottom: 16px; }
.eyebrow { font-size: 10px; font-weight: 700; color: #aaa; letter-spacing: 3px; margin-bottom: 8px; }
.page-title { font-size: 36px; font-weight: 900; color: #111; letter-spacing: -0.5px; }
.header-divider { height: 1px; background: rgba(0,0,0,0.08); margin-bottom: 28px; }
.btn-write { display: inline-flex; align-items: center; gap: 4px; background: #111; color: #fff; border: none; border-radius: 8px; padding: 9px 14px; font-size: 12px; font-weight: 600; cursor: pointer; transition: opacity 0.15s; margin-bottom: 4px; }
.btn-write:hover { opacity: 0.8; }
.error-bar { background: #FFF0F0; border: 1px solid rgba(220,50,50,0.2); border-radius: 8px; padding: 10px 14px; font-size: 13px; color: #cc3333; margin-bottom: 16px; }
.loading-wrap { display: flex; justify-content: center; padding: 60px 0; }
.empty { text-align: center; padding: 60px 0; font-size: 13px; color: #bbb; letter-spacing: 2px; }

.featured { cursor: pointer; margin-bottom: 32px; transition: opacity 0.15s; }
.featured:hover { opacity: 0.8; }
.featured-thumb { position: relative; width: 100%; aspect-ratio: 16/9; border-radius: 10px; overflow: hidden; background: #eee; margin-bottom: 16px; }
.thumb-img { width: 100%; height: 100%; object-fit: cover; display: block; }
.thumb-empty { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; }
.chip { position: absolute; top: 12px; left: 12px; background: #111; color: #fff; font-size: 10px; font-weight: 800; padding: 3px 10px; border-radius: 4px; letter-spacing: 1.5px; }
.featured-title { font-size: 22px; font-weight: 800; color: #111; letter-spacing: -0.3px; line-height: 1.3; margin-bottom: 8px; }
.featured-excerpt { font-size: 14px; color: #999; line-height: 1.6; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; margin-bottom: 12px; }
.article-list { background: #fff; border-radius: 12px; border: 1px solid rgba(0,0,0,0.07); overflow: hidden; }
.article-row { display: flex; gap: 14px; padding: 18px; border-bottom: 1px solid rgba(0,0,0,0.06); cursor: pointer; transition: background 0.12s; }
.article-row:hover { background: #FAFAFA; }
.article-row:last-child { border-bottom: none; }
.article-thumb { width: 80px; height: 80px; flex-shrink: 0; border-radius: 8px; overflow: hidden; background: #f0f0f0; }
.thumb-empty.small { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; }
.article-body { flex: 1; min-width: 0; }
.article-category { font-size: 10px; font-weight: 700; color: #bbb; letter-spacing: 2px; margin-bottom: 4px; }
.article-title { font-size: 15px; font-weight: 700; color: #111; line-height: 1.4; margin-bottom: 4px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.article-excerpt { font-size: 12px; color: #aaa; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; margin-bottom: 8px; }
.meta { display: flex; align-items: center; justify-content: space-between; }
.author { font-size: 11px; color: #bbb; }
.btn-del { background: none; border: none; color: #ccc; font-size: 11px; font-weight: 600; cursor: pointer; transition: color 0.15s; }
.btn-del:hover { color: #cc3333; }

.overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 2000; display: flex; align-items: center; justify-content: center; }
.modal { background: #fff; border: 1px solid rgba(0,0,0,0.1); border-radius: 16px; width: calc(100% - 40px); max-width: 500px; padding: 12px 20px 24px; box-shadow: 0 20px 60px rgba(0,0,0,0.15); user-select: none; }
.modal.dragging { cursor: grabbing; }
.drag-bar { width: 36px; height: 3px; background: #eee; border-radius: 2px; margin: 0 auto 14px; }
.modal-header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 20px; cursor: grab; }
.modal-header:active, .modal.dragging .modal-header { cursor: grabbing; }
.modal-eyebrow { font-size: 10px; font-weight: 700; color: #bbb; letter-spacing: 2px; margin-bottom: 4px; }
.modal-title { font-size: 20px; font-weight: 800; color: #111; }
.btn-icon { background: none; border: none; cursor: pointer; padding: 4px; border-radius: 6px; }
.modal-body { display: flex; flex-direction: column; gap: 10px; margin-bottom: 20px; }
.modal-input { width: 100%; padding: 12px 14px; background: #F5F5F5; border: 1px solid rgba(0,0,0,0.08); border-radius: 8px; font-size: 14px; color: #111; outline: none; box-sizing: border-box; transition: border-color 0.15s; }
.modal-input:focus { border-color: rgba(0,0,0,0.2); }
.modal-input::placeholder { color: #bbb; }
.modal-textarea { width: 100%; padding: 12px 14px; background: #F5F5F5; border: 1px solid rgba(0,0,0,0.08); border-radius: 8px; font-size: 14px; color: #111; outline: none; resize: none; box-sizing: border-box; }
.modal-textarea::placeholder { color: #bbb; }
.file-label { display: inline-flex; align-items: center; gap: 4px; font-size: 12px; color: #aaa; font-weight: 500; cursor: pointer; }
.file-hidden { display: none; }
.modal-footer { display: flex; justify-content: flex-end; gap: 10px; }
.btn-cancel { background: #F5F5F5; color: #888; border: none; border-radius: 8px; padding: 10px 18px; font-size: 13px; font-weight: 600; cursor: pointer; }
.btn-submit { display: inline-flex; align-items: center; background: #111; color: #fff; border: none; border-radius: 8px; padding: 10px 20px; font-size: 13px; font-weight: 800; cursor: pointer; transition: opacity 0.15s; }
.btn-submit:disabled { opacity: 0.3; cursor: not-allowed; }
</style>
