<template>
  <v-app>
    <v-navigation-drawer
      v-model="drawer"
      absolute
      width="260"
      style="z-index: 1050;"
    >
      <v-list>
        <v-list-item>
          <v-list-item-title font-weight="bold">Grocey</v-list-item-title>
        </v-list-item>
        <!-- Añade tus items -->
      </v-list>
    </v-navigation-drawer>

    <!-- Topbar: z-index menor para quedar debajo de la sidebar -->
    <v-app-bar height="64" style="z-index: 100;">
      <v-app-bar-nav-icon @click="drawer = !drawer" />
      <v-toolbar-title>Barra superior</v-toolbar-title>
    </v-app-bar>

    <v-main>
      <slot />
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { ref } from 'vue'
const drawer = ref(true)
</script>