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
      :class="['py-2 mx-2 my-3 nav-item', (selected || isHovering) ? 'bg-green text-white' : '']"
      :style="{ opacity: (isHovering && !selected) ? 0.6 : 1 }"
    >
      <v-row align="center">
        <v-icon :class="['text-h4 ma-4', (selected || isHovering) ? 'text-white' : 'text-green']">
          {{ icon }}
        </v-icon>
        <v-list-item-title :class="['text-h5 font-weight-bold', (selected || isHovering) ? 'text-white' : '']">
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