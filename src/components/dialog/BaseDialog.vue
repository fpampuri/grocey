<script setup lang="ts">
import { defineProps, defineEmits, computed } from 'vue';

interface Props {
  modelValue: boolean;
  maxWidth?: string;
  persistent?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  maxWidth: '480px',
  persistent: false,
});

const emit = defineEmits<{
  'update:modelValue': [value: boolean];
}>();

const dialogModel = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

function handleBackdropClick() {
  if (!props.persistent) {
    emit('update:modelValue', false);
  }
}
</script>

<template>
  <!-- Dialog Backdrop -->
  <div v-if="modelValue" class="dialog-backdrop" @click="handleBackdropClick">
    <!-- Dialog Container with consistent styling -->
    <div class="dialog-container" @click.stop :style="{ maxWidth }">
      <!-- Completely customizable content via slot -->
      <slot />
    </div>
  </div>
</template>

<style scoped>
.dialog-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  width: 90%;
  max-height: 90vh;
  overflow: visible;
  display: flex;
  flex-direction: column;
}

/* Provide utility classes for consistent header styling */
.dialog-container :deep(.dialog-header) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #e0e0e0;
}

.dialog-container :deep(.dialog-title) {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.dialog-container :deep(.close-button) {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s ease;
}

.dialog-container :deep(.close-button:hover) {
  background-color: #f5f5f5;
}

.dialog-container :deep(.close-button svg) {
  width: 20px;
  height: 20px;
  fill: #666;
}

/* Provide utility classes for consistent body styling */
.dialog-container :deep(.dialog-body) {
  padding: 24px;
  overflow: visible;
}

/* Provide utility classes for consistent footer styling */
.dialog-container :deep(.dialog-footer) {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e0e0e0;
  background-color: #fafafa;
}

/* Utility styles for form elements */
.dialog-container :deep(.form-field) {
  margin-bottom: 20px;
}

.dialog-container :deep(.field-label) {
  display: block;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
}

.dialog-container :deep(.text-input) {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid var(--primary-green);
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.2s ease;
}

.dialog-container :deep(.text-input:focus) {
  border-color: var(--primary-green);
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
}

.dialog-container :deep(.select-input) {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.2s ease;
  background: white;
}

.dialog-container :deep(.select-input:focus) {
  border-color: var(--primary-green);
}

.dialog-container :deep(.cancel-button) {
  padding: 10px 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: white;
  color: #666;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s ease;
}

.dialog-container :deep(.cancel-button:hover) {
  background-color: #f5f5f5;
  border-color: #999;
}
</style>