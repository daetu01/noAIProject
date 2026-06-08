<template>
  <nav :class="['studio-nav', { scrolled }]">
    <a href="#hero" class="nav-logo">
      <span class="g-text">DS</span>
    </a>

    <ul class="nav-links" :class="{ open: menuOpen }">
      <li v-for="link in links" :key="link.id">
        <a :href="`#${link.id}`" :class="{ active: active === link.id }" @click="menuOpen = false">
          {{ link.label }}
        </a>
      </li>
      <li class="nav-community-item">
        <RouterLink to="/board" class="nav-community" @click="menuOpen = false">Community</RouterLink>
      </li>
      <!-- mobile only -->
      <li class="mobile-login">
        <RouterLink v-if="store.isLoggedIn" to="/city" @click="menuOpen = false">Go to App →</RouterLink>
        <RouterLink v-else to="/login" @click="menuOpen = false">Sign In →</RouterLink>
      </li>
    </ul>

    <div class="nav-right">
      <template v-if="store.isLoggedIn">
        <span class="nav-user">{{ store.user?.nickName }}</span>
        <RouterLink to="/city" class="nav-login-btn">
          <span class="login-dot" />
          Go to App
        </RouterLink>
        <button class="nav-logout-btn" @click="logout">Sign Out</button>
      </template>
      <template v-else>
        <RouterLink to="/login" class="nav-login-btn">
          <span class="login-dot" />
          Sign In
        </RouterLink>
      </template>

      <button class="nav-ham" :class="{ open: menuOpen }" @click="menuOpen = !menuOpen" aria-label="menu">
        <span /><span /><span />
      </button>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'

const store = useAppStore()
const router = useRouter()

function logout() {
  store.logout()
  router.push('/login')
}

const links = [
  { id: 'hero',     label: 'Home' },
  { id: 'projects', label: 'Projects' },
  { id: 'music',    label: 'Music' },
  { id: 'design',   label: 'Design' },
  { id: 'games',    label: 'Games' },
  { id: 'lab',      label: 'Lab' },
  { id: 'about',    label: 'About' },
]

const scrolled  = ref(false)
const active    = ref('hero')
const menuOpen  = ref(false)

function onScroll() {
  scrolled.value = window.scrollY > 40
  const sections = links.map(l => document.getElementById(l.id)).filter(Boolean) as HTMLElement[]
  for (let i = sections.length - 1; i >= 0; i--) {
    if (window.scrollY >= sections[i].offsetTop - 120) {
      active.value = sections[i].id
      break
    }
  }
}

onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.studio-nav {
  position: fixed; top: 0; left: 0; right: 0;
  z-index: 900;
  display: flex; align-items: center; justify-content: space-between;
  padding: 22px 48px;
  transition: all .4s cubic-bezier(.4,0,.2,1);
}
.studio-nav.scrolled {
  background: rgba(11,11,11,.82);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border-bottom: 1px solid rgba(255,255,255,.07);
  padding: 16px 48px;
}

.nav-logo {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 20px; font-weight: 700;
  letter-spacing: .15em; flex-shrink: 0;
}

.nav-links {
  display: flex; gap: 32px; list-style: none;
}
.nav-links a {
  font-size: 13px; font-weight: 400;
  color: #6e6e73; letter-spacing: .04em;
  transition: color .25s ease;
  position: relative; padding-bottom: 2px;
}
.nav-links a::after {
  content: ''; position: absolute;
  bottom: -3px; left: 0; width: 0; height: 1px;
  background: var(--gradient);
  transition: width .3s ease;
}
.nav-links a:hover,
.nav-links a.active { color: #f5f5f7; }
.nav-links a.active::after,
.nav-links a:hover::after { width: 100%; }

.mobile-login { display: none; }

.nav-user {
  font-size: 12px; color: var(--text-3);
}
.nav-logout-btn {
  font-size: 13px; color: var(--text-3);
  background: none; border: none; cursor: pointer;
  font-family: inherit; letter-spacing: .04em;
  transition: color .2s ease;
}
.nav-logout-btn:hover { color: var(--text); }

.nav-community {
  font-size: 13px; font-weight: 500;
  color: var(--blue); letter-spacing: .04em;
  padding: 4px 12px;
  border: 1px solid rgba(91,156,246,.25);
  border-radius: 100px;
  background: rgba(91,156,246,.08);
  transition: all .25s ease;
  white-space: nowrap;
}
.nav-community:hover {
  background: rgba(91,156,246,.18);
  border-color: rgba(91,156,246,.5);
  color: #7fb3ff;
  transform: translateY(-1px);
}

/* ─── Login button ─────────────────────────────────────── */
.nav-right {
  display: flex; align-items: center; gap: 16px;
}

.nav-login-btn {
  display: inline-flex; align-items: center; gap: 7px;
  padding: 8px 18px;
  border-radius: 100px;
  background: rgba(255,255,255,.06);
  border: 1px solid rgba(255,255,255,.1);
  font-size: 13px; font-weight: 500;
  color: #a1a1a6;
  text-decoration: none;
  transition: all .25s ease;
  backdrop-filter: blur(10px);
  white-space: nowrap;
}
.nav-login-btn:hover {
  background: rgba(255,255,255,.11);
  border-color: rgba(255,255,255,.2);
  color: #f5f5f7;
  transform: translateY(-1px);
}

.login-dot {
  width: 6px; height: 6px; border-radius: 50%;
  background: #34d399;
  box-shadow: 0 0 6px #34d399;
  flex-shrink: 0;
  animation: pulse 2.4s ease-in-out infinite;
}

/* ─── Hamburger ────────────────────────────────────────── */
.nav-ham {
  display: none; flex-direction: column; gap: 5px;
  background: none; border: none; cursor: pointer; padding: 4px;
}
.nav-ham span {
  display: block; width: 22px; height: 1.5px;
  background: #a1a1a6; transition: all .3s ease;
}
.nav-ham.open span:nth-child(1) { transform: translateY(6.5px) rotate(45deg); }
.nav-ham.open span:nth-child(2) { opacity: 0; }
.nav-ham.open span:nth-child(3) { transform: translateY(-6.5px) rotate(-45deg); }

@media (max-width: 768px) {
  .studio-nav { padding: 18px 24px; }
  .studio-nav.scrolled { padding: 14px 24px; }
  .nav-ham { display: flex; }
  .nav-login-btn { display: none; }
  .mobile-login { display: block; padding-top: 20px; border-top: 1px solid rgba(255,255,255,.08); margin-top: 8px; }
  .mobile-login a { font-size: 15px !important; color: #5b9cf6 !important; }

  .nav-links {
    position: fixed; top: 0; right: 0; bottom: 0;
    width: 260px;
    background: rgba(13,13,13,.97);
    backdrop-filter: blur(24px);
    border-left: 1px solid rgba(255,255,255,.08);
    flex-direction: column; gap: 0; padding: 80px 32px 32px;
    transform: translateX(100%); opacity: 0;
    transition: all .35s cubic-bezier(.4,0,.2,1);
    z-index: 800;
  }
  .nav-links.open { transform: none; opacity: 1; }
  .nav-links a { font-size: 16px; padding: 14px 0; display: block; }
}
</style>
