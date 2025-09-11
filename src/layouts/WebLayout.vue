<template>
    <v-navigation-drawer
      v-model="drawer"
      permanent
      width="270"
      style="z-index: 1050;"
    >
      <div class="d-flex flex-column h-100">
        <!-- Drawer header -->
        <v-sheet class="px-4 pt-4 pb-2 d-flex align-center" color="transparent" elevation="0">
          <v-icon class="text-h3 mr-2 text-green">mdi-cart</v-icon>
          <span class="text-h4 font-weight-bold">Grocey</span>
        </v-sheet>
        <v-divider class="mb-2" />

  <v-list class="d-flex flex-column flex-grow-1 nav-list overflow-hidden">
<v-hover v-slot="{ isHovering, props }">
          <v-list-item
            v-bind="props"
            link
            rounded="lg"
            @click="selectItem('lists')"
            :class="[
              'py-2 mx-2 my-1 nav-item',
              (selectedItem === 'lists' || isHovering) ? 'bg-green text-white' : ''
            ]"
            :style="{ opacity: (isHovering && selectedItem !== 'lists') ? 0.7 : 1 }"
          >
            <v-row align="center">
              <v-icon :class="['text-h4 ma-4', (selectedItem === 'lists' || isHovering) ? 'text-white' : 'text-green']">
                mdi-format-list-bulleted
              </v-icon>
              <v-list-item-title
                :class="['text-h5 font-weight-medium', (selectedItem === 'lists' || isHovering) ? 'text-white' : '']">
                Lists
              </v-list-item-title>
            </v-row>
          </v-list-item>
        </v-hover>

        <v-hover v-slot="{ isHovering, props }">
          <v-list-item
            v-bind="props"
            link
            rounded="lg"
            @click="selectItem('products')"
            :class="[
              'py-2 mx-2 my-1 nav-item',
              (selectedItem === 'products' || isHovering) ? 'bg-green text-white' : ''
            ]"
            :style="{ opacity: (isHovering && selectedItem !== 'products') ? 0.7 : 1 }"
          >
            <v-row align="center">
              <v-icon :class="['text-h4 ma-4', (selectedItem === 'products' || isHovering) ? 'text-white' : 'text-green']">
                mdi-food-apple-outline
              </v-icon>
              <v-list-item-title
                :class="['text-h5 font-weight-medium', (selectedItem === 'products' || isHovering) ? 'text-white' : '']">
                Products
              </v-list-item-title>
            </v-row>
          </v-list-item>
        </v-hover>

        </v-list>

        <!-- Bottom pinned settings item, no scrolling -->
       <div class="mt-auto">
        <v-hover v-slot="{ isHovering, props }">
          <v-list-item
            v-bind="props"
            link
            rounded="lg"
            @click="selectItem('settings')"
            :class="[
              'py-2 mx-2 my-1 nav-item',
              (selectedItem === 'settings' || isHovering) ? 'bg-green text-white' : ''
            ]"
            :style="{ opacity: (isHovering && selectedItem !== 'settings') ? 0.7 : 1 }"
          >
            <v-row align="center">
              <v-icon :class="['ma-4', (selectedItem === 'settings' || isHovering) ? 'text-white' : 'text-green']">
                mdi-cog
              </v-icon>
              <v-list-item-title
                :class="['text-h5 font-weight-medium', (selectedItem === 'settings' || isHovering) ? 'text-white' : '']">
                Settings
              </v-list-item-title>
            </v-row>
          </v-list-item>
        </v-hover>
        </div>
      </div>
    </v-navigation-drawer>

    <!-- Topbar: z-index menor para quedar debajo de la sidebar -->
      <v-app-bar height="72" style="z-index: 100;">
    <v-toolbar-title></v-toolbar-title>
    <v-spacer></v-spacer>
    <v-hover v-slot="{ isHovering, props }">
      <v-btn
        v-bind="props"
        icon
        rounded="3xl"
        :class="[isHovering ? 'bg-green' : '']"
        :style="{ opacity: isHovering ? 0.75 : 1, transition: 'background-color 150ms ease, color 150ms ease, opacity 150ms ease' }"
      >
        <v-icon
          class="text-h4"
          :class="[isHovering ? 'text-white' : 'text-green']"
          :style="{ transition: 'color 150ms ease' }"
        >
          mdi-account-circle
        </v-icon>
      </v-btn>
    </v-hover>
  </v-app-bar>

    <v-main>
      <slot />
    </v-main>
</template>


<script setup lang="ts">
import { ref } from 'vue'

const drawer = ref(true)
const selectedItem = ref('lists') // Default selected item

const selectItem = (item: string) => {
  selectedItem.value = item
}
</script>

<style scoped>
.nav-item {
  transition: background-color 150ms ease, color 150ms ease, opacity 150ms ease;
}

.nav-item .v-icon,
.nav-item .v-list-item-title {
  transition: color 150ms ease;
}
</style>