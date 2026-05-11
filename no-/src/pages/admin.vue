<script lang="ts" setup>
import { ref } from 'vue'
import client from '@/api/client'

const name = ref('')
const eqLevel = ref(1)
const description = ref('')
const imageFile = ref<File | null>(null)
const submitting = ref(false)
const successMsg = ref('')
const errorMsg = ref('')

function onFileChange(e: Event) {
  imageFile.value = (e.target as HTMLInputElement).files?.[0] ?? null
}

function openFilePicker() {
  (document.querySelector('.file-hidden') as HTMLInputElement)?.click()
}

async function submit() {
  if (!imageFile.value) return
  submitting.value = true
  try {
    const form = new FormData()
    form.append('dto', new Blob([JSON.stringify({
      name: name.value, eqLevel: eqLevel.value, description: description.value,
    })], { type: 'application/json' }))
    form.append('imageFile', imageFile.value)
    await client.post('/admin/register', form, { headers: { 'Content-Type': 'multipart/form-data' } })
    successMsg.value = `${name.value} 아이템이 등록되었습니다.`
    name.value = ''; eqLevel.value = 1; description.value = ''; imageFile.value = null
  } catch { errorMsg.value = '아이템 등록에 실패했습니다.' }
  finally { submitting.value = false }
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">관리자</h1>
      <p class="page-subtitle">아이템 등록</p>
    </div>

    <div v-if="successMsg" class="success-bar">{{ successMsg }}</div>
    <div v-if="errorMsg" class="error-bar">{{ errorMsg }}</div>

    <div class="form-card">
      <div class="field-section">
        <label class="field-label">아이템 이름</label>
        <input v-model="name" type="text" placeholder="이름 입력" class="field-input" required />
      </div>
      <div class="field-divider" />
      <div class="field-section">
        <label class="field-label">장비 레벨</label>
        <input v-model="eqLevel" type="number" min="1" placeholder="1" class="field-input" required />
      </div>
      <div class="field-divider" />
      <div class="field-section">
        <label class="field-label">설명</label>
        <textarea v-model="description" placeholder="아이템 설명" class="field-textarea" rows="3" />
      </div>
    </div>

    <div class="upload-area" @click="openFilePicker">
      <v-icon size="28" :color="imageFile ? '#0071E3' : '#AEAEB2'">
        {{ imageFile ? 'mdi-image-check' : 'mdi-image-plus' }}
      </v-icon>
      <p class="upload-text">{{ imageFile ? imageFile.name : '이미지 파일 선택' }}</p>
      <input ref="fileInput" type="file" accept="image/*" class="file-hidden" @change="onFileChange" />
    </div>

    <button
      class="btn-submit"
      :disabled="!name || !imageFile || submitting"
      @click="submit"
    >
      <v-progress-circular v-if="submitting" indeterminate size="16" width="2" color="white" class="mr-2" />
      아이템 등록하기
    </button>
  </div>
</template>

<style scoped>
.page { min-height: 100vh; background: #F5F5F7; padding: 24px 16px; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 26px; font-weight: 700; color: #1D1D1F; letter-spacing: -0.4px; }
.page-subtitle { font-size: 13px; color: #6E6E73; margin-top: 2px; }

.success-bar { background: rgba(48,209,88,0.1); border: 1px solid rgba(48,209,88,0.2); border-radius: 10px; padding: 10px 14px; font-size: 13px; color: #28A745; margin-bottom: 12px; }
.error-bar { background: rgba(255,59,48,0.08); border: 1px solid rgba(255,59,48,0.15); border-radius: 10px; padding: 10px 14px; font-size: 13px; color: #FF3B30; margin-bottom: 12px; }

.form-card { background: #FFFFFF; border-radius: 14px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.06); margin-bottom: 12px; }
.field-section { padding: 12px 16px; }
.field-divider { height: 1px; background: rgba(0,0,0,0.06); margin: 0 16px; }
.field-label { display: block; font-size: 12px; font-weight: 600; color: #6E6E73; text-transform: uppercase; letter-spacing: 0.4px; margin-bottom: 4px; }
.field-input { width: 100%; background: transparent; border: none; outline: none; font-size: 15px; color: #1D1D1F; padding: 2px 0; font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif; }
.field-input::placeholder { color: #AEAEB2; }
.field-textarea { width: 100%; background: transparent; border: none; outline: none; resize: none; font-size: 15px; color: #1D1D1F; padding: 2px 0; font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif; }
.field-textarea::placeholder { color: #AEAEB2; }

.upload-area {
  background: #FFFFFF; border-radius: 14px; box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  padding: 24px; display: flex; flex-direction: column; align-items: center; gap: 8px;
  cursor: pointer; margin-bottom: 20px; transition: background 0.1s;
}
.upload-area:active { background: #F5F5F7; }
.upload-text { font-size: 14px; color: #6E6E73; }
.file-hidden { display: none; }

.btn-submit {
  width: 100%; padding: 16px; background: #0071E3; color: white; border: none;
  border-radius: 14px; font-size: 16px; font-weight: 600; cursor: pointer;
  transition: background 0.15s; display: flex; align-items: center; justify-content: center;
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif;
}
.btn-submit:hover:not(:disabled) { background: #0077ED; }
.btn-submit:disabled { opacity: 0.4; cursor: not-allowed; }
</style>
