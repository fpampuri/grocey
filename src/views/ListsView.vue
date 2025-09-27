<script setup lang="ts">
  import ListCard from "@/components/List/ListCard.vue";

  // temporary sample data - render the same card multiple times for preview
  const samples = Array.from({ length: 6 }).map((_, i) => ({
    id: i + 1,
    title: i % 2 ? "Market List" : "Cleaning Products",
    icon: i % 2 ? "mdi-apple" : "mdi-broom",
    itemsCount: Math.floor(Math.random() * 8),
  }));
</script>

<template>
  <div class="pa-3">
    <v-container fluid>
      <v-row>
        <v-col
          cols="12"
          sm="6"
          md="4"
          lg="3"
          v-for="item in samples"
          :key="item.id"
        >
          <ListCard
            :title="item.title"
            :icon="item.icon"
            :itemsCount="item.itemsCount"
          />
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>
