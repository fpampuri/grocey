<template>
  <div class="product-card">
    <div class="checkbox">
      <v-checkbox v-model="selected" density="compact" hide-details />
    </div>
    <div class="info">
      <h4>{{ name }}</h4>
      <p>{{ quantity }} pcs</p>
    </div>
    <div class="actions">
      <v-btn icon>
        <v-icon>mdi-pencil</v-icon>
      </v-btn>
      <v-btn icon>
        <v-icon>mdi-delete</v-icon>
      </v-btn>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, ref } from 'vue';
 
const props = defineProps<{ 
  name: string; 
  quantity?: number;
}>();
const selected = ref(false);
</script>

<style scoped>
.product-card {
  border: 1px solid #e0e0e0;
  border-radius: 16px;
  padding: 12px;
  display: flex;
  align-items: center;
  background-color: white;
}
.checkbox {
  flex-shrink: 0;
}
.info {
  flex: 1;
  margin: 0 12px;
}
.info h4 {
  margin: 0;
  font-size: 1rem;
}
.info p {
  margin: 4px 0 0;
  color: #757575;
}
.actions {
  display: flex;
  gap: 8px;
}
</style>
