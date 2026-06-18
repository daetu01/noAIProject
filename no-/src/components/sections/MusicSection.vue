<template>
  <section id="music" class="music-section">
    <div class="section-wrap">
      <div class="section-header reveal">
        <p class="s-label">Sonic Universe</p>
        <h2 class="s-title">Music</h2>
        <p class="s-sub">Original electronic productions — each track a chapter in a digital world.</p>
        <RouterLink v-if="store.isLoggedIn" to="/music" class="upload-btn">
          <span>+ 트랙 업로드</span>
        </RouterLink>
      </div>

      <div v-if="loading" class="music-state reveal">불러오는 중...</div>
      <div v-else-if="tracks.length === 0" class="music-state reveal">아직 등록된 트랙이 없습니다.</div>

      <template v-else>
        <div class="music-layout">
          <!-- Track list -->
          <div class="track-list reveal-l">
            <div class="list-header">
              <span class="col-num">#</span>
              <span class="col-title">Title</span>
              <span class="col-bpm">Plays</span>
              <span class="col-dur">Likes</span>
            </div>

            <div
              v-for="(t, i) in tracks" :key="t.id"
              :class="['track-row', { active: current?.id === t.id }]"
              @click="selectTrack(t)"
            >
              <div class="col-num">
                <span v-if="current?.id !== t.id" class="t-num">{{ i + 1 }}</span>
                <div v-else class="wave-icon">
                  <div v-for="b in 3" :key="b" class="wbar" :style="`animation-delay:${b*.18}s`" />
                </div>
              </div>

              <div class="t-cover" :style="coverStyle(t)">
                <v-icon v-if="!t.coverImageUrl" size="20" color="#ffffff">mdi-music-note</v-icon>
              </div>

              <div class="t-info">
                <span class="t-name">{{ t.title }}</span>
                <span class="t-meta">{{ t.artist }} · {{ t.genre }}</span>
              </div>

              <span class="col-bpm t-bpm">{{ t.play }}</span>
              <span class="col-dur t-dur">{{ t.likedCount }}</span>

              <button class="t-detail-btn" @click.stop="openModal(t)">↗</button>
            </div>
          </div>

          <!-- Now playing -->
          <div class="now-playing reveal-r">
            <div class="np-art" :style="coverStyle(current)">
              <v-icon v-if="!current?.coverImageUrl" size="56" color="#ffffff" class="np-icon">mdi-music-note</v-icon>
              <div class="np-overlay" />
            </div>

            <div class="np-info">
              <div class="np-badge">NOW PLAYING</div>
              <h3 class="np-track">{{ current?.title }}</h3>
              <p class="np-album">{{ current?.artist }} · {{ current?.genre }}</p>

              <!-- Waveform -->
              <div class="waveform">
                <div v-for="n in 28" :key="n" class="wf-bar"
                  :style="`animation-delay:${n*.06}s;height:${8+Math.sin(n*0.7)*14}px`" />
              </div>

              <!-- Progress -->
              <div class="progress-wrap">
                <div class="progress-track" @click="seekProgress">
                  <div class="progress-fill" :style="`width:${progress}%`" />
                  <div class="progress-thumb" :style="`left:${progress}%`" />
                </div>
                <div class="progress-times">
                  <span>{{ currentTime }}</span>
                  <span>{{ durationLabel }}</span>
                </div>
              </div>

              <!-- Controls -->
              <div class="controls">
                <button class="ctrl-btn" @click="prevTrack">⏮</button>
                <button :class="['ctrl-play', playing ? 'pause' : 'play']" @click="togglePlay">
                  <span>{{ playing ? '⏸' : '▶' }}</span>
                </button>
                <button class="ctrl-btn" @click="nextTrack">⏭</button>
              </div>

              <button class="np-open-btn" @click="openModal(current)">
                View Details →
              </button>
            </div>
          </div>
        </div>

        <audio
          ref="audioEl"
          class="np-audio"
          :src="current?.audioUrl"
          @timeupdate="onTimeUpdate"
          @loadedmetadata="onLoadedMetadata"
          @ended="nextTrack"
          @play="playing = true"
          @pause="playing = false"
        />
      </template>
    </div>

    <!-- Track modal -->
    <div :class="['modal-overlay', { open: !!modalTrack }]" @click.self="modalTrack = null">
      <div class="modal-box" v-if="modalTrack">
        <button class="modal-close" @click="modalTrack = null">✕</button>

        <div class="modal-art" :style="coverStyle(modalTrack)">
          <v-icon v-if="!modalTrack.coverImageUrl" size="64" color="#ffffff" class="modal-art-icon">mdi-music-note</v-icon>
          <div class="modal-art-overlay" />
          <div class="modal-art-meta">
            <span class="tag tag-purple">{{ modalTrack.genre }}</span>
            <span class="tag tag-blue">▶ {{ modalTrack.play }}</span>
            <span class="tag tag-gray">♥ {{ modalTrack.likedCount }}</span>
          </div>
        </div>

        <div class="modal-body">
          <h2 class="modal-title">{{ modalTrack.title }}</h2>
          <p class="modal-album">{{ modalTrack.artist }}</p>

          <div class="modal-section" v-if="modalTrack.description">
            <h4>Description</h4>
            <p>{{ modalTrack.description }}</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { RouterLink } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { musicService, type MusicItem } from '@/api/musicService'

const store = useAppStore()

const tracks = ref<MusicItem[]>([])
const loading = ref(true)
const current = ref<MusicItem | null>(null)
const playing = ref(false)
const progress = ref(0)
const currentTime = ref('0:00')
const durationLabel = ref('0:00')
const modalTrack = ref<MusicItem | null>(null)
const audioEl = ref<HTMLAudioElement | null>(null)

const palettes = [
  ['#1a1a3e', '#5b9cf6'],
  ['#1a1228', '#a78bfa'],
  ['#1a0a2e', '#f472b6'],
  ['#0a1a0a', '#34d399'],
  ['#1a1500', '#fb923c'],
  ['#050a1a', '#7dd3fc'],
]

function paletteFor(id: number) {
  return palettes[id % palettes.length]
}

function coverStyle(t?: MusicItem | null) {
  if (!t) return ''
  if (t.coverImageUrl) {
    return `background-image:url(${t.coverImageUrl});background-size:cover;background-position:center;`
  }
  const [a, b] = paletteFor(t.id)
  return `background:linear-gradient(135deg,${a},${b})`
}

function formatTime(sec: number) {
  if (!isFinite(sec) || sec < 0) return '0:00'
  const m = Math.floor(sec / 60)
  const s = Math.floor(sec % 60)
  return `${m}:${String(s).padStart(2, '0')}`
}

function selectTrack(t: MusicItem) {
  if (current.value?.id === t.id) {
    togglePlay()
    return
  }
  current.value = t
  progress.value = 0
  currentTime.value = '0:00'
  durationLabel.value = '0:00'
  nextTick(() => audioEl.value?.play().catch(() => {}))
}

function togglePlay() {
  const audio = audioEl.value
  if (!audio) return
  if (audio.paused) audio.play().catch(() => {})
  else audio.pause()
}

function onTimeUpdate() {
  const audio = audioEl.value
  if (!audio || !audio.duration) return
  progress.value = (audio.currentTime / audio.duration) * 100
  currentTime.value = formatTime(audio.currentTime)
}

function onLoadedMetadata() {
  const audio = audioEl.value
  if (!audio) return
  durationLabel.value = formatTime(audio.duration)
}

function seekProgress(e: MouseEvent) {
  const audio = audioEl.value
  if (!audio || !audio.duration) return
  const rect = (e.currentTarget as HTMLElement).getBoundingClientRect()
  const ratio = Math.max(0, Math.min((e.clientX - rect.left) / rect.width, 1))
  audio.currentTime = ratio * audio.duration
}

function prevTrack() {
  if (!current.value || tracks.value.length < 2) return
  const i = tracks.value.findIndex(t => t.id === current.value!.id)
  selectTrack(tracks.value[(i - 1 + tracks.value.length) % tracks.value.length])
}

function nextTrack() {
  if (!current.value || tracks.value.length < 2) return
  const i = tracks.value.findIndex(t => t.id === current.value!.id)
  selectTrack(tracks.value[(i + 1) % tracks.value.length])
}

function openModal(t: MusicItem | null) {
  if (t) modalTrack.value = t
}

async function loadTracks() {
  loading.value = true
  try {
    tracks.value = await musicService.list()
    current.value = tracks.value[0] ?? null
  } catch {
    tracks.value = []
  } finally {
    loading.value = false
  }
}

onMounted(loadTracks)
</script>

<style scoped>
.music-section { background: var(--bg-2); }
.section-header { margin-bottom: 56px; }
.upload-btn {
  display: inline-flex; align-items: center;
  margin-top: 20px; padding: 9px 20px;
  border-radius: 100px;
  background: rgba(91,156,246,.1); border: 1px solid rgba(91,156,246,.3);
  font-size: 13px; font-weight: 500; color: #5b9cf6;
  text-decoration: none; transition: all .25s ease;
}
.upload-btn:hover {
  background: rgba(91,156,246,.2); border-color: rgba(91,156,246,.55);
  transform: translateY(-1px); box-shadow: 0 6px 20px rgba(91,156,246,.2);
}

.music-state {
  padding: 60px 0; text-align: center;
  font-size: 14px; color: var(--text-3);
}

.music-layout { display: grid; grid-template-columns: 1fr 360px; gap: 32px; align-items: start; }

/* ─ Track list ─ */
.track-list {
  background: var(--bg-3);
  border: 1px solid var(--glass-border);
  border-radius: var(--r);
  overflow: hidden;
}

.list-header {
  display: grid; grid-template-columns: 36px 48px 1fr auto auto 32px;
  gap: 12px; align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid var(--glass-border);
  font-size: 10px; letter-spacing: .1em; text-transform: uppercase; color: var(--text-3);
}
.col-num { text-align: center; }
.col-bpm { text-align: right; }
.col-dur { text-align: right; }

.track-row {
  display: grid; grid-template-columns: 36px 48px 1fr auto auto 32px;
  gap: 12px; align-items: center;
  padding: 12px 20px;
  cursor: pointer; border-radius: 0;
  transition: background .2s ease;
  border-bottom: 1px solid rgba(255,255,255,.04);
}
.track-row:last-child { border-bottom: none; }
.track-row:hover { background: rgba(255,255,255,.04); }
.track-row.active { background: rgba(91,156,246,.06); }

.t-num { font-size: 13px; color: var(--text-3); text-align: center; }

.wave-icon {
  display: flex; align-items: center; justify-content: center; gap: 3px; height: 20px;
}
.wbar {
  width: 3px; border-radius: 2px;
  background: var(--blue);
  animation: waveBar 1s ease-in-out infinite;
}

.t-cover {
  width: 44px; height: 44px; border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  font-size: 20px; flex-shrink: 0;
}

.t-info { min-width: 0; }
.t-name { display: block; font-size: 14px; font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.t-meta { display: block; font-size: 11px; color: var(--text-3); margin-top: 2px; }

.t-bpm { font-size: 12px; color: var(--text-3); font-variant-numeric: tabular-nums; }
.t-dur { font-size: 12px; color: var(--text-3); font-variant-numeric: tabular-nums; }
.track-row.active .t-name { color: var(--blue); }

.t-detail-btn {
  width: 28px; height: 28px;
  background: rgba(255,255,255,.06); border: 1px solid rgba(255,255,255,.1);
  border-radius: 50%; color: var(--text-3); font-size: 13px;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: all .2s ease; opacity: 0;
}
.track-row:hover .t-detail-btn { opacity: 1; }
.t-detail-btn:hover { background: var(--gradient); border-color: transparent; color: #fff; }

/* ─ Now playing ─ */
.now-playing {
  background: var(--bg-3);
  border: 1px solid var(--glass-border);
  border-radius: var(--r);
  overflow: hidden;
  position: sticky; top: 88px;
}
.np-art {
  position: relative; aspect-ratio: 1;
  display: flex; align-items: center; justify-content: center;
}
.np-icon { position: relative; z-index: 1; filter: drop-shadow(0 0 30px rgba(0,0,0,.5)); animation: float 3s ease-in-out infinite; }
.np-overlay { position: absolute; inset: 0; background: linear-gradient(to bottom, transparent 50%, var(--bg-3)); }

.np-info { padding: 20px 24px 28px; }
.np-badge {
  font-size: 9px; letter-spacing: .18em; color: var(--blue); font-weight: 600; margin-bottom: 8px;
}
.np-track { font-family: 'Space Grotesk',sans-serif; font-size: 20px; font-weight: 700; margin-bottom: 4px; letter-spacing: -.01em; }
.np-album { font-size: 12px; color: var(--text-3); margin-bottom: 16px; }

.np-audio { display: none; }

/* Waveform */
.waveform { display: flex; align-items: center; gap: 2px; height: 36px; margin-bottom: 16px; }
.wf-bar {
  flex: 1; border-radius: 2px;
  background: linear-gradient(to top, var(--blue), var(--purple));
  min-height: 4px;
  animation: waveBar 1.2s ease-in-out infinite;
  opacity: .6;
}

/* Progress */
.progress-wrap { margin-bottom: 18px; }
.progress-track {
  height: 3px; background: rgba(255,255,255,.1);
  border-radius: 2px; position: relative;
  cursor: pointer; margin-bottom: 8px;
}
.progress-fill {
  height: 100%; background: var(--gradient);
  border-radius: 2px; transition: width .3s linear;
}
.progress-thumb {
  position: absolute; top: 50%; width: 10px; height: 10px;
  background: white; border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: left .3s linear;
  pointer-events: none;
}
.progress-times { display: flex; justify-content: space-between; font-size: 10px; color: var(--text-3); }

/* Controls */
.controls {
  display: flex; align-items: center; justify-content: center;
  gap: 20px; margin-bottom: 20px;
}
.ctrl-btn {
  background: none; border: none; color: var(--text-2);
  font-size: 18px; cursor: pointer; padding: 6px;
  border-radius: 50%; transition: all .2s ease;
}
.ctrl-btn:hover { color: var(--text); transform: scale(1.1); }
.ctrl-play {
  width: 52px; height: 52px;
  background: var(--gradient); border: none;
  border-radius: 50%; color: #fff;
  font-size: 20px; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all .2s ease; box-shadow: 0 4px 20px rgba(91,156,246,.3);
}
.ctrl-play:hover { transform: scale(1.07); box-shadow: 0 8px 32px rgba(91,156,246,.5); }

.np-open-btn {
  width: 100%; padding: 10px; background: rgba(255,255,255,.05);
  border: 1px solid rgba(255,255,255,.08); border-radius: 10px;
  color: var(--text-2); font-size: 13px; cursor: pointer;
  transition: all .2s ease;
}
.np-open-btn:hover { background: rgba(255,255,255,.09); color: var(--text); }

/* ─ Modal ─ */
.modal-art {
  position: relative; height: 260px;
  display: flex; align-items: center; justify-content: center;
}
.modal-art-icon { z-index: 1; filter: drop-shadow(0 0 40px rgba(0,0,0,.5)); }
.modal-art-overlay { position: absolute; inset: 0; background: linear-gradient(to bottom, transparent 40%, var(--bg-3)); }
.modal-art-meta {
  position: absolute; bottom: 16px; left: 20px;
  display: flex; gap: 8px; z-index: 2;
}

.modal-body { padding: 24px 28px 32px; }
.modal-title { font-family: 'Space Grotesk',sans-serif; font-size: 28px; font-weight: 700; margin-bottom: 4px; }
.modal-album { font-size: 13px; color: var(--text-3); margin-bottom: 28px; }
.modal-section { margin-bottom: 24px; }
.modal-section h4 { font-size: 11px; letter-spacing: .15em; text-transform: uppercase; color: var(--text-3); margin-bottom: 10px; }
.modal-section p { font-size: 14px; color: var(--text-2); line-height: 1.7; }

@media (max-width: 1024px) {
  .music-layout { grid-template-columns: 1fr; }
  .now-playing { position: relative; top: 0; }
}
@media (max-width: 600px) {
  .list-header, .track-row { grid-template-columns: 36px 40px 1fr auto; }
  .col-bpm, .t-bpm { display: none; }
}
</style>
