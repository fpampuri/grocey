<template>
  <v-hover v-slot="{ isHovering, props }">
    <v-btn
      v-bind="props"
      icon
      :rounded="rounded"
      :class="[isHovering ? hoverClass : '']"
      :style="{ opacity: isHovering ? hoverOpacity : 1, transition: 'background-color 150ms ease, color 150ms ease, opacity 150ms ease' }"
      @click="$emit('click')"
    >
      <v-icon
        :class="[iconClass, isHovering ? 'text-white' : iconColor]"
        :style="{ transition: 'color 150ms ease' }"
      >
        {{ icon }}
      </v-icon>
    </v-btn>
  </v-hover>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'

const props = defineProps({
  icon: { type: String, required: true },
  rounded: { type: [String, Boolean], default: '3xl' },
  hoverClass: { type: String, default: 'bg-green' },
  iconClass: { type: String, default: 'text-h4' },
  iconColor: { type: String, default: 'text-green' },
  hoverOpacity: { type: Number, default: 0.75 }
})
</script>