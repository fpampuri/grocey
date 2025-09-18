<script lang="ts" setup>
import NavBar from "./layouts/NavBar.vue";
import { RouterView } from "vue-router";
</script>

<template>
  <v-app>
    <NavBar>
      <RouterView />
    </NavBar>
  </v-app>
</template>
