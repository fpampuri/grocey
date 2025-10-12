<script setup lang="ts">
  import { ref } from 'vue'

  import BaseDialog from '@/components/dialog/BaseDialog.vue'
  import StandardButton from '@/components/StandardButton.vue'

  const props = defineProps({
    modelValue: { type: Boolean, default: false },
  })

  const emit = defineEmits(['update:modelValue', 'add-item'])

  // Form data
  const productName = ref('')

  function closeDialog () {
    emit('update:modelValue', false)
    // Reset form
    productName.value = ''
  }

  function handleModelValueUpdate (value: boolean) {
    if (!value) {
      // Reset form when dialog closes
      productName.value = ''
    }
    emit('update:modelValue', value)
  }

  function addItem () {
    if (!productName.value.trim()) {
      // We should show some type of error message
      return // Don't create if name is empty
    }

    emit('add-item', {
      name: productName.value.trim(),
    })

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
      <h2 class="dialog-title">Add Item</h2>
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
      <!-- Product Name Field -->
      <div class="form-field">
        <label class="field-label">Product Name</label>
        <!-- ESTARIA EPICO SI VA MOSTRANDO LOS PRODUCTOS Q MATCHEAN EN TIEMPO REAL -->
        <input
          v-model="productName"
          class="text-input"
          placeholder="Enter product name"
          type="text"
          @keyup.enter="addItem"
        >
      </div>
    </div>

    <!-- Dialog Footer -->
    <div class="dialog-footer">
      <button class="cancel-button" @click="closeDialog">Cancel</button>
      <StandardButton icon="mdi-plus" title="Add Item" @click="addItem" />
    </div>
  </BaseDialog>
</template>

<style scoped>
/* Custom styles specific to this dialog - icon selector */

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
