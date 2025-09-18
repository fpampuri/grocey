<script setup lang="ts">
import { ref } from "vue";
import NavBarButton from "@/components/NavBarButton.vue";
import NavListItem from "@/components/NavListItem.vue";
import NavBarTitle from "@/components/NavBarTitle.vue";

const selectedItem = ref("lists"); // Default selected item

const selectItem = (item: string) => {
  selectedItem.value = item;
};

const onProfileClick = () => {
  console.log("profile clicked");
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
        class="d-flex flex-column flex-grow-1 overflow-hidden"
        style="min-height: 0"
      >
        <!-- Lists Button -->
        <NavListItem
          icon="mdi-format-list-bulleted"
          title="Lists"
          :selected="selectedItem === 'lists'"
          @click="selectItem('lists')"
        />

        <!-- Products Button -->
        <NavListItem
          icon="mdi-food-apple-outline"
          title="Products"
          :selected="selectedItem === 'products'"
          @click="selectItem('products')"
        />
      </v-list>

      <!-- Bottom settings (Pinned to bottom) -->
      <div class="mt-auto">
        <NavListItem
          icon="mdi-cog"
          title="Settings"
          :selected="selectedItem === 'settings'"
          @click="selectItem('settings')"
        />
      </div>
    </div>
  </v-navigation-drawer>

  <!-- Topbar: z-index menor para quedar debajo de la sidebar -->
  <v-app-bar height="72" style="z-index: 100">
    <v-toolbar-title></v-toolbar-title>
    <v-spacer></v-spacer>
    <NavBarButton icon="mdi-account-circle" @click="onProfileClick" />
  </v-app-bar>

  <v-main>
    <slot />
  </v-main>
</template>
