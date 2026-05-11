<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'

const store = useAppStore()
const router = useRouter()

const email = ref('')
const password = ref('')
const errorMsg = ref('')
const loading = ref(false)

async function submit() {
  errorMsg.value = ''
  loading.value = true
  try {
    await store.login({ email: email.value, password: password.value })
    router.push('/')
  } catch {
    errorMsg.value = '이메일 또는 비밀번호가 올바르지 않습니다.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-bg">
    <div class="auth-card">
      <div class="auth-header">
        <div class="auth-logo">
          <v-icon size="32" color="white">mdi-storefront</v-icon>
        </div>
        <h1 class="auth-title">NoAI Market</h1>
        <p class="auth-subtitle">계정에 로그인하세요</p>
      </div>

      <transition name="fade">
        <div v-if="errorMsg" class="error-banner">
          {{ errorMsg }}
        </div>
      </transition>

      <form class="auth-form" @submit.prevent="submit">
        <div class="field-group">
          <div class="field-wrapper">
            <input
              v-model="email"
              type="email"
              placeholder="이메일"
              class="apple-input"
              required
            />
          </div>
          <div class="field-divider" />
          <div class="field-wrapper">
            <input
              v-model="password"
              type="password"
              placeholder="비밀번호"
              class="apple-input"
              required
            />
          </div>
        </div>

        <button type="submit" class="apple-btn-primary" :disabled="loading">
          <span v-if="!loading">로그인</span>
          <v-progress-circular v-else indeterminate size="18" width="2" color="white" />
        </button>
      </form>

      <p class="auth-footer">
        계정이 없으신가요?
        <router-link to="/signup" class="auth-link">회원가입</router-link>
      </p>
    </div>
  </div>
</template>

<style scoped>
.auth-bg {
  min-height: 100vh;
  background: #F5F5F7;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.auth-card {
  width: 100%;
  max-width: 380px;
  background: #FFFFFF;
  border-radius: 20px;
  padding: 40px 32px 32px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08), 0 1px 2px rgba(0,0,0,0.04);
}

.auth-header {
  text-align: center;
  margin-bottom: 28px;
}

.auth-logo {
  width: 56px;
  height: 56px;
  background: #0071E3;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.auth-title {
  font-size: 22px;
  font-weight: 700;
  color: #1D1D1F;
  letter-spacing: -0.4px;
  margin-bottom: 4px;
}

.auth-subtitle {
  font-size: 14px;
  color: #6E6E73;
}

.error-banner {
  background: rgba(255, 59, 48, 0.08);
  border: 1px solid rgba(255, 59, 48, 0.2);
  border-radius: 10px;
  padding: 10px 14px;
  font-size: 13px;
  color: #FF3B30;
  margin-bottom: 16px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.field-group {
  background: #F5F5F7;
  border-radius: 12px;
  overflow: hidden;
}

.field-wrapper {
  padding: 0 14px;
}

.field-divider {
  height: 1px;
  background: rgba(0,0,0,0.08);
  margin: 0 14px;
}

.apple-input {
  width: 100%;
  padding: 14px 0;
  background: transparent;
  border: none;
  outline: none;
  font-size: 15px;
  color: #1D1D1F;
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif;
}

.apple-input::placeholder {
  color: #AEAEB2;
}

.apple-btn-primary {
  width: 100%;
  padding: 14px;
  background: #0071E3;
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s ease, transform 0.1s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 50px;
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif;
}

.apple-btn-primary:hover:not(:disabled) {
  background: #0077ED;
}

.apple-btn-primary:active:not(:disabled) {
  transform: scale(0.98);
  background: #006ACD;
}

.apple-btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.auth-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 13px;
  color: #6E6E73;
}

.auth-link {
  color: #0071E3;
  text-decoration: none;
  font-weight: 500;
}

.auth-link:hover {
  text-decoration: underline;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
