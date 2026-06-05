import client from './client'

export type TrafficStatus = 'NORMAL' | 'WARNING' | 'DANGER'

export interface TrafficSpotVolume {
  spotNum: string
  ymd: string
  hour: number
  inVolume: number
  outVolume: number
  totalVolume: number
}

export interface TrafficHourVolume {
  hour: number
  totalVolume: number
}

export interface TrafficRisk {
  spotNum: string
  hour: number
  currentVolume: number
  averageVolume: number
  riskScore: number
  status: TrafficStatus
}

export interface TrafficRiskAll {
  spotNum: string
  spotName: string
  riskScore: number
  status: TrafficStatus
  tmX: number
  tmY: number
}

export interface TrafficDetail {
  spotName: string
  spotNum: string
  riskScore: number
  status: TrafficStatus
  todayVolumes: { hour: number; totalVolume: number }[]
  aiAnomaly: boolean | null
  aiScore: number | null
  aiStatus: string | null
  message: string | null
}

function toYmd(date: Date): string {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}${m}${d}`
}

function toHour(date: Date): number {
  const h = date.getHours()
  return h === 0 ? 23 : h - 1
}

export interface WeatherRow {
  ymd: string
  stnId: string
  avgTemp: number | null
  maxTemp: number | null
  minTemp: number | null
  morningMinTemp: number | null
  daytimeMaxTemp: number | null
  nightMinTemp: number | null
}

export interface ForecastItem {
  tmFc: string
  tmEf: string
  temperature: number | null
  rainProbability: number | null
  skyCode: string | null
  precipitationCode: number | null
  weatherText: string | null
}

export const cityService = {
  // 특정 시간 모든 지점 교통량
  getSpotVolumes(ymd?: string, hour?: string): Promise<TrafficSpotVolume[]> {
    const now = new Date()
    return client
      .get<{ data: TrafficSpotVolume[] }>('/api/traffic/spot', {
        params: { ymd: ymd ?? toYmd(now), hour: hour ?? toHour(now) }
      })
      .then(res => res.data.data)
  },

  // 특정 지점 하루 시간별 교통량
  getHourVolumes(spotNum: string, ymd?: string): Promise<TrafficHourVolume[]> {
    const now = new Date()
    return client
      .get<{ data: TrafficHourVolume[] }>('/api/traffic/volumes', {
        params: { spotNum, ymd: ymd ?? toYmd(now) }
      })
      .then(res => res.data.data)
  },

  // 특정 지점 위험도
  getRisk(spotNum: string, ymd?: string, hour?: number): Promise<TrafficRisk> {
    const now = new Date()
    return client
      .get<{ data: TrafficRisk }>('/api/traffic/risk', {
        params: { spotNum, ymd: ymd ?? toYmd(now), hour: hour ?? toHour(now) }
      })
      .then(res => res.data.data)
  },

  // 전체 지점 위험도 (지도용)
  getAllRisks(ymd?: string, hour?: number): Promise<TrafficRiskAll[]> {
    const now = new Date()
    return client
      .get<TrafficRiskAll[]>('/api/traffic/risks', {
        params: { ymd: ymd ?? toYmd(now), hour: hour ?? toHour(now) }
      })
      .then(res => (res.data ?? []).map(r => ({ ...r, riskScore: Number(r.riskScore) })))
  },

  // 오늘 일기상 (평균/최고/최저기온)
  getDailyWeather(ymd?: string): Promise<WeatherRow> {
    const yesterday = new Date()
    yesterday.setDate(yesterday.getDate() - 1)
    return client
      .get<WeatherRow>('/api/weather/daily', {
        params: { ymd: ymd ?? toYmd(yesterday) }
      })
      .then(res => res.data)
  },

  // 단기예보 목록
  getForecastWeather(): Promise<ForecastItem[]> {
    return client
      .get<ForecastItem[]>('/api/weather/daily/short')
      .then(res => res.data ?? [])
  },

  // 특정 지점 상세 (당일 시간별 교통량 포함)
  getDetail(spotNum: string, ymd?: string, hour?: number): Promise<TrafficDetail> {
    const now = new Date()
    return client
      .get<TrafficDetail>(`/api/traffic/details/${spotNum}`, {
        params: { ymd: ymd ?? toYmd(now), hour: hour ?? toHour(now) }
      })
      .then(res => ({
        ...res.data,
        riskScore: Number(res.data.riskScore),
        aiScore: res.data.aiScore != null ? Number(res.data.aiScore) : null,
        todayVolumes: res.data.todayVolumes.map(v => ({ ...v, totalVolume: Number(v.totalVolume) })),
      }))
  },
}
