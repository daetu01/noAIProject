import { createVuetify } from 'vuetify'
import '@mdi/font/css/materialdesignicons.css'
import '../styles/layers.css'
import 'vuetify/styles'

export default createVuetify({
  theme: {
    defaultTheme: 'light',
    themes: {
      light: {
        dark: false,
        colors: {
          background: '#F5F5F7',
          surface: '#FFFFFF',
          primary: '#0071E3',
          secondary: '#6E6E73',
          error: '#FF3B30',
          warning: '#FF9500',
          success: '#30D158',
          info: '#0071E3',
        },
      },
    },
  },
  defaults: {
    VBtn: {
      rounded: 'lg',
      elevation: 0,
    },
    VCard: {
      rounded: 'xl',
      elevation: 0,
    },
    VTextField: {
      rounded: 'lg',
      variant: 'outlined',
      hideDetails: 'auto',
    },
    VTextarea: {
      rounded: 'lg',
      variant: 'outlined',
      hideDetails: 'auto',
    },
    VFileInput: {
      rounded: 'lg',
      variant: 'outlined',
      hideDetails: 'auto',
    },
    VAlert: {
      rounded: 'lg',
    },
    VChip: {
      rounded: 'lg',
    },
  },
  display: {
    mobileBreakpoint: 'md',
  },
})
