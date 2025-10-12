<script setup lang="ts">
  import { ref, computed } from 'vue'
  import BaseDialog from '@/components/dialog/BaseDialog.vue'
  import StandardButton from '@/components/StandardButton.vue'

  type CategoryOption = { value: number, label: string, icon?: string }

  const props = defineProps({
    modelValue: { type: Boolean, default: false },
    categories: { type: Array as () => CategoryOption[], default: () => [] },
  })

  const emit = defineEmits(['update:modelValue', 'move-to-category'])

  const selected = ref<number | null>(null)
  const isDropdownOpen = ref(false)

  const selectedCategory = computed(() => {
    if (!selected.value) return null
    return props.categories.find(cat => cat.value === selected.value) || null
  })

  function toggleDropdown () {
    isDropdownOpen.value = !isDropdownOpen.value
  }

  function selectCategory (category: CategoryOption) {
    selected.value = category.value
    isDropdownOpen.value = false
  }

  function closeDialog () {
    emit('update:modelValue', false)
    selected.value = null
    isDropdownOpen.value = false
  }

  function confirmMove () {
    if (selected.value == null) return
    emit('move-to-category', { categoryId: selected.value })
    closeDialog()
  }
</script>

<template>
  <BaseDialog :model-value="modelValue" @update:model-value="closeDialog">
    <div class="dialog-header">
      <h2 class="dialog-title">Move to Category</h2>
      <button class="close-button" @click="closeDialog">
        <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z" /></svg>
      </button>
    </div>
    <div class="dialog-body">
      <div class="form-field">
        <label class="field-label">Category</label>
        <div class="custom-select-container">
          <div class="custom-select" :class="{ 'is-open': isDropdownOpen }" @click="toggleDropdown">
            <div class="selected-option">
              <template v-if="selectedCategory">
                <v-icon class="option-icon" :icon="selectedCategory.icon" />
                <span class="option-label">{{ selectedCategory.label }}</span>
              </template>
              <span v-else class="placeholder">Select a category...</span>
            </div>
            <v-icon class="dropdown-arrow" icon="mdi-chevron-down" />
          </div>
          <div v-if="isDropdownOpen" class="dropdown-options">
            <div
              v-for="category in categories"
              :key="category.value"
              class="dropdown-option"
              @click="selectCategory(category)"
            >
              <v-icon class="option-icon" :icon="category.icon" />
              <span class="option-label">{{ category.label }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="dialog-footer">
      <button class="cancel-button" @click="closeDialog">Cancel</button>
      <StandardButton icon="mdi-arrow-right" title="Move" @click="confirmMove" />
    </div>
  </BaseDialog>
</template>

<style scoped>
/* Custom dropdown styles */
.custom-select-container { position: relative; width: 100%; }
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
.custom-select:hover { border-color: #4CAF50; }
.custom-select.is-open {
  border-color: #4CAF50;
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
}
.selected-option {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}
.option-icon {
  font-size: 18px !important;
  color: #4CAF50;
}
.option-label {
  color: #333;
  font-weight: 500;
}
.placeholder {
  color: #999;
}
.dropdown-arrow {
  font-size: 18px !important;
  color: #666;
  transition: transform 0.2s ease;
}
.custom-select.is-open .dropdown-arrow {
  transform: rotate(180deg);
}
.dropdown-options {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 2px solid #4CAF50;
  border-top: none;
  border-bottom-left-radius: 8px;
  border-bottom-right-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 1001;
  max-height: 200px;
  overflow-y: auto;
}
.dropdown-option {
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}
.dropdown-option:hover {
  background-color: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
}
.dropdown-option:hover .option-icon {
  color: #4CAF50;
}
.dropdown-option:last-child {
  border-bottom-left-radius: 6px;
  border-bottom-right-radius: 6px;
}
</style>
