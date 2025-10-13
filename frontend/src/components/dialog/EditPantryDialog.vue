<script setup lang="ts">
  import { ref, watch } from 'vue'
  import StandardButton from '@/components/StandardButton.vue'

  interface PantryData {
    id: number
    title: string
    icon: string
  }

  const props = defineProps({
    modelValue: { type: Boolean, default: false },
    pantryData: { type: Object as () => PantryData | null, default: null },
  })

  const emit = defineEmits(['update:modelValue', 'edit-pantry'])

  // Form data
  const pantryName = ref('')
  const selectedIcon = ref('mdi-fridge')

  // Available icons for selection
  const iconOptions = [
    'mdi-fridge',
    'mdi-fridge-outline',
    'mdi-fridge-variant',
    'mdi-fridge-variant-outline',
    'mdi-home',
    'mdi-store',
    'mdi-warehouse',
    'mdi-cupboard',
    'mdi-basket',
    'mdi-cart',
    'mdi-apple',
    'mdi-food-apple',
    'mdi-food',
    'mdi-carrot',
    'mdi-cheese',
    'mdi-fish',
    'mdi-egg',
    'mdi-bread-slice',
    'mdi-baguette',
    'mdi-grain',
    'mdi-pasta',
    'mdi-rice',
    'mdi-bottle-wine',
    'mdi-coffee',
    'mdi-tea',
    'mdi-beer',
    'mdi-glass-cocktail',
    'mdi-cupcake',
    'mdi-cake',
    'mdi-ice-cream',
    'mdi-candy',
    'mdi-fruit-cherries',
    'mdi-fruit-grapes',
    'mdi-fruit-watermelon',
    'mdi-leaf',
    'mdi-tree',
    'mdi-sprout',
    'mdi-flower',
    'mdi-microwave',
    'mdi-silverware-fork-knife',
    'mdi-grill',
    'mdi-pot',
    'mdi-package',
    'mdi-package-variant',
    'mdi-snowflake',
  ]

  // Watch for changes in pantryData prop to populate form
  watch(
    () => props.pantryData,
    newData => {
      if (newData) {
        pantryName.value = newData.title
        selectedIcon.value = newData.icon
      }
    },
    { immediate: true },
  )

  function closeDialog () {
    emit('update:modelValue', false)
    // Reset form
    pantryName.value = ''
    selectedIcon.value = 'mdi-fridge'
    iconOptionsOpen.value = false
  }

  function editPantry () {
    if (!pantryName.value.trim() || !props.pantryData) {
      return // Don't edit if name is empty or no pantry data
    }

    emit('edit-pantry', {
      id: props.pantryData.id,
      name: pantryName.value.trim(),
      icon: selectedIcon.value,
    })

    closeDialog()
  }

  function selectIcon (iconValue: string) {
    selectedIcon.value = iconValue
    iconOptionsOpen.value = false
  }

  // Toggle icon options on click instead of hover
  const iconOptionsOpen = ref(false)
</script>

<template>
  <!-- Dialog Backdrop -->
  <div v-if="modelValue" class="dialog-backdrop" @click="closeDialog">
    <!-- Dialog Content -->
    <div class="dialog-container" @click.stop>
      <!-- Dialog Header -->
      <div class="dialog-header">
        <h2 class="dialog-title">Edit Pantry</h2>
        <button class="close-button" @click="closeDialog">
          <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z" />
          </svg>
        </button>
      </div>

      <!-- Dialog Body -->
      <div class="dialog-body">

        <!-- Pantry Name Field -->
        <div class="form-field">
          <label class="field-label">Pantry Name</label>
          <input
            v-model="pantryName"
            class="text-input"
            placeholder="Enter pantry name"
            type="text"
            @keyup.enter="editPantry"
          >
        </div>

        <!-- Icon Selection -->
        <div class="form-field">
          <label class="field-label">Icon</label>
          <div class="icon-selector">
            <div class="selected-icon" @click="iconOptionsOpen = !iconOptionsOpen">
              <v-icon :icon="selectedIcon" size="24" />
              <svg class="dropdown-arrow" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M7,10L12,15L17,10H7Z" />
              </svg>
            </div>

            <!-- Icon Grid -->
            <div v-if="iconOptionsOpen" class="icon-options">
              <button
                v-for="iconValue in iconOptions"
                :key="iconValue"
                class="icon-option"
                :class="{ active: selectedIcon === iconValue }"
                @click="selectIcon(iconValue)"
              >
                <v-icon :icon="iconValue" size="24" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Dialog Footer -->
      <div class="dialog-footer">
        <button class="cancel-button" @click="closeDialog">
          Cancel
        </button>
        <StandardButton
          icon="mdi-content-save"
          title="Save Changes"
          @click="editPantry"
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

.dialog-description {
  color: #666;
  margin-bottom: 24px;
  line-height: 1.5;
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
  border-color: var(--primary-green);
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
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

.dropdown-arrow {
  width: 16px;
  height: 16px;
  fill: #666;
  margin-left: auto;
}

.icon-options {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 4px;
  background: white;
  border: 2px solid #4CAF50;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 2201;
  max-height: 300px;
  overflow-y: auto;
  padding: 12px;
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 8px;
}

.icon-option {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  aspect-ratio: 1;
  padding: 8px;
  border: 2px solid transparent;
  border-radius: 8px;
  background: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.icon-option:hover {
  background-color: #f5f5f5;
  border-color: var(--primary-green);
}

.icon-option.active {
  background-color: #e8f5e8;
  border-color: var(--primary-green);
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
