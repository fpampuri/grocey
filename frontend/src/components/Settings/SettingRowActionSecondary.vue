<script setup lang="ts">
const props = defineProps<{ icon: string; title: string; subtitle?: string; actionText?: string }>();
const emit = defineEmits(['click']);
</script>

<template>
  <div class="setting-row" @click="emit('click')">
    <div class="left">
      <v-icon :icon="icon" class="left-icon" />
      <div class="text">
        <div class="title">{{ title }}</div>
        <div v-if="subtitle" class="subtitle">{{ subtitle }}</div>
      </div>
    </div>
    <div class="right">
      <v-btn
        variant="tonal"
        color="grey-darken-1"
        class="action-btn"
        size="small"
      >
        {{ actionText ?? 'Contact' }}
      </v-btn>
    </div>
  </div>
</template>

<style scoped>
.setting-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 8px;
  border-radius: 8px;
  cursor: pointer;
}
.setting-row:hover { background-color: #fafafa; }
.left { display: flex; gap: 12px; align-items: center; }
.left-icon { color: var(--primary-green); }
.text .title { font-weight: 600; }
.text .subtitle { color: #666; font-size: 0.9rem; }
.action-btn { text-transform: none; font-weight: 600; border-radius: 8px; }
</style>
