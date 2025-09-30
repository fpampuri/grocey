<script setup lang="ts">
  import { ref, computed } from "vue";
  import NavBarButton from "@/components/NavBar/NavBarButton.vue";
  import NavListItem from "@/components/NavBar/NavListItem.vue";
  import NavBarTitle from "@/components/NavBar/NavBarTitle.vue";
  import ProfilePanel from "@/components/Profile/ProfilePanel.vue";
  import { useRoute } from "vue-router";

  const route = useRoute();

  const selectedItem = ref("lists"); // Default selected item
  const showProfilePanel = ref(false);

  const selectItem = (item: string) => {
    selectedItem.value = item;
  };

  const pageTitle = computed(() => {
    const matched = route.matched
      .slice()
      .reverse()
      .find((r) => r.meta && (r.meta as any).title);
    if (matched) return (matched.meta as any).title;
    // fallback to a nicer name based on route.name
    return route.name
      ? String(route.name).replace(/^\w/, (c) => c.toUpperCase())
      : "Grocey";
  });

  const onProfileClick = () => {
    showProfilePanel.value = true;
  };
</script>

<template>
  <v-navigation-drawer
    permanent
    class="d-flex flex-column"
    style="height: 100vh"
  >
    <div class="d-flex flex-column h-100">
      <!-- Header -->
      <NavBarTitle icon="mdi-cart" title="Grocey" />

      <!-- Main navigation bar -->
      <v-list
        nav
        class="d-flex flex-column flex-grow-1 overflow-hidden nav-list"
        style="min-height: 0"
      >
        <!-- Lists Button -->
        <NavListItem
          icon="mdi-format-list-bulleted"
          title="Lists"
          :to="{ name: 'lists' }"
          :selected="route.name === 'lists'"
          @click="selectItem('lists')"
        />

        <!-- Products Button -->
        <NavListItem
          icon="mdi-food-apple-outline"
          title="Products"
          :to="{ name: 'products' }"
          :selected="route.name === 'products'"
          @click="selectItem('products')"
        />
      </v-list>

      <!-- Bottom settings (Pinned to bottom) -->
      <div class="mt-auto">
        <NavListItem
          icon="mdi-cog"
          title="Settings"
          :to="{ name: 'settings' }"
          :selected="route.name === 'settings'"
          @click="selectItem('settings')"
        />
      </div>
    </div>
  </v-navigation-drawer>


  <v-app-bar height="72" >
    <v-toolbar-title class="text-h4 font-weight-bold">
      {{ pageTitle }}
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <NavBarButton icon="mdi-account-circle" @click="onProfileClick" />
  </v-app-bar>

  <v-main>
    <slot/>
  </v-main>

  <!-- Profile Panel -->
  <ProfilePanel 
    v-model="showProfilePanel"
  />
</template>

<style scoped>
  /* Target the specific issue - v-list-item height constraints */
  .nav-list :deep(.v-list-item) {
    min-height: auto !important;
    height: auto !important;
  }

  /* The content wrapper inside v-list-item that's constraining the text */
  .nav-list :deep(.v-list-item__content) {
    min-height: auto !important;
    height: auto !important;
    padding: 0 !important;
  }

  /* Make sure the title can expand vertically */
  .nav-list :deep(.v-list-item-title) {
    white-space: normal !important;
    line-height: normal !important;
    height: auto !important;
  }
</style>