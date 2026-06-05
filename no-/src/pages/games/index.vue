<script lang="ts" setup>
import { useRouter } from 'vue-router'
import { RouterLink } from 'vue-router'
const router = useRouter()
</script>

<template>
  <div class="games-hub">

    <!-- 헤더 -->
    <header class="hub-header">
      <RouterLink to="/" class="back-studio">
        <span class="back-arrow">←</span>
        <span class="back-text">DAEAN STUDIO</span>
      </RouterLink>
      <div class="hub-header-center">
        <p class="hub-eyebrow">GAME SERVICES</p>
        <h1 class="hub-title">Gaming Hub</h1>
      </div>
      <div class="hub-header-right" />
    </header>

    <!-- 메인 콘텐츠 -->
    <main class="hub-main">
      <p class="hub-desc">외부 게임 API와 연동된 실시간 캐릭터·전적 조회 서비스입니다.</p>

      <div class="game-list">

        <!-- 메이플스토리 -->
        <div class="game-entry" @click="router.push('/games/maple')">
          <div class="game-entry-thumb maple-thumb">
            <div class="maple-bg-grid" />
            <div class="maple-orb orb1" />
            <div class="maple-orb orb2" />
            <div class="maple-char">🍁</div>
            <div class="maple-sparkles">
              <span v-for="n in 6" :key="n" class="sparkle" :style="`--i:${n}`" />
            </div>
          </div>

          <div class="game-entry-info">
            <div class="game-entry-top">
              <div>
                <p class="game-entry-publisher">NEXON</p>
                <h2 class="game-entry-name">메이플스토리</h2>
                <p class="game-entry-sub">전적 검색 · 캐릭터 정보 · 상세 스탯</p>
              </div>
              <div class="game-entry-badges">
                <span class="entry-badge live">
                  <span class="live-dot" />
                  LIVE
                </span>
                <span class="entry-tag">캐릭터 조회</span>
                <span class="entry-tag">스탯 분석</span>
              </div>
            </div>

            <div class="game-entry-features">
              <div class="feature-item">
                <span class="feature-icon">⚔️</span>
                <span>캐릭터명으로 실시간 검색</span>
              </div>
              <div class="feature-item">
                <span class="feature-icon">📊</span>
                <span>레벨 · 직업 · 경험치 · AP 확인</span>
              </div>
              <div class="feature-item">
                <span class="feature-icon">✨</span>
                <span>파이널 스탯 전체 열람</span>
              </div>
            </div>

            <div class="game-entry-footer">
              <span class="entry-api-label">NEXON Open API</span>
              <button class="entry-cta">캐릭터 검색 →</button>
            </div>
          </div>
        </div>

        <!-- World of Warcraft (준비 중) -->
        <div class="game-entry disabled">
          <div class="game-entry-thumb wow-thumb">
            <div class="wow-bg" />
            <div class="wow-rune">⚜️</div>
          </div>

          <div class="game-entry-info">
            <div class="game-entry-top">
              <div>
                <p class="game-entry-publisher">BLIZZARD</p>
                <h2 class="game-entry-name">World of Warcraft</h2>
                <p class="game-entry-sub">캐릭터 · 레이드 · 아이템 레벨</p>
              </div>
              <div class="game-entry-badges">
                <span class="entry-badge soon">SOON</span>
              </div>
            </div>

            <div class="game-entry-features">
              <div class="feature-item muted">
                <span class="feature-icon">🛡️</span>
                <span>캐릭터 아이템 레벨 조회</span>
              </div>
              <div class="feature-item muted">
                <span class="feature-icon">⚔️</span>
                <span>레이드 클리어 현황</span>
              </div>
              <div class="feature-item muted">
                <span class="feature-icon">🏆</span>
                <span>업적 및 칭호 확인</span>
              </div>
            </div>

            <div class="game-entry-footer">
              <span class="entry-api-label muted">Blizzard Battle.net API</span>
              <span class="entry-coming">준비 중</span>
            </div>
          </div>
        </div>

      </div>
    </main>

  </div>
</template>

<style scoped>
/* ─── Layout ────────────────────────────────────────────── */
.games-hub {
  min-height: 100vh;
  background: #0b0b0f;
  background-image:
    radial-gradient(ellipse at 15% 20%, rgba(167,139,250,.08) 0%, transparent 50%),
    radial-gradient(ellipse at 85% 80%, rgba(91,156,246,.06) 0%, transparent 50%);
  color: #d0d0e8;
  font-family: 'Inter', -apple-system, sans-serif;
  -webkit-font-smoothing: antialiased;
  margin-top: -56px;
  padding-bottom: 80px;
}

/* ─── Header ─────────────────────────────────────────────── */
.hub-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 20px 32px;
  background: rgba(11,11,15,0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255,255,255,0.06);
  position: sticky; top: 0; z-index: 100;
}
.hub-header-center { text-align: center; }
.hub-header-right { width: 140px; }

.back-studio {
  display: inline-flex; align-items: center; gap: 8px;
  padding: 7px 14px;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 10px;
  text-decoration: none; color: #9090b0;
  font-size: 12px; font-weight: 600;
  letter-spacing: .04em;
  transition: all .2s ease;
  width: 140px;
}
.back-studio:hover {
  background: rgba(167,139,250,0.1);
  border-color: rgba(167,139,250,0.3);
  color: #a78bfa;
}
.back-arrow { font-size: 14px; transition: transform .2s ease; }
.back-studio:hover .back-arrow { transform: translateX(-3px); }
.back-text { font-family: 'Space Grotesk', sans-serif; }

.hub-eyebrow {
  font-size: 9px; font-weight: 700; color: #a78bfa;
  letter-spacing: .25em; text-transform: uppercase; margin-bottom: 3px;
}
.hub-title {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 22px; font-weight: 700; color: #f0f0ff; letter-spacing: -.02em;
}

/* ─── Main ───────────────────────────────────────────────── */
.hub-main {
  max-width: 900px; margin: 0 auto;
  padding: 48px 32px 0;
}
.hub-desc {
  font-size: 14px; color: #6060a0; margin-bottom: 40px;
  line-height: 1.6;
}

/* ─── Game list ──────────────────────────────────────────── */
.game-list { display: flex; flex-direction: column; gap: 16px; }

.game-entry {
  display: grid; grid-template-columns: 280px 1fr;
  background: rgba(255,255,255,0.02);
  border: 1px solid rgba(255,255,255,0.07);
  border-radius: 20px;
  overflow: hidden; cursor: pointer;
  transition: all .35s cubic-bezier(.4,0,.2,1);
}
.game-entry:not(.disabled):hover {
  border-color: rgba(167,139,250,.35);
  transform: translateY(-4px);
  box-shadow: 0 24px 60px rgba(0,0,0,.5), 0 0 50px rgba(167,139,250,.06);
}
.game-entry.disabled { cursor: default; opacity: 0.45; }

/* ─── Maple thumb ────────────────────────────────────────── */
.game-entry-thumb {
  position: relative; height: 220px;
  display: flex; align-items: center; justify-content: center;
  overflow: hidden;
}
.maple-thumb {
  background: linear-gradient(160deg, #1a0b2e 0%, #0d0820 50%, #0b1520 100%);
}
.maple-bg-grid {
  position: absolute; inset: 0;
  background-image:
    linear-gradient(rgba(167,139,250,.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(167,139,250,.05) 1px, transparent 1px);
  background-size: 28px 28px;
}
.maple-orb {
  position: absolute; border-radius: 50%;
  filter: blur(32px); opacity: .6;
}
.orb1 {
  width: 120px; height: 120px;
  background: rgba(167,139,250,.35);
  top: -20px; right: -10px;
  animation: orb-float 4s ease-in-out infinite;
}
.orb2 {
  width: 80px; height: 80px;
  background: rgba(91,156,246,.3);
  bottom: 0; left: 10px;
  animation: orb-float 5s ease-in-out infinite reverse;
}
@keyframes orb-float { 0%,100% { transform: translateY(0); } 50% { transform: translateY(-12px); } }

.maple-char {
  font-size: 64px; position: relative; z-index: 2;
  filter: drop-shadow(0 0 20px rgba(167,139,250,.8));
  animation: char-float 3s ease-in-out infinite;
}
@keyframes char-float { 0%,100% { transform: translateY(0) scale(1); } 50% { transform: translateY(-8px) scale(1.05); } }

.maple-sparkles { position: absolute; inset: 0; z-index: 1; pointer-events: none; }
.sparkle {
  position: absolute;
  width: 4px; height: 4px;
  background: rgba(255,255,255,.6);
  border-radius: 50%;
  top: calc(15% + var(--i) * 12%);
  left: calc(10% + var(--i) * 14%);
  animation: sparkle-blink 2s ease-in-out infinite;
  animation-delay: calc(var(--i) * 0.3s);
}
@keyframes sparkle-blink { 0%,100% { opacity: .2; transform: scale(1); } 50% { opacity: 1; transform: scale(1.6); } }

/* ─── WoW thumb ──────────────────────────────────────────── */
.wow-thumb {
  background: linear-gradient(160deg, #1a1208 0%, #0f0c08 100%);
}
.wow-bg {
  position: absolute; inset: 0;
  background: radial-gradient(circle at 50% 50%, rgba(251,191,36,.08) 0%, transparent 70%);
}
.wow-rune {
  font-size: 56px; position: relative; z-index: 2; opacity: .5;
  filter: grayscale(0.4);
}

/* ─── Game info ──────────────────────────────────────────── */
.game-entry-info {
  padding: 28px 32px;
  display: flex; flex-direction: column; gap: 20px; justify-content: space-between;
}
.game-entry-top {
  display: flex; justify-content: space-between; align-items: flex-start;
  gap: 16px;
}
.game-entry-publisher {
  font-size: 9px; font-weight: 700; color: #a78bfa;
  letter-spacing: .2em; text-transform: uppercase; margin-bottom: 6px;
}
.game-entry-name {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 24px; font-weight: 700; color: #f0f0ff;
  letter-spacing: -.02em; margin-bottom: 6px;
}
.game-entry-sub { font-size: 12px; color: #5060a0; }

.game-entry-badges { display: flex; flex-direction: column; gap: 8px; align-items: flex-end; flex-shrink: 0; }
.entry-badge {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 4px 12px; border-radius: 100px;
  font-size: 10px; font-weight: 800; letter-spacing: .08em;
}
.entry-badge.live {
  background: rgba(52,211,153,.12); color: #34d399; border: 1px solid rgba(52,211,153,.25);
}
.entry-badge.soon {
  background: rgba(255,255,255,.05); color: #5060a0; border: 1px solid rgba(255,255,255,.08);
}
.live-dot {
  width: 5px; height: 5px; border-radius: 50%;
  background: #34d399; box-shadow: 0 0 5px #34d399;
  animation: live-pulse 2s ease-in-out infinite;
}
@keyframes live-pulse { 0%,100% { opacity:1; } 50% { opacity:.3; } }

.entry-tag {
  padding: 3px 10px; border-radius: 6px;
  background: rgba(167,139,250,.1); color: #a78bfa;
  border: 1px solid rgba(167,139,250,.2);
  font-size: 10px; font-weight: 600;
}

/* ─── Features ───────────────────────────────────────────── */
.game-entry-features { display: flex; flex-direction: column; gap: 8px; }
.feature-item {
  display: flex; align-items: center; gap: 10px;
  font-size: 13px; color: #9090b0;
}
.feature-item.muted { color: #3a3a5a; }
.feature-icon { font-size: 14px; }

/* ─── Footer ─────────────────────────────────────────────── */
.game-entry-footer { display: flex; align-items: center; justify-content: space-between; }
.entry-api-label {
  font-size: 10px; font-weight: 700; color: #4040a0;
  letter-spacing: .06em; font-family: 'SF Mono', 'Fira Code', monospace;
}
.entry-api-label.muted { color: #2a2a4a; }
.entry-cta {
  padding: 9px 20px;
  background: linear-gradient(135deg, #5b9cf6 0%, #a78bfa 100%);
  border: none; border-radius: 10px;
  font-size: 13px; font-weight: 700; color: #fff;
  font-family: inherit; cursor: pointer;
  transition: all .2s ease;
}
.entry-cta:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(91,156,246,.35); }
.entry-coming {
  font-size: 12px; font-weight: 600; color: #3a3a5a;
}

@media (max-width: 700px) {
  .game-entry { grid-template-columns: 1fr; }
  .game-entry-thumb { height: 160px; }
}
</style>
