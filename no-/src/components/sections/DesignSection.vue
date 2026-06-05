<template>
  <section id="design" class="design-section">
    <div class="section-wrap">
      <div class="section-header reveal">
        <p class="s-label">Visual Work</p>
        <h2 class="s-title">Design</h2>
        <p class="s-sub">Posters, interfaces, identities — visual language as a form of system design.</p>
      </div>

      <div class="masonry">
        <div
          v-for="(d, i) in designs" :key="d.id"
          :class="['masonry-item', { tall: d.tall }, `reveal`]"
          :style="`transition-delay:${i*60}ms`"
          @click="open(d)"
        >
          <div class="design-visual" :class="`dv-${d.type}`"
            :style="`background:linear-gradient(135deg,${d.colorA} 0%,${d.colorB} 100%)`">
            <!-- UI Design -->
            <template v-if="d.type === 'ui'">
              <div class="dv-ui-shell">
                <div class="dv-bar">
                  <span v-for="c in ['#ff5f57','#ffbd2e','#28c840']" :key="c" :style="`background:${c}`" />
                </div>
                <div class="dv-ui-content">
                  <div class="dv-sidebar">
                    <div v-for="n in 5" :key="n" class="dv-menu-item" :style="`opacity:${1-n*.12}`" />
                  </div>
                  <div class="dv-main-area">
                    <div class="dv-chart">
                      <div v-for="(h,i) in [60,85,40,70,95,50,80]" :key="i" class="dv-chart-bar" :style="`height:${h}%`" />
                    </div>
                    <div class="dv-stats">
                      <div v-for="n in 3" :key="n" class="dv-stat" />
                    </div>
                  </div>
                </div>
              </div>
            </template>

            <!-- Brand Identity -->
            <template v-else-if="d.type === 'brand'">
              <div class="dv-brand">
                <div class="dv-brand-mark">DS</div>
                <div class="dv-brand-lines">
                  <div v-for="n in 3" :key="n" class="dv-line" :style="`width:${40+n*20}%;opacity:${.4+n*.18}`" />
                </div>
                <div class="dv-brand-palette">
                  <div v-for="c in ['#5b9cf6','#a78bfa','#f5f5f7','#1a1a2e']" :key="c" :style="`background:${c}`" />
                </div>
              </div>
            </template>

            <!-- Poster -->
            <template v-else-if="d.type === 'poster'">
              <div class="dv-poster">
                <div class="dv-poster-text">NEON<br>GLOW</div>
                <div class="dv-poster-glow" />
              </div>
            </template>

            <!-- OS Concept -->
            <template v-else-if="d.type === 'concept'">
              <div class="dv-os">
                <div class="dv-os-dock">
                  <div v-for="n in 4" :key="n" class="dv-dock-icon" />
                </div>
                <div class="dv-os-window">
                  <div class="dv-os-titlebar" />
                  <div class="dv-os-content">
                    <div v-for="n in 3" :key="n" class="dv-os-row" :style="`width:${90-n*15}%`" />
                  </div>
                </div>
              </div>
            </template>

            <!-- Typography -->
            <template v-else-if="d.type === 'type'">
              <div class="dv-type">
                <div class="dv-letter">Aa</div>
                <div class="dv-type-grid">
                  <span v-for="c in 'ABCDEF'" :key="c">{{ c }}</span>
                </div>
              </div>
            </template>

            <!-- HUD -->
            <template v-else-if="d.type === 'hud'">
              <div class="dv-hud">
                <div class="dv-hud-ring" />
                <div class="dv-hud-bars">
                  <div v-for="(v,i) in [80,55,90,40]" :key="i" class="dv-hud-bar" :style="`height:${v}%`" />
                </div>
                <div class="dv-hud-label">HP 80 / 100</div>
              </div>
            </template>
          </div>

          <div class="design-overlay">
            <div class="design-info">
              <span class="design-cat">{{ d.category }}</span>
              <h4 class="design-title">{{ d.title }}</h4>
              <div class="design-tags">
                <span v-for="t in d.tags" :key="t" class="tag tag-gray">{{ t }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal -->
    <div :class="['modal-overlay', { open: !!active }]" @click.self="active = null">
      <div class="modal-box" v-if="active">
        <button class="modal-close" @click="active = null">✕</button>

        <div class="dm-visual" :style="`background:linear-gradient(135deg,${active.colorA},${active.colorB})`">
          <div class="dm-category">{{ active.category }}</div>
        </div>

        <div class="dm-body">
          <div class="dm-tags">
            <span v-for="t in active.tags" :key="t" class="tag tag-gray">{{ t }}</span>
            <span class="tag tag-gray">{{ active.year }}</span>
          </div>
          <h2 class="dm-title">{{ active.title }}</h2>
          <p class="dm-desc">A visual exploration of {{ active.category.toLowerCase() }} — combining systematic thinking with aesthetic precision.</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { designs } from '@/data'

type Design = typeof designs[0]
const active = ref<Design | null>(null)
function open(d: Design) { active.value = d }
</script>

<style scoped>
.design-section { background: var(--bg); }
.section-header { margin-bottom: 56px; }

/* ─ Masonry ─ */
.masonry {
  columns: 3; column-gap: 18px;
}
.masonry-item {
  break-inside: avoid; margin-bottom: 18px;
  border-radius: var(--r); overflow: hidden; cursor: pointer;
  background: var(--bg-3);
  border: 1px solid var(--glass-border);
  position: relative;
  transition: all .4s cubic-bezier(.4,0,.2,1);
}
.masonry-item:hover {
  border-color: rgba(167,139,250,.4);
  transform: scale(1.02);
  box-shadow: 0 20px 60px rgba(0,0,0,.4), 0 0 30px rgba(167,139,250,.08);
  z-index: 5;
}

.design-visual {
  width: 100%; height: 220px;
  display: flex; align-items: center; justify-content: center;
  position: relative; overflow: hidden;
}
.masonry-item.tall .design-visual { height: 320px; }

/* ─ Design visuals ─ */
/* UI */
.dv-ui-shell { width: 90%; height: 85%; background: rgba(0,0,0,.5); border-radius: 8px; overflow: hidden; }
.dv-bar {
  height: 20px; background: rgba(255,255,255,.06);
  display: flex; align-items: center; gap: 5px; padding: 0 10px;
}
.dv-bar span { width: 8px; height: 8px; border-radius: 50%; }
.dv-ui-content { display: flex; height: calc(100% - 20px); }
.dv-sidebar { width: 60px; padding: 10px 8px; display: flex; flex-direction: column; gap: 8px; }
.dv-menu-item { height: 6px; border-radius: 3px; background: rgba(255,255,255,.15); }
.dv-main-area { flex: 1; padding: 10px; display: flex; flex-direction: column; gap: 8px; }
.dv-chart { display: flex; align-items: flex-end; gap: 4px; height: 60px; }
.dv-chart-bar { flex: 1; border-radius: 3px 3px 0 0; background: linear-gradient(to top,#5b9cf6,#a78bfa); min-width: 8px; }
.dv-stats { display: flex; gap: 8px; }
.dv-stat { flex: 1; height: 20px; border-radius: 4px; background: rgba(255,255,255,.08); }

/* Brand */
.dv-brand { display: flex; flex-direction: column; align-items: center; gap: 12px; }
.dv-brand-mark { font-family: 'Space Grotesk',sans-serif; font-size: 40px; font-weight: 700; color: rgba(255,255,255,.9); letter-spacing: .05em; }
.dv-brand-lines { display: flex; flex-direction: column; gap: 4px; width: 80%; }
.dv-line { height: 2px; background: rgba(255,255,255,.5); border-radius: 1px; }
.dv-brand-palette { display: flex; gap: 4px; margin-top: 4px; }
.dv-brand-palette div { width: 20px; height: 20px; border-radius: 50%; }

/* Poster */
.dv-poster { position: relative; display: flex; align-items: center; justify-content: center; }
.dv-poster-text {
  font-family: 'Space Grotesk',sans-serif;
  font-size: 32px; font-weight: 700;
  color: rgba(255,255,255,.9); text-align: center; line-height: 1.1;
  letter-spacing: -.02em; z-index: 1; position: relative;
  text-shadow: 0 0 40px rgba(251,146,60,.8);
}
.dv-poster-glow {
  position: absolute; width: 120px; height: 120px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(251,146,60,.4) 0%, transparent 70%);
  animation: pulse 3s ease-in-out infinite;
}

/* OS */
.dv-os { width: 90%; height: 85%; display: flex; flex-direction: column; align-items: center; gap: 12px; }
.dv-os-window { width: 100%; flex: 1; background: rgba(0,0,0,.6); border-radius: 8px; overflow: hidden; border: 1px solid rgba(255,255,255,.1); }
.dv-os-titlebar { height: 20px; background: rgba(255,255,255,.04); border-bottom: 1px solid rgba(255,255,255,.06); }
.dv-os-content { padding: 10px; display: flex; flex-direction: column; gap: 8px; }
.dv-os-row { height: 8px; border-radius: 4px; background: rgba(255,255,255,.1); }
.dv-os-dock { display: flex; gap: 6px; }
.dv-dock-icon { width: 28px; height: 28px; border-radius: 8px; background: rgba(255,255,255,.12); }

/* Type */
.dv-type { display: flex; flex-direction: column; align-items: center; gap: 12px; }
.dv-letter { font-size: 56px; font-weight: 700; color: rgba(255,255,255,.9); line-height: 1; }
.dv-type-grid { display: flex; gap: 8px; }
.dv-type-grid span { font-size: 14px; font-weight: 600; color: rgba(255,255,255,.5); }

/* HUD */
.dv-hud { position: relative; display: flex; flex-direction: column; align-items: center; gap: 10px; }
.dv-hud-ring { width: 80px; height: 80px; border-radius: 50%; border: 2px solid #34d399; box-shadow: 0 0 20px rgba(52,211,153,.4); animation: pulse 2s ease-in-out infinite; }
.dv-hud-bars { display: flex; align-items: flex-end; gap: 4px; height: 40px; }
.dv-hud-bar { width: 10px; border-radius: 3px 3px 0 0; background: #34d399; }
.dv-hud-label { font-size: 10px; color: rgba(52,211,153,.8); font-family: 'SF Mono',monospace; }

/* Overlay */
.design-overlay {
  position: absolute; inset: 0;
  background: rgba(0,0,0,0);
  display: flex; align-items: flex-end;
  padding: 20px;
  transition: background .3s ease;
}
.masonry-item:hover .design-overlay { background: rgba(0,0,0,.75); }
.design-info { opacity: 0; transform: translateY(8px); transition: all .3s ease; }
.masonry-item:hover .design-info { opacity: 1; transform: none; }
.design-cat { font-size: 10px; letter-spacing: .12em; text-transform: uppercase; color: var(--purple); margin-bottom: 4px; display: block; }
.design-title { font-size: 16px; font-weight: 600; margin-bottom: 8px; }
.design-tags { display: flex; gap: 6px; flex-wrap: wrap; }

/* Modal */
.dm-visual { height: 220px; display: flex; align-items: center; justify-content: center; position: relative; }
.dm-category {
  font-size: 11px; letter-spacing: .15em; text-transform: uppercase;
  color: rgba(255,255,255,.5); position: absolute; bottom: 16px; left: 20px;
}
.dm-body { padding: 24px 28px 32px; }
.dm-tags { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 12px; }
.dm-title { font-family: 'Space Grotesk',sans-serif; font-size: 26px; font-weight: 700; margin-bottom: 12px; }
.dm-desc { font-size: 14px; color: var(--text-2); line-height: 1.7; }

@media (max-width: 900px) { .masonry { columns: 2; } }
@media (max-width: 560px) { .masonry { columns: 1; } }
</style>
