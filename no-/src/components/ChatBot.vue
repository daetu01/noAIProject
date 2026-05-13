<script lang="ts" setup>
import { ref, nextTick } from 'vue'

interface Message {
  role: 'user' | 'bot'
  text: string
}

const open = ref(false)
const input = ref('')
const messages = ref<Message[]>([
  { role: 'bot', text: '안녕하세요! 무엇을 도와드릴까요?' }
])
const messagesEl = ref<HTMLElement | null>(null)

function toggle() {
  open.value = !open.value
}

async function send() {
  const text = input.value.trim()
  if (!text) return

  messages.value.push({ role: 'user', text })
  input.value = ''
  await scrollBottom()

  // TODO: 백엔드 API 연결
  messages.value.push({ role: 'bot', text: '...' })
  await scrollBottom()
}

async function scrollBottom() {
  await nextTick()
  if (messagesEl.value) {
    messagesEl.value.scrollTop = messagesEl.value.scrollHeight
  }
}
</script>

<template>
  <!-- 챗 패널 -->
  <Transition name="chat">
    <div v-if="open" class="chat-panel">
      <div class="chat-header">
        <div class="chat-header-left">
          <div class="bot-avatar">🤖</div>
          <div>
            <p class="bot-name">AI ASSISTANT</p>
            <p class="bot-status">● ONLINE</p>
          </div>
        </div>
        <button class="btn-close" @click="open = false">
          <v-icon size="18" color="#4A5568">mdi-close</v-icon>
        </button>
      </div>

      <div ref="messagesEl" class="chat-messages">
        <div
          v-for="(msg, i) in messages"
          :key="i"
          class="msg-wrap"
          :class="msg.role"
        >
          <div class="bubble">{{ msg.text }}</div>
        </div>
      </div>

      <div class="chat-input-wrap">
        <input
          v-model="input"
          type="text"
          placeholder="메시지 입력..."
          class="chat-input"
          @keyup.enter="send"
        />
        <button class="btn-send" :disabled="!input.trim()" @click="send">
          <v-icon size="18">mdi-send</v-icon>
        </button>
      </div>
    </div>
  </Transition>

  <!-- 토글 버튼 -->
  <button class="chat-toggle" :class="{ active: open }" @click="toggle">
    <v-icon size="24" color="#E2E8F0">{{ open ? 'mdi-close' : 'mdi-robot-outline' }}</v-icon>
    <span v-if="!open" class="toggle-label">AI</span>
  </button>
</template>

<style scoped>
/* 토글 버튼 */
.chat-toggle {
  position: fixed;
  bottom: 80px;
  right: 16px;
  z-index: 1500;
  width: 52px; height: 52px;
  border-radius: 16px;
  background: linear-gradient(135deg, #1E3A5F, #0E1F3D);
  border: 1px solid rgba(96, 165, 250, 0.4);
  box-shadow: 0 0 20px rgba(96, 165, 250, 0.2), 0 4px 16px rgba(0,0,0,0.4);
  cursor: pointer;
  display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 1px;
  transition: all 0.2s;
}
.chat-toggle:hover {
  box-shadow: 0 0 28px rgba(96, 165, 250, 0.35), 0 4px 20px rgba(0,0,0,0.5);
  border-color: rgba(96, 165, 250, 0.7);
  transform: translateY(-1px);
}
.chat-toggle.active {
  background: linear-gradient(135deg, #1a1a2e, #0A0A12);
}
.toggle-label {
  font-size: 9px; font-weight: 800; color: #60A5FA;
  letter-spacing: 1px; font-family: 'Courier New', monospace;
}

/* 챗 패널 */
.chat-panel {
  position: fixed;
  bottom: 144px;
  right: 16px;
  z-index: 1500;
  width: 320px;
  height: 460px;
  background: #080810;
  border: 1px solid rgba(96, 165, 250, 0.2);
  border-radius: 18px;
  box-shadow: 0 0 40px rgba(96, 165, 250, 0.1), 0 20px 60px rgba(0,0,0,0.6);
  display: flex; flex-direction: column;
  overflow: hidden;
}

/* 헤더 */
.chat-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(96, 165, 250, 0.1);
  background: #0E0E1A;
  flex-shrink: 0;
}
.chat-header-left { display: flex; align-items: center; gap: 10px; }
.bot-avatar {
  width: 34px; height: 34px; border-radius: 10px;
  background: rgba(96, 165, 250, 0.1);
  border: 1px solid rgba(96, 165, 250, 0.25);
  display: flex; align-items: center; justify-content: center;
  font-size: 17px;
}
.bot-name { font-size: 12px; font-weight: 800; color: #E2E8F0; letter-spacing: 1.5px; font-family: 'Courier New', monospace; }
.bot-status { font-size: 10px; color: #4ADE80; letter-spacing: 1px; font-family: 'Courier New', monospace; margin-top: 1px; }
.btn-close { background: none; border: none; cursor: pointer; padding: 4px; border-radius: 6px; }

/* 메시지 영역 */
.chat-messages {
  flex: 1; overflow-y: auto;
  padding: 16px 12px;
  display: flex; flex-direction: column; gap: 10px;
  scrollbar-width: thin;
  scrollbar-color: rgba(96,165,250,0.2) transparent;
}

.msg-wrap { display: flex; }
.msg-wrap.user { justify-content: flex-end; }
.msg-wrap.bot { justify-content: flex-start; }

.bubble {
  max-width: 75%;
  padding: 9px 13px;
  font-size: 13px; line-height: 1.5;
  border-radius: 14px;
  word-break: break-word;
}
.msg-wrap.user .bubble {
  background: linear-gradient(135deg, #2563EB, #1D4ED8);
  color: #E2E8F0;
  border-bottom-right-radius: 4px;
  box-shadow: 0 2px 12px rgba(37, 99, 235, 0.3);
}
.msg-wrap.bot .bubble {
  background: #0E0E1A;
  color: #94A3B8;
  border: 1px solid rgba(96, 165, 250, 0.12);
  border-bottom-left-radius: 4px;
}

/* 입력창 */
.chat-input-wrap {
  display: flex; gap: 8px;
  padding: 12px;
  border-top: 1px solid rgba(96, 165, 250, 0.1);
  background: #0E0E1A;
  flex-shrink: 0;
}
.chat-input {
  flex: 1; padding: 10px 13px;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(96, 165, 250, 0.2);
  border-radius: 10px;
  font-size: 13px; color: #E2E8F0; outline: none;
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif;
  transition: border-color 0.15s;
}
.chat-input::placeholder { color: #2D3748; }
.chat-input:focus { border-color: rgba(96, 165, 250, 0.5); }
.btn-send {
  width: 38px; height: 38px; border-radius: 10px;
  background: rgba(96, 165, 250, 0.15);
  border: 1px solid rgba(96, 165, 250, 0.3);
  color: #60A5FA; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.15s; flex-shrink: 0;
}
.btn-send:hover:not(:disabled) {
  background: rgba(96, 165, 250, 0.25);
  box-shadow: 0 0 12px rgba(96, 165, 250, 0.2);
}
.btn-send:disabled { opacity: 0.3; cursor: not-allowed; }

/* 애니메이션 */
.chat-enter-active, .chat-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.chat-enter-from, .chat-leave-to {
  opacity: 0;
  transform: translateY(12px) scale(0.97);
}
</style>
