<template>
  <section id="lab" class="lab-section">
    <div class="section-wrap">
      <div class="section-header reveal">
        <p class="s-label">AI Terminal</p>
        <h2 class="s-title">Chat</h2>
        <p class="s-sub">Ask me anything — projects, skills, music, or just say hi.</p>
      </div>

      <!-- macOS terminal window -->
      <div class="terminal reveal">

        <!-- Title bar -->
        <div class="term-titlebar">
          <div class="term-dots">
            <span class="dot dot-red" />
            <span class="dot dot-yellow" />
            <span class="dot dot-green" />
          </div>
          <span class="term-title-label">daean@studio — chat — 80×24</span>
          <div style="flex:1" />
        </div>

        <!-- Output area -->
        <div class="term-body" ref="bodyEl">
          <div class="term-line">
            <span class="term-sys">daean-studio AI terminal v1.0.0</span>
          </div>
          <div class="term-line">
            <span class="term-sys">Type a message and press Enter ↵</span>
          </div>
          <div class="term-line">&nbsp;</div>

          <div v-for="(item, i) in renderLines" :key="i" class="term-line">
            <template v-if="item.type === 'user'">
              <span class="term-prompt">daean@studio:~$&nbsp;</span>
              <span class="term-user">{{ item.text }}</span>
            </template>
            <template v-else>
              <span class="term-bot-pfx">{{ item.first ? '▶' : '&nbsp;' }}&nbsp;</span>
              <span class="term-bot">{{ item.text }}</span>
            </template>
          </div>

          <!-- Typing dots -->
          <div v-if="loading" class="term-line">
            <span class="term-bot-pfx">▶&nbsp;</span>
            <span class="typing-dots">
              <span /><span /><span />
            </span>
          </div>
        </div>

        <!-- Input line -->
        <div class="term-input-row" @click="inputEl?.focus()">
          <span class="term-prompt">daean@studio:~$&nbsp;</span>
          <input
            ref="inputEl"
            v-model="input"
            class="term-input"
            type="text"
            placeholder="ask something…"
            :disabled="loading"
            @keydown.enter="send"
          />
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted } from 'vue'
import client from '@/api/client'

interface Message {
  role: 'user' | 'bot'
  text: string
}

interface RenderLine {
  type: 'user' | 'bot'
  text: string
  first: boolean
}

const messages = ref<Message[]>([])

const renderLines = computed<RenderLine[]>(() => {
  const lines: RenderLine[] = []
  for (const msg of messages.value) {
    if (msg.role === 'user') {
      lines.push({ type: 'user', text: msg.text, first: true })
    } else {
      msg.text.split('\n').forEach((line, j) =>
        lines.push({ type: 'bot', text: line, first: j === 0 })
      )
    }
  }
  return lines
})

const input    = ref('')
const loading  = ref(false)
const bodyEl   = ref<HTMLElement | null>(null)
const inputEl  = ref<HTMLInputElement | null>(null)

async function scrollBottom() {
  await nextTick()
  if (bodyEl.value) bodyEl.value.scrollTop = bodyEl.value.scrollHeight
}

async function send() {
  const text = input.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', text })
  input.value = ''
  loading.value = true
  await scrollBottom()

  try {
    const res = await client.post<{ response: string }>('/api/chat', { message: text })
    messages.value.push({ role: 'bot', text: res.data.response })
  } catch (err: any) {
    const status = err?.response?.status
    const msg =
      status === 401 ? 'Authentication required. Please sign in first.' :
      status === 404 ? 'Chat endpoint not configured yet.' :
      'Connection error. Please try again.'
    messages.value.push({ role: 'bot', text: msg })
  } finally {
    loading.value = false
    await scrollBottom()
    inputEl.value?.focus()
  }
}

onMounted(() => inputEl.value?.focus())
</script>

<style scoped>
.lab-section {
  background: var(--bg);
  position: relative;
}
.section-header { margin-bottom: 48px; }

/* ─── Terminal window ──────────────────────────── */
.terminal {
  background: #0d1117;
  border: 1px solid rgba(255,255,255,.1);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 32px 80px rgba(0,0,0,.6), 0 0 0 1px rgba(255,255,255,.04);
}

/* Title bar */
.term-titlebar {
  height: 40px;
  background: #161b22;
  border-bottom: 1px solid rgba(255,255,255,.07);
  display: flex; align-items: center;
  padding: 0 16px; gap: 14px;
  user-select: none;
}
.term-dots { display: flex; gap: 7px; }
.dot {
  width: 12px; height: 12px; border-radius: 50%;
}
.dot-red    { background: #ff5f57; }
.dot-yellow { background: #ffbd2e; }
.dot-green  { background: #28c840; }
.term-title-label {
  font-family: 'SF Mono','Fira Code',monospace;
  font-size: 12px; color: rgba(255,255,255,.3);
  letter-spacing: .04em;
}

/* Output body */
.term-body {
  padding: 20px 24px 12px;
  min-height: 340px;
  max-height: 440px;
  overflow-y: auto;
  font-family: 'SF Mono','Fira Code','Menlo',monospace;
  font-size: 13px;
  line-height: 1.8;
  display: flex; flex-direction: column; gap: 1px;
  scrollbar-width: thin;
  scrollbar-color: rgba(255,255,255,.1) transparent;
}
.term-body::-webkit-scrollbar { width: 4px; }
.term-body::-webkit-scrollbar-thumb { background: rgba(255,255,255,.1); border-radius: 2px; }

/* Lines */
.term-line { display: flex; align-items: baseline; word-break: break-word; }
.term-sys  { color: rgba(255,255,255,.2); font-style: italic; }
.term-prompt { color: #28c840; white-space: nowrap; flex-shrink: 0; }
.term-user  { color: #f0f6fc; }
.term-bot-pfx { color: #a78bfa; white-space: nowrap; flex-shrink: 0; }
.term-bot   { color: #8b949e; }

/* Typing animation */
.typing-dots { display: flex; align-items: center; gap: 4px; padding: 2px 0; }
.typing-dots span {
  width: 5px; height: 5px; border-radius: 50%;
  background: #a78bfa; opacity: .4;
  animation: typingPulse 1.2s ease-in-out infinite;
}
.typing-dots span:nth-child(2) { animation-delay: .2s; }
.typing-dots span:nth-child(3) { animation-delay: .4s; }
@keyframes typingPulse {
  0%, 100% { opacity: .2; transform: translateY(0); }
  50%       { opacity: .9; transform: translateY(-3px); }
}

/* Input line */
.term-input-row {
  display: flex; align-items: center;
  padding: 10px 24px 18px;
  border-top: 1px solid rgba(255,255,255,.05);
  cursor: text;
}
.term-input {
  flex: 1;
  background: transparent;
  border: none; outline: none;
  font-family: 'SF Mono','Fira Code','Menlo',monospace;
  font-size: 13px; color: #f0f6fc;
  caret-color: #28c840;
}
.term-input::placeholder { color: rgba(255,255,255,.15); }
.term-input:disabled { opacity: .5; }

@media (max-width: 600px) {
  .term-body { min-height: 260px; max-height: 340px; font-size: 12px; padding: 16px 16px 10px; }
  .term-input-row { padding: 8px 16px 14px; }
  .term-input { font-size: 12px; }
}
</style>
