<template>
  <section id="games" class="games-section">
    <div class="section-wrap">
      <div class="section-header reveal">
        <p class="s-label">Interactive Worlds</p>
        <h2 class="s-title">Games</h2>
        <p class="s-sub">Playable experiences built from first principles — design, code, and feel as one.</p>
      </div>

      <!-- Game API 서비스 배너 -->
      <RouterLink to="/games" class="game-api-banner reveal">
        <div class="api-banner-left">
          <div class="api-banner-icon">🎮</div>
          <div>
            <p class="api-banner-label">LIVE SERVICE</p>
            <p class="api-banner-title">Game API Dashboard</p>
            <p class="api-banner-desc">메이플스토리 캐릭터 검색 · 실시간 스탯 조회</p>
          </div>
        </div>
        <div class="api-banner-right">
          <span class="api-banner-badge">
            <span class="api-live-dot" />
            NEXON API
          </span>
          <span class="api-banner-arrow">→</span>
        </div>
      </RouterLink>

      <div class="games-grid stagger">
        <div
          v-for="g in games" :key="g.id"
          class="game-card reveal-s"
          @click="open(g)"
        >
          <div class="game-thumb" :class="`gt-${g.id}`"
            :style="`background:linear-gradient(160deg,${g.colorA} 0%,${g.colorB} 100%)`">

            <!-- Mirror World visual -->
            <template v-if="g.id === 'mirror'">
              <div class="gt-mirror">
                <div class="gt-portal left" />
                <div class="gt-divider" />
                <div class="gt-portal right" />
              </div>
              <div class="gt-particles">
                <div v-for="n in 8" :key="n" class="gt-particle"
                  :style="`top:${10+n*10}%;left:${8+n*11}%;animation-delay:${n*.25}s`" />
              </div>
            </template>

            <!-- Echoes visual -->
            <template v-else-if="g.id === 'echoes'">
              <div class="gt-neon-city">
                <div v-for="n in 8" :key="n" class="gt-building"
                  :style="`height:${30+n*8}%;left:${n*12}%;animation-delay:${n*.15}s`" />
              </div>
              <div class="gt-ground" />
            </template>

            <!-- Void Protocol visual -->
            <template v-else-if="g.id === 'void'">
              <div class="gt-pixel">
                <div class="gt-pixel-char">◈</div>
                <div class="gt-pixel-grid">
                  <div v-for="n in 16" :key="n" class="gt-pixel-cell"
                    :class="{ lit: [2,5,7,10,13,15].includes(n) }" />
                </div>
              </div>
            </template>

            <!-- Play button overlay -->
            <div class="game-play-overlay">
              <div class="game-play-btn">▶</div>
            </div>

            <!-- Status badge -->
            <div class="game-status-badge" :class="`gs-${g.status.toLowerCase().replace(' ','-')}`">
              {{ g.status }}
            </div>
          </div>

          <div class="game-info">
            <div class="game-meta-row">
              <span class="game-genre">{{ g.genre }}</span>
              <div class="game-rating">
                <span class="stars">★</span>
                <span class="rating-val">{{ g.rating }}</span>
                <span class="rating-cnt">({{ g.reviews }})</span>
              </div>
            </div>

            <h3 class="game-title">{{ g.title }}</h3>
            <p class="game-desc">{{ g.desc }}</p>

            <div class="game-tech">
              <span v-for="t in g.techs" :key="t" class="tech-badge">{{ t }}</span>
            </div>

            <div class="game-footer">
              <span class="game-year">{{ g.year }}</span>
              <button class="game-trailer-btn">Watch Trailer →</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal -->
    <div :class="['modal-overlay', { open: !!active }]" @click.self="active = null">
      <div class="modal-box" v-if="active">
        <button class="modal-close" @click="active = null">✕</button>

        <div class="gm-thumb"
          :style="`background:linear-gradient(160deg,${active.colorA},${active.colorB})`">
          <div class="gm-emoji">{{ active.emoji }}</div>
          <div class="gm-play-hint">
            <div class="gm-play-ring">▶</div>
            <span>Trailer Preview</span>
          </div>
        </div>

        <div class="gm-body">
          <div class="gm-head">
            <span class="game-genre">{{ active.genre }}</span>
            <div class="game-rating">
              <span class="stars">★</span>
              <span class="rating-val">{{ active.rating }}</span>
            </div>
          </div>
          <h2 class="gm-title">{{ active.title }}</h2>
          <p class="gm-trailer-text">"{{ active.trailer }}"</p>
          <p class="gm-desc">{{ active.desc }}</p>

          <div class="gm-stack">
            <h4>Tech Stack</h4>
            <div class="gm-techs">
              <span v-for="t in active.techs" :key="t" class="tech-badge">{{ t }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink } from 'vue-router'
import { games } from '@/data'

type Game = typeof games[0]
const active = ref<Game | null>(null)
function open(g: Game) { active.value = g }
</script>

<style scoped>
.games-section { background: var(--bg-2); }
.section-header { margin-bottom: 56px; }

.games-grid {
  display: grid; grid-template-columns: repeat(3,1fr);
  gap: 24px;
}

.game-card {
  background: var(--bg-3);
  border: 1px solid var(--glass-border);
  border-radius: var(--r); overflow: hidden;
  cursor: pointer; transition: all .4s cubic-bezier(.4,0,.2,1);
}
.game-card:hover {
  border-color: rgba(167,139,250,.4);
  transform: translateY(-8px);
  box-shadow: 0 32px 80px rgba(0,0,0,.5), 0 0 50px rgba(167,139,250,.1);
}

/* ─ Thumbnails ─ */
.game-thumb {
  position: relative; height: 220px;
  overflow: hidden;
  display: flex; align-items: center; justify-content: center;
}

/* Mirror */
.gt-mirror { display: flex; align-items: center; gap: 24px; z-index: 1; }
.gt-portal {
  width: 60px; height: 80px; border-radius: 50%;
  border: 2px solid rgba(255,255,255,.5);
  animation: pulse 2.5s ease-in-out infinite;
}
.gt-portal.left { background: radial-gradient(circle, rgba(91,156,246,.4) 0%,transparent 70%); box-shadow: 0 0 30px rgba(91,156,246,.5); }
.gt-portal.right { background: radial-gradient(circle, rgba(244,114,182,.4) 0%,transparent 70%); box-shadow: 0 0 30px rgba(244,114,182,.5); animation-delay: .4s; }
.gt-divider { width: 1px; height: 100px; background: linear-gradient(to bottom,transparent,rgba(255,255,255,.4),transparent); }

.gt-particles { position: absolute; inset: 0; }
.gt-particle {
  position: absolute; width: 4px; height: 4px;
  border-radius: 50%; background: rgba(255,255,255,.5);
  animation: float 3s ease-in-out infinite;
}

/* Echoes neon city */
.gt-neon-city { position: absolute; bottom: 20px; left: 0; right: 0; display: flex; align-items: flex-end; padding: 0 8px; gap: 3px; }
.gt-building {
  flex: 1; border-radius: 2px 2px 0 0;
  background: linear-gradient(to top, #a78bfa, rgba(167,139,250,.3));
  box-shadow: 0 0 8px rgba(167,139,250,.5);
  animation: pulse 2s ease-in-out infinite;
  min-height: 20px;
}
.gt-ground { position: absolute; bottom: 0; left: 0; right: 0; height: 20px; background: rgba(0,0,0,.5); }

/* Void pixel */
.gt-pixel { display: flex; flex-direction: column; align-items: center; gap: 12px; z-index: 1; }
.gt-pixel-char { font-size: 36px; color: #fb923c; text-shadow: 0 0 20px #fb923c; animation: pulse 2s ease-in-out infinite; }
.gt-pixel-grid { display: grid; grid-template-columns: repeat(4,1fr); gap: 2px; }
.gt-pixel-cell { width: 12px; height: 12px; border-radius: 2px; background: rgba(255,255,255,.06); }
.gt-pixel-cell.lit { background: rgba(251,146,60,.6); box-shadow: 0 0 4px rgba(251,146,60,.5); }

/* Play overlay */
.game-play-overlay {
  position: absolute; inset: 0;
  background: rgba(0,0,0,0);
  display: flex; align-items: center; justify-content: center;
  transition: background .3s ease;
}
.game-card:hover .game-play-overlay { background: rgba(0,0,0,.45); }

.game-play-btn {
  width: 56px; height: 56px;
  background: rgba(255,255,255,.9); border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 20px; color: #000;
  opacity: 0; transform: scale(.75);
  transition: all .3s cubic-bezier(.34,1.56,.64,1);
  padding-left: 4px;
}
.game-card:hover .game-play-btn { opacity: 1; transform: scale(1); }

.game-status-badge {
  position: absolute; top: 12px; left: 12px;
  padding: 4px 10px; border-radius: 100px;
  font-size: 10px; font-weight: 700; letter-spacing: .06em; text-transform: uppercase;
}
.gs-released    { background: rgba(52,211,153,.2); color: #34d399; border: 1px solid rgba(52,211,153,.3); }
.gs-early-access { background: rgba(251,146,60,.2); color: #fb923c; border: 1px solid rgba(251,146,60,.3); }
.gs-in-development { background: rgba(255,255,255,.08); color: var(--text-3); border: 1px solid rgba(255,255,255,.1); }

/* Card info */
.game-info { padding: 22px; display: flex; flex-direction: column; gap: 10px; }
.game-meta-row { display: flex; justify-content: space-between; align-items: center; }
.game-genre { font-size: 11px; color: var(--text-3); letter-spacing: .06em; text-transform: uppercase; }
.game-rating { display: flex; align-items: center; gap: 4px; }
.stars { color: #fbbf24; font-size: 13px; }
.rating-val { font-size: 13px; font-weight: 600; }
.rating-cnt { font-size: 11px; color: var(--text-3); }

.game-title { font-family: 'Space Grotesk',sans-serif; font-size: 20px; font-weight: 700; letter-spacing: -.01em; }
.game-desc { font-size: 13px; color: var(--text-2); line-height: 1.6; }
.game-tech { display: flex; flex-wrap: wrap; gap: 6px; }
.game-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 4px; }
.game-year { font-size: 11px; color: var(--text-3); }
.game-trailer-btn {
  background: none; border: none; color: var(--blue);
  font-size: 12px; font-weight: 500; cursor: pointer; transition: color .2s ease;
}
.game-trailer-btn:hover { color: var(--purple); }

/* Modal */
.gm-thumb {
  height: 260px; display: flex;
  flex-direction: column; align-items: center; justify-content: center;
  position: relative; overflow: hidden; gap: 16px;
}
.gm-emoji { font-size: 64px; filter: drop-shadow(0 0 30px rgba(0,0,0,.5)); animation: float 3s ease-in-out infinite; }
.gm-play-hint {
  display: flex; flex-direction: column; align-items: center; gap: 8px; z-index: 1;
}
.gm-play-ring {
  width: 52px; height: 52px;
  border: 2px solid rgba(255,255,255,.4); border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 18px; color: rgba(255,255,255,.8); padding-left: 3px;
  animation: pulse 2s ease-in-out infinite;
}
.gm-play-hint span { font-size: 11px; color: rgba(255,255,255,.5); letter-spacing: .1em; text-transform: uppercase; }

.gm-body { padding: 24px 28px 32px; }
.gm-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.gm-title { font-family: 'Space Grotesk',sans-serif; font-size: 26px; font-weight: 700; margin-bottom: 10px; }
.gm-trailer-text {
  font-size: 15px; font-style: italic; color: var(--text-2);
  border-left: 2px solid var(--purple); padding-left: 16px; margin-bottom: 16px;
  line-height: 1.6;
}
.gm-desc { font-size: 14px; color: var(--text-2); line-height: 1.7; margin-bottom: 20px; }
.gm-stack h4 { font-size: 11px; letter-spacing: .12em; text-transform: uppercase; color: var(--text-3); margin-bottom: 10px; }
.gm-techs { display: flex; flex-wrap: wrap; gap: 8px; }

/* ─ Game API Banner ─ */
.game-api-banner {
  display: flex; align-items: center; justify-content: space-between;
  padding: 20px 28px;
  background: var(--bg-3);
  border: 1px solid rgba(167,139,250,.2);
  border-radius: var(--r);
  margin-bottom: 32px;
  text-decoration: none; color: inherit;
  transition: all .3s cubic-bezier(.4,0,.2,1);
  position: relative; overflow: hidden;
}
.game-api-banner::before {
  content: '';
  position: absolute; inset: 0;
  background: linear-gradient(135deg, rgba(91,156,246,.04) 0%, rgba(167,139,250,.04) 100%);
  opacity: 0; transition: opacity .3s ease;
}
.game-api-banner:hover {
  border-color: rgba(167,139,250,.45);
  transform: translateY(-3px);
  box-shadow: 0 16px 48px rgba(0,0,0,.4), 0 0 40px rgba(167,139,250,.07);
}
.game-api-banner:hover::before { opacity: 1; }

.api-banner-left { display: flex; align-items: center; gap: 18px; }
.api-banner-icon { font-size: 32px; filter: drop-shadow(0 0 12px rgba(167,139,250,.6)); }
.api-banner-label {
  font-size: 9px; font-weight: 700; color: #a78bfa;
  letter-spacing: .2em; text-transform: uppercase; margin-bottom: 4px;
}
.api-banner-title {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 18px; font-weight: 700; color: var(--text); margin-bottom: 3px;
}
.api-banner-desc { font-size: 12px; color: var(--text-3); }

.api-banner-right { display: flex; align-items: center; gap: 16px; }
.api-banner-badge {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 5px 12px;
  background: rgba(52,211,153,.1); border: 1px solid rgba(52,211,153,.25);
  border-radius: 100px; font-size: 10px; font-weight: 700;
  color: #34d399; letter-spacing: .06em;
}
.api-live-dot {
  width: 5px; height: 5px; border-radius: 50%;
  background: #34d399; box-shadow: 0 0 5px #34d399;
  animation: pulse 2s ease-in-out infinite;
}
.api-banner-arrow {
  font-size: 18px; color: var(--text-3);
  transition: transform .2s ease, color .2s ease;
}
.game-api-banner:hover .api-banner-arrow { transform: translateX(4px); color: #a78bfa; }

@media (max-width: 900px) { .games-grid { grid-template-columns: repeat(2,1fr); } }
@media (max-width: 560px) {
  .games-grid { grid-template-columns: 1fr; }
  .game-api-banner { flex-direction: column; align-items: flex-start; gap: 16px; }
  .api-banner-right { width: 100%; justify-content: space-between; }
}
</style>
