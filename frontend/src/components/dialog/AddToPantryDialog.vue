<script setup lang="ts">
  import { computed, ref, watch } from 'vue'
  import StandardButton from '@/components/StandardButton.vue'

  export interface PantryOption {
    value: number
    label: string
    icon: string
  }

  const props = defineProps<{
    modelValue: boolean
    pantries: PantryOption[]
    productName?: string
  }>()

  const emit = defineEmits(['update:modelValue', 'add-to-pantry'])

  const selectedPantryId = ref<number | null>(null)
  const quantity = ref(1)

  // Auto-select first pantry when dialog opens
  watch(() => props.modelValue, (newVal) => {
    if (newVal && props.pantries.length > 0 && !selectedPantryId.value) {
      selectedPantryId.value = props.pantries[0].value
    }
  })

  // Dropdown state
  const dropdownOpen = ref(false)

  function selectPantry (pantryId: number) {
    selectedPantryId.value = pantryId
    dropdownOpen.value = false
  }

  const selectedPantry = computed(() => {
    if (!selectedPantryId.value) return null
    return props.pantries.find(p => p.value === selectedPantryId.value)
  })

  function closeDialog () {
    emit('update:modelValue', false)
    // Reset after animation
    setTimeout(() => {
      selectedPantryId.value = null
      quantity.value = 1
      dropdownOpen.value = false
    }, 200)
  }

  function confirmAdd () {
    if (!selectedPantryId.value || quantity.value <= 0) return

    emit('add-to-pantry', {
      pantryId: selectedPantryId.value,
      quantity: quantity.value,
    })

    closeDialog()
  }

  function incrementQuantity () {
    quantity.value++
  }

  function decrementQuantity () {
    if (quantity.value > 1) {
      quantity.value--
    }
  }
</script>

<template>
  <!-- Dialog Backdrop -->
  <div v-if="modelValue" class="dialog-backdrop" @click="closeDialog">
    <!-- Dialog Content -->
    <div class="dialog-container" @click.stop>
      <!-- Dialog Header -->
      <div class="dialog-header">
        <h2 class="dialog-title">Add to Pantry</h2>
        <button aria-label="Close dialog" class="close-btn" @click="closeDialog">
          <v-icon icon="mdi-close" />
        </button>
      </div>

      <!-- Dialog Body -->
      <div class="dialog-body">
        <p v-if="productName" class="product-name">{{ productName }}</p>

        <!-- Pantry Selection -->
        <div class="form-group">
          <label class="form-label">Select Pantry</label>
          <div class="custom-dropdown">
            <button
              class="dropdown-toggle"
              type="button"
              @click="dropdownOpen = !dropdownOpen"
            >
              <div class="dropdown-display">
                <v-icon
                  v-if="selectedPantry"
                  :icon="selectedPantry.icon"
                  class="pantry-icon"
                  size="20"
                />
                <span>{{ selectedPantry?.label || 'Select a pantry' }}</span>
              </div>
              <v-icon
                :class="{ 'rotate': dropdownOpen }"
                class="dropdown-arrow"
                icon="mdi-chevron-down"
              />
            </button>

            <div v-if="dropdownOpen" class="dropdown-menu">
              <button
                v-for="pantry in pantries"
                :key="pantry.value"
                :class="{ 'selected': selectedPantryId === pantry.value }"
                class="dropdown-item"
                type="button"
                @click="selectPantry(pantry.value)"
              >
                <v-icon :icon="pantry.icon" class="pantry-icon" size="20" />
                <span>{{ pantry.label }}</span>
                <v-icon
                  v-if="selectedPantryId === pantry.value"
                  class="check-icon"
                  color="success"
                  icon="mdi-check"
                  size="20"
                />
              </button>
            </div>
          </div>
        </div>

        <!-- Quantity Input -->
        <div class="form-group">
          <label class="form-label">Quantity</label>
          <div class="quantity-controls">
            <button class="quantity-btn" type="button" @click="decrementQuantity">
              <v-icon icon="mdi-minus" />
            </button>
            <input
              v-model.number="quantity"
              class="quantity-input"
              min="1"
              type="number"
            >
            <button class="quantity-btn" type="button" @click="incrementQuantity">
              <v-icon icon="mdi-plus" />
            </button>
          </div>
        </div>
      </div>

      <!-- Dialog Footer -->
      <div class="dialog-footer">
        <StandardButton
          title="Cancel"
          variant="text"
          @click="closeDialog"
        />
        <StandardButton
          :disabled="!selectedPantryId || quantity <= 0"
          title="Add to Pantry"
          @click="confirmAdd"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.dialog-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2200;
  padding: 16px;
}

.dialog-container {
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  max-width: 500px;
  width: 100%;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  overflow: visible;
  position: relative;
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 24px 16px;
  border-bottom: 1px solid #e0e0e0;
}

.dialog-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0;
  color: #1a1a1a;
}

.close-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s;
  color: #666;
}

.close-btn:hover {
  background-color: #f5f5f5;
}

.dialog-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.product-name {
  font-size: 1.1rem;
  font-weight: 500;
  margin-bottom: 24px;
  color: #333;
}

.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: block;
  font-weight: 500;
  margin-bottom: 8px;
  color: #333;
  font-size: 0.95rem;
}

/* Custom Dropdown */
.custom-dropdown {
  position: relative;
  width: 100%;
}

.dropdown-toggle {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 1rem;
  transition: all 0.2s;
}

.dropdown-toggle:hover {
  border-color: #4CAF50;
}

.dropdown-toggle:focus {
  outline: none;
  border-color: #4CAF50;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.1);
}

.dropdown-display {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pantry-icon {
  color: #4CAF50;
}

.dropdown-arrow {
  transition: transform 0.2s;
}

.dropdown-arrow.rotate {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  z-index: 2300;
  max-height: 300px;
  overflow-y: auto;
}

.dropdown-item {
  width: 100%;
  padding: 12px 16px;
  border: none;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 1rem;
  text-align: left;
  transition: background-color 0.2s;
}

.dropdown-item:hover {
  background-color: #f5f5f5;
}

.dropdown-item.selected {
  background-color: rgba(76, 175, 80, 0.08);
}

.dropdown-item .check-icon {
  margin-left: auto;
}

/* Quantity Controls */
.quantity-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.quantity-btn {
  width: 40px;
  height: 40px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.quantity-btn:hover {
  background-color: #f5f5f5;
  border-color: #4CAF50;
}

.quantity-btn:active {
  transform: scale(0.95);
}

.quantity-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  text-align: center;
  max-width: 120px;
}

.quantity-input:focus {
  outline: none;
  border-color: #4CAF50;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.1);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #e0e0e0;
}

/* Remove number input arrows */
.quantity-input::-webkit-inner-spin-button,
.quantity-input::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.quantity-input[type=number] {
  -moz-appearance: textfield;
}
</style>
