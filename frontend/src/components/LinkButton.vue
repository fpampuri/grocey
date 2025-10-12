<template>
  <component
    :is="to ? 'RouterLink' : 'button'"
    :class="['link-button', { disabled }]"
    :disabled="disabled"
    :to="to"
    type="button"
    @click="handleClick"
  >
    <slot />
  </component>
</template>

<script setup lang="ts">
  interface Props {
    to?: string
    disabled?: boolean
  }

  defineProps<Props>()

  const emit = defineEmits<{
    click: []
  }>()

  function handleClick () {
    emit('click')
  }
</script>

<style scoped>
.link-button {
  color: var(--primary-green);
  font-weight: 600;
  text-decoration: none;
  transition: color 0.2s ease;
  font-size: 0.9rem;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  font-family: inherit;
}

.link-button:hover:not(.disabled) {
  color: var(--primary-green-dark);
  text-decoration: underline;
}

.link-button.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
