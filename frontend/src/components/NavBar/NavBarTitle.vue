<script setup lang="ts">

  const { icon, title } = defineProps({
    icon: { type: String, required: true },
    title: { type: String, required: true },
  })

</script>

<template>
  <!-- Compact header (do not occupy full height) -->
  <div>
    <v-sheet class="px-4 pt-4 pb-2 d-flex align-center" color="transparent" elevation="0">
      <v-icon class="text-h3 mr-2 text-green">{{ icon }}</v-icon>
      <span class="text-h4 font-weight-bold">{{ title }}</span>
    </v-sheet>
    <v-divider class="mb-2" />
  </div>
</template>

<style scoped>
.text-green {
  color: var(--primary-green) !important;
}
</style>
