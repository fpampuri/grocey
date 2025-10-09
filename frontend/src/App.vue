<script lang="ts" setup>
  import { computed, onBeforeMount } from 'vue'
  import { RouterView, useRoute } from 'vue-router'
  import NavBar from './layouts/NavBar.vue'
  import { useUserStore } from '@/stores/user'

  const route = useRoute()
  const useNavLayout = computed(() => (route.meta?.layout as string | undefined) !== 'plain')

  const userStore = useUserStore()

  onBeforeMount(() => {
    userStore.init()
  })
</script>

<template>
  <v-app>
    <NavBar v-if="useNavLayout">
      <RouterView />
    </NavBar>
    <v-main v-else class="plain-layout">
      <RouterView />
    </v-main>
  </v-app>
</template>

<style scoped>
  .plain-layout {
    min-height: 100vh;
    background-color: #fff;
  }
</style>
