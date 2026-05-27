<template>
  <section id="about" class="about-section">
    <div class="section-wrap">
      <div class="section-header reveal">
        <p class="s-label">The Person</p>
        <h2 class="s-title">About</h2>
      </div>

      <div class="about-layout">
        <!-- Left: Profile + Skills -->
        <div class="about-left">
          <div class="profile-card reveal-l">
            <div class="profile-avatar">
              <span>大</span>
              <div class="avatar-ring" />
            </div>
            <h3 class="profile-name">Daean</h3>
            <p class="profile-role">Software Engineer · Creative Developer · Music Producer</p>

            <div class="profile-links">
              <a href="#" class="p-link" title="GitHub">
                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"/></svg>
              </a>
              <a href="#" class="p-link" title="Velog">
                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M3 3h18v2.5L12 19 3 5.5V3zm9 12.2L17.5 7H6.5L12 15.2z"/></svg>
              </a>
              <a href="mailto:hello@daean.studio" class="p-link" title="Email">
                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/></svg>
              </a>
            </div>
          </div>

          <!-- Skills -->
          <div class="skills-card reveal-l" style="transition-delay:100ms">
            <h4 class="skills-heading">Skills</h4>
            <div class="skills-list" ref="skillsRef">
              <div v-for="s in skills" :key="s.name" class="skill-item">
                <div class="skill-header">
                  <span class="skill-name">{{ s.name }}</span>
                  <span class="skill-pct">{{ s.level }}%</span>
                </div>
                <div class="skill-track">
                  <div
                    class="skill-fill"
                    :style="`width:${skillsVisible ? s.level : 0}%;transition-delay:${skills.indexOf(s)*80}ms`"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- Tech icons -->
          <div class="tech-grid reveal-l" style="transition-delay:200ms">
            <div v-for="t in techStack" :key="t.name" class="tech-chip">
              <span class="tech-emoji">{{ t.emoji }}</span>
              <span class="tech-name">{{ t.name }}</span>
            </div>
          </div>
        </div>

        <!-- Right: Timeline -->
        <div class="about-right">
          <h3 class="timeline-heading reveal-r">Timeline</h3>
          <div class="timeline">
            <div
              v-for="(item, i) in timeline" :key="item.date"
              :class="['timeline-item', 'reveal-r']"
              :style="`transition-delay:${i*100}ms`"
            >
              <div class="tl-dot" />
              <div class="tl-date">{{ item.date }}</div>
              <h4 class="tl-title">{{ item.title }}</h4>
              <p class="tl-org">{{ item.org }}</p>
              <p class="tl-desc">{{ item.desc }}</p>
            </div>
          </div>

          <!-- Quote block -->
          <blockquote class="about-quote reveal-r" style="transition-delay:400ms">
            <p>"The best code I've written feels like music. The best music I've made feels like architecture."</p>
          </blockquote>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { timeline, skills, techStack } from '@/data'

const skillsRef = ref<HTMLElement | null>(null)
const skillsVisible = ref(false)

let obs: IntersectionObserver | null = null

onMounted(() => {
  obs = new IntersectionObserver(([e]) => {
    if (e.isIntersecting) { skillsVisible.value = true; obs?.disconnect() }
  }, { threshold: .3 })
  if (skillsRef.value) obs.observe(skillsRef.value)
})

onUnmounted(() => obs?.disconnect())
</script>

<style scoped>
.about-section { background: var(--bg-2); }
.section-header { margin-bottom: 56px; }

.about-layout {
  display: grid; grid-template-columns: 380px 1fr;
  gap: 72px; align-items: start;
}

/* ─ Left ─ */
.about-left {
  position: sticky; top: 88px;
  display: flex; flex-direction: column; gap: 18px;
}

.profile-card {
  background: var(--bg-3);
  border: 1px solid var(--glass-border);
  border-radius: var(--r-lg);
  padding: 40px 32px;
  text-align: center;
}
.profile-avatar {
  width: 100px; height: 100px;
  background: var(--gradient);
  border-radius: 50%;
  margin: 0 auto 20px;
  display: flex; align-items: center; justify-content: center;
  font-size: 40px; font-weight: 700;
  position: relative;
}
.avatar-ring {
  position: absolute; inset: -4px;
  border-radius: 50%;
  background: var(--gradient);
  z-index: -1; opacity: .4;
  animation: pulse 3s ease-in-out infinite alternate;
  filter: blur(4px);
}
.profile-name {
  font-family: 'Space Grotesk',sans-serif;
  font-size: 22px; font-weight: 700; margin-bottom: 6px;
}
.profile-role { font-size: 12px; color: var(--text-3); line-height: 1.6; margin-bottom: 24px; }
.profile-links { display: flex; gap: 10px; justify-content: center; }
.p-link {
  width: 38px; height: 38px;
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  color: var(--text-2); transition: all .25s ease;
}
.p-link svg { width: 16px; height: 16px; }
.p-link:hover { background: rgba(255,255,255,.1); border-color: rgba(255,255,255,.2); color: #fff; transform: translateY(-2px); }

/* Skills */
.skills-card {
  background: var(--bg-3);
  border: 1px solid var(--glass-border);
  border-radius: var(--r);
  padding: 24px;
}
.skills-heading {
  font-size: 11px; letter-spacing: .15em; text-transform: uppercase;
  color: var(--text-3); margin-bottom: 18px;
}
.skills-list { display: flex; flex-direction: column; gap: 14px; }
.skill-item {}
.skill-header { display: flex; justify-content: space-between; margin-bottom: 7px; }
.skill-name { font-size: 13px; font-weight: 500; }
.skill-pct { font-size: 12px; color: var(--text-3); }
.skill-track { height: 3px; background: rgba(255,255,255,.07); border-radius: 2px; overflow: hidden; }
.skill-fill {
  height: 100%;
  background: var(--gradient);
  border-radius: 2px;
  transition: width 1.1s cubic-bezier(.4,0,.2,1);
}

/* Tech grid */
.tech-grid {
  display: grid; grid-template-columns: repeat(4,1fr); gap: 10px;
}
.tech-chip {
  background: var(--bg-3);
  border: 1px solid var(--glass-border);
  border-radius: 12px;
  padding: 12px 8px;
  display: flex; flex-direction: column; align-items: center; gap: 6px;
  transition: all .25s ease;
  cursor: default;
}
.tech-chip:hover { border-color: rgba(91,156,246,.3); transform: translateY(-3px); box-shadow: 0 8px 24px rgba(0,0,0,.3); }
.tech-emoji { font-size: 20px; }
.tech-name { font-size: 9px; color: var(--text-3); text-align: center; }

/* ─ Timeline ─ */
.timeline-heading {
  font-family: 'Space Grotesk',sans-serif;
  font-size: 22px; font-weight: 700; margin-bottom: 40px; letter-spacing: -.01em;
}

.timeline {
  position: relative; padding-left: 24px;
}
.timeline::before {
  content: '';
  position: absolute; left: 0; top: 0; bottom: 0; width: 1px;
  background: linear-gradient(to bottom, var(--blue), var(--purple), transparent);
}

.timeline-item {
  position: relative; margin-bottom: 44px; padding-bottom: 0;
}
.timeline-item:last-child { margin-bottom: 0; }

.tl-dot {
  position: absolute; left: -28px; top: 6px;
  width: 8px; height: 8px; border-radius: 50%;
  background: var(--blue);
  box-shadow: 0 0 12px rgba(91,156,246,.6);
}

.tl-date {
  font-size: 11px; color: var(--blue); letter-spacing: .1em; text-transform: uppercase; margin-bottom: 8px;
}
.tl-title {
  font-family: 'Space Grotesk',sans-serif;
  font-size: 18px; font-weight: 700; margin-bottom: 4px; letter-spacing: -.01em;
}
.tl-org { font-size: 12px; color: var(--text-3); margin-bottom: 10px; }
.tl-desc { font-size: 14px; color: var(--text-2); line-height: 1.7; }

/* Quote */
.about-quote {
  border-left: 2px solid rgba(167,139,250,.4);
  padding: 16px 20px;
  background: rgba(167,139,250,.04);
  border-radius: 0 var(--r-sm) var(--r-sm) 0;
  margin-top: 48px;
}
.about-quote p {
  font-size: 15px; font-style: italic; color: var(--text-2); line-height: 1.7;
}

@media (max-width: 1024px) {
  .about-layout { grid-template-columns: 1fr; }
  .about-left { position: relative; top: 0; }
}
@media (max-width: 480px) {
  .tech-grid { grid-template-columns: repeat(4,1fr); }
}
</style>
