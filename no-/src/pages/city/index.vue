<script lang="ts" setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { cityService, type TrafficRiskAll, type TrafficDetail } from '@/api/cityService'
import TrafficMap from '@/components/TrafficMap.vue'

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

// 교통 실데이터
const trafficRisks = ref<TrafficRiskAll[]>([])
const trafficLoading = ref(false)
const trafficError = ref('')

const statusLabel = (s: string) => ({ NORMAL: '원활', WARNING: '서행', DANGER: '혼잡' }[s] ?? s)
const statusColor = (s: string) => ({ NORMAL: '#2ED573', WARNING: '#FFA502', DANGER: '#FF4757' }[s] ?? '#888')

function setTab(key: string) {
  activeTab.value = key as typeof activeTab.value
}

async function loadTraffic() {
  trafficLoading.value = true
  trafficError.value = ''
  try { trafficRisks.value = (await cityService.getAllRisks()) ?? [] }
  catch { trafficError.value = '교통 데이터를 불러오지 못했습니다.' }
  finally { trafficLoading.value = false }
}

// detail 패널
const detail = ref<TrafficDetail | null>(null)
const detailLoading = ref(false)
const detailOpen = ref(false)

async function openDetail(spotNum: string) {
  detailOpen.value = true
  detailLoading.value = true
  detail.value = null
  try { detail.value = await cityService.getDetail(spotNum) }
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
  { label: '위험 지점', value: String(dangerCount.value), unit: '개', color: '#FF4757', icon: 'mdi-alert-circle' },
  { label: '서행 지점', value: String(warningCount.value), unit: '개', color: '#FFA502', icon: 'mdi-car-clock' },
  { label: '모니터링 지점', value: String(trafficRisks.value.length), unit: '개', color: '#2ED573', icon: 'mdi-access-point' },
  { label: '대기질 지수', value: '82', unit: 'AQI', color: '#1E90FF', icon: 'mdi-weather-windy' },
  { label: 'CCTV 운영', value: '2,458', unit: '대', color: '#A29BFE', icon: 'mdi-cctv' },
])

const airStations = [
  { name: '강남구', pm25: 18, pm10: 34, aqi: 72, grade: '보통', color: '#FFA502' },
  { name: '종로구', pm25: 12, pm10: 24, aqi: 51, grade: '좋음', color: '#2ED573' },
  { name: '마포구', pm25: 22, pm10: 41, aqi: 88, grade: '보통', color: '#FFA502' },
  { name: '영등포구', pm25: 35, pm10: 67, aqi: 134, grade: '나쁨', color: '#FF4757' },
  { name: '송파구', pm25: 15, pm10: 29, aqi: 61, grade: '보통', color: '#FFA502' },
]

const airPollutants = [
  { name: 'PM2.5', value: 18, unit: 'μg/m³', max: 75, color: '#FFA502' },
  { name: 'PM10', value: 34, unit: 'μg/m³', max: 150, color: '#1E90FF' },
  { name: 'NO₂', value: 0.032, unit: 'ppm', max: 0.1, color: '#A29BFE' },
  { name: 'CO', value: 0.8, unit: 'ppm', max: 9, color: '#2ED573' },
  { name: 'O₃', value: 0.052, unit: 'ppm', max: 0.1, color: '#FF6B81' },
  { name: 'SO₂', value: 0.004, unit: 'ppm', max: 0.05, color: '#FDCB6E' },
]

const weatherData = [
  { time: '현재', temp: 23, icon: 'mdi-weather-partly-cloudy', desc: '구름 조금' },
  { time: '15시', temp: 25, icon: 'mdi-weather-sunny', desc: '맑음' },
  { time: '18시', temp: 22, icon: 'mdi-weather-partly-cloudy', desc: '구름 많음' },
  { time: '21시', temp: 19, icon: 'mdi-weather-cloudy', desc: '흐림' },
  { time: '00시', temp: 17, icon: 'mdi-weather-rainy', desc: '비' },
]

const weatherSensors = [
  { name: '강남관측소', temp: 23.4, humidity: 58, wind: 3.2, rain: 0 },
  { name: '종로관측소', temp: 22.8, humidity: 62, wind: 2.8, rain: 0 },
  { name: '마포관측소', temp: 23.1, humidity: 55, wind: 4.1, rain: 0 },
  { name: '인천관측소', temp: 21.5, humidity: 71, wind: 5.6, rain: 0.2 },
]

const populationZones = [
  { name: '강남역', density: 9821, level: '매우 혼잡', pct: 95, color: '#FF4757' },
  { name: '홍대입구', density: 7432, level: '혼잡', pct: 74, color: '#FFA502' },
  { name: '명동', density: 6891, level: '혼잡', pct: 69, color: '#FFA502' },
  { name: '여의도', density: 4230, level: '보통', pct: 42, color: '#1E90FF' },
  { name: '잠실', density: 8103, level: '혼잡', pct: 81, color: '#FFA502' },
  { name: '신촌', density: 3120, level: '여유', pct: 31, color: '#2ED573' },
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

onMounted(() => { updateClock(); timer = setInterval(updateClock, 1000); loadTraffic() })
onUnmounted(() => clearInterval(timer))
</script>

<template>
  <div class="dashboard">

    <!-- 헤더 -->
    <header class="dash-header">
      <div class="dash-header-left">
        <div class="dash-logo">
          <v-icon size="18" color="#00C8FF">mdi-city</v-icon>
        </div>
        <div>
          <p class="dash-eyebrow">SMART CITY</p>
          <h1 class="dash-title">도시 통합 모니터링</h1>
        </div>
      </div>
      <div class="dash-header-right">
        <div class="status-dot-row">
          <span class="status-dot green" />
          <span class="status-label">LIVE</span>
        </div>
        <p class="dash-clock">{{ now }}</p>
      </div>
    </header>

    <!-- 상단 통계 카드 -->
    <div class="stat-row">
      <div v-for="s in stats" :key="s.label" class="stat-card">
        <div class="stat-icon-wrap" :style="{ background: s.color + '22' }">
          <v-icon size="18" :color="s.color">{{ s.icon }}</v-icon>
        </div>
        <div>
          <p class="stat-label">{{ s.label }}</p>
          <p class="stat-value">{{ s.value }}<span class="stat-unit">{{ s.unit }}</span></p>
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
        <v-icon size="14" class="mr-1">{{ tab.icon }}</v-icon>
        {{ tab.label }}
      </button>
    </div>

    <!-- ── 교통 ── -->
    <div v-if="activeTab === 'traffic'" class="panel-grid">
      <div class="panel col-3">
        <div class="panel-title-row">
          <p class="panel-title"><v-icon size="14" color="#00C8FF" class="mr-1">mdi-map-marker-radius</v-icon>교통 지점 위험도 현황</p>
          <button class="refresh-btn" :disabled="trafficLoading" @click="loadTraffic">
            <v-icon size="14" :class="{ spinning: trafficLoading }">mdi-refresh</v-icon>
            새로고침
          </button>
        </div>

        <div v-if="trafficError" class="data-error">{{ trafficError }}</div>

        <div v-if="trafficLoading" class="data-loading">
          <v-progress-circular indeterminate color="#00C8FF" size="24" width="2" />
          <span>데이터 수집 중...</span>
        </div>

        <div v-else-if="trafficRisks.length === 0 && !trafficError" class="data-empty">
          — 데이터 없음 —
        </div>

        <TrafficMap v-else :risks="trafficRisks" @spot-click="openDetail" />
      </div>

      <!-- 지점 카드 목록 -->
      <div v-if="trafficRisks.length" class="panel col-3">
        <p class="panel-title"><v-icon size="14" color="#00C8FF" class="mr-1">mdi-format-list-bulleted</v-icon>지점별 위험도</p>
        <div class="zone-grid">
          <div
            v-for="r in trafficRisks" :key="r.spotNum"
            class="zone-card clickable"
            :style="{ borderColor: statusColor(r.status) + '55' }"
            @click="openDetail(r.spotNum)"
          >
            <div class="zone-top">
              <span class="zone-name">{{ r.spotName }}</span>
              <span class="zone-badge" :style="{ background: statusColor(r.status) + '22', color: statusColor(r.status) }">
                {{ statusLabel(r.status) }}
              </span>
            </div>
            <p class="zone-speed">{{ r.riskScore.toFixed(0) }}<span class="zone-unit">점</span></p>
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
          <!-- 헤더 -->
          <div class="detail-header">
            <div>
              <p class="detail-eyebrow">SPOT DETAIL</p>
              <p class="detail-name">{{ detail?.spotName ?? '로딩 중...' }}</p>
              <p class="detail-spotnum">{{ detail?.spotNum }}</p>
            </div>
            <button class="detail-close" @click="closeDetail">
              <v-icon size="20" color="#4A7AB5">mdi-close</v-icon>
            </button>
          </div>

          <!-- 로딩 -->
          <div v-if="detailLoading" class="detail-loading">
            <v-progress-circular indeterminate color="#00C8FF" size="28" width="2" />
            <span>데이터 불러오는 중...</span>
          </div>

          <template v-else-if="detail">
            <!-- 위험도 요약 -->
            <div class="detail-risk-row">
              <div class="detail-risk-box" :style="{ borderColor: statusColor(detail.status) + '55', background: statusColor(detail.status) + '11' }">
                <p class="detail-risk-label">위험 점수</p>
                <p class="detail-risk-score" :style="{ color: statusColor(detail.status) }">
                  {{ detail.riskScore.toFixed(1) }}<span class="detail-risk-unit">점</span>
                </p>
              </div>
              <div class="detail-risk-box" :style="{ borderColor: statusColor(detail.status) + '55', background: statusColor(detail.status) + '11' }">
                <p class="detail-risk-label">현재 상태</p>
                <p class="detail-status-text" :style="{ color: statusColor(detail.status) }">
                  {{ statusLabel(detail.status) }}
                </p>
              </div>
            </div>

            <!-- 시간별 교통량 바 차트 -->
            <div class="detail-chart-wrap">
              <p class="detail-section-title">당일 시간별 교통량</p>
              <div class="detail-chart">
                <div
                  v-for="v in detail.todayVolumes" :key="v.hour"
                  class="chart-col"
                >
                  <div class="chart-bar-wrap">
                    <div
                      class="chart-bar"
                      :style="{
                        height: Math.round(v.totalVolume / maxVolume * 100) + '%',
                        background: v.totalVolume / maxVolume > 0.75 ? '#FF4757'
                          : v.totalVolume / maxVolume > 0.4 ? '#FFA502' : '#2ED573'
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
            <span style="color:#FF4757">데이터를 불러오지 못했습니다.</span>
          </div>
        </div>
      </div>
    </transition>

    <!-- ── 대기질 ── -->
    <div v-if="activeTab === 'air'" class="panel-grid">
      <div class="panel col-1">
        <p class="panel-title"><v-icon size="14" color="#00C8FF" class="mr-1">mdi-gauge</v-icon>주요 오염물질</p>
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
        <p class="panel-title"><v-icon size="14" color="#00C8FF" class="mr-1">mdi-map-marker</v-icon>측정소별 현황</p>
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
        <p class="panel-title"><v-icon size="14" color="#00C8FF" class="mr-1">mdi-weather-sunny</v-icon>시간별 예보</p>
        <div class="forecast-list">
          <div v-for="w in weatherData" :key="w.time" class="forecast-item" :class="{ current: w.time === '현재' }">
            <p class="forecast-time">{{ w.time }}</p>
            <v-icon size="28" color="#1E90FF">{{ w.icon }}</v-icon>
            <p class="forecast-temp">{{ w.temp }}°</p>
            <p class="forecast-desc">{{ w.desc }}</p>
          </div>
        </div>
      </div>

      <div class="panel col-2">
        <p class="panel-title"><v-icon size="14" color="#00C8FF" class="mr-1">mdi-weather-station</v-icon>기상관측소 현황</p>
        <div class="table-wrap">
          <table class="data-table">
            <thead>
              <tr><th>관측소</th><th>기온</th><th>습도</th><th>풍속</th><th>강수</th></tr>
            </thead>
            <tbody>
              <tr v-for="s in weatherSensors" :key="s.name">
                <td>{{ s.name }}</td>
                <td>{{ s.temp }}°C</td>
                <td>{{ s.humidity }}%</td>
                <td>{{ s.wind }} m/s</td>
                <td>{{ s.rain > 0 ? s.rain + ' mm' : '-' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- ── 인구밀도 ── -->
    <div v-if="activeTab === 'population'" class="panel-grid">
      <div class="panel col-3">
        <p class="panel-title"><v-icon size="14" color="#00C8FF" class="mr-1">mdi-account-group</v-icon>주요 지역 실시간 밀집도</p>
        <div class="pop-grid">
          <div v-for="z in populationZones" :key="z.name" class="pop-card">
            <div class="pop-top">
              <span class="pop-name">{{ z.name }}</span>
              <span class="pop-level" :style="{ color: z.color }">{{ z.level }}</span>
            </div>
            <p class="pop-count">{{ z.density.toLocaleString() }}<span class="pop-unit">명</span></p>
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
          <p class="panel-title"><v-icon size="14" color="#00C8FF" class="mr-1">mdi-cctv</v-icon>CCTV 운영 현황</p>
          <div class="cctv-summary">
            <span class="cctv-sum-item green"><span class="status-dot green small" />정상 {{ cctvList.filter(c=>c.status==='정상').length }}</span>
            <span class="cctv-sum-item yellow"><span class="status-dot yellow small" />점검 {{ cctvList.filter(c=>c.status==='점검중').length }}</span>
            <span class="cctv-sum-item red"><span class="status-dot red small" />장애 {{ cctvList.filter(c=>c.status==='장애').length }}</span>
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
                  >
                    {{ cam.status }}
                  </span>
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
/* 전체 레이아웃 */
.dashboard {
  min-height: 100vh;
  background: #060D1A;
  background-image:
    radial-gradient(ellipse at 10% 20%, rgba(0,200,255,0.05) 0%, transparent 40%),
    radial-gradient(ellipse at 90% 80%, rgba(162,155,254,0.05) 0%, transparent 40%);
  padding: 0 0 60px;
  color: #C8D6E8;
}

/* 헤더 */
.dash-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(0,200,255,0.15);
  background: rgba(6,13,26,0.95);
  position: sticky; top: 56px; z-index: 100;
}
.dash-header-left { display: flex; align-items: center; gap: 12px; }
.dash-logo {
  width: 36px; height: 36px; border-radius: 10px;
  background: rgba(0,200,255,0.1); border: 1px solid rgba(0,200,255,0.3);
  display: flex; align-items: center; justify-content: center;
}
.dash-eyebrow { font-size: 9px; font-weight: 700; color: #00C8FF; letter-spacing: 2.5px; margin-bottom: 2px; }
.dash-title { font-size: 16px; font-weight: 800; color: #E0F4FF; letter-spacing: 0.5px; }
.dash-header-right { display: flex; align-items: center; gap: 16px; }
.status-dot-row { display: flex; align-items: center; gap: 6px; }
.status-dot { width: 8px; height: 8px; border-radius: 50%; }
.status-dot.green { background: #2ED573; box-shadow: 0 0 6px #2ED573; animation: pulse 2s infinite; }
.status-dot.yellow { background: #FFA502; box-shadow: 0 0 6px #FFA502; }
.status-dot.red { background: #FF4757; box-shadow: 0 0 6px #FF4757; }
.status-dot.small { width: 6px; height: 6px; }
@keyframes pulse { 0%,100% { opacity: 1; } 50% { opacity: 0.4; } }
.status-label { font-size: 11px; font-weight: 700; color: #2ED573; letter-spacing: 1px; }
.dash-clock { font-size: 12px; color: #4A7AB5; font-family: 'Courier New', monospace; letter-spacing: 0.5px; }

/* 통계 카드 */
.stat-row { display: flex; gap: 1px; background: rgba(0,200,255,0.08); border-bottom: 1px solid rgba(0,200,255,0.1); }
.stat-card { flex: 1; display: flex; align-items: center; gap: 12px; padding: 14px 16px; background: #060D1A; transition: background 0.15s; }
.stat-card:hover { background: rgba(0,200,255,0.04); }
.stat-icon-wrap { width: 36px; height: 36px; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.stat-label { font-size: 10px; color: #4A7AB5; letter-spacing: 0.5px; margin-bottom: 3px; }
.stat-value { font-size: 18px; font-weight: 800; color: #E0F4FF; line-height: 1; }
.stat-unit { font-size: 11px; font-weight: 400; color: #4A7AB5; margin-left: 3px; }

/* 탭 */
.tab-bar { display: flex; gap: 4px; padding: 12px 20px 0; border-bottom: 1px solid rgba(0,200,255,0.1); }
.tab-btn {
  display: inline-flex; align-items: center; padding: 9px 16px; border: none;
  background: transparent; color: #4A7AB5; font-size: 12px; font-weight: 600;
  cursor: pointer; border-bottom: 2px solid transparent; transition: all 0.15s;
  letter-spacing: 0.3px; margin-bottom: -1px;
}
.tab-btn:hover { color: #A0C4E8; }
.tab-btn.active { color: #00C8FF; border-bottom-color: #00C8FF; }

/* 패널 그리드 */
.panel-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 1px; padding: 16px 20px; gap: 12px; }
.panel { background: rgba(255,255,255,0.02); border: 1px solid rgba(0,200,255,0.1); border-radius: 10px; padding: 16px; }
.col-1 { grid-column: span 1; }
.col-2 { grid-column: span 2; }
.col-3 { grid-column: span 3; }

.panel-title { font-size: 11px; font-weight: 700; color: #4A7AB5; letter-spacing: 1.5px; text-transform: uppercase; margin-bottom: 14px; display: flex; align-items: center; }
.panel-title-row { display: flex; align-items: center; justify-content: space-between; margin-bottom: 14px; }
.panel-title-row .panel-title { margin-bottom: 0; }
.cctv-summary { display: flex; gap: 14px; }
.cctv-sum-item { display: flex; align-items: center; gap: 5px; font-size: 11px; font-weight: 600; }
.cctv-sum-item.green { color: #2ED573; }
.cctv-sum-item.yellow { color: #FFA502; }
.cctv-sum-item.red { color: #FF4757; }

/* 새로고침 / 로딩 */
.refresh-btn { display: inline-flex; align-items: center; gap: 4px; background: rgba(0,200,255,0.08); border: 1px solid rgba(0,200,255,0.2); color: #00C8FF; border-radius: 6px; padding: 5px 12px; font-size: 11px; font-weight: 600; cursor: pointer; transition: all 0.15s; }
.refresh-btn:hover:not(:disabled) { background: rgba(0,200,255,0.15); }
.refresh-btn:disabled { opacity: 0.4; cursor: not-allowed; }
.spinning { animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.data-loading { display: flex; align-items: center; gap: 10px; padding: 32px 0; justify-content: center; font-size: 13px; color: #4A7AB5; }
.data-empty { text-align: center; padding: 32px 0; font-size: 13px; color: #2D4A6A; letter-spacing: 2px; }
.data-error { background: rgba(255,71,87,0.08); border: 1px solid rgba(255,71,87,0.2); border-radius: 6px; padding: 8px 12px; font-size: 12px; color: #FF4757; margin-bottom: 12px; }
.zone-spotnum { font-size: 10px; color: #2D4A6A; margin-top: 4px; font-family: 'Courier New', monospace; }

/* 교통 */
.zone-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; }
.zone-card { background: rgba(255,255,255,0.02); border: 1px solid; border-radius: 8px; padding: 12px; }
.zone-top { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
.zone-name { font-size: 12px; font-weight: 600; color: #C8D6E8; }
.zone-badge { font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: 4px; }
.zone-speed { font-size: 24px; font-weight: 900; color: #E0F4FF; margin-bottom: 8px; }
.zone-unit { font-size: 11px; font-weight: 400; color: #4A7AB5; margin-left: 3px; }
.zone-bar-bg { height: 3px; background: rgba(255,255,255,0.06); border-radius: 2px; overflow: hidden; }
.zone-bar-fill { height: 100%; border-radius: 2px; transition: width 0.5s; }

.incident-list { display: flex; flex-direction: column; gap: 8px; }
.incident-row { display: flex; gap: 10px; padding: 10px; background: rgba(255,255,255,0.02); border-radius: 8px; border: 1px solid rgba(255,255,255,0.04); }
.inc-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; margin-top: 4px; }
.inc-body { flex: 1; min-width: 0; }
.inc-top { display: flex; justify-content: space-between; margin-bottom: 3px; }
.inc-type { font-size: 12px; font-weight: 700; color: #C8D6E8; }
.inc-status { font-size: 11px; font-weight: 600; }
.inc-location { font-size: 11px; color: #4A7AB5; margin-bottom: 2px; }
.inc-time { font-size: 10px; color: #2D4A6A; font-family: 'Courier New', monospace; }

/* 대기질 */
.pollutant-list { display: flex; flex-direction: column; gap: 12px; }
.pollutant-row { display: flex; align-items: center; gap: 10px; }
.poll-name-wrap { display: flex; align-items: center; gap: 6px; width: 60px; flex-shrink: 0; }
.poll-dot { width: 6px; height: 6px; border-radius: 50%; flex-shrink: 0; }
.poll-name { font-size: 12px; font-weight: 600; color: #C8D6E8; }
.poll-bar-bg { flex: 1; height: 4px; background: rgba(255,255,255,0.06); border-radius: 2px; overflow: hidden; }
.poll-bar-fill { height: 100%; border-radius: 2px; transition: width 0.5s; }
.poll-value { font-size: 12px; font-weight: 700; width: 80px; text-align: right; flex-shrink: 0; }
.poll-unit { font-size: 10px; color: #4A7AB5; }

/* 날씨 */
.forecast-list { display: flex; gap: 8px; }
.forecast-item { flex: 1; display: flex; flex-direction: column; align-items: center; gap: 6px; padding: 14px 8px; background: rgba(255,255,255,0.02); border: 1px solid rgba(255,255,255,0.04); border-radius: 8px; transition: border-color 0.15s; }
.forecast-item.current { border-color: rgba(0,200,255,0.3); background: rgba(0,200,255,0.04); }
.forecast-time { font-size: 10px; font-weight: 700; color: #4A7AB5; letter-spacing: 0.5px; }
.forecast-temp { font-size: 20px; font-weight: 800; color: #E0F4FF; }
.forecast-desc { font-size: 10px; color: #4A7AB5; text-align: center; }

/* 인구밀도 */
.pop-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; }
.pop-card { background: rgba(255,255,255,0.02); border: 1px solid rgba(255,255,255,0.05); border-radius: 8px; padding: 14px; }
.pop-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.pop-name { font-size: 13px; font-weight: 700; color: #C8D6E8; }
.pop-level { font-size: 11px; font-weight: 600; }
.pop-count { font-size: 26px; font-weight: 900; color: #E0F4FF; margin-bottom: 10px; }
.pop-unit { font-size: 12px; font-weight: 400; color: #4A7AB5; margin-left: 3px; }
.pop-bar-bg { height: 4px; background: rgba(255,255,255,0.06); border-radius: 2px; overflow: hidden; margin-bottom: 6px; }
.pop-bar-fill { height: 100%; border-radius: 2px; transition: width 0.5s; }
.pop-pct { font-size: 11px; color: #4A7AB5; text-align: right; }

/* 테이블 */
.table-wrap { overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th { font-size: 10px; font-weight: 700; color: #2D4A6A; letter-spacing: 1px; padding: 8px 12px; border-bottom: 1px solid rgba(0,200,255,0.1); text-align: left; }
.data-table td { font-size: 12px; color: #8AABCC; padding: 10px 12px; border-bottom: 1px solid rgba(255,255,255,0.03); }
.data-table tbody tr:hover td { background: rgba(0,200,255,0.03); color: #C8D6E8; }
.mono { font-family: 'Courier New', monospace; }
.grade-badge { font-size: 10px; font-weight: 700; padding: 2px 10px; border-radius: 4px; }
.status-chip { font-size: 10px; font-weight: 700; padding: 3px 10px; border-radius: 4px; }
.chip-green { background: rgba(46,213,115,0.15); color: #2ED573; }
.chip-yellow { background: rgba(255,165,2,0.15); color: #FFA502; }
.chip-red { background: rgba(255,71,87,0.15); color: #FF4757; }

/* 카드 클릭 */
.zone-card.clickable { cursor: pointer; transition: background 0.15s, transform 0.1s; }
.zone-card.clickable:hover { background: rgba(0,200,255,0.06); transform: translateY(-1px); }

/* detail 오버레이 */
.detail-overlay {
  position: fixed; inset: 0; z-index: 1000;
  background: rgba(0,0,0,0.5);
  backdrop-filter: blur(4px);
  display: flex; justify-content: flex-end;
}

.detail-panel {
  width: 420px; max-width: 95vw;
  height: 100%;
  background: #0A1628;
  border-left: 1px solid rgba(0,200,255,0.2);
  overflow-y: auto;
  padding: 24px 22px 40px;
  display: flex; flex-direction: column; gap: 20px;
}

.detail-header {
  display: flex; justify-content: space-between; align-items: flex-start;
}
.detail-eyebrow { font-size: 9px; font-weight: 700; color: #00C8FF; letter-spacing: 2px; margin-bottom: 6px; }
.detail-name { font-size: 18px; font-weight: 800; color: #E0F4FF; line-height: 1.3; margin-bottom: 4px; }
.detail-spotnum { font-size: 12px; color: #4A7AB5; font-family: 'Courier New', monospace; }
.detail-close {
  background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1);
  border-radius: 6px; width: 32px; height: 32px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; flex-shrink: 0;
}
.detail-close:hover { background: rgba(255,255,255,0.1); }

.detail-loading {
  display: flex; align-items: center; gap: 12px;
  justify-content: center; padding: 40px 0;
  font-size: 13px; color: #4A7AB5;
}

.detail-risk-row { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.detail-risk-box {
  border: 1px solid; border-radius: 10px;
  padding: 14px 16px; text-align: center;
}
.detail-risk-label { font-size: 10px; font-weight: 700; color: #4A7AB5; letter-spacing: 1px; margin-bottom: 8px; }
.detail-risk-score { font-size: 32px; font-weight: 900; line-height: 1; }
.detail-risk-unit { font-size: 14px; font-weight: 400; color: #4A7AB5; margin-left: 3px; }
.detail-status-text { font-size: 28px; font-weight: 900; line-height: 1.2; }

.detail-chart-wrap { flex: 1; }
.detail-section-title { font-size: 10px; font-weight: 700; color: #4A7AB5; letter-spacing: 1.5px; text-transform: uppercase; margin-bottom: 14px; }

.detail-chart {
  display: flex; align-items: flex-end; gap: 4px;
  height: 160px;
  padding-bottom: 28px;
  position: relative;
}
.chart-col { flex: 1; display: flex; flex-direction: column; align-items: center; height: 100%; }
.chart-bar-wrap { flex: 1; width: 100%; display: flex; align-items: flex-end; }
.chart-bar { width: 100%; border-radius: 3px 3px 0 0; transition: height 0.4s; min-height: 2px; }
.chart-hour { font-size: 8px; color: #2D4A6A; margin-top: 4px; }
.chart-vol { font-size: 7px; color: #2D4A6A; }

/* 슬라이드 애니메이션 */
.slide-panel-enter-active, .slide-panel-leave-active { transition: opacity 0.25s, transform 0.25s; }
.slide-panel-enter-from, .slide-panel-leave-to { opacity: 0; }
.slide-panel-enter-from .detail-panel, .slide-panel-leave-to .detail-panel { transform: translateX(100%); }
.slide-panel-enter-active .detail-panel, .slide-panel-leave-active .detail-panel { transition: transform 0.25s; }
</style>
