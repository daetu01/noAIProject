<script lang="ts" setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import 'leaflet/dist/leaflet.css'
import L from 'leaflet'
import proj4 from 'proj4'
import type { TrafficRiskAll } from '@/api/cityService'

const props = defineProps<{ risks: TrafficRiskAll[] }>()
const emit = defineEmits<{ (e: 'spot-click', spotNum: string): void }>()

const mapEl = ref<HTMLElement | null>(null)
let map: L.Map | null = null
let markerGroup: L.LayerGroup | null = null

// 서울 중부원점 TM → WGS84
const KOREA_TM = '+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=500000 +ellps=GRS80 +units=m +no_defs'
const WGS84    = '+proj=longlat +datum=WGS84 +no_defs'

function tmToLatLng(tmX: number, tmY: number): [number, number] {
  const [lon, lat] = proj4(KOREA_TM, WGS84, [tmX, tmY])
  return [lat, lon]
}

const STATUS_COLOR: Record<string, string> = {
  NORMAL:  '#2ED573',
  WARNING: '#FFA502',
  DANGER:  '#FF4757',
}
const STATUS_LABEL: Record<string, string> = {
  NORMAL:  '원활',
  WARNING: '서행',
  DANGER:  '혼잡',
}

function renderMarkers() {
  if (!map || !markerGroup) return
  markerGroup.clearLayers()

  props.risks.forEach(r => {
    const latlng = tmToLatLng(r.tmX, r.tmY)
    const color  = STATUS_COLOR[r.status] ?? '#888'
    const radius = r.status === 'DANGER' ? 14 : r.status === 'WARNING' ? 11 : 8

    const circle = L.circleMarker(latlng, {
      radius,
      fillColor: color,
      color:     '#000',
      weight:    1.5,
      opacity:   0.9,
      fillOpacity: 0.8,
    })

    circle.bindTooltip(`
      <div style="font-family:-apple-system,sans-serif;padding:2px 4px">
        <b style="font-size:13px;color:#111">${r.spotName}</b><br/>
        <span style="font-size:11px;color:#666">${r.spotNum} · 위험도 ${r.riskScore.toFixed(0)}점</span><br/>
        <span style="color:${color};font-weight:700;font-size:11px">${STATUS_LABEL[r.status] ?? r.status}</span>
        <span style="font-size:10px;color:#999"> — 클릭하면 상세 보기</span>
      </div>
    `, { sticky: true })

    circle.on('click', () => emit('spot-click', r.spotNum))

    markerGroup!.addLayer(circle)
  })
}

onMounted(() => {
  if (!mapEl.value) return

  map = L.map(mapEl.value, { center: [37.55, 127.0], zoom: 11 })

  // 다크 타일 (CartoDB Dark Matter)
  L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
    attribution: '© OpenStreetMap © CARTO',
    subdomains: 'abcd',
    maxZoom: 19,
  }).addTo(map)

  markerGroup = L.layerGroup().addTo(map)

  // 범례
  const legend = new L.Control({ position: 'bottomright' })
  legend.onAdd = () => {
    const div = L.DomUtil.create('div')
    div.innerHTML = `
      <div style="background:rgba(10,16,30,0.88);border:1px solid rgba(0,200,255,0.2);border-radius:8px;padding:10px 14px;font-family:sans-serif;font-size:12px;color:#ccc">
        <p style="font-weight:700;margin:0 0 8px;color:#fff;letter-spacing:1px">STATUS</p>
        <div style="display:flex;align-items:center;gap:6px;margin-bottom:5px"><span style="width:10px;height:10px;border-radius:50%;background:#FF4757;display:inline-block"></span> 혼잡 (DANGER)</div>
        <div style="display:flex;align-items:center;gap:6px;margin-bottom:5px"><span style="width:10px;height:10px;border-radius:50%;background:#FFA502;display:inline-block"></span> 서행 (WARNING)</div>
        <div style="display:flex;align-items:center;gap:6px"><span style="width:10px;height:10px;border-radius:50%;background:#2ED573;display:inline-block"></span> 원활 (NORMAL)</div>
      </div>
    `
    return div
  }
  legend.addTo(map)

  renderMarkers()
})

watch(() => props.risks, renderMarkers, { deep: true })

onUnmounted(() => { map?.remove() })
</script>

<template>
  <div ref="mapEl" class="map-container" />
</template>

<style scoped>
.map-container {
  width: 100%;
  height: 480px;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid rgba(0, 200, 255, 0.15);
}
</style>
