<script setup lang="ts">
  import { computed, ref } from 'vue'
  import StandardButton from '@/components/StandardButton.vue'

  type PantryOption = { value: number, label: string, icon?: string }

  const props = defineProps({
    modelValue: { type: Boolean, default: false },
    pantries: { type: Array as () => PantryOption[], default: () => [] },
  })

  const emit = defineEmits(['update:modelValue', 'move-to-pantry'])

  const selected = ref<number | null>(null)
  const isDropdownOpen = ref(false)

  const selectedPantry = computed(() => {
    if (!selected.value) return null
    return props.pantries.find(pantry => pantry.value === selected.value) || null
  })

  function selectPantry (pantry: PantryOption) {
    selected.value = pantry.value
    isDropdownOpen.value = false
  }

  function closeDialog () {
    emit('update:modelValue', false)
    selected.value = null
    isDropdownOpen.value = false
  }

  function confirmMove () {
    if (selected.value == null) return
    emit('move-to-pantry', { pantryId: selected.value })
    closeDialog()
  }
</script>

<template>
  <!-- Dialog Backdrop -->
  <div v-if="modelValue" class="dialog-backdrop" @click="closeDialog">
    <!-- Dialog Content -->
    <div class="dialog-container" @click.stop>
      <!-- Dialog Header -->
      <div class="dialog-header">
        <h2 class="dialog-title">Move to Pantry</h2>
        <button class="close-button" @click="closeDialog">
          <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z" />
          </svg>
        </button>
      </div>

      <!-- Dialog Body -->
      <div class="dialog-body">
        <div class="form-field">
          <label class="field-label">Pantry</label>
          <div class="custom-select-container">
            <div class="custom-select" @click="isDropdownOpen = !isDropdownOpen">
              <div class="selected-option">
                <template v-if="selectedPantry">
                  <v-icon class="option-icon" :icon="selectedPantry.icon" />
                  <span class="option-label">{{ selectedPantry.label }}</span>
                </template>
                <span v-else class="placeholder">Select a pantry...</span>
              </div>
              <svg class="dropdown-arrow" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M7,10L12,15L17,10H7Z" />
              </svg>
            </div>

            <div v-if="isDropdownOpen" class="dropdown-options">
              <div
                v-for="pantry in pantries"
                :key="pantry.value"
                class="dropdown-option"
                @click="selectPantry(pantry)"
              >
                <v-icon class="option-icon" :icon="pantry.icon" />
                <span class="option-label">{{ pantry.label }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Dialog Footer -->
      <div class="dialog-footer">
        <button class="cancel-button" @click="closeDialog">Cancel</button>
        <StandardButton icon="mdi-arrow-right" title="Move" @click="confirmMove" />
      </div>
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
  z-index: 2200;
}

.dialog-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  width: 90%;
  max-width: 480px;
  max-height: 90vh;
  overflow: visible;
  display: flex;
  flex-direction: column;
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #e0e0e0;
}

.dialog-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.close-button {
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

.close-button:hover {
  background-color: #f5f5f5;
}

.close-button svg {
  width: 20px;
  height: 20px;
  fill: #666;
}

.dialog-body {
  padding: 24px;
  overflow: visible;
}

.form-field {
  margin-bottom: 20px;
}

.field-label {
  display: block;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
}

.custom-select-container {
  position: relative;
  width: 100%;
}

.custom-select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: border-color 0.2s ease;
}

.custom-select:hover {
  border-color: #4CAF50;
}

.selected-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.option-icon {
  font-size: 20px;
  color: #666;
}

.option-label {
  color: #333;
}

.placeholder {
  color: #999;
}

.dropdown-arrow {
  width: 20px;
  height: 20px;
  fill: #666;
  transition: transform 0.2s ease;
}

.dropdown-options {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  right: 0;
  background: white;
  border: 2px solid #4CAF50;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  max-height: 300px;
  overflow-y: auto;
  z-index: 2300;
}

.dropdown-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.dropdown-option:hover {
  background-color: #f5f5f5;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #e0e0e0;
}

.cancel-button {
  padding: 10px 20px;
  background: transparent;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #666;
}

.cancel-button:hover {
  background-color: #f5f5f5;
  border-color: #ccc;
}
</style>
