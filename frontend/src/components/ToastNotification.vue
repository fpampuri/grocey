<template>
  <v-snackbar
    v-model="isVisible"
    :color="type === 'success' ? 'success' : 'error'"
    location="bottom right"
    :timeout="timeout"
    variant="elevated"
    @update:model-value="handleVisibilityChange"
  >
    <v-icon 
      :icon="type === 'success' ? 'mdi-check-circle' : 'mdi-alert-circle'"
      class="mr-2"
    />
    {{ message }}
    
    <template v-slot:actions>
      <v-btn
        icon="mdi-close"
        color="white"
        variant="text"
        size="small"
        @click="close"
      />
    </template>
  </v-snackbar>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';

interface Props {
  modelValue: boolean;
  message: string;
  type?: 'success' | 'error';
  timeout?: number;
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void;
}

const props = withDefaults(defineProps<Props>(), {
  type: 'success',
  timeout: 2000,
});

const emit = defineEmits<Emits>();

const isVisible = ref(props.modelValue);

watch(() => props.modelValue, (newValue) => {
  isVisible.value = newValue;
});

function handleVisibilityChange(value: boolean) {
  isVisible.value = value;
  emit('update:modelValue', value);
}

function close() {
  handleVisibilityChange(false);
}
</script>