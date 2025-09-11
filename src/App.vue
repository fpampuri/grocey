<template>
  <v-app>
    <WebLayout >
      <router-view />
    </WebLayout>
  </v-app>
</template>

<script lang="ts" setup>
  import WebLayout from '@/layouts/WebLayout.vue'
</script>
