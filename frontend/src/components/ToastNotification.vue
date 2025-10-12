<template>
  <v-snackbar
    v-model="isVisible"
    :class="toastClass"
    location="bottom right"
    :timeout="timeout"
    variant="elevated"
    @update:model-value="handleVisibilityChange"
  >
    <v-icon
      class="mr-2"
      :icon="type === 'success' ? 'mdi-check-circle' : 'mdi-alert-circle'"
    />
    {{ message }}
  </v-snackbar>
</template>

<script setup lang="ts">
  import { computed, ref, watch } from 'vue'

  interface Props {
    modelValue: boolean
    message: string
    type?: 'success' | 'error'
    timeout?: number
  }

  interface Emits {
    (e: 'update:modelValue', value: boolean): void
  }

  const props = withDefaults(defineProps<Props>(), {
    type: 'success',
    timeout: 2000,
  })

  const emit = defineEmits<Emits>()

  const isVisible = ref(props.modelValue)

  const toastClass = computed(() => {
    return props.type === 'success' ? 'toast-success' : 'toast-error'
  })

  watch(() => props.modelValue, newValue => {
    isVisible.value = newValue
  })

  function handleVisibilityChange (value: boolean) {
    isVisible.value = value
    emit('update:modelValue', value)
  }
</script>

<style scoped>
/* Success toast using custom green color */
.toast-success :deep(.v-snackbar__wrapper),
.toast-success :deep(.v-snackbar__content) {
  background-color: var(--primary-green-light) !important;
  color: white !important;
}

/* Error toast using custom red color */
.toast-error :deep(.v-snackbar__wrapper),
.toast-error :deep(.v-snackbar__content) {
  background-color: var(--delete-red) !important;
  color: white !important;
}

/* Ensure icons are visible */
.toast-success :deep(.v-icon),
.toast-error :deep(.v-icon) {
  color: white !important;
}

/* Additional targeting for Vuetify v3 structure */
.toast-success :deep(.v-overlay__content),
.toast-error :deep(.v-overlay__content) {
  background-color: inherit !important;
}
</style>
