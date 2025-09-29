/**
 * plugins/vuetify.ts
 *
 * Framework documentation: https://vuetifyjs.com`
**/

// Styles
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

// Composables
import { createVuetify } from 'vuetify'
import type { ThemeDefinition } from 'vuetify'

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
      } as ThemeDefinition,
    },
  },
})
