<template>
  <section id="music" class="music-section">
    <div class="section-wrap">
      <div class="section-header reveal">
        <p class="s-label">Sonic Universe</p>
        <h2 class="s-title">Music</h2>
        <p class="s-sub">Original electronic productions — each track a chapter in a digital world.</p>
      </div>

      <div class="music-layout">
        <!-- Track list -->
        <div class="track-list reveal-l">
          <div class="list-header">
            <span class="col-num">#</span>
            <span class="col-title">Title</span>
            <span class="col-bpm">BPM</span>
            <span class="col-dur">Duration</span>
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

            <div class="t-cover" :style="`background:linear-gradient(135deg,${t.colorA},${t.colorB})`">
              <span>{{ t.emoji }}</span>
            </div>

            <div class="t-info">
              <span class="t-name">{{ t.title }}</span>
              <span class="t-meta">{{ t.album }} · {{ t.genre }}</span>
            </div>

            <span class="col-bpm t-bpm">{{ t.bpm }}</span>
            <span class="col-dur t-dur">{{ t.duration }}</span>

            <button class="t-detail-btn" @click.stop="openModal(t)">↗</button>
          </div>
        </div>

        <!-- Now playing -->
        <div class="now-playing reveal-r">
          <div class="np-art" :style="`background:linear-gradient(135deg,${(current ?? tracks[0]).colorA},${(current ?? tracks[0]).colorB})`">
            <span class="np-emoji">{{ (current ?? tracks[0]).emoji }}</span>
            <div class="np-overlay" />
          </div>

          <div class="np-info">
            <div class="np-badge">NOW PLAYING</div>
            <h3 class="np-track">{{ (current ?? tracks[0]).title }}</h3>
            <p class="np-album">{{ (current ?? tracks[0]).album }} · {{ (current ?? tracks[0]).genre }}</p>

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
                <span>{{ (current ?? tracks[0]).duration }}</span>
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

            <button class="np-open-btn" @click="openModal(current ?? tracks[0])">
              View Story & Lyrics →
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Track modal -->
    <div :class="['modal-overlay', { open: !!modalTrack }]" @click.self="modalTrack = null">
      <div class="modal-box" v-if="modalTrack">
        <button class="modal-close" @click="modalTrack = null">✕</button>

        <div class="modal-art" :style="`background:linear-gradient(135deg,${modalTrack.colorA},${modalTrack.colorB})`">
          <span class="modal-art-emoji">{{ modalTrack.emoji }}</span>
          <div class="modal-art-overlay" />
          <div class="modal-art-meta">
            <span class="tag tag-purple">{{ modalTrack.genre }}</span>
            <span class="tag tag-blue">{{ modalTrack.bpm }} BPM</span>
            <span class="tag tag-gray">{{ modalTrack.released }}</span>
          </div>
        </div>

        <div class="modal-body">
          <h2 class="modal-title">{{ modalTrack.title }}</h2>
          <p class="modal-album">{{ modalTrack.album }}</p>

          <div class="modal-section">
            <h4>The Story</h4>
            <p>{{ modalTrack.story }}</p>
          </div>

          <div class="modal-section">
            <h4>Lyrics</h4>
            <pre class="modal-lyrics">{{ modalTrack.lyrics }}</pre>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { tracks } from '@/data'

type Track = typeof tracks[0]

const current = ref<Track>(tracks[0])
const playing = ref(false)
const progress = ref(18)
const currentTime = ref('0:42')
const modalTrack = ref<Track | null>(null)

let timer: ReturnType<typeof setInterval> | null = null

function selectTrack(t: Track) {
  current.value = t
  playing.value = true
  progress.value = 0
  currentTime.value = '0:00'
  startTimer()
}

function togglePlay() {
  playing.value = !playing.value
  if (playing.value) startTimer()
  else stopTimer()
}

function startTimer() {
  stopTimer()
  timer = setInterval(() => {
    if (!playing.value) return
    progress.value = Math.min(progress.value + .12, 99)
    const total = parseDuration(current.value.duration)
    const secs = Math.floor(progress.value / 100 * total)
    currentTime.value = `${Math.floor(secs/60)}:${String(secs%60).padStart(2,'0')}`
  }, 300)
}

function stopTimer() { if (timer) { clearInterval(timer); timer = null } }

function parseDuration(d: string) {
  const [m, s] = d.split(':').map(Number)
  return m * 60 + s
}

function seekProgress(e: MouseEvent) {
  const rect = (e.currentTarget as HTMLElement).getBoundingClientRect()
  progress.value = Math.max(0, Math.min(((e.clientX - rect.left) / rect.width) * 100, 99))
}

function prevTrack() {
  const i = tracks.findIndex(t => t.id === current.value.id)
  selectTrack(tracks[(i - 1 + tracks.length) % tracks.length])
}

function nextTrack() {
  const i = tracks.findIndex(t => t.id === current.value.id)
  selectTrack(tracks[(i + 1) % tracks.length])
}

function openModal(t: Track) { modalTrack.value = t }

onMounted(() => startTimer())
onUnmounted(() => stopTimer())
</script>

<style scoped>
.music-section { background: var(--bg-2); }
.section-header { margin-bottom: 56px; }

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
.np-emoji { font-size: 72px; filter: drop-shadow(0 0 30px rgba(0,0,0,.5)); animation: float 3s ease-in-out infinite; }
.np-overlay { position: absolute; inset: 0; background: linear-gradient(to bottom, transparent 50%, var(--bg-3)); }

.np-info { padding: 20px 24px 28px; }
.np-badge {
  font-size: 9px; letter-spacing: .18em; color: var(--blue); font-weight: 600; margin-bottom: 8px;
}
.np-track { font-family: 'Space Grotesk',sans-serif; font-size: 20px; font-weight: 700; margin-bottom: 4px; letter-spacing: -.01em; }
.np-album { font-size: 12px; color: var(--text-3); margin-bottom: 16px; }

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
.modal-art-emoji { font-size: 80px; z-index: 1; filter: drop-shadow(0 0 40px rgba(0,0,0,.5)); }
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
.modal-lyrics {
  font-family: 'SF Mono','Fira Code',monospace;
  font-size: 13px; color: var(--text-2);
  line-height: 1.9; white-space: pre-line;
  border-left: 2px solid rgba(167,139,250,.3);
  padding-left: 16px;
}

@media (max-width: 1024px) {
  .music-layout { grid-template-columns: 1fr; }
  .now-playing { position: relative; top: 0; }
}
@media (max-width: 600px) {
  .list-header, .track-row { grid-template-columns: 36px 40px 1fr auto; }
  .col-bpm, .t-bpm { display: none; }
}
</style>
