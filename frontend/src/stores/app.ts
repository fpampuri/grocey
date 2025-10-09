// Utilities
import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    darkMode: false,
    notificationsEnabled: false,
    autoSortItems: false,
    autoDeleteCompleted: false,
    dataBackupEnabled: true,
  }),
  actions: {
    setDarkMode(val: boolean) { this.darkMode = val },
    setNotifications(val: boolean) { this.notificationsEnabled = val },
    setAutoSortItems(val: boolean) { this.autoSortItems = val },
    setAutoDeleteCompleted(val: boolean) { this.autoDeleteCompleted = val },
    setDataBackupEnabled(val: boolean) { this.dataBackupEnabled = val },
  },
})
