<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { defineProps, defineEmits } from 'vue';
import StandardButton from '@/components/StandardButton.vue';

type CategoryOption = { value: number; label: string; icon?: string };

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  categories: { type: Array as () => CategoryOption[], default: () => [] },
});

const emit = defineEmits(['update:modelValue', 'create-product']);

const productName = ref('');
const selectedCategoryId = ref<number | null>(null);

watch(() => props.modelValue, (open) => {
  if (open) {
    productName.value = '';
    selectedCategoryId.value = props.categories[0]?.value ?? null;
  }
});

const canCreate = computed(() => !!productName.value.trim() && selectedCategoryId.value !== null);

function closeDialog() {
  emit('update:modelValue', false);
}

function createProduct() {
  if (!canCreate.value) return;
  emit('create-product', {
    name: productName.value.trim(),
    categoryId: selectedCategoryId.value,
  });
  closeDialog();
}
</script>

<template>
  <div v-if="modelValue" class="dialog-backdrop" @click="closeDialog">
    <div class="dialog-container" @click.stop>
      <div class="dialog-header">
        <h2 class="dialog-title">Add Product</h2>
        <button class="close-button" @click="closeDialog">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
            <path d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z" />
          </svg>
        </button>
      </div>

      <div class="dialog-body">
        <div class="form-field">
          <label class="field-label">Product Name</label>
          <input
            v-model="productName"
            type="text"
            placeholder="Enter product name"
            class="text-input"
            @keyup.enter="createProduct"
          />
        </div>

        <div class="form-field">
          <label class="field-label">Category</label>
          <div class="select">
            <select v-model.number="selectedCategoryId" class="select-input">
              <option v-for="cat in categories" :key="cat.value" :value="cat.value">
                {{ cat.label }}
              </option>
            </select>
          </div>
        </div>
      </div>

      <div class="dialog-footer">
        <button @click="closeDialog" class="cancel-button">Cancel</button>
        <StandardButton title="Add" icon="mdi-plus" :disabled="!canCreate" @click="createProduct" />
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
  z-index: 1000;
}

.dialog-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  width: 90%;
  max-width: 480px;
  max-height: 90vh;
  overflow: hidden;
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
}

.form-field { margin-bottom: 20px; }
.field-label { display: block; font-weight: 600; margin-bottom: 8px; color: #333; }

.text-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #4CAF50;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.2s ease;
}
.text-input:focus { border-color: #388E3C; box-shadow: 0 0 0 3px rgba(76,175,80,0.1); }

.select-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.2s ease;
  background: white;
}
.select-input:focus { border-color: #4CAF50; }

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
.cancel-button:hover { background-color: #f5f5f5; border-color: #999; }
</style>