<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useAppStore } from '@/stores/app'
import { boardService, type BoardItem } from '@/api/boardService'

const store = useAppStore()
const boards = ref<BoardItem[]>([])
const loading = ref(false)
const errorMsg = ref('')
const dialog = ref(false)
const title = ref('')
const content = ref('')
const file = ref<File | null>(null)
const submitting = ref(false)

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
    dialog.value = false
    title.value = ''; content.value = ''; file.value = null
    await load()
  } catch { errorMsg.value = '게시글 작성에 실패했습니다.' }
  finally { submitting.value = false }
}

async function remove(id: number) {
  try { await boardService.remove(id); await load() }
  catch { errorMsg.value = '삭제에 실패했습니다.' }
}

function onFileChange(e: Event) {
  file.value = (e.target as HTMLInputElement).files?.[0] ?? null
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h1 class="page-title">게시판</h1>
        <p class="page-subtitle">{{ boards.length }}개의 게시글</p>
      </div>
      <button class="btn-primary" @click="dialog = true">
        <v-icon size="16" class="mr-1">mdi-plus</v-icon>글쓰기
      </button>
    </div>

    <div v-if="errorMsg" class="error-bar">{{ errorMsg }}</div>

    <div v-if="loading" class="loading-wrap">
      <v-progress-circular indeterminate color="#0071E3" size="32" width="2.5" />
    </div>

    <div v-else-if="boards.length === 0" class="empty">
      <v-icon size="40" color="#AEAEB2">mdi-note-text-outline</v-icon>
      <p>아직 게시글이 없습니다</p>
    </div>

    <div v-else class="list-card">
      <div v-for="(board, i) in boards" :key="board.id" class="list-row" :class="{ last: i === boards.length - 1 }">
        <div class="list-content">
          <p class="list-title">{{ board.title }}</p>
          <p class="list-meta">{{ board.writer }}</p>
          <p class="list-body">{{ board.content }}</p>
        </div>
        <button v-if="board.writer === store.user?.nickName" class="btn-delete" @click="remove(board.id)">
          삭제
        </button>
      </div>
    </div>

    <!-- 글쓰기 다이얼로그 -->
    <div v-if="dialog" class="overlay" @click.self="dialog = false">
      <div class="modal">
        <div class="modal-header">
          <h2 class="modal-title">새 게시글</h2>
          <button class="btn-icon" @click="dialog = false">
            <v-icon size="20" color="#6E6E73">mdi-close</v-icon>
          </button>
        </div>
        <div class="modal-body">
          <input v-model="title" type="text" placeholder="제목" class="modal-input" />
          <textarea v-model="content" placeholder="내용을 입력하세요" class="modal-textarea" rows="5" />
          <label class="file-label">
            <v-icon size="18" color="#0071E3" class="mr-1">mdi-paperclip</v-icon>
            <span>{{ file ? file.name : '파일 첨부' }}</span>
            <input type="file" class="file-hidden" @change="onFileChange" />
          </label>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="dialog = false">취소</button>
          <button class="btn-primary" :disabled="!file || submitting" @click="submit">
            <v-progress-circular v-if="submitting" indeterminate size="14" width="2" color="white" class="mr-1" />
            등록
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { min-height: 100vh; background: #F5F5F7; padding: 24px 16px; }
.page-header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 20px; }
.page-title { font-size: 26px; font-weight: 700; color: #1D1D1F; letter-spacing: -0.4px; }
.page-subtitle { font-size: 13px; color: #6E6E73; margin-top: 2px; }

.btn-primary {
  display: inline-flex; align-items: center;
  background: #0071E3; color: white; border: none;
  border-radius: 10px; padding: 9px 16px; font-size: 14px; font-weight: 600;
  cursor: pointer; transition: background 0.15s;
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif;
}
.btn-primary:hover:not(:disabled) { background: #0077ED; }
.btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }

.error-bar { background: rgba(255,59,48,0.08); border: 1px solid rgba(255,59,48,0.15); border-radius: 10px; padding: 10px 14px; font-size: 13px; color: #FF3B30; margin-bottom: 16px; }

.loading-wrap { display: flex; justify-content: center; padding: 60px 0; }
.empty { display: flex; flex-direction: column; align-items: center; gap: 10px; padding: 60px 0; color: #AEAEB2; font-size: 14px; }

.list-card { background: #FFFFFF; border-radius: 14px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.06); }
.list-row {
  display: flex; align-items: flex-start; justify-content: space-between;
  padding: 16px; border-bottom: 1px solid rgba(0,0,0,0.06);
  transition: background 0.1s;
}
.list-row.last { border-bottom: none; }
.list-row:active { background: #F5F5F7; }
.list-content { flex: 1; min-width: 0; }
.list-title { font-size: 15px; font-weight: 600; color: #1D1D1F; margin-bottom: 2px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.list-meta { font-size: 12px; color: #6E6E73; margin-bottom: 6px; }
.list-body { font-size: 13px; color: #3A3A3C; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }

.btn-delete { background: none; border: none; color: #FF3B30; font-size: 13px; font-weight: 500; cursor: pointer; padding: 4px 0 0 12px; flex-shrink: 0; font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif; }

/* Modal */
.overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 100; display: flex; align-items: flex-end; justify-content: center; }
.modal { background: #FFFFFF; border-radius: 20px 20px 0 0; width: 100%; max-width: 600px; padding: 20px 20px 36px; }
.modal-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.modal-title { font-size: 17px; font-weight: 700; color: #1D1D1F; }
.btn-icon { background: none; border: none; cursor: pointer; padding: 4px; border-radius: 50%; }
.modal-body { display: flex; flex-direction: column; gap: 12px; margin-bottom: 20px; }
.modal-input { width: 100%; padding: 13px 14px; background: #F5F5F7; border: none; border-radius: 10px; font-size: 15px; color: #1D1D1F; outline: none; font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif; }
.modal-textarea { width: 100%; padding: 13px 14px; background: #F5F5F7; border: none; border-radius: 10px; font-size: 15px; color: #1D1D1F; outline: none; resize: none; font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif; }
.file-label { display: inline-flex; align-items: center; gap: 4px; font-size: 14px; color: #0071E3; font-weight: 500; cursor: pointer; }
.file-hidden { display: none; }
.modal-footer { display: flex; justify-content: flex-end; gap: 10px; }
.btn-cancel { background: #F5F5F7; color: #1D1D1F; border: none; border-radius: 10px; padding: 10px 18px; font-size: 14px; font-weight: 500; cursor: pointer; font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif; }
</style>
