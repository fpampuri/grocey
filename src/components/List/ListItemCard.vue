<script setup lang="ts">
import { defineProps, defineEmits, ref, watch, nextTick } from "vue";

const props = defineProps({
  title: { type: String, required: true },
  quantity: { type: Number, default: 1 },
  completed: { type: Boolean, default: false },
  id: { type: [String, Number], required: true }
});

const emits = defineEmits(["update-quantity", "toggle-complete", "delete"]);

const localQuantity = ref(props.quantity);
const localCompleted = ref(props.completed);
const isEditingQuantity = ref(false);
const quantityInput = ref(props.quantity);
const quantityInputRef = ref<HTMLInputElement | null>(null);

// Keep local state in sync with props
watch(() => props.quantity, (v) => {
  localQuantity.value = v;
  quantityInput.value = v;
});
watch(() => props.completed, (v) => (localCompleted.value = v));

function decreaseQuantity() {
  if (localQuantity.value > 1) {
    localQuantity.value--;
    quantityInput.value = localQuantity.value;
    emits("update-quantity", localQuantity.value);
  }
}

function increaseQuantity() {
  localQuantity.value++;
  quantityInput.value = localQuantity.value;
  emits("update-quantity", localQuantity.value);
}

function toggleComplete() {
  localCompleted.value = !localCompleted.value;
  emits("toggle-complete", localCompleted.value);
}

function deleteItem() {
  emits("delete", props.id);
}

function startEditingQuantity() {
  isEditingQuantity.value = true;
  nextTick(() => {
    if (quantityInputRef.value) {
      quantityInputRef.value.focus();
      quantityInputRef.value.select();
    }
  });
}

function finishEditingQuantity() {
  isEditingQuantity.value = false;
  const newQuantity = Math.max(1, parseInt(quantityInput.value.toString()) || 1);
  localQuantity.value = newQuantity;
  quantityInput.value = newQuantity;
  emits("update-quantity", newQuantity);
}

function onQuantityKeydown(event: KeyboardEvent) {
  if (event.key === 'Enter') {
    finishEditingQuantity();
  }
}
</script>

<template>
  <v-card class="list-item-card" outlined>
    <v-row class="item-row" align="center" no-gutters>
      <!-- Completion checkbox -->
      <v-col cols="auto">
        <v-checkbox
          v-model="localCompleted"
          hide-details
          color="green"
          class="green-border-checkbox"
          aria-label="mark item as completed"
          @click.stop="toggleComplete"
        />
      </v-col>
      
      <!-- Item title -->
      <v-col>
        <div 
          class="item-title"
          :class="{ 'completed': localCompleted }"
        >
          {{ title }}
        </div>
      </v-col>

      <!-- Quantity controls -->
      <v-col cols="auto">
        <div class="quantity-controls">
          <!-- Decrease button -->
          <button 
            class="quantity-btn decrease-btn" 
            @click="decreaseQuantity"
            :disabled="localQuantity <= 1"
            aria-label="decrease quantity"
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
              v-model="quantityInput"
              class="quantity-input"
              type="number"
              min="1"
              @blur="finishEditingQuantity"
              @keydown="onQuantityKeydown"
              @focus="$event.target.select()"
              ref="quantityInputRef"
            />
          </div>

          <!-- Increase button -->
          <button 
            class="quantity-btn increase-btn" 
            @click="increaseQuantity"
            aria-label="increase quantity"
          >
            <v-icon icon="mdi-plus" size="small" />
          </button>

          <!-- Delete button -->
          <button 
            class="delete-btn" 
            @click="deleteItem"
            aria-label="delete item"
          >
            <v-icon icon="mdi-delete" size="small" />
          </button>
        </div>
      </v-col>
    </v-row>
  </v-card>
</template>

<style scoped>
    .green-border-checkbox :deep(.mdi-checkbox-blank-outline):before {
    color: var(--primary-green) !important;
    }

    .list-item-card {
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 12px;
    box-sizing: border-box;
    background: var(--v-surface, #fff);
    border: 1px solid var(--v-border-color, #e0e0e0);
    transition: box-shadow 0.2s ease;
    }

    .list-item-card:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .item-row {
    gap: 16px;
    }

    .item-title {
    font-weight: 500;
    font-size: 1rem;
    color: var(--text-primary);
    transition: all 0.2s ease;
    }

    .item-title.completed {
    text-decoration: line-through;
    color: var(--text-secondary);
    opacity: 0.7;
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
    background-color: var(--primary-green, #1976d2);
    color: white;
    border-color: var(--primary-green, #1976d2);
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
    border: 2px solid var(--v-theme-primary, #1976d2);
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
    border-color: var(--v-theme-primary, #1976d2);
    box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.2);
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
