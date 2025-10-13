<script setup lang="ts">
  import { nextTick, ref, watch } from 'vue'

  const props = defineProps({
    title: { type: String, required: true },
    quantity: { type: Number, default: 1 },
    unit: { type: String, default: 'unit' },
    id: { type: [String, Number], required: true },
  })

  const emits = defineEmits(['update-quantity', 'move-to-pantry', 'rename', 'delete'])

  const localQuantity = ref(props.quantity)
  const isEditingQuantity = ref(false)
  const isEditingName = ref(false)
  const quantityInput = ref(props.quantity)
  const nameInput = ref(props.title)
  const quantityInputRef = ref<HTMLInputElement | null>(null)
  const nameInputRef = ref<HTMLInputElement | null>(null)

  // Keep local state in sync with props
  watch(() => props.quantity, v => {
    localQuantity.value = v
    quantityInput.value = v
  })
  watch(() => props.title, v => (nameInput.value = v))

  function decreaseQuantity () {
    if (localQuantity.value > 1) {
      localQuantity.value--
      quantityInput.value = localQuantity.value
      emits('update-quantity', localQuantity.value)
    }
  }

  function increaseQuantity () {
    localQuantity.value++
    quantityInput.value = localQuantity.value
    emits('update-quantity', localQuantity.value)
  }

  function moveToPantry () {
    emits('move-to-pantry', props.id)
  }

  function deleteItem () {
    emits('delete', props.id)
  }

  function startEditingQuantity () {
    isEditingQuantity.value = true
    nextTick(() => {
      if (quantityInputRef.value) {
        quantityInputRef.value.focus()
        quantityInputRef.value.select()
      }
    })
  }

  function finishEditingQuantity () {
    isEditingQuantity.value = false
    const newQuantity = Math.max(1, Number.parseInt(quantityInput.value.toString()) || 1)
    localQuantity.value = newQuantity
    quantityInput.value = newQuantity
    emits('update-quantity', newQuantity)
  }

  function onQuantityKeydown (event: KeyboardEvent) {
    if (event.key === 'Enter') {
      finishEditingQuantity()
    }
  }

  function onQuantityFocus (event: FocusEvent) {
    const target = event.target as HTMLInputElement | null
    target?.select()
  }

  function startEditingName () {
    isEditingName.value = true
    nextTick(() => {
      if (nameInputRef.value) {
        nameInputRef.value.focus()
        nameInputRef.value.select()
      }
    })
  }

  function finishEditingName () {
    isEditingName.value = false
    const newName = nameInput.value.trim()
    if (newName && newName !== props.title) {
      emits('rename', { id: props.id, name: newName })
    } else {
      nameInput.value = props.title // Revert if empty or unchanged
    }
  }

  function onNameKeydown (event: KeyboardEvent) {
    if (event.key === 'Enter') {
      finishEditingName()
    } else if (event.key === 'Escape') {
      nameInput.value = props.title
      isEditingName.value = false
    }
  }
</script>

<template>
  <v-card class="pantry-item-card" outlined>
    <v-row align="center" class="item-row" no-gutters>
      <!-- Item title (editable on click) -->
      <v-col>
        <div v-if="!isEditingName" class="item-title" @click="startEditingName">
          {{ title }}
        </div>
        <input
          v-else
          ref="nameInputRef"
          v-model="nameInput"
          class="name-input"
          type="text"
          @blur="finishEditingName"
          @keydown="onNameKeydown"
        >
      </v-col>

      <!-- Quantity controls -->
      <v-col cols="auto">
        <div class="quantity-controls">
          <!-- Decrease button -->
          <button
            aria-label="decrease quantity"
            class="quantity-btn decrease-btn"
            :disabled="localQuantity <= 1"
            @click="decreaseQuantity"
          >
            <v-icon icon="mdi-minus" size="small" />
          </button>

          <!-- Editable quantity display/input -->
          <div class="quantity-container">
            <span
              v-if="!isEditingQuantity"
              class="quantity-display"
              @click="startEditingQuantity"
            >
              {{ localQuantity }}
            </span>
            <input
              v-else
              ref="quantityInputRef"
              v-model="quantityInput"
              class="quantity-input"
              min="1"
              type="number"
              @blur="finishEditingQuantity"
              @focus="onQuantityFocus"
              @keydown="onQuantityKeydown"
            >
          </div>

          <!-- Increase button -->
          <button
            aria-label="increase quantity"
            class="quantity-btn increase-btn"
            @click="increaseQuantity"
          >
            <v-icon icon="mdi-plus" size="small" />
          </button>

          <!-- Move to pantry button -->
          <button
            aria-label="move to another pantry"
            class="move-btn"
            @click="moveToPantry"
          >
            <v-icon icon="mdi-swap-horizontal" size="small" />
          </button>

          <!-- Delete button -->
          <button
            aria-label="delete item"
            class="delete-btn"
            @click="deleteItem"
          >
            <v-icon icon="mdi-delete" size="small" />
          </button>
        </div>
      </v-col>
    </v-row>
  </v-card>
</template>

<style scoped>
.pantry-item-card {
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-sizing: border-box;
  background: var(--v-surface, #fff);
  border: 1px solid var(--v-border-color, #e0e0e0);
  transition: box-shadow 0.2s ease;
}

.pantry-item-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.item-row {
  gap: 16px;
}

.item-title {
  font-weight: 500;
  font-size: 1rem;
  color: var(--text-primary);
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.item-title:hover {
  background-color: rgba(76, 175, 80, 0.1);
}

.name-input {
  font-weight: 500;
  font-size: 1rem;
  width: 100%;
  border: 2px solid var(--primary-green, #4CAF50);
  border-radius: 4px;
  padding: 4px 8px;
  background: white;
  color: var(--text-primary);
  outline: none;
}

.name-input:focus {
  border-color: var(--primary-green, #4CAF50);
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quantity-btn {
  background: none;
  border: 1px solid var(--v-border-color, #e0e0e0);
  border-radius: 6px;
  width: 32px;
  height: 32px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  color: var(--text-primary);
}

.quantity-btn:hover:not(:disabled) {
  background-color: var(--primary-green, #4CAF50);
  color: white;
  border-color: var(--primary-green, #4CAF50);
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-container {
  min-width: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quantity-display {
  font-weight: 600;
  font-size: 1rem;
  min-width: 20px;
  text-align: center;
  color: var(--text-primary);
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.quantity-display:hover {
  background-color: var(--primary-green);
  color: white;
}

.quantity-input {
  font-weight: 600;
  font-size: 1rem;
  width: 50px;
  text-align: center;
  border: 2px solid var(--primary-green, #4CAF50);
  border-radius: 4px;
  padding: 4px 8px;
  background: white;
  color: var(--text-primary);
  outline: none;
}

/* Hide number input spinner arrows */
.quantity-input::-webkit-outer-spin-button,
.quantity-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.quantity-input:focus {
  border-color: var(--primary-green, #4CAF50);
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.move-btn {
  background: none;
  border: 1px solid var(--primary-green, #4CAF50);
  border-radius: 6px;
  width: 32px;
  height: 32px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  color: var(--primary-green, #4CAF50);
  margin-left: 4px;
}

.move-btn:hover {
  background-color: var(--primary-green, #4CAF50);
  color: white;
}

.delete-btn {
  background: none;
  border: 1px solid var(--delete-red);
  border-radius: 6px;
  width: 32px;
  height: 32px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  color: var(--delete-red);
  margin-left: 4px;
}

.delete-btn:hover {
  background-color: var(--delete-red);
  color: white;
}
</style>
