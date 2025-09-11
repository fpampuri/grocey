<template>
  <WebLayout>
    <section style="padding: 1rem;">
      <h2>ListCard & ListGrid</h2>
      <ListGrid :lists="demoLists" />
    </section>

    <section style="padding: 1rem;">
      <h2>ProductCard & ProductGrid</h2>
      <ProductGrid :products="demoProducts" />
    </section>
  </WebLayout>
</template>

<script setup lang="ts">
import WebLayout from '@/layouts/WebLayout.vue'
import ListGrid from '@/components/ListGrid.vue'
import ProductGrid from '@/components/ProductGrid.vue'

const demoLists = [
  { title: 'Cleaning Products', icon: 'mdi-home', items: 0 },
  { title: 'Market List', icon: 'mdi-apple', items: 0 },
  { title: 'General', icon: 'mdi-cart', items: 2 },
  { title: 'Dairy', icon: 'mdi-bottle-milk', items: 0 },
  { title: 'Fruits', icon: 'mdi-apple', items: 0 },
  { title: 'Vegetables', icon: 'mdi-carrot', items: 0 }
]

const demoProducts = [
  { name: 'Milk', quantity: 1 },
  { name: 'Bread', quantity: 2 },
  { name: 'Eggs', quantity: 12 },
  { name: 'Soap', quantity: 3 }
]
</script>

<style scoped>
h2 {
  margin-bottom: 0.5rem;
  color: #333;
}
section {
  background: #f9f9f9;
  margin-bottom: 1rem;
  border-radius: 8px;
}
</style>
