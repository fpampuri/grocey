<script setup lang="ts">


  const props = defineProps({
    icon: { type: String, required: true },
    title: { type: String, required: true },
    selected: { type: Boolean, default: false },
    to: { type: [String, Object], default: null }
  })

  const emits = defineEmits(['click'])

  const onClick = () => emits('click')
</script>

<template>
  <v-hover v-slot="{ isHovering, props }">
    <v-list-item
      v-bind="props"
      link
      rounded="lg"
      @click="onClick"
      :to="to"
      :class="['py-2 mx-2 my-3 nav-item', (selected || isHovering) ? 'selected' : '']"
      :style="{ opacity: (isHovering && !selected) ? 0.6 : 1 }"
    >
      <v-row align="center">
        <v-icon :class="['text-h4 ma-4', (selected || isHovering) ? 'icon-selected' : 'icon-default']">
          {{ icon }}
        </v-icon>
        <v-list-item-title :class="['text-h5 font-weight-bold', (selected || isHovering) ? 'text-selected' : '']">
          {{ title }}
        </v-list-item-title>
      </v-row>
    </v-list-item>
  </v-hover>
</template>

<style scoped>
.nav-item {
  transition: background-color 150ms ease, color 150ms ease, opacity 150ms ease;
}
.nav-item .v-icon,
.nav-item .v-list-item-title {
  transition: color 150ms ease;
}

/* Selected state styling */
.nav-item.selected {
  background-color: var(--primary-green) !important;
}

/* Icon styling */
.icon-default {
  color: var(--primary-green) !important;
}

.icon-selected {
  color: white !important;
}

/* Text styling */
.text-selected {
  color: white !important;
}
</style>