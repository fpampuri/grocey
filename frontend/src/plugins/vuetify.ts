/**
 * plugins/vuetify.ts
 *
 * Framework documentation: https://vuetifyjs.com`
**/

import type { ThemeDefinition } from 'vuetify'
// Composables
import { createVuetify } from 'vuetify'

// Styles
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

// https://vuetifyjs.com/en/introduction/why-vuetify/#feature-guides
export default createVuetify({
  theme: {
    defaultTheme: 'light',
    themes: {
      light: {
        dark: false,
      } as ThemeDefinition,
      dark: {
        dark: true,
        colors: {
          background: '#121212',
          surface: '#1E1E1E',
          primary: '#66bb6a', // verde más claro para fondos oscuros
          secondary: '#90caf9',
          error: '#ff5252',
          info: '#2196F3',
          success: '#4CAF50',
          warning: '#FB8C00',
        },
      } as ThemeDefinition,
    },
  },
})
