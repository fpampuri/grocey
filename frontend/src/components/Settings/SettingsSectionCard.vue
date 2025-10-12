<script setup lang="ts">

  const props = defineProps<{ title: string, subtitle?: string }>()

</script>

<template>
  <v-card class="settings-section">
    <div class="section-header">
      <h2 class="section-title">{{ title }}</h2>
      <p v-if="subtitle" class="section-subtitle">{{ subtitle }}</p>
    </div>
    <div class="section-body">
      <slot />
    </div>
  </v-card>
</template>

<style scoped>
.settings-section {
  border-radius: 12px;
  padding: 16px;
  margin: 6px;
  border: 1px solid var(--v-border-color, #e0e0e0);
  background: var(--primary-lightgrey);
}
.section-header { margin-bottom: 8px; }
.section-title { font-size: 1.1rem; font-weight: 700; margin: 0 0 4px; }
.section-subtitle { margin: 0; color: #666; }
.section-body { display: flex; flex-direction: column; gap: 4px; }
</style>
