import vuetify from 'eslint-config-vuetify'

export default [
  ...vuetify({ ts: true }),
  {
    rules: {
      'vue/no-multiple-template-root': 'off',
    },
  },
]
