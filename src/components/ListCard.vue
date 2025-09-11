<template>
  <div class="list-card">
    <div class="icon">
      <v-icon size="24">{{ icon }}</v-icon>
    </div>
    <div class="details">
      <h3>{{ title }}</h3>
      <p>{{ items }} items</p>
    </div>
    <div class="actions">
      <v-btn icon>
        <v-icon>mdi-dots-vertical</v-icon>
      </v-btn>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue';
 
const props = defineProps<{ 
  title: string; 
  icon: string; 
  items?: number;
}>();

</script>

<style scoped>
.list-card {
  border: 2px solid #00C853;
    border-radius: 16px;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: white;
}
.icon {
  flex-shrink: 0;
}
.details {
  flex: 1;
  margin: 0 16px;
}
.details h3 {
  margin: 0;
  font-size: 1.25rem;
}
.details p {
  margin: 4px 0 0;
  color: #607d8b;
}
.actions {
  flex-shrink: 0;
}
</style>


<!-- CODIGO TARJETAS DE LISTA -->
<!--  <v-row dense>
          <v-col
            v-for="list in lists"
            :key="list.title"
            cols="12"
            sm="6"
            md="4"
          >
            <v-card outlined class="pa-4">
              <div class="d-flex justify-space-between align-center">
                <div class="d-flex align-center">
                  <v-checkbox v-model="list.checked" hide-details class="me-2" />
                  <v-icon class="me-1" :color="list.iconColor" size="20">{{ list.icon }}</v-icon>
                  <span class="text-subtitle-1 font-weight-medium">{{ list.title }}</span>
                </div>
                <div class="d-flex align-center">
                  <v-icon :color="list.favorite ? 'amber' : ''">mdi-star{{ list.favorite ? '' : '-outline' }}</v-icon>
                  <v-menu>
                    <template #activator="{ props }">
                      <v-btn v-bind="props" icon variant="text">
                        <v-icon>mdi-dots-horizontal</v-icon>
                      </v-btn>
                    </template>
                    <v-list>
                      <v-list-item title="Edit"></v-list-item>
                      <v-list-item title="Delete"></v-list-item>
                    </v-list>
                  </v-menu>
                </div>
              </div>
              <div class="mt-2 text-medium-emphasis">{{ list.items }} items</div>
            </v-card>
          </v-col>
        </v-row> -->