<script lang="ts" setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { musicService, type MusicItem } from '@/api/musicService'
import { useAppStore } from '@/stores/app'

const store = useAppStore()

const cursor    = ref<HTMLElement | null>(null)
const ring      = ref<HTMLElement | null>(null)
const cursorBig = ref(false)
let rx = 0, ry = 0, tx = 0, ty = 0
let raf = 0

function onMouseMove(e: MouseEvent) {
  tx = e.clientX; ty = e.clientY
  if (cursor.value) { cursor.value.style.left = `${tx}px`; cursor.value.style.top = `${ty}px` }
}
function animateCursor() {
  rx += (tx - rx) * .13; ry += (ty - ry) * .13
  if (ring.value) { ring.value.style.left = `${rx}px`; ring.value.style.top = `${ry}px` }
  raf = requestAnimationFrame(animateCursor)
}

const title = ref('')
const artist = ref('')
const description = ref('')
const genre = ref('')
const audioFile = ref<File | null>(null)
const coverFile = ref<File | null>(null)
const audioPreviewUrl = ref<string | null>(null)
const coverPreviewUrl = ref<string | null>(null)
const submitting = ref(false)
const successMsg = ref('')
const errorMsg = ref('')

const musicList = ref<MusicItem[]>([])
const loadingList = ref(false)

const editingId = ref<number | null>(null)
const editForm = ref({ title: '', artist: '', description: '', genre: '' })

const GENRES = ['Pop', 'Rock', 'Hip-Hop', 'R&B', 'Jazz', 'Classical', 'Electronic', 'Lo-fi', 'Indie', 'Other']

function onAudioChange(e: Event) {
  const f = (e.target as HTMLInputElement).files?.[0] ?? null
  audioFile.value = f
  audioPreviewUrl.value = f ? URL.createObjectURL(f) : null
}

function onCoverChange(e: Event) {
  const f = (e.target as HTMLInputElement).files?.[0] ?? null
  coverFile.value = f
  coverPreviewUrl.value = f ? URL.createObjectURL(f) : null
}

function removeCover() {
  coverFile.value = null
  coverPreviewUrl.value = null
}

function reset() {
  title.value = ''
  artist.value = ''
  description.value = ''
  genre.value = ''
  audioFile.value = null
  coverFile.value = null
  audioPreviewUrl.value = null
  coverPreviewUrl.value = null
}

async function submit() {
  if (!audioFile.value || !title.value.trim() || !artist.value.trim() || !genre.value) return
  submitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    await musicService.create({
      title: title.value.trim(),
      artist: artist.value.trim(),
      description: description.value.trim(),
      genre: genre.value,
      audio: audioFile.value,
      cover: coverFile.value,
    })
    successMsg.value = `"${title.value}" 업로드 완료!`
    reset()
    await loadMusicList()
  } catch {
    errorMsg.value = '업로드에 실패했습니다. 다시 시도해주세요.'
  } finally {
    submitting.value = false
  }
}

async function loadMusicList() {
  loadingList.value = true
  try {
    musicList.value = await musicService.list()
  } catch {
    musicList.value = []
  } finally {
    loadingList.value = false
  }
}

async function onPlay(m: MusicItem) {
  try {
    await musicService.increasePlay(m.id)
    m.play += 1
  } catch {
    // ignore
  }
}

async function toggleLike(m: MusicItem) {
  try {
    await musicService.toggleLike(m.id)
    m.liked = !m.liked
    m.likedCount += m.liked ? 1 : -1
  } catch {
    // ignore
  }
}

function startEdit(m: MusicItem) {
  editingId.value = m.id
  editForm.value = {
    title: m.title,
    artist: m.artist,
    description: m.description,
    genre: m.genre,
  }
}

function cancelEdit() {
  editingId.value = null
}

async function saveEdit(m: MusicItem) {
  const title = editForm.value.title.trim()
  const artist = editForm.value.artist.trim()
  if (!title || !artist || !editForm.value.genre) return

  try {
    await musicService.update({
      id: m.id,
      title,
      artist,
      description: editForm.value.description.trim(),
      genre: editForm.value.genre,
      audioUrl: m.audioUrl,
      coverImageUrl: m.coverImageUrl,
    })
    m.title = title
    m.artist = artist
    m.description = editForm.value.description.trim()
    m.genre = editForm.value.genre
    editingId.value = null
  } catch {
    // ignore
  }
}

async function removeTrack(id: number) {
  if (!window.confirm('이 트랙을 삭제하시겠습니까?')) return
  try {
    await musicService.remove(id)
    musicList.value = musicList.value.filter(m => m.id !== id)
  } catch {
    // ignore
  }
}

const canSubmit = () =>
  title.value.trim() && artist.value.trim() && genre.value && audioFile.value && !submitting.value

onMounted(() => {
  window.addEventListener('mousemove', onMouseMove, { passive: true })
  animateCursor()
  loadMusicList()
})
onUnmounted(() => {
  window.removeEventListener('mousemove', onMouseMove)
  cancelAnimationFrame(raf)
})
</script>

<template>
  <div class="music-page">
    <div class="ds-cursor" ref="cursor" :class="{ big: cursorBig }" />
    <div class="ds-cursor-ring" ref="ring" />

    <!-- ── Header ── -->
    <header class="music-header">
      <div class="header-left">
        <RouterLink to="/" class="ds-logo">
          <span class="g-text">DS</span>
        </RouterLink>
        <span class="header-sep">/</span>
        <span class="header-section">Music</span>
      </div>
      <span class="header-user">{{ store.user?.nickName }}</span>
    </header>

    <!-- ── Body ── -->
    <div class="page-inner">
      <template v-if="store.isAdmin">
        <div class="page-title-block">
          <p class="eyebrow">UPLOAD</p>
          <h1 class="page-title">새 트랙 등록</h1>
          <p class="page-sub">오디오 파일과 메타데이터를 입력해 트랙을 등록하세요.</p>
        </div>

        <div v-if="successMsg" class="msg-bar success">
          <v-icon size="16" class="mr-1">mdi-check-circle-outline</v-icon>
          {{ successMsg }}
        </div>
        <div v-if="errorMsg" class="msg-bar error">
          <v-icon size="16" class="mr-1">mdi-alert-circle-outline</v-icon>
          {{ errorMsg }}
        </div>

        <form class="upload-form" @submit.prevent="submit">

          <!-- ── 커버 + 메타 ── -->
          <div class="form-top">

            <!-- 커버 이미지 -->
            <div class="cover-upload" :class="{ 'has-cover': coverPreviewUrl }">
              <img v-if="coverPreviewUrl" :src="coverPreviewUrl" alt="cover" class="cover-preview" />
              <div v-else class="cover-placeholder">
                <v-icon size="32" color="rgba(255,255,255,.18)">mdi-image-outline</v-icon>
                <span>커버 이미지</span>
                <span class="cover-hint">선택 사항</span>
              </div>
              <label class="cover-label">
                <input type="file" accept="image/*" class="hidden-input" @change="onCoverChange" />
              </label>
              <button v-if="coverPreviewUrl" type="button" class="cover-remove" @click="removeCover">
                <v-icon size="14">mdi-close</v-icon>
              </button>
            </div>

            <!-- 메타데이터 -->
            <div class="meta-fields">
              <div class="field-group">
                <label class="field-label">제목 <span class="required">*</span></label>
                <input v-model="title" class="field-input" type="text" placeholder="트랙 제목" maxlength="100" />
              </div>
              <div class="field-group">
                <label class="field-label">아티스트 <span class="required">*</span></label>
                <input v-model="artist" class="field-input" type="text" placeholder="아티스트명" maxlength="100" />
              </div>
              <div class="field-group">
                <label class="field-label">장르 <span class="required">*</span></label>
                <div class="genre-grid">
                  <button
                    v-for="g in GENRES"
                    :key="g"
                    type="button"
                    class="genre-chip"
                    :class="{ active: genre === g }"
                    @click="genre = g"
                  >{{ g }}</button>
                </div>
              </div>
            </div>
          </div>

          <!-- 설명 -->
          <div class="field-group">
            <label class="field-label">설명</label>
            <textarea
              v-model="description"
              class="field-input field-textarea"
              placeholder="트랙에 대한 설명을 입력하세요..."
              rows="3"
              maxlength="500"
            />
            <span class="char-count">{{ description.length }} / 500</span>
          </div>

          <!-- 오디오 업로드 -->
          <div class="field-group">
            <label class="field-label">오디오 파일 <span class="required">*</span></label>
            <label class="audio-drop" :class="{ 'has-file': audioFile }">
              <input type="file" accept="audio/*" class="hidden-input" @change="onAudioChange" />
              <template v-if="!audioFile">
                <v-icon size="28" color="rgba(255,255,255,.25)">mdi-music-note-plus</v-icon>
                <span class="drop-text">클릭하여 오디오 파일 선택</span>
                <span class="drop-hint">MP3, WAV, FLAC, AAC 등</span>
              </template>
              <template v-else>
                <v-icon size="22" color="#5b9cf6">mdi-music-circle</v-icon>
                <span class="drop-text file-name">{{ audioFile.name }}</span>
                <span class="drop-hint">{{ (audioFile.size / 1024 / 1024).toFixed(1) }} MB</span>
              </template>
            </label>

            <!-- 오디오 미리듣기 -->
            <audio v-if="audioPreviewUrl" :src="audioPreviewUrl" controls class="audio-player" />
          </div>

          <!-- 제출 -->
          <div class="form-footer">
            <button type="button" class="btn-ghost" @click="reset" :disabled="submitting">초기화</button>
            <button type="submit" class="btn-pri submit-btn" :disabled="!canSubmit()">
              <v-progress-circular v-if="submitting" indeterminate size="14" width="2" color="#fff" class="mr-1" />
              <v-icon v-else size="14" class="mr-1">mdi-upload</v-icon>
              {{ submitting ? '업로드 중...' : '트랙 등록' }}
            </button>
          </div>
        </form>
      </template>

      <!-- ── 트랙 목록 ── -->
      <div class="library-block">
        <div class="library-header">
          <div>
            <p class="eyebrow">LIBRARY</p>
            <h2 class="page-title library-title">전체 트랙</h2>
          </div>
          <span class="track-count">{{ musicList.length }}곡</span>
        </div>

        <div v-if="loadingList" class="library-state">불러오는 중...</div>
        <div v-else-if="musicList.length === 0" class="library-state">아직 업로드된 트랙이 없습니다.</div>

        <div v-else class="library-list">
          <div v-for="m in musicList" :key="m.id" class="track-item">
            <div class="track-main">
              <div class="track-cover">
                <img v-if="m.coverImageUrl" :src="m.coverImageUrl" alt="" class="track-cover-img" />
                <div v-else class="track-cover-placeholder">
                  <v-icon size="20" color="rgba(255,255,255,.2)">mdi-music-note</v-icon>
                </div>
              </div>

              <!-- 일반 표시 -->
              <div v-if="editingId !== m.id" class="track-info">
                <p class="track-title">{{ m.title }}</p>
                <p class="track-meta">{{ m.artist }} · {{ m.genre }}</p>
                <p v-if="m.description" class="track-desc">{{ m.description }}</p>
              </div>

              <!-- 수정 폼 -->
              <div v-else class="track-edit-form">
                <input v-model="editForm.title" class="field-input edit-input" placeholder="제목" maxlength="100" />
                <input v-model="editForm.artist" class="field-input edit-input" placeholder="아티스트" maxlength="100" />
                <select v-model="editForm.genre" class="field-input edit-input">
                  <option v-for="g in GENRES" :key="g" :value="g">{{ g }}</option>
                </select>
                <textarea
                  v-model="editForm.description"
                  class="field-input edit-input edit-textarea"
                  placeholder="설명"
                  rows="2"
                  maxlength="500"
                />
              </div>
            </div>

            <audio :src="m.audioUrl" controls class="track-audio" @play="onPlay(m)" />

            <div class="track-actions">
              <span class="track-stat">
                <v-icon size="14">mdi-play-circle-outline</v-icon>
                {{ m.play }}
              </span>

              <div class="track-action-buttons">
                <button class="track-action-btn" :class="{ liked: m.liked }" @click="toggleLike(m)">
                  <v-icon size="15">{{ m.liked ? 'mdi-heart' : 'mdi-heart-outline' }}</v-icon>
                  {{ m.likedCount }}
                </button>

                <template v-if="m.nickName === store.user?.nickName">
                  <template v-if="editingId === m.id">
                    <button class="track-action-btn save" @click="saveEdit(m)">저장</button>
                    <button class="track-action-btn" @click="cancelEdit">취소</button>
                  </template>
                  <template v-else>
                    <button class="track-action-btn" @click="startEdit(m)">
                      <v-icon size="14">mdi-pencil-outline</v-icon> 수정
                    </button>
                    <button class="track-action-btn danger" @click="removeTrack(m.id)">
                      <v-icon size="14">mdi-delete-outline</v-icon> 삭제
                    </button>
                  </template>
                </template>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.music-page {
  min-height: 100vh;
  background: var(--bg);
  color: var(--text);
  font-family: var(--font);
}

/* ── Header ───────────────────────────────────────────────── */
.music-header {
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
.header-section { font-size: 13px; color: var(--text-2); }
.header-user { font-size: 12px; color: var(--text-3); }

/* ── Inner ────────────────────────────────────────────────── */
.page-inner {
  max-width: 760px; margin: 0 auto;
  padding: 100px 48px 80px;
}

/* ── Title ────────────────────────────────────────────────── */
.page-title-block { margin-bottom: 40px; }
.eyebrow {
  font-size: 10px; font-weight: 700; letter-spacing: .22em;
  text-transform: uppercase; color: var(--blue); margin-bottom: 12px;
}
.page-title {
  font-family: var(--font-d); font-size: clamp(28px, 4vw, 40px);
  font-weight: 700; letter-spacing: -.03em; line-height: 1.1;
  color: var(--text); margin-bottom: 10px;
}
.page-sub { font-size: 14px; color: var(--text-3); }

/* ── Messages ─────────────────────────────────────────────── */
.msg-bar {
  display: flex; align-items: center;
  border-radius: var(--r-sm); padding: 12px 16px;
  font-size: 13px; margin-bottom: 24px;
}
.msg-bar.success {
  background: rgba(91,156,246,.08); border: 1px solid rgba(91,156,246,.25); color: #5b9cf6;
}
.msg-bar.error {
  background: rgba(255,71,87,.08); border: 1px solid rgba(255,71,87,.2); color: #ff4757;
}

/* ── Form ─────────────────────────────────────────────────── */
.upload-form { display: flex; flex-direction: column; gap: 28px; }

.form-top {
  display: grid; grid-template-columns: 180px 1fr; gap: 28px; align-items: start;
}

/* ── Cover ────────────────────────────────────────────────── */
.cover-upload {
  position: relative;
  aspect-ratio: 1; border-radius: var(--r);
  border: 1px solid var(--glass-border);
  background: var(--glass); overflow: hidden;
  cursor: pointer; transition: border-color .2s;
}
.cover-upload:hover { border-color: rgba(255,255,255,.2); }
.cover-placeholder {
  width: 100%; height: 100%;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 6px; font-size: 12px; color: var(--text-3);
}
.cover-hint { font-size: 10px; color: var(--text-3); opacity: .6; }
.cover-preview { width: 100%; height: 100%; object-fit: cover; display: block; }
.cover-label {
  position: absolute; inset: 0; cursor: pointer;
}
.cover-remove {
  position: absolute; top: 8px; right: 8px;
  width: 24px; height: 24px; border-radius: 50%;
  background: rgba(0,0,0,.6); border: none;
  color: #fff; cursor: pointer; display: flex; align-items: center; justify-content: center;
  z-index: 2;
}

/* ── Fields ───────────────────────────────────────────────── */
.meta-fields { display: flex; flex-direction: column; gap: 16px; }
.field-group { display: flex; flex-direction: column; gap: 8px; position: relative; }
.field-label {
  font-size: 11px; font-weight: 600; letter-spacing: .1em;
  text-transform: uppercase; color: var(--text-3);
}
.required { color: #f472b6; }
.field-input {
  background: var(--glass); border: 1px solid var(--glass-border);
  border-radius: var(--r-sm); padding: 10px 14px;
  color: var(--text); font: 14px var(--font);
  outline: none; transition: border-color .2s; width: 100%;
}
.field-input::placeholder { color: var(--text-3); }
.field-input:focus { border-color: rgba(91,156,246,.45); }
.field-textarea { resize: vertical; min-height: 72px; }
.char-count { font-size: 11px; color: var(--text-3); text-align: right; }

/* ── Genre chips ──────────────────────────────────────────── */
.genre-grid { display: flex; flex-wrap: wrap; gap: 6px; }
.genre-chip {
  padding: 5px 12px; border-radius: 100px;
  background: var(--glass); border: 1px solid var(--glass-border);
  font: 12px var(--font); color: var(--text-3);
  cursor: pointer; transition: all .15s;
}
.genre-chip:hover { border-color: rgba(255,255,255,.2); color: var(--text-2); }
.genre-chip.active {
  background: rgba(91,156,246,.15); border-color: rgba(91,156,246,.5);
  color: #5b9cf6; font-weight: 600;
}

/* ── Audio upload ─────────────────────────────────────────── */
.audio-drop {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 8px; padding: 32px 20px;
  border: 1px dashed var(--glass-border); border-radius: var(--r);
  background: var(--glass); cursor: pointer; transition: all .2s;
}
.audio-drop:hover { border-color: rgba(255,255,255,.2); background: rgba(255,255,255,.04); }
.audio-drop.has-file { border-style: solid; border-color: rgba(91,156,246,.35); }
.drop-text { font-size: 13px; color: var(--text-2); }
.drop-hint { font-size: 11px; color: var(--text-3); }
.file-name { color: var(--text); font-weight: 500; word-break: break-all; text-align: center; }
.hidden-input { display: none; }

.audio-player {
  width: 100%; margin-top: 12px;
  border-radius: var(--r-sm); height: 40px;
  accent-color: #5b9cf6;
}

/* ── Footer ───────────────────────────────────────────────── */
.form-footer {
  display: flex; justify-content: flex-end; gap: 12px;
  padding-top: 8px; border-top: 1px solid var(--glass-border);
}
.submit-btn {
  display: inline-flex; align-items: center;
  padding: 11px 28px; font-size: 13px;
}
.submit-btn:disabled { opacity: .4; cursor: not-allowed; }

/* ── Library ──────────────────────────────────────────────── */
.library-block {
  margin-top: 56px; padding-top: 40px;
  border-top: 1px solid var(--glass-border);
}
.library-header {
  display: flex; align-items: flex-end; justify-content: space-between;
  margin-bottom: 24px;
}
.library-title { margin-bottom: 0; }
.track-count { font-size: 12px; color: var(--text-3); }

.library-state {
  padding: 32px 0; text-align: center;
  font-size: 13px; color: var(--text-3);
}

.library-list { display: flex; flex-direction: column; gap: 12px; }

.track-item {
  display: flex; flex-direction: column; gap: 12px;
  padding: 14px 16px; border-radius: var(--r-sm);
  background: var(--glass); border: 1px solid var(--glass-border);
}

.track-main { display: flex; gap: 16px; align-items: flex-start; }

.track-cover {
  width: 52px; height: 52px; border-radius: 8px; overflow: hidden; flex-shrink: 0;
  background: rgba(255,255,255,.04);
}
.track-cover-img { width: 100%; height: 100%; object-fit: cover; display: block; }
.track-cover-placeholder {
  width: 100%; height: 100%;
  display: flex; align-items: center; justify-content: center;
}

.track-info { min-width: 0; flex: 1; }
.track-title {
  font-size: 14px; font-weight: 600; color: var(--text);
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.track-meta { font-size: 12px; color: var(--text-3); margin-top: 2px; }
.track-desc {
  font-size: 12px; color: var(--text-3); margin-top: 4px;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}

.track-edit-form { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 8px; }
.edit-input { padding: 8px 10px; font-size: 13px; }
.edit-textarea { resize: vertical; min-height: 50px; }

.track-audio { width: 100%; height: 36px; accent-color: #5b9cf6; }

/* ── Actions ──────────────────────────────────────────────── */
.track-actions { display: flex; align-items: center; justify-content: space-between; }
.track-stat {
  display: inline-flex; align-items: center; gap: 4px;
  font-size: 12px; color: var(--text-3);
}
.track-action-buttons { display: flex; gap: 8px; }
.track-action-btn {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 5px 11px; border-radius: 100px;
  background: rgba(255,255,255,.05); border: 1px solid var(--glass-border);
  font: 12px var(--font); color: var(--text-2);
  cursor: pointer; transition: all .15s;
}
.track-action-btn:hover { background: rgba(255,255,255,.09); color: var(--text); }
.track-action-btn.liked { color: #f472b6; border-color: rgba(244,114,182,.35); background: rgba(244,114,182,.08); }
.track-action-btn.danger:hover { color: #ff4757; border-color: rgba(255,71,87,.35); background: rgba(255,71,87,.08); }
.track-action-btn.save { color: #5b9cf6; border-color: rgba(91,156,246,.35); background: rgba(91,156,246,.08); }

/* ── Responsive ───────────────────────────────────────────── */
@media (max-width: 600px) {
  .music-header { padding: 14px 20px; }
  .page-inner { padding: 80px 20px 60px; }
  .form-top { grid-template-columns: 1fr; }
  .cover-upload { max-width: 180px; }
}
</style>
