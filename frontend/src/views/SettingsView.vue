<script setup lang="ts">
  import { storeToRefs } from 'pinia'
  import { watch } from 'vue'
  import { useTheme } from 'vuetify'
  import SettingRowAction from '@/components/Settings/SettingRowAction.vue'
  import SettingRowActionSecondary from '@/components/Settings/SettingRowActionSecondary.vue'
  import SettingRowToggle from '@/components/Settings/SettingRowToggle.vue'
  import SettingsSectionCard from '@/components/Settings/SettingsSectionCard.vue'
  import { useAppStore } from '@/stores/app'

  const app = useAppStore()
  const theme = useTheme()
  const {
    darkMode,
    notificationsEnabled,
    autoSortItems,
    autoDeleteCompleted,
    dataBackupEnabled,
  } = storeToRefs(app)

  function exportData () {
    // Placeholder: implement real export logic later
    console.log('Exporting data...')
  }

  function openHelp () {
    console.log('Open Help & FAQ')
  }

  function contactSupport () {
    console.log('Contact Support')
  }

  // Hook dark mode toggle to Vuetify theme
  watch(darkMode, v => {
    theme.global.name.value = v ? 'dark' : 'light'
  })
</script>

<template>
  <div class="pa-4">
    <v-container class="settings-page" fluid>
      <v-row justify="center">
        <v-col cols="12" lg="8" md="10" xl="7">
          <!-- App Settings -->
          <SettingsSectionCard subtitle="Customize your shopping experience" title="App Settings">
            <SettingRowToggle
              v-model="darkMode"
              icon="mdi-weather-night"
              subtitle="Toggle between light and dark themes"
              title="Dark Mode"
            />
          </SettingsSectionCard>

          <v-spacer />

          <!-- Data & Privacy -->
          <SettingsSectionCard title="Data & Privacy">
            <SettingRowToggle
              v-model="dataBackupEnabled"
              icon="mdi-shield-outline"
              subtitle="Automatically backup your lists"
              title="Data backup"
            />
          </SettingsSectionCard>

          <v-spacer />

          <!-- Support -->
          <SettingsSectionCard title="Support">
            <SettingRowAction
              action-text="View"
              icon="mdi-help-circle-outline"
              title="Help & FAQ"
              @click="openHelp"
            />
            <SettingRowActionSecondary
              action-text="Contact"
              icon="mdi-email-outline"
              title="Contact Support"
              @click="contactSupport"
            />
          </SettingsSectionCard>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<style scoped>
.settings-page { max-width: 1200px; }
.spacer { height: 20px; }
.export-row {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 12px 8px;
	border-radius: 8px;
}
.export-row:hover { background: #fafafa; }
.left { display: flex; gap: 12px; align-items: center; }
.left-icon { color: var(--primary-green); }
.text .title { font-weight: 600; }
.text .subtitle { color: #666; font-size: 0.9rem; }
.export-btn { text-transform: none; font-weight: 600; border-radius: 8px; }
</style>
