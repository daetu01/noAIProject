import { ref, onUnmounted } from 'vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

export interface TrafficAlert {
  spotNum: string
  hour: number
  riskScore: number
  status: 'WARNING' | 'DANGER'
  message: string
  receivedAt: Date
}

const STATUS_LABEL: Record<string, string> = { WARNING: '서행', DANGER: '혼잡' }
const STATUS_COLOR: Record<string, string> = { WARNING: '#FFA502', DANGER: '#FF4757' }

export function useTrafficAlerts() {
  const alerts = ref<TrafficAlert[]>([])
  const unread = ref(0)
  const toasts = ref<TrafficAlert[]>([])
  const connected = ref(false)

  let client: Client | null = null

  function connect() {
    client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000,
      onConnect: () => {
        connected.value = true
        client!.subscribe('/topic/traffic-alerts', (frame) => {
          const data = JSON.parse(frame.body)
          const alert: TrafficAlert = { ...data, receivedAt: new Date() }

          alerts.value.unshift(alert)
          if (alerts.value.length > 50) alerts.value.pop()

          unread.value++

          toasts.value.push(alert)
          setTimeout(() => {
            toasts.value = toasts.value.filter(t => t !== alert)
          }, 5000)
        })
      },
      onDisconnect: () => { connected.value = false },
    })
    client.activate()
  }

  function disconnect() {
    client?.deactivate()
    client = null
    connected.value = false
  }

  function markRead() {
    unread.value = 0
  }

  function dismissToast(alert: TrafficAlert) {
    toasts.value = toasts.value.filter(t => t !== alert)
  }

  onUnmounted(disconnect)

  return {
    alerts,
    unread,
    toasts,
    connected,
    connect,
    disconnect,
    markRead,
    dismissToast,
    statusLabel: (s: string) => STATUS_LABEL[s] ?? s,
    statusColor: (s: string) => STATUS_COLOR[s] ?? '#888',
  }
}
