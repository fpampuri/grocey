/**
 * main.ts
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Composables
import { createApp } from 'vue'
// Plugins
import { registerPlugins } from '@/plugins'
// Router
import router from '@/router'
// Components
import App from './App.vue'
// Styles
import 'unfonts.css'
import '@/styles/global-font.css'

const app = createApp(App)

registerPlugins(app)

app.use(router)

app.mount('#app')
