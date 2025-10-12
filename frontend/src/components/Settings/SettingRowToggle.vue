<script setup lang="ts">

  const props = defineProps<{ icon: string, title: string, subtitle?: string, modelValue: boolean }>()
  const emit = defineEmits(['update:modelValue'])
</script>

<template>
  <div class="setting-row">
    <div class="left">
      <v-icon class="left-icon" :icon="icon" />
      <div class="text">
        <div class="title">{{ title }}</div>
        <div v-if="subtitle" class="subtitle">{{ subtitle }}</div>
      </div>
    </div>
    <div class="right">
      <v-switch
        color="green"
        hide-details
        inset
        :model-value="modelValue"
        @update:model-value="(v: boolean | null) => emit('update:modelValue', !!v)"
      />
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
}
.setting-row:hover { background-color: #fafafa; }
.left { display: flex; gap: 12px; align-items: center; }
.left-icon { color: var(--primary-green); }
.text .title { font-weight: 600; }
.text .subtitle { color: #666; font-size: 0.9rem; }
</style>
