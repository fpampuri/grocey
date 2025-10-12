<script setup lang="ts">
  import { computed } from 'vue'
  import BaseDialog from '@/components/dialog/BaseDialog.vue'
  import StandardButton from '@/components/StandardButton.vue'

  interface Props {
    modelValue: boolean
    itemType?: string // "list", "category", "pantry", etc.
    itemName?: string // Name/title of the item to delete
    title?: string // Custom dialog title
    description?: string // Custom description text
    confirmText?: string // Custom confirm button text
    loading?: boolean // Loading state during deletion
  }

  const props = withDefaults(defineProps<Props>(), {
    itemType: 'item',
    itemName: '',
    title: '',
    description: 'This action cannot be undone.',
    confirmText: 'Delete',
    loading: false,
  })

  const emit = defineEmits<{
    'update:modelValue': [value: boolean]
    'confirm': []
  }>()

  // Computed properties for dynamic content
  const dialogTitle = computed(() => {
    if (props.title) return props.title
    return `Delete ${props.itemType.charAt(0).toUpperCase() + props.itemType.slice(1)}`
  })

  const confirmButtonText = computed(() => {
    if (props.confirmText !== 'Delete') return props.confirmText
    return `Delete ${props.itemType.charAt(0).toUpperCase() + props.itemType.slice(1)}`
  })

  function handleModelValueUpdate (value: boolean) {
    emit('update:modelValue', value)
  }

  function closeDialog () {
    emit('update:modelValue', false)
  }

  function confirmDelete () {
    emit('confirm')
    closeDialog()
  }

</script>

<template>
  <BaseDialog
    :model-value="modelValue"
    @update:model-value="handleModelValueUpdate"
  >
    <!-- Dialog Header -->
    <div class="dialog-header">
      <h2 class="dialog-title">{{ dialogTitle }}</h2>
      <button class="close-button" @click="closeDialog">
        <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          <path
            d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z"
          />
        </svg>
      </button>
    </div>

    <!-- Dialog Body -->
    <div class="dialog-body">
      <div class="form-field">
        <p class="text-body-1 mb-3">
          Are you sure you want to delete
          <strong v-if="itemName">"{{ itemName }}"</strong>
          <span v-else>this {{ itemType }}</span>?
        </p>
        <p class="text-body-2 text-medium-emphasis">
          {{ description }}
        </p>
      </div>
    </div>

    <!-- Dialog Footer -->
    <div class="dialog-footer">
      <button class="cancel-button" :disabled="loading" @click="closeDialog">
        Cancel
      </button>
      <StandardButton
        class="red-button"
        :disabled="loading"
        icon="mdi-delete"
        :loading="loading"
        :title="confirmButtonText"
        @click="confirmDelete"
      />
    </div>
  </BaseDialog>
</template>

<style scoped>

:deep(.red-button) {
  background-color: red !important;
}

.text-medium-emphasis {
  color: #666;
}

.icon-selector {
  position: relative;
}

.selected-icon {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: border-color 0.2s ease;
  background: white;
}

.selected-icon:hover {
  border-color: var(--primary-green);
}

.selected-label {
  flex: 1;
  color: #333;
}

.dropdown-arrow {
  width: 16px;
  height: 16px;
  fill: #666;
}

.icon-options {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 10;
  max-height: 200px;
  overflow-y: auto;
  display: block;
}

.icon-option {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 12px 16px;
  border: none;
  background: none;
  cursor: pointer;
  text-align: left;
  transition: background-color 0.2s ease;
}

.icon-option:hover {
  background-color: #f5f5f5;
}

.icon-option.active {
  background-color: #e8f5e8;
  color: var(--primary-green);
}

.dialog-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e0e0e0;
  background-color: white;
}

.cancel-button {
  padding: 10px 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: white;
  color: #666;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s ease;
}

.cancel-button:hover {
  background-color: white;
  border-color: #999;
}
</style>
