<template>
  <v-hover v-slot="{ isHovering, props }">
    <v-btn
      v-bind="props"
      :class="['nav-btn', isHovering ? hoverClass : '', { 'nav-btn--hover': isHovering }]"
      icon
      :rounded="rounded"
      @click="$emit('click')"
    >
      <v-icon
        :class="['nav-btn-icon', iconClass, isHovering ? 'text-white' : iconColor]"
      >
        {{ icon }}
      </v-icon>
    </v-btn>
  </v-hover>
</template>

<script setup lang="ts">
  import { computed } from 'vue'

  const props = defineProps({
    icon: { type: String, required: true },
    rounded: { type: [String, Boolean], default: '3xl' },
    hoverClass: { type: String, default: 'bg-green' },
    iconClass: { type: String, default: 'text-h4' },
    iconColor: { type: String, default: 'text-green' },
    hoverOpacity: { type: Number, default: 0.75 },
  })

  const hoverOpacityCss = computed(() => props.hoverOpacity ?? 0.75)
</script>

<style scoped>
.nav-btn {
  transition: background-color 150ms ease, color 150ms ease, opacity 150ms ease;
  opacity: 1;
}

.nav-btn--hover {
  opacity: v-bind(hoverOpacityCss);
}

.nav-btn-icon {
  transition: color 150ms ease;
}

/* Define color classes using CSS custom properties */
.bg-green {
  background-color: var(--primary-green) !important;
}

.text-green {
  color: var(--primary-green) !important;
}

.text-white {
  color: white !important;
}
</style>
