<template>
  <div class="product-grid">
    <ProductCard
      v-for="(product, index) in products"
      :key="index"
      :name="product.name"
      :quantity="product.quantity"
    />
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue';

const props = defineProps<{
  products: Array<{ name: string; quantity?: number }>;
}>();
</script>

<style scoped>
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}
</style>
