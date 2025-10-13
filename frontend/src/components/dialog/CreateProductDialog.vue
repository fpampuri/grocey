<script setup lang="ts">
  import { computed, ref, watch } from 'vue'
  import StandardButton from '@/components/StandardButton.vue'

  type CategoryOption = { value: number, label: string, icon?: string }

  const props = defineProps({
    modelValue: { type: Boolean, default: false },
    categories: { type: Array as () => CategoryOption[], default: () => [] },
    defaultCategoryId: { type: Number, default: null },
  })

  const emit = defineEmits(['update:modelValue', 'create-product'])

  const productName = ref('')
  const selectedCategoryId = ref<number | null>(null)
  const isDropdownOpen = ref(false)
  const DEFAULT_CATEGORY_NAME = 'Miscellaneous'
  const DEFAULT_CATEGORY_NAME_LOWER = DEFAULT_CATEGORY_NAME.toLowerCase()

  const selectedCategory = computed(() => {
    if (!selectedCategoryId.value) return null
    return props.categories.find(cat => cat.value === selectedCategoryId.value) || null
  })

  function resolveDefaultCategoryId (): number | null {
    if (typeof props.defaultCategoryId === 'number') {
      return props.defaultCategoryId
    }
    const miscOption = props.categories.find(
      category => category.label?.toLowerCase() === DEFAULT_CATEGORY_NAME_LOWER,
    )
    if (miscOption) return miscOption.value
    return props.categories[0]?.value ?? null
  }

  watch(() => props.modelValue, open => {
    if (open) {
      productName.value = ''
      selectedCategoryId.value = resolveDefaultCategoryId()
    }
  })

  watch(
    () => [props.categories, props.defaultCategoryId] as const,
    () => {
      if (!props.modelValue) return
      const optionValues = props.categories.map(category => category.value)
      if (
        selectedCategoryId.value === null
        || !optionValues.includes(selectedCategoryId.value)
      ) {
        selectedCategoryId.value = resolveDefaultCategoryId()
      }
    },
  )

  const canCreate = computed(
    () => !!productName.value.trim() && selectedCategoryId.value !== null,
  )

  function selectCategory (category: CategoryOption) {
    selectedCategoryId.value = category.value
    isDropdownOpen.value = false
  }

  function closeDialog () {
    emit('update:modelValue', false)
    isDropdownOpen.value = false
  }

  function createProduct () {
    if (!canCreate.value) return
    emit('create-product', {
      name: productName.value.trim(),
      categoryId: selectedCategoryId.value,
    })
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
        <h2 class="dialog-title">Add Product</h2>
        <button class="close-button" @click="closeDialog">
          <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z" />
          </svg>
        </button>
      </div>

      <!-- Dialog Body -->
      <div class="dialog-body">
        <div class="form-field">
          <label class="field-label">Product Name</label>
          <input
            v-model="productName"
            class="text-input"
            placeholder="Enter product name"
            type="text"
            @keyup.enter="createProduct"
          >
        </div>

        <div class="form-field">
          <label class="field-label">Category</label>
          <div class="custom-select-container">
            <div class="custom-select" @click="isDropdownOpen = !isDropdownOpen">
              <div class="selected-option">
                <template v-if="selectedCategory">
                  <v-icon class="option-icon" :icon="selectedCategory.icon" />
                  <span class="option-label">{{ selectedCategory.label }}</span>
                </template>
                <span v-else class="placeholder">Select a category...</span>
              </div>
              <svg class="dropdown-arrow" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M7,10L12,15L17,10H7Z" />
              </svg>
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

      <!-- Dialog Footer -->
      <div class="dialog-footer">
        <button class="cancel-button" @click="closeDialog">Cancel</button>
        <StandardButton :disabled="!canCreate" icon="mdi-plus" title="Add" @click="createProduct" />
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

.text-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.2s ease;
}

.text-input:focus {
  border-color: #4CAF50;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
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
  width: 20px;
  height: 20px;
  fill: #666;
  transition: transform 0.2s ease;
}

.dropdown-options {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 4px;
  background: white;
  border: 2px solid #4CAF50;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 2300;
  max-height: 300px;
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
}

.dropdown-option:first-child {
  border-top-left-radius: 6px;
  border-top-right-radius: 6px;
}

.dropdown-option:last-child {
  border-bottom-left-radius: 6px;
  border-bottom-right-radius: 6px;
}

.dialog-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e0e0e0;
  background-color: #fafafa;
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
  background-color: #f5f5f5;
  border-color: #999;
}

/* Dark Mode Styles */
.v-theme--dark .dialog-container {
  background: rgb(var(--v-theme-surface));
  color: var(--text-primary);
}

.v-theme--dark .dialog-header {
  border-bottom: 1px solid var(--primary-lightgrey);
}

.v-theme--dark .dialog-title {
  color: var(--text-primary);
}

.v-theme--dark .close-button:hover {
  background-color: rgba(255, 255, 255, 0.08);
}

.v-theme--dark .close-button svg {
  fill: var(--text-secondary);
}

.v-theme--dark .field-label {
  color: var(--text-primary);
}

.v-theme--dark .text-input {
  background: var(--primary-lightgrey);
  color: var(--text-primary);
  border-color: var(--primary-lightgrey);
}

.v-theme--dark .text-input:focus {
  border-color: var(--primary-green);
}

.v-theme--dark .custom-select {
  background: var(--primary-lightgrey);
  border-color: var(--primary-lightgrey);
  color: var(--text-primary);
}

.v-theme--dark .custom-select:hover {
  border-color: var(--primary-green);
}

.v-theme--dark .option-label {
  color: var(--text-primary);
}

.v-theme--dark .placeholder {
  color: var(--text-secondary);
}

.v-theme--dark .dropdown-arrow {
  fill: var(--text-secondary);
}

.v-theme--dark .dropdown-options {
  background: var(--primary-lightgrey);
  border-color: var(--primary-green);
}

.v-theme--dark .dropdown-option:hover {
  background-color: rgba(102, 187, 106, 0.2);
}

.v-theme--dark .dialog-footer {
  border-top: 1px solid var(--primary-lightgrey);
  background-color: rgb(var(--v-theme-background));
}

.v-theme--dark .cancel-button {
  background: var(--primary-lightgrey);
  color: var(--text-primary);
  border-color: var(--primary-lightgrey);
}

.v-theme--dark .cancel-button:hover {
  background-color: rgba(255, 255, 255, 0.08);
  border-color: var(--text-secondary);
}
</style>
