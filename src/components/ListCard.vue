<template>
  <div class="list-card">
    <div class="icon">
      <v-icon size="24">{{ icon }}</v-icon>
    </div>
    <div class="details">
      <h3>{{ title }}</h3>
      <p>{{ items }} items</p>
    </div>
    <div class="actions">
      <v-btn icon>
        <v-icon>mdi-dots-vertical</v-icon>
      </v-btn>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue';
 
const props = defineProps<{ 
  title: string; 
  icon: string; 
  items?: number;
}>();

</script>

<style scoped>
.list-card {
  border: 2px solid #00C853;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: white;
}
.icon {
  flex-shrink: 0;
}
.details {
  flex: 1;
  margin: 0 16px;
}
.details h3 {
  margin: 0;
  font-size: 1.25rem;
}
.details p {
  margin: 4px 0 0;
  color: #607d8b;
}
.actions {
  flex-shrink: 0;
}
</style>
