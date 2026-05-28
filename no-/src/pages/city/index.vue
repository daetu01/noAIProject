<script lang="ts" setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { RouterLink } from 'vue-router'
import { cityService, type TrafficRiskAll, type TrafficDetail, type WeatherRow, type ForecastItem } from '@/api/cityService'
import TrafficMap from '@/components/TrafficMap.vue'
import { useTrafficAlerts } from '@/composables/useTrafficAlerts'

const {
  alerts, unread, toasts, connected,
  connect, markRead, dismissToast,
  statusLabel: alertStatusLabel,
  statusColor: alertStatusColor,
} = useTrafficAlerts()

const alertPanelOpen = ref(false)

function toggleAlertPanel() {
  alertPanelOpen.value = !alertPanelOpen.value
  if (alertPanelOpen.value) markRead()
}

const activeTab = ref<'traffic' | 'air' | 'weather' | 'population' | 'cctv'>('traffic')
const now = ref('')

let timer: ReturnType<typeof setInterval>

function updateClock() {
  const d = new Date()
  now.value = d.toLocaleString('ko-KR', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false
  })
}

const trafficRisks = ref<TrafficRiskAll[]>([])
const trafficLoading = ref(false)
const trafficError = ref('')

const statusLabel = (s: string) => ({ NORMAL: '원활', WARNING: '서행', DANGER: '혼잡' }[s] ?? s)
const statusColor = (s: string) => ({ NORMAL: '#34d399', WARNING: '#fb923c', DANGER: '#ff4757' }[s] ?? '#888')

function setTab(key: string) {
  activeTab.value = key as typeof activeTab.value
  if (key === 'weather' && forecastList.value.length === 0) loadWeather()
}

function defaultHour(): number {
  const h = new Date().getHours()
  return h === 0 ? 23 : h - 1
}

const selectedHour = ref(defaultHour())
const isCurrentHour = computed(() => selectedHour.value === defaultHour())

async function loadTraffic(hour?: number) {
  trafficLoading.value = true
  trafficError.value = ''
  try { trafficRisks.value = (await cityService.getAllRisks(undefined, hour ?? selectedHour.value)) ?? [] }
  catch { trafficError.value = '교통 데이터를 불러오지 못했습니다.' }
  finally { trafficLoading.value = false }
}

async function selectHour(h: number) {
  selectedHour.value = h
  await loadTraffic(h)
}

async function goToNow() {
  selectedHour.value = defaultHour()
  await loadTraffic()
}

const detail = ref<TrafficDetail | null>(null)
const detailLoading = ref(false)
const detailOpen = ref(false)

async function openDetail(spotNum: string) {
  detailOpen.value = true
  detailLoading.value = true
  detail.value = null
  try { detail.value = await cityService.getDetail(spotNum, undefined, selectedHour.value) }
  catch { detail.value = null }
  finally { detailLoading.value = false }
}

function closeDetail() {
  detailOpen.value = false
  detail.value = null
}

const maxVolume = computed(() =>
  detail.value ? Math.max(...detail.value.todayVolumes.map(v => v.totalVolume), 1) : 1
)

const dangerCount = computed(() => trafficRisks.value.filter(r => r.status === 'DANGER').length)
const warningCount = computed(() => trafficRisks.value.filter(r => r.status === 'WARNING').length)

const stats = computed(() => [
  { label: '위험 지점', value: String(dangerCount.value), unit: '개', color: '#ff4757', icon: 'mdi-alert-circle' },
  { label: '서행 지점', value: String(warningCount.value), unit: '개', color: '#fb923c', icon: 'mdi-car-clock' },
  { label: '모니터링', value: String(trafficRisks.value.length), unit: '개', color: '#34d399', icon: 'mdi-access-point' },
  { label: '대기질 지수', value: '82', unit: 'AQI', color: '#5b9cf6', icon: 'mdi-weather-windy' },
  { label: 'CCTV 운영', value: '2,458', unit: '대', color: '#a78bfa', icon: 'mdi-cctv' },
])

const airStations = [
  { name: '강남구', pm25: 18, pm10: 34, aqi: 72, grade: '보통', color: '#fb923c' },
  { name: '종로구', pm25: 12, pm10: 24, aqi: 51, grade: '좋음', color: '#34d399' },
  { name: '마포구', pm25: 22, pm10: 41, aqi: 88, grade: '보통', color: '#fb923c' },
  { name: '영등포구', pm25: 35, pm10: 67, aqi: 134, grade: '나쁨', color: '#ff4757' },
  { name: '송파구', pm25: 15, pm10: 29, aqi: 61, grade: '보통', color: '#fb923c' },
]

const airPollutants = [
  { name: 'PM2.5', value: 18, unit: 'μg/m³', max: 75, color: '#fb923c' },
  { name: 'PM10', value: 34, unit: 'μg/m³', max: 150, color: '#5b9cf6' },
  { name: 'NO₂', value: 0.032, unit: 'ppm', max: 0.1, color: '#a78bfa' },
  { name: 'CO', value: 0.8, unit: 'ppm', max: 9, color: '#34d399' },
  { name: 'O₃', value: 0.052, unit: 'ppm', max: 0.1, color: '#f472b6' },
  { name: 'SO₂', value: 0.004, unit: 'ppm', max: 0.05, color: '#fbbf24' },
]

const dailyWeather = ref<WeatherRow | null>(null)
const forecastList = ref<ForecastItem[]>([])
const weatherLoading = ref(false)
const weatherError = ref('')

function fmtForecastTime(tmEf: string): string {
  if (!tmEf || tmEf.length < 10) return tmEf
  return `${parseInt(tmEf.substring(8, 10))}시`
}

function fmtYmd(ymd: string): string {
  if (!ymd || ymd.length !== 8) return ymd
  return `${ymd.slice(0, 4)}-${ymd.slice(4, 6)}-${ymd.slice(6, 8)}`
}

function skyIcon(skyCode: string | null, precipCode: number | null): string {
  if (precipCode && precipCode > 0) {
    if (precipCode === 3) return 'mdi-weather-snowy'
    if (precipCode === 2) return 'mdi-weather-snowy-rainy'
    return 'mdi-weather-rainy'
  }
  const code = Number(skyCode)
  if (code === 1) return 'mdi-weather-sunny'
  if (code === 3) return 'mdi-weather-partly-cloudy'
  return 'mdi-weather-cloudy'
}

async function loadWeather() {
  weatherLoading.value = true
  weatherError.value = ''
  try {
    const [daily, forecast] = await Promise.allSettled([
      cityService.getDailyWeather(),
      cityService.getForecastWeather(),
    ])
    if (daily.status === 'fulfilled') dailyWeather.value = daily.value
    if (forecast.status === 'fulfilled') forecastList.value = forecast.value
    if (daily.status === 'rejected' && forecast.status === 'rejected') {
      weatherError.value = '날씨 데이터를 불러오지 못했습니다.'
    }
  } finally {
    weatherLoading.value = false
  }
}

const populationZones = [
  { name: '강남역', density: 9821, level: '매우 혼잡', pct: 95, color: '#ff4757' },
  { name: '홍대입구', density: 7432, level: '혼잡', pct: 74, color: '#fb923c' },
  { name: '명동', density: 6891, level: '혼잡', pct: 69, color: '#fb923c' },
  { name: '여의도', density: 4230, level: '보통', pct: 42, color: '#5b9cf6' },
  { name: '잠실', density: 8103, level: '혼잡', pct: 81, color: '#fb923c' },
  { name: '신촌', density: 3120, level: '여유', pct: 31, color: '#34d399' },
]

const cctvList = [
  { id: 'CAM-001', location: '강남대로 · 강남역', status: '정상', type: '교통', fps: 30 },
  { id: 'CAM-002', location: '올림픽대로 · 잠실대교', status: '정상', type: '교통', fps: 30 },
  { id: 'CAM-003', location: '종로 · 광화문', status: '장애', type: '방범', fps: 0 },
  { id: 'CAM-004', location: '서울역 광장', status: '정상', type: '방범', fps: 25 },
  { id: 'CAM-005', location: '마포대교 북단', status: '정상', type: '교통', fps: 30 },
  { id: 'CAM-006', location: '한강공원 · 여의도', status: '점검중', type: '환경', fps: 15 },
  { id: 'CAM-007', location: '강변북로 · 성수', status: '정상', type: '교통', fps: 30 },
  { id: 'CAM-008', location: '이태원로 · 한남동', status: '정상', type: '방범', fps: 30 },
]

onMounted(() => { updateClock(); timer = setInterval(updateClock, 1000); loadTraffic(); connect() })
onUnmounted(() => clearInterval(timer))
</script>

<template>
  <div class="dashboard">

    <!-- 헤더 -->
    <header class="dash-header">
      <div class="dash-header-left">
        <!-- 스튜디오 홈 버튼 -->
        <RouterLink to="/" class="back-studio">
          <v-icon size="13">mdi-arrow-left</v-icon>
          <span class="back-studio-text">DS</span>
        </RouterLink>

        <div class="header-divider" />

        <div class="dash-logo">
          <v-icon size="16" color="#5b9cf6">mdi-city-variant-outline</v-icon>
        </div>
        <div>
          <p class="dash-eyebrow">SMART CITY</p>
          <h1 class="dash-title">도시 통합 모니터링</h1>
        </div>
      </div>

      <div class="dash-header-right">
        <div class="status-dot-row">
          <span class="status-dot" :class="connected ? 'green' : 'red'" />
          <span class="live-label" :style="{ color: connected ? '#34d399' : '#ff4757' }">
            {{ connected ? 'LIVE' : 'OFF' }}
          </span>
        </div>

        <!-- 알림 벨 -->
        <button class="alert-bell" @click="toggleAlertPanel">
          <v-icon size="16" :color="unread > 0 ? '#fb923c' : '#6e6e73'">
            {{ unread > 0 ? 'mdi-bell-ring' : 'mdi-bell-outline' }}
          </v-icon>
          <span v-if="unread > 0" class="bell-badge">{{ unread > 99 ? '99+' : unread }}</span>
        </button>

        <p class="dash-clock">{{ now }}</p>
      </div>
    </header>

    <!-- 알림 패널 -->
    <transition name="fade-down">
      <div v-if="alertPanelOpen" class="alert-panel-overlay" @click.self="alertPanelOpen = false">
        <div class="alert-panel">
          <div class="alert-panel-header">
            <p class="alert-panel-title">
              <v-icon size="13" color="#fb923c" class="mr-1">mdi-bell-ring</v-icon>
              실시간 교통 알람
            </p>
            <button class="alert-panel-close" @click="alertPanelOpen = false">
              <v-icon size="15" color="#6e6e73">mdi-close</v-icon>
            </button>
          </div>
          <div v-if="alerts.length === 0" class="alert-empty">수신된 알람 없음</div>
          <div v-else class="alert-list">
            <div v-for="(a, i) in alerts" :key="i" class="alert-item">
              <div class="alert-item-left">
                <span class="alert-dot" :style="{ background: alertStatusColor(a.status) }" />
              </div>
              <div class="alert-item-body">
                <div class="alert-item-top">
                  <span class="alert-spot">{{ a.spotNum }}</span>
                  <span class="alert-status-badge" :style="{ color: alertStatusColor(a.status), background: alertStatusColor(a.status) + '22' }">
                    {{ alertStatusLabel(a.status) }}
                  </span>
                  <span class="alert-time">{{ a.receivedAt.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' }) }}</span>
                </div>
                <p class="alert-msg">{{ a.message }}</p>
                <p class="alert-score">위험도 {{ a.riskScore.toFixed(1) }}점 · {{ a.hour }}시</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- 토스트 알람 -->
    <div class="toast-stack">
      <transition-group name="toast">
        <div
          v-for="(t, i) in toasts" :key="i"
          class="toast-item"
          :style="{ borderColor: alertStatusColor(t.status) + '66' }"
        >
          <div class="toast-bar" :style="{ background: alertStatusColor(t.status) }" />
          <div class="toast-body">
            <div class="toast-top">
              <span class="toast-icon">
                <v-icon size="13" :color="alertStatusColor(t.status)">mdi-alert-circle</v-icon>
              </span>
              <span class="toast-spot">{{ t.spotNum }}</span>
              <span class="toast-badge" :style="{ color: alertStatusColor(t.status), background: alertStatusColor(t.status) + '22' }">
                {{ alertStatusLabel(t.status) }}
              </span>
              <button class="toast-close" @click="dismissToast(t)">
                <v-icon size="11" color="#6e6e73">mdi-close</v-icon>
              </button>
            </div>
            <p class="toast-msg">{{ t.message.split('\n')[0] }}</p>
            <p class="toast-score">위험도 {{ t.riskScore.toFixed(1) }}점 · {{ t.hour }}시</p>
          </div>
        </div>
      </transition-group>
    </div>

    <!-- 상단 통계 카드 -->
    <div class="stat-row">
      <div v-for="s in stats" :key="s.label" class="stat-card">
        <div class="stat-icon-wrap" :style="{ background: s.color + '18', border: '1px solid ' + s.color + '30' }">
          <v-icon size="16" :color="s.color">{{ s.icon }}</v-icon>
        </div>
        <div>
          <p class="stat-label">{{ s.label }}</p>
          <p class="stat-value" :style="{ color: s.color }">{{ s.value }}<span class="stat-unit">{{ s.unit }}</span></p>
        </div>
      </div>
    </div>

    <!-- 탭 -->
    <div class="tab-bar">
      <button v-for="tab in [
        { key: 'traffic',    label: '교통',    icon: 'mdi-car' },
        { key: 'air',        label: '대기질',  icon: 'mdi-weather-windy' },
        { key: 'weather',    label: '날씨',    icon: 'mdi-weather-partly-cloudy' },
        { key: 'population', label: '인구밀도', icon: 'mdi-account-group' },
        { key: 'cctv',       label: 'CCTV',   icon: 'mdi-cctv' },
      ]" :key="tab.key"
        class="tab-btn"
        :class="{ active: activeTab === tab.key }"
        @click="setTab(tab.key)"
      >
        <v-icon size="13" class="mr-1">{{ tab.icon }}</v-icon>
        {{ tab.label }}
      </button>
    </div>

    <!-- ── 교통 ── -->
    <div v-if="activeTab === 'traffic'" class="panel-grid">
      <div class="panel col-3">
        <div class="panel-title-row">
          <p class="panel-title">
            <v-icon size="13" color="#5b9cf6" class="mr-1">mdi-map-marker-radius</v-icon>
            교통 지점 위험도 현황
          </p>
          <div style="display:flex;align-items:center;gap:8px">
            <button v-if="!isCurrentHour" class="action-btn" @click="goToNow">
              <v-icon size="13">mdi-clock-outline</v-icon>
              현재 시각
            </button>
            <button class="action-btn" :disabled="trafficLoading" @click="() => loadTraffic()">
              <v-icon size="13" :class="{ spinning: trafficLoading }">mdi-refresh</v-icon>
              새로고침
            </button>
          </div>
        </div>

        <div class="hour-selector">
          <span class="hour-label">
            <v-icon size="12" color="#5b9cf6">mdi-clock</v-icon>
            {{ selectedHour }}시 데이터
            <span v-if="isCurrentHour" class="hour-now-badge">NOW</span>
          </span>
          <div class="hour-pills">
            <button
              v-for="h in defaultHour() + 1" :key="h - 1"
              class="hour-pill"
              :class="{ active: selectedHour === h - 1 }"
              @click="selectHour(h - 1)"
            >{{ h - 1 }}</button>
          </div>
        </div>

        <div v-if="trafficError" class="data-error">{{ trafficError }}</div>

        <div v-if="trafficLoading" class="data-loading">
          <v-progress-circular indeterminate color="#5b9cf6" size="22" width="2" />
          <span>데이터 수집 중...</span>
        </div>

        <div v-else-if="trafficRisks.length === 0 && !trafficError" class="data-empty">
          — 데이터 없음 —
        </div>

        <TrafficMap v-else :risks="trafficRisks" @spot-click="openDetail" />
      </div>

      <div v-if="trafficRisks.length" class="panel col-3">
        <p class="panel-title">
          <v-icon size="13" color="#5b9cf6" class="mr-1">mdi-format-list-bulleted</v-icon>
          지점별 위험도
        </p>
        <div class="zone-grid">
          <div
            v-for="r in trafficRisks" :key="r.spotNum"
            class="zone-card clickable"
            :style="{ borderColor: statusColor(r.status) + '40' }"
            @click="openDetail(r.spotNum)"
          >
            <div class="zone-top">
              <span class="zone-name">{{ r.spotName }}</span>
              <span class="zone-badge" :style="{ background: statusColor(r.status) + '1a', color: statusColor(r.status) }">
                {{ statusLabel(r.status) }}
              </span>
            </div>
            <p class="zone-score" :style="{ color: statusColor(r.status) }">
              {{ r.riskScore.toFixed(0) }}<span class="zone-unit">점</span>
            </p>
            <div class="zone-bar-bg">
              <div class="zone-bar-fill" :style="{ width: Math.min(r.riskScore / 2, 100) + '%', background: statusColor(r.status) }" />
            </div>
            <p class="zone-spotnum">{{ r.spotNum }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- ── detail 슬라이드 패널 ── -->
    <transition name="slide-panel">
      <div v-if="detailOpen" class="detail-overlay" @click.self="closeDetail">
        <div class="detail-panel">
          <div class="detail-header">
            <div>
              <p class="detail-eyebrow">SPOT DETAIL</p>
              <p class="detail-name">{{ detail?.spotName ?? '로딩 중...' }}</p>
              <p class="detail-spotnum">{{ detail?.spotNum }}</p>
            </div>
            <button class="detail-close" @click="closeDetail">
              <v-icon size="18" color="#6e6e73">mdi-close</v-icon>
            </button>
          </div>

          <div v-if="detailLoading" class="detail-loading">
            <v-progress-circular indeterminate color="#5b9cf6" size="26" width="2" />
            <span>데이터 불러오는 중...</span>
          </div>

          <template v-else-if="detail">
            <div class="detail-risk-row">
              <div class="detail-risk-box" :style="{ borderColor: statusColor(detail.status) + '44', background: statusColor(detail.status) + '0d' }">
                <p class="detail-risk-label">위험 점수</p>
                <p class="detail-risk-score" :style="{ color: statusColor(detail.status) }">
                  {{ detail.riskScore.toFixed(1) }}<span class="detail-risk-unit">점</span>
                </p>
              </div>
              <div class="detail-risk-box" :style="{ borderColor: statusColor(detail.status) + '44', background: statusColor(detail.status) + '0d' }">
                <p class="detail-risk-label">현재 상태</p>
                <p class="detail-status-text" :style="{ color: statusColor(detail.status) }">
                  {{ statusLabel(detail.status) }}
                </p>
              </div>
            </div>

            <div v-if="detail.message" class="event-message-box" :style="{ borderColor: statusColor(detail.status) + '55' }">
              <div class="event-message-header">
                <v-icon size="12" :color="statusColor(detail.status)">mdi-alert-circle-outline</v-icon>
                <span :style="{ color: statusColor(detail.status) }">위험 분석 리포트</span>
              </div>
              <p class="event-message-body">{{ detail.message }}</p>
            </div>

            <div v-if="detail.aiScore != null" class="ai-section">
              <p class="detail-section-title">
                <v-icon size="12" color="#a78bfa" class="mr-1">mdi-brain</v-icon>
                AI 이상 탐지 (Isolation Forest)
              </p>
              <div class="ai-result-row">
                <div class="ai-result-box" :class="detail.aiAnomaly ? 'ai-anomaly' : 'ai-normal'">
                  <v-icon size="18">{{ detail.aiAnomaly ? 'mdi-alert-decagram' : 'mdi-check-circle' }}</v-icon>
                  <p class="ai-result-label">{{ detail.aiAnomaly ? '이상 패턴 감지' : '정상 패턴' }}</p>
                  <p class="ai-result-sub">{{ detail.aiStatus ?? '-' }}</p>
                </div>
                <div class="ai-score-box">
                  <p class="detail-risk-label">AI Score</p>
                  <p class="ai-score-val" :style="{ color: detail.aiAnomaly ? '#ff4757' : '#34d399' }">
                    {{ detail.aiScore.toFixed(4) }}
                  </p>
                  <p class="ai-score-desc">{{ detail.aiAnomaly ? '음수일수록 이상도 높음' : '양수 = 정상 범위' }}</p>
                  <div class="ai-score-bar-bg">
                    <div class="ai-score-bar-fill" :style="{
                      width: Math.min(Math.abs(detail.aiScore) * 200, 100) + '%',
                      background: detail.aiAnomaly ? '#ff4757' : '#34d399'
                    }" />
                  </div>
                </div>
              </div>
            </div>

            <div class="detail-chart-wrap">
              <p class="detail-section-title">
                <v-icon size="12" color="#5b9cf6" class="mr-1">mdi-chart-bar</v-icon>
                당일 시간별 교통량
              </p>
              <div class="detail-chart">
                <div v-for="v in detail.todayVolumes" :key="v.hour" class="chart-col">
                  <div class="chart-bar-wrap">
                    <div
                      class="chart-bar"
                      :style="{
                        height: Math.round(v.totalVolume / maxVolume * 100) + '%',
                        background: v.totalVolume / maxVolume > 0.75 ? '#ff4757'
                          : v.totalVolume / maxVolume > 0.4 ? '#fb923c' : '#5b9cf6'
                      }"
                    />
                  </div>
                  <p class="chart-hour">{{ v.hour }}시</p>
                  <p class="chart-vol">{{ v.totalVolume.toLocaleString() }}</p>
                </div>
              </div>
            </div>
          </template>

          <div v-else class="detail-loading">
            <span style="color:#ff4757">데이터를 불러오지 못했습니다.</span>
          </div>
        </div>
      </div>
    </transition>

    <!-- ── 대기질 ── -->
    <div v-if="activeTab === 'air'" class="panel-grid">
      <div class="panel col-1">
        <p class="panel-title">
          <v-icon size="13" color="#5b9cf6" class="mr-1">mdi-gauge</v-icon>
          주요 오염물질
        </p>
        <div class="pollutant-list">
          <div v-for="p in airPollutants" :key="p.name" class="pollutant-row">
            <div class="poll-name-wrap">
              <span class="poll-dot" :style="{ background: p.color }" />
              <span class="poll-name">{{ p.name }}</span>
            </div>
            <div class="poll-bar-bg">
              <div class="poll-bar-fill" :style="{ width: Math.min(p.value / p.max * 100, 100) + '%', background: p.color }" />
            </div>
            <span class="poll-value" :style="{ color: p.color }">{{ p.value }}<span class="poll-unit"> {{ p.unit }}</span></span>
          </div>
        </div>
      </div>

      <div class="panel col-2">
        <p class="panel-title">
          <v-icon size="13" color="#5b9cf6" class="mr-1">mdi-map-marker</v-icon>
          측정소별 현황
        </p>
        <div class="table-wrap">
          <table class="data-table">
            <thead>
              <tr><th>측정소</th><th>PM2.5</th><th>PM10</th><th>AQI</th><th>등급</th></tr>
            </thead>
            <tbody>
              <tr v-for="s in airStations" :key="s.name">
                <td>{{ s.name }}</td>
                <td>{{ s.pm25 }} μg/m³</td>
                <td>{{ s.pm10 }} μg/m³</td>
                <td>{{ s.aqi }}</td>
                <td><span class="grade-badge" :style="{ background: s.color + '22', color: s.color }">{{ s.grade }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- ── 날씨 ── -->
    <div v-if="activeTab === 'weather'" class="panel-grid">
      <div class="panel col-1">
        <div class="panel-title-row">
          <p class="panel-title">
            <v-icon size="13" color="#5b9cf6" class="mr-1">mdi-weather-sunny</v-icon>
            단기 예보
          </p>
          <button class="action-btn" :disabled="weatherLoading" @click="loadWeather">
            <v-icon size="13" :class="{ spinning: weatherLoading }">mdi-refresh</v-icon>
            새로고침
          </button>
        </div>

        <div v-if="weatherLoading" class="data-loading">
          <v-progress-circular indeterminate color="#5b9cf6" size="22" width="2" />
          <span>날씨 데이터 수집 중...</span>
        </div>
        <div v-else-if="weatherError" class="data-error">{{ weatherError }}</div>
        <div v-else-if="forecastList.length === 0" class="data-empty">— 예보 없음 —</div>
        <div v-else class="forecast-list">
          <div
            v-for="(w, i) in forecastList.slice(0, 5)" :key="i"
            class="forecast-item" :class="{ current: i === 0 }"
          >
            <p class="forecast-time">{{ fmtForecastTime(w.tmEf) }}</p>
            <v-icon size="26" color="#5b9cf6">{{ skyIcon(w.skyCode, w.precipitationCode) }}</v-icon>
            <p class="forecast-temp">{{ w.temperature != null ? w.temperature + '°' : '-' }}</p>
            <p class="forecast-desc">{{ w.weatherText ?? '-' }}</p>
            <p v-if="w.rainProbability != null" class="forecast-rain">{{ w.rainProbability }}%</p>
          </div>
        </div>
      </div>

      <div class="panel col-2">
        <p class="panel-title">
          <v-icon size="13" color="#5b9cf6" class="mr-1">mdi-thermometer</v-icon>
          서울 기상 현황
          <span v-if="dailyWeather" class="panel-date">{{ fmtYmd(dailyWeather.ymd) }}</span>
        </p>
        <div v-if="dailyWeather" class="table-wrap">
          <table class="data-table">
            <thead>
              <tr><th>구분</th><th>평균기온</th><th>일최고</th><th>일최저</th><th>낮최고</th><th>오전최저</th><th>야간최저</th></tr>
            </thead>
            <tbody>
              <tr>
                <td>서울 ({{ dailyWeather.stnId }})</td>
                <td>{{ dailyWeather.avgTemp != null ? dailyWeather.avgTemp + '°C' : '-' }}</td>
                <td>{{ dailyWeather.maxTemp != null ? dailyWeather.maxTemp + '°C' : '-' }}</td>
                <td>{{ dailyWeather.minTemp != null ? dailyWeather.minTemp + '°C' : '-' }}</td>
                <td>{{ dailyWeather.daytimeMaxTemp != null ? dailyWeather.daytimeMaxTemp + '°C' : '-' }}</td>
                <td>{{ dailyWeather.morningMinTemp != null ? dailyWeather.morningMinTemp + '°C' : '-' }}</td>
                <td>{{ dailyWeather.nightMinTemp != null ? dailyWeather.nightMinTemp + '°C' : '-' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else-if="!weatherLoading" class="data-empty">— 데이터 없음 —</div>
      </div>
    </div>

    <!-- ── 인구밀도 ── -->
    <div v-if="activeTab === 'population'" class="panel-grid">
      <div class="panel col-3">
        <p class="panel-title">
          <v-icon size="13" color="#5b9cf6" class="mr-1">mdi-account-group</v-icon>
          주요 지역 실시간 밀집도
        </p>
        <div class="pop-grid">
          <div v-for="z in populationZones" :key="z.name" class="pop-card">
            <div class="pop-top">
              <span class="pop-name">{{ z.name }}</span>
              <span class="pop-level" :style="{ color: z.color }">{{ z.level }}</span>
            </div>
            <p class="pop-count" :style="{ color: z.color }">{{ z.density.toLocaleString() }}<span class="pop-unit">명</span></p>
            <div class="pop-bar-bg">
              <div class="pop-bar-fill" :style="{ width: z.pct + '%', background: z.color }" />
            </div>
            <p class="pop-pct">{{ z.pct }}%</p>
          </div>
        </div>
      </div>
    </div>

    <!-- ── CCTV ── -->
    <div v-if="activeTab === 'cctv'" class="panel-grid">
      <div class="panel col-3">
        <div class="panel-title-row">
          <p class="panel-title">
            <v-icon size="13" color="#5b9cf6" class="mr-1">mdi-cctv</v-icon>
            CCTV 운영 현황
          </p>
          <div class="cctv-summary">
            <span class="cctv-sum-item" style="color:#34d399"><span class="status-dot green small" />정상 {{ cctvList.filter(c=>c.status==='정상').length }}</span>
            <span class="cctv-sum-item" style="color:#fb923c"><span class="status-dot yellow small" />점검 {{ cctvList.filter(c=>c.status==='점검중').length }}</span>
            <span class="cctv-sum-item" style="color:#ff4757"><span class="status-dot red small" />장애 {{ cctvList.filter(c=>c.status==='장애').length }}</span>
          </div>
        </div>
        <div class="table-wrap">
          <table class="data-table">
            <thead>
              <tr><th>카메라 ID</th><th>위치</th><th>유형</th><th>FPS</th><th>상태</th></tr>
            </thead>
            <tbody>
              <tr v-for="cam in cctvList" :key="cam.id">
                <td class="mono">{{ cam.id }}</td>
                <td>{{ cam.location }}</td>
                <td>{{ cam.type }}</td>
                <td class="mono">{{ cam.fps > 0 ? cam.fps : '-' }}</td>
                <td>
                  <span class="status-chip"
                    :class="{
                      'chip-green': cam.status === '정상',
                      'chip-yellow': cam.status === '점검중',
                      'chip-red': cam.status === '장애'
                    }"
                  >{{ cam.status }}</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>
/* ─── Design tokens ───────────────────────────────────── */
:root {
  --city-bg:      #0b0b0f;
  --city-bg2:     #0f0f18;
  --city-glass:   rgba(255,255,255,0.03);
  --city-border:  rgba(255,255,255,0.07);
  --city-blue:    #5b9cf6;
  --city-purple:  #a78bfa;
  --city-text:    #f0f0ff;
  --city-text2:   #8080a0;
}

/* ─── Layout ────────────────────────────────────────────── */
.dashboard {
  min-height: 100vh;
  background: #0b0b0f;
  background-image:
    radial-gradient(ellipse at 8% 15%, rgba(91,156,246,0.07) 0%, transparent 45%),
    radial-gradient(ellipse at 92% 85%, rgba(167,139,250,0.07) 0%, transparent 45%);
  padding: 0 0 80px;
  color: #d0d0e8;
  font-family: 'Inter', -apple-system, sans-serif;
  -webkit-font-smoothing: antialiased;
}

/* ─── Header ─────────────────────────────────────────────── */
.dash-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 24px;
  background: rgba(11,11,15,0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255,255,255,0.06);
  position: sticky; top: 0; z-index: 100;
}
.dash-header-left { display: flex; align-items: center; gap: 14px; }
.dash-header-right { display: flex; align-items: center; gap: 16px; }

/* Back to Studio button */
.back-studio {
  display: inline-flex; align-items: center; gap: 5px;
  padding: 6px 12px;
  background: rgba(91,156,246,0.08);
  border: 1px solid rgba(91,156,246,0.2);
  border-radius: 8px;
  font-size: 11px; font-weight: 700;
  color: #5b9cf6;
  letter-spacing: .06em;
  text-decoration: none;
  transition: all .2s ease;
  white-space: nowrap;
}
.back-studio:hover {
  background: rgba(91,156,246,0.16);
  border-color: rgba(91,156,246,0.4);
  transform: translateX(-2px);
}
.back-studio-text {
  font-family: 'Space Grotesk', sans-serif;
  background: linear-gradient(135deg, #5b9cf6 0%, #a78bfa 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-divider {
  width: 1px; height: 28px;
  background: rgba(255,255,255,0.07);
}

.dash-logo {
  width: 32px; height: 32px; border-radius: 8px;
  background: rgba(91,156,246,0.1);
  border: 1px solid rgba(91,156,246,0.2);
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.dash-eyebrow {
  font-size: 8px; font-weight: 700; color: #5b9cf6;
  letter-spacing: .25em; text-transform: uppercase; margin-bottom: 2px;
}
.dash-title {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 15px; font-weight: 700; color: #f0f0ff; letter-spacing: -.01em;
}

.status-dot-row { display: flex; align-items: center; gap: 6px; }
.status-dot { width: 7px; height: 7px; border-radius: 50%; flex-shrink: 0; }
.status-dot.green { background: #34d399; box-shadow: 0 0 5px #34d399; animation: pulse-dot 2s infinite; }
.status-dot.yellow { background: #fb923c; box-shadow: 0 0 5px #fb923c; }
.status-dot.red { background: #ff4757; box-shadow: 0 0 5px #ff4757; }
.status-dot.small { width: 5px; height: 5px; }
@keyframes pulse-dot { 0%,100% { opacity:1; } 50% { opacity:.3; } }

.live-label { font-size: 10px; font-weight: 800; letter-spacing: .1em; }
.dash-clock {
  font-size: 11px; color: #4a4a6a;
  font-family: 'SF Mono', 'Fira Code', monospace; letter-spacing: .03em;
}

/* ─── Alert bell ─────────────────────────────────────────── */
.alert-bell {
  position: relative;
  background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.07);
  border-radius: 8px; width: 32px; height: 32px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all .2s ease;
}
.alert-bell:hover { background: rgba(251,146,60,0.1); border-color: rgba(251,146,60,0.3); }
.bell-badge {
  position: absolute; top: -5px; right: -5px;
  background: #ff4757; color: #fff;
  font-size: 8px; font-weight: 800; min-width: 15px; height: 15px;
  border-radius: 8px; display: flex; align-items: center; justify-content: center;
  padding: 0 3px; border: 1.5px solid #0b0b0f;
}

/* ─── Alert panel ────────────────────────────────────────── */
.alert-panel-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  z-index: 900; display: flex; justify-content: flex-end; align-items: flex-start;
}
.alert-panel {
  width: 340px; max-height: 70vh; margin-top: 6px; margin-right: 14px;
  background: #12121a; border: 1px solid rgba(91,156,246,0.15);
  border-radius: 14px; overflow: hidden; display: flex; flex-direction: column;
  box-shadow: 0 20px 60px rgba(0,0,0,0.6);
}
.alert-panel-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 16px; border-bottom: 1px solid rgba(255,255,255,0.05); flex-shrink: 0;
}
.alert-panel-title {
  font-size: 10px; font-weight: 700; color: #5b9cf6;
  letter-spacing: .12em; display: flex; align-items: center;
}
.alert-panel-close { background: none; border: none; cursor: pointer; display: flex; }
.alert-empty { padding: 32px 16px; text-align: center; font-size: 12px; color: #3a3a5a; }
.alert-list { overflow-y: auto; flex: 1; }
.alert-item {
  display: flex; gap: 10px; padding: 12px 16px;
  border-bottom: 1px solid rgba(255,255,255,0.03);
  transition: background .1s;
}
.alert-item:hover { background: rgba(91,156,246,0.04); }
.alert-item-left { padding-top: 5px; }
.alert-dot { display: block; width: 6px; height: 6px; border-radius: 50%; flex-shrink: 0; }
.alert-item-body { flex: 1; min-width: 0; }
.alert-item-top { display: flex; align-items: center; gap: 6px; margin-bottom: 4px; }
.alert-spot { font-size: 12px; font-weight: 700; color: #d0d0e8; }
.alert-status-badge { font-size: 9px; font-weight: 700; padding: 1px 7px; border-radius: 4px; }
.alert-time { font-size: 9px; color: #3a3a5a; margin-left: auto; font-family: 'SF Mono', monospace; }
.alert-msg { font-size: 11px; color: #7070a0; white-space: pre-line; line-height: 1.5; margin-bottom: 3px; }
.alert-score { font-size: 10px; color: #3a3a5a; font-family: 'SF Mono', monospace; }

/* ─── Toast ──────────────────────────────────────────────── */
.toast-stack {
  position: fixed; top: 72px; right: 16px; z-index: 2000;
  display: flex; flex-direction: column; gap: 8px; pointer-events: none;
}
.toast-item {
  width: 290px; background: rgba(18,18,26,0.97);
  border: 1px solid; border-radius: 12px; overflow: hidden;
  display: flex; pointer-events: all;
  box-shadow: 0 12px 40px rgba(0,0,0,0.6);
  backdrop-filter: blur(12px);
}
.toast-bar { width: 3px; flex-shrink: 0; }
.toast-body { flex: 1; padding: 10px 12px; }
.toast-top { display: flex; align-items: center; gap: 6px; margin-bottom: 4px; }
.toast-spot { font-size: 12px; font-weight: 700; color: #d0d0e8; flex: 1; }
.toast-badge { font-size: 9px; font-weight: 700; padding: 1px 7px; border-radius: 4px; }
.toast-close { background: none; border: none; cursor: pointer; display: flex; margin-left: auto; }
.toast-msg { font-size: 11px; color: #7070a0; margin-bottom: 2px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.toast-score { font-size: 10px; color: #3a3a5a; font-family: 'SF Mono', monospace; }

/* ─── Stat row ───────────────────────────────────────────── */
.stat-row {
  display: flex; gap: 1px;
  background: rgba(255,255,255,0.04);
  border-bottom: 1px solid rgba(255,255,255,0.05);
}
.stat-card {
  flex: 1; display: flex; align-items: center; gap: 12px;
  padding: 16px 18px; background: #0b0b0f;
  transition: background .15s;
}
.stat-card:hover { background: rgba(91,156,246,0.03); }
.stat-icon-wrap {
  width: 34px; height: 34px; border-radius: 9px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.stat-label { font-size: 9px; color: #4a4a6a; letter-spacing: .08em; text-transform: uppercase; margin-bottom: 4px; }
.stat-value { font-size: 20px; font-weight: 800; line-height: 1; font-family: 'Space Grotesk', sans-serif; }
.stat-unit { font-size: 10px; font-weight: 400; color: #4a4a6a; margin-left: 3px; }

/* ─── Tab bar ────────────────────────────────────────────── */
.tab-bar {
  display: flex; gap: 2px; padding: 12px 24px 0;
  border-bottom: 1px solid rgba(255,255,255,0.05);
}
.tab-btn {
  display: inline-flex; align-items: center;
  padding: 9px 16px; border: none;
  background: transparent; color: #4a4a6a;
  font-size: 12px; font-weight: 600; font-family: inherit;
  cursor: pointer; border-bottom: 2px solid transparent;
  transition: all .18s; letter-spacing: .02em; margin-bottom: -1px;
}
.tab-btn:hover { color: #9090c0; }
.tab-btn.active { color: #5b9cf6; border-bottom-color: #5b9cf6; }

/* ─── Panels ─────────────────────────────────────────────── */
.panel-grid {
  display: grid; grid-template-columns: repeat(3, 1fr);
  gap: 12px; padding: 16px 24px;
}
.panel {
  background: rgba(255,255,255,0.02);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 14px; padding: 18px;
  backdrop-filter: blur(8px);
}
.col-1 { grid-column: span 1; }
.col-2 { grid-column: span 2; }
.col-3 { grid-column: span 3; }

.panel-title {
  font-size: 10px; font-weight: 700; color: #5070a0;
  letter-spacing: .15em; text-transform: uppercase;
  margin-bottom: 16px; display: flex; align-items: center;
}
.panel-title-row {
  display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px;
}
.panel-title-row .panel-title { margin-bottom: 0; }

.cctv-summary { display: flex; gap: 14px; }
.cctv-sum-item { display: flex; align-items: center; gap: 5px; font-size: 11px; font-weight: 600; }

/* ─── Action buttons ─────────────────────────────────────── */
.action-btn {
  display: inline-flex; align-items: center; gap: 5px;
  background: rgba(91,156,246,0.08); border: 1px solid rgba(91,156,246,0.2);
  color: #5b9cf6; border-radius: 7px; padding: 5px 12px;
  font-size: 11px; font-weight: 600; font-family: inherit;
  cursor: pointer; transition: all .15s;
}
.action-btn:hover:not(:disabled) { background: rgba(91,156,246,0.16); border-color: rgba(91,156,246,0.4); }
.action-btn:disabled { opacity: 0.4; cursor: not-allowed; }

.spinning { animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.data-loading {
  display: flex; align-items: center; gap: 10px;
  padding: 36px 0; justify-content: center;
  font-size: 12px; color: #5070a0;
}
.data-empty {
  text-align: center; padding: 36px 0;
  font-size: 12px; color: #2a2a4a; letter-spacing: .15em;
}
.data-error {
  background: rgba(255,71,87,0.07); border: 1px solid rgba(255,71,87,0.18);
  border-radius: 8px; padding: 8px 12px;
  font-size: 12px; color: #ff4757; margin-bottom: 12px;
}

/* ─── Hour selector ──────────────────────────────────────── */
.hour-selector { margin-bottom: 16px; }
.hour-label {
  display: flex; align-items: center; gap: 6px;
  font-size: 11px; font-weight: 700; color: #5070a0; margin-bottom: 10px;
}
.hour-now-badge {
  font-size: 8px; font-weight: 800; color: #5b9cf6;
  background: rgba(91,156,246,0.12); border: 1px solid rgba(91,156,246,0.3);
  border-radius: 4px; padding: 1px 6px; letter-spacing: .08em;
}
.hour-pills { display: flex; flex-wrap: wrap; gap: 4px; }
.hour-pill {
  width: 32px; height: 24px;
  background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06);
  border-radius: 5px; color: #4a4a6a; font-size: 11px; font-weight: 600;
  font-family: 'SF Mono', monospace; cursor: pointer; transition: all .12s;
}
.hour-pill:hover { background: rgba(91,156,246,0.08); color: #9090c0; border-color: rgba(91,156,246,0.2); }
.hour-pill.active {
  background: rgba(91,156,246,0.15); border-color: rgba(91,156,246,0.45);
  color: #5b9cf6; font-weight: 800;
}

/* ─── Zone cards ─────────────────────────────────────────── */
.zone-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; }
.zone-card {
  background: rgba(255,255,255,0.02); border: 1px solid;
  border-radius: 10px; padding: 12px;
}
.zone-card.clickable { cursor: pointer; transition: all .15s; }
.zone-card.clickable:hover { background: rgba(91,156,246,0.05); transform: translateY(-2px); }
.zone-top { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
.zone-name { font-size: 11px; font-weight: 600; color: #c0c0d8; }
.zone-badge { font-size: 9px; font-weight: 700; padding: 2px 8px; border-radius: 4px; }
.zone-score { font-size: 22px; font-weight: 900; margin-bottom: 8px; font-family: 'Space Grotesk', sans-serif; }
.zone-unit { font-size: 10px; font-weight: 400; color: #4a4a6a; margin-left: 2px; }
.zone-bar-bg { height: 3px; background: rgba(255,255,255,0.05); border-radius: 2px; overflow: hidden; }
.zone-bar-fill { height: 100%; border-radius: 2px; transition: width .5s; }
.zone-spotnum { font-size: 9px; color: #2a2a4a; margin-top: 4px; font-family: 'SF Mono', monospace; }

/* ─── Pollutants ─────────────────────────────────────────── */
.pollutant-list { display: flex; flex-direction: column; gap: 14px; }
.pollutant-row { display: flex; align-items: center; gap: 10px; }
.poll-name-wrap { display: flex; align-items: center; gap: 6px; width: 58px; flex-shrink: 0; }
.poll-dot { width: 5px; height: 5px; border-radius: 50%; flex-shrink: 0; }
.poll-name { font-size: 11px; font-weight: 600; color: #c0c0d8; }
.poll-bar-bg { flex: 1; height: 4px; background: rgba(255,255,255,0.05); border-radius: 2px; overflow: hidden; }
.poll-bar-fill { height: 100%; border-radius: 2px; transition: width .5s; }
.poll-value { font-size: 11px; font-weight: 700; width: 78px; text-align: right; flex-shrink: 0; }
.poll-unit { font-size: 9px; color: #4a4a6a; }

/* ─── Forecast ───────────────────────────────────────────── */
.panel-date {
  font-size: 9px; font-weight: 500; color: #3a3a5a;
  font-family: 'SF Mono', monospace; margin-left: 8px; letter-spacing: .04em;
}
.forecast-list { display: flex; gap: 8px; }
.forecast-item {
  flex: 1; display: flex; flex-direction: column; align-items: center; gap: 6px;
  padding: 14px 8px;
  background: rgba(255,255,255,0.02); border: 1px solid rgba(255,255,255,0.05);
  border-radius: 10px; transition: border-color .15s;
}
.forecast-item.current { border-color: rgba(91,156,246,0.3); background: rgba(91,156,246,0.04); }
.forecast-time { font-size: 9px; font-weight: 700; color: #5070a0; letter-spacing: .05em; }
.forecast-temp { font-size: 20px; font-weight: 800; color: #f0f0ff; font-family: 'Space Grotesk', sans-serif; }
.forecast-desc { font-size: 9px; color: #5070a0; text-align: center; }
.forecast-rain { font-size: 9px; color: #5b9cf6; font-weight: 600; }

/* ─── Population ─────────────────────────────────────────── */
.pop-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; }
.pop-card {
  background: rgba(255,255,255,0.02); border: 1px solid rgba(255,255,255,0.05);
  border-radius: 10px; padding: 14px;
}
.pop-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.pop-name { font-size: 13px; font-weight: 700; color: #c0c0d8; }
.pop-level { font-size: 11px; font-weight: 600; }
.pop-count { font-size: 24px; font-weight: 900; margin-bottom: 10px; font-family: 'Space Grotesk', sans-serif; }
.pop-unit { font-size: 11px; font-weight: 400; color: #4a4a6a; margin-left: 3px; }
.pop-bar-bg { height: 4px; background: rgba(255,255,255,0.05); border-radius: 2px; overflow: hidden; margin-bottom: 6px; }
.pop-bar-fill { height: 100%; border-radius: 2px; transition: width .5s; }
.pop-pct { font-size: 10px; color: #4a4a6a; text-align: right; }

/* ─── Table ──────────────────────────────────────────────── */
.table-wrap { overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th {
  font-size: 9px; font-weight: 700; color: #2a2a4a;
  letter-spacing: .12em; text-transform: uppercase;
  padding: 8px 12px; border-bottom: 1px solid rgba(255,255,255,0.05); text-align: left;
}
.data-table td {
  font-size: 12px; color: #7070a0; padding: 10px 12px;
  border-bottom: 1px solid rgba(255,255,255,0.03);
}
.data-table tbody tr:hover td { background: rgba(91,156,246,0.03); color: #c0c0d8; }
.mono { font-family: 'SF Mono', 'Fira Code', monospace; }
.grade-badge { font-size: 10px; font-weight: 700; padding: 2px 10px; border-radius: 5px; }
.status-chip { font-size: 10px; font-weight: 700; padding: 3px 10px; border-radius: 5px; }
.chip-green { background: rgba(52,211,153,0.15); color: #34d399; }
.chip-yellow { background: rgba(251,146,60,0.15); color: #fb923c; }
.chip-red { background: rgba(255,71,87,0.15); color: #ff4757; }

/* ─── Detail panel ───────────────────────────────────────── */
.detail-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0; z-index: 1000;
  background: rgba(0,0,0,0.6);
  backdrop-filter: blur(6px);
  display: flex; justify-content: flex-end;
}
.detail-panel {
  width: 420px; max-width: 95vw; height: 100%;
  background: #0f0f1a;
  border-left: 1px solid rgba(91,156,246,0.15);
  overflow-y: auto; padding: 24px 22px 40px;
  display: flex; flex-direction: column; gap: 20px;
}
.detail-header { display: flex; justify-content: space-between; align-items: flex-start; }
.detail-eyebrow {
  font-size: 8px; font-weight: 700; color: #5b9cf6;
  letter-spacing: .2em; text-transform: uppercase; margin-bottom: 6px;
}
.detail-name {
  font-size: 18px; font-weight: 800; color: #f0f0ff;
  font-family: 'Space Grotesk', sans-serif; line-height: 1.3; margin-bottom: 4px;
}
.detail-spotnum { font-size: 11px; color: #5070a0; font-family: 'SF Mono', monospace; }
.detail-close {
  background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.08);
  border-radius: 7px; width: 30px; height: 30px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; flex-shrink: 0; transition: all .15s;
}
.detail-close:hover { background: rgba(255,255,255,0.09); }

.detail-loading {
  display: flex; align-items: center; gap: 12px;
  justify-content: center; padding: 40px 0;
  font-size: 12px; color: #5070a0;
}

.detail-risk-row { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.detail-risk-box { border: 1px solid; border-radius: 12px; padding: 14px 16px; text-align: center; }
.detail-risk-label {
  font-size: 9px; font-weight: 700; color: #5070a0;
  letter-spacing: .12em; text-transform: uppercase; margin-bottom: 8px;
}
.detail-risk-score { font-size: 30px; font-weight: 900; line-height: 1; font-family: 'Space Grotesk', sans-serif; }
.detail-risk-unit { font-size: 13px; font-weight: 400; color: #4a4a6a; margin-left: 3px; }
.detail-status-text { font-size: 26px; font-weight: 900; line-height: 1.2; font-family: 'Space Grotesk', sans-serif; }

.event-message-box { border: 1px solid; border-radius: 12px; padding: 14px 16px; background: rgba(255,255,255,0.02); }
.event-message-header {
  display: flex; align-items: center; gap: 6px;
  font-size: 9px; font-weight: 700; letter-spacing: .12em; text-transform: uppercase;
  margin-bottom: 10px;
}
.event-message-body { font-size: 12px; color: #8080a0; line-height: 1.8; white-space: pre-line; }

.detail-section-title {
  font-size: 9px; font-weight: 700; color: #5070a0;
  letter-spacing: .15em; text-transform: uppercase; margin-bottom: 12px;
  display: flex; align-items: center;
}

/* AI section */
.ai-section { display: flex; flex-direction: column; gap: 10px; }
.ai-result-row { display: grid; grid-template-columns: 1fr 1.4fr; gap: 10px; }
.ai-result-box {
  border: 1px solid; border-radius: 12px; padding: 14px 12px;
  display: flex; flex-direction: column; align-items: center; gap: 6px; text-align: center;
}
.ai-anomaly { border-color: rgba(255,71,87,0.35); background: rgba(255,71,87,0.07); color: #ff4757; }
.ai-normal  { border-color: rgba(52,211,153,0.35); background: rgba(52,211,153,0.07); color: #34d399; }
.ai-result-label { font-size: 12px; font-weight: 800; }
.ai-result-sub { font-size: 9px; opacity: .7; }
.ai-score-box {
  border: 1px solid rgba(167,139,250,0.2); background: rgba(167,139,250,0.05);
  border-radius: 12px; padding: 14px;
}
.ai-score-val {
  font-size: 20px; font-weight: 900; font-family: 'SF Mono', monospace; margin: 4px 0;
}
.ai-score-desc { font-size: 9px; color: #5070a0; margin-bottom: 8px; }
.ai-score-bar-bg { height: 3px; background: rgba(255,255,255,0.05); border-radius: 2px; overflow: hidden; }
.ai-score-bar-fill { height: 100%; border-radius: 2px; transition: width .5s; }

/* Detail chart */
.detail-chart-wrap { flex: 1; }
.detail-chart {
  display: flex; align-items: flex-end; gap: 4px;
  height: 160px; padding-bottom: 28px; position: relative;
}
.chart-col { flex: 1; display: flex; flex-direction: column; align-items: center; height: 100%; }
.chart-bar-wrap { flex: 1; width: 100%; display: flex; align-items: flex-end; }
.chart-bar { width: 100%; border-radius: 3px 3px 0 0; transition: height .4s; min-height: 2px; }
.chart-hour { font-size: 7px; color: #2a2a4a; margin-top: 4px; }
.chart-vol { font-size: 6px; color: #2a2a4a; }

/* ─── Animations ─────────────────────────────────────────── */
.fade-down-enter-active, .fade-down-leave-active { transition: opacity .2s, transform .2s; }
.fade-down-enter-from, .fade-down-leave-to { opacity: 0; transform: translateY(-8px); }

.toast-enter-active, .toast-leave-active { transition: opacity .3s, transform .3s; }
.toast-enter-from { opacity: 0; transform: translateX(100%); }
.toast-leave-to { opacity: 0; transform: translateX(100%); }

.slide-panel-enter-active, .slide-panel-leave-active { transition: opacity .25s, transform .25s; }
.slide-panel-enter-from, .slide-panel-leave-to { opacity: 0; }
.slide-panel-enter-from .detail-panel, .slide-panel-leave-to .detail-panel { transform: translateX(100%); }
.slide-panel-enter-active .detail-panel, .slide-panel-leave-active .detail-panel { transition: transform .25s; }
</style>
