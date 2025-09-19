<script setup lang="ts">
import { defineProps, defineEmits } from "vue";

const props = defineProps({
  title: { type: String, required: true },
  icon: { type: String, default: "mdi-format-list-bulleted" },
  itemsCount: { type: Number, default: 0 },
  starred: { type: Boolean, default: false },
});

const emits = defineEmits(["click", "toggle-star", "edit", "delete", "share"]);
</script>

<template>
  <v-card class="list-card" outlined>
    <v-row class="card-row" align="center" no-gutters>
      <v-col cols="auto">
        <v-checkbox
          :model-value="false"
          hide-details
          dense
          aria-label="select list"
        />
      </v-col>

      <v-col>
        <v-row align="center" no-gutters>
          
          <v-icon class="list-icon mr-3 text-green">{{ icon }}</v-icon>

          <div
            class="list-body"
            @click="$emit('click')"
            role="button"
            tabindex="0"
          >
            <div class="list-title">{{ title }}</div>
          </div>

          <v-spacer />

          <v-btn
            icon
            small
            @click.stop="$emit('toggle-star')"
            aria-label="toggle star"
          >
            <v-icon :class="starred ? 'text-yellow' : ''">{{
              starred ? "mdi-star" : "mdi-star-outline"
            }}</v-icon>
          </v-btn>

          <v-menu offset-y>
            <template #activator="{ props: activator }">
              <v-btn v-bind="activator" icon aria-label="more actions">
                <v-icon>mdi-dots-vertical</v-icon>
              </v-btn>
            </template>
            <v-list>
              <v-list-item @click="$emit('share')">
                <v-list-item-title>Share</v-list-item-title>
              </v-list-item>
              <v-list-item @click="$emit('edit')">
                <v-list-item-title>Edit</v-list-item-title>
              </v-list-item>
              <v-list-item @click="$emit('delete')">
                <v-list-item-title>Delete</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </v-row>

        <div class="list-count text-center">
          {{ itemsCount }} {{ itemsCount === 1 ? "item" : "items" }}
        </div>
      </v-col>
    </v-row>
  </v-card>
</template>

<style scoped>
.list-card {
  border-radius: 12px;
  padding: 12px;
  box-sizing: border-box;
  background: var(--v-surface, #fff);
  border: 2px solid green;
  /* subtle inner shadow to match card look */
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.02);
}

.card-row {
  gap: 8px;
}

.list-icon {
  font-size: 26px;
}

.list-title {
  font-weight: 600;
  font-size: 1rem;
}

.list-count {
  margin-top: 18px;
  color: rgba(0, 0, 0, 0.6);
}
</style>
