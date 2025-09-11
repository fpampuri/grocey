<template>
  <div class="list-grid">
    <ListCard
      v-for="(list, index) in lists"
      :key="index"
      :title="list.title"
      :icon="list.icon"
      :items="list.items"
    />
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue';

const props = defineProps<{
  lists: Array<{ title: string; icon: string; items: number }>;
}>();
</script>

<style scoped>
.list-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}
</style>
