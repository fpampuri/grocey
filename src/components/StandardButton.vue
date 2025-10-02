<script setup lang="ts">
  import { defineProps, defineEmits } from "vue";

    const props = defineProps({
      title: { type: String, required: true },
      icon: { type: String, default: "mdi-plus" },
      disabled: { type: Boolean, default: false },
    });
    
    const emits = defineEmits(["click"]);


  function handleClick() {
    if (!props.disabled) {
      emits("click");
    }
  }
</script>

<template>
  <button
    @click="handleClick"
    class="standard-button"
    :disabled="disabled"
  >
    <v-icon v-if="icon" :icon="icon" class="button-icon" />
    <span class="button-text">{{ title }}</span>
  </button>
</template>

<style scoped>
.standard-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 12px;
  border: none;
  font-weight: 600;
  font-size: 16px;
  text-transform: none;
  min-width: 120px;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  
  background-color: var(--primary-green-light);
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.standard-button:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  transform: translateY(-1px);
}

.standard-button:active {
  transform: translateY(1px);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.button-icon {
  font-size: 22px;
}

.standard-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.standard-button:disabled:hover {
  background-color: #ccc;
  transform: none;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}
</style>