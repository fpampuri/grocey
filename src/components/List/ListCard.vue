<script setup lang="ts">
    import { defineProps, defineEmits, ref, watch } from "vue";

    const props = defineProps({
      title: { type: String, required: true },
      icon: { type: String, default: "mdi-format-list-bulleted" },
      itemsCount: { type: Number, default: 0 },
      starred: { type: Boolean, default: false },
      selected: { type: Boolean, default: false },
    });

    const emits = defineEmits(["click", "toggle-star", "edit", "delete", "share"]);

    // local reactive starred state so UI updates immediately
    const localStarred = ref(props.starred);

    // keep local state in sync when prop changes
    watch(() => props.starred, (v) => (localStarred.value = v));

    function toggleStar() {
      localStarred.value = !localStarred.value;
      emits("toggle-star", localStarred.value);
    }

    const selectedForAction = ref(false);

    watch(() => props.selected, (v) => (selectedForAction.value = v));

    function toggleSelection() {
      selectedForAction.value = !selectedForAction.value;
    }

</script>

<template>
  <v-card class="list-card" outlined>
    <v-row class="card-row" align="center" no-gutters>
      <v-col cols="auto">
        <v-checkbox
          v-model="selectedForAction"
          hide-details
          dense
          color="green"
          class="green-border-checkbox"
          aria-label="select list"
        />
      </v-col>

      <v-col>
        <v-row class="card-main-row" align="center" no-gutters>
          <v-icon class="list-icon mr-3 text-green no-shrink">{{ icon }}</v-icon>

          <div
            class="list-body"
            @click="$emit('click')"
            role="button"
            tabindex="0"
          >
            <div class="list-title">{{ title }}</div>
          </div>

          <v-spacer />

          <div class="actions no-shrink">
            <button
              class="action-btn star-btn"
              @click.stop="toggleStar()"
              :aria-pressed="localStarred"
              aria-label="toggle star"
            >
              <v-icon :class="localStarred ? 'text-yellow' : ''">{{
                localStarred ? 'mdi-star' : 'mdi-star-outline'
              }}</v-icon>
            </button>

            <v-menu offset-y>
              <template #activator="{ props: activator }">
                <button v-bind="activator" class="action-btn dots-btn" aria-label="more actions">
                  <v-icon>mdi-dots-horizontal</v-icon>
                </button>
              </template>
              <v-list class="action-menu">

                <v-list-item @click="$emit('edit')" class="menu-item">
                  <template #prepend>
                    <v-icon class="menu-icon">mdi-pencil</v-icon>
                  </template>
                  <v-list-item-title class="menu-title">Edit</v-list-item-title>
                </v-list-item>

                <v-list-item @click="$emit('share')" class="menu-item">
                  <template #prepend>
                    <v-icon class="menu-icon">mdi-share-variant</v-icon>
                  </template>
                  <v-list-item-title class="menu-title">Share</v-list-item-title>
                </v-list-item>

                <v-list-item @click="$emit('delete')" class="menu-item delete-item">
                  <template #prepend>
                    <v-icon class="menu-icon">mdi-delete</v-icon>
                  </template>
                  <v-list-item-title class="menu-title">Delete list</v-list-item-title>
                </v-list-item>

              </v-list>
            </v-menu>
          </div>
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

.card-main-row {
  flex-wrap: nowrap; /* prevent wrapping */
  gap: 8px;
}

.list-body {
  flex: 1 1 auto; /* allow body to grow and take available space */
  min-width: 0; /* allow truncation */
}

.no-shrink {
  flex: 0 0 auto; /* prevent icon/buttons from shrinking */
}

.list-icon {
  font-size: 26px;
}

.list-title {
  font-weight: 600;
  font-size: 1rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.list-count {
  margin-top: 18px;
  color: rgba(0, 0, 0, 0.6);
  font-weight: 600;
}

.green-border-checkbox :deep(.mdi-checkbox-blank-outline):before {
  color: rgb(7, 138, 7) !important;
}

.action-btn {
  background: none;
  border: none;
  border-radius: 6px;
  padding: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s ease;
  color: black;
}

.action-btn:hover {
  background-color: rgb(7, 138, 7);
  color: white;
}

.star-btn .v-icon.text-yellow {
  color: #e0d041 !important; /* Muted gold color instead of bright yellow */
}

.actions {
  display: flex;
  gap: 8px;
  align-items: center;
  flex: 0 0 auto;
}

.action-menu {
  min-width: 150px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.menu-item {
  padding: 12px 16px;
  color: black;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.menu-icon {
  margin-right: 12px;
  transition: color 0.2s ease;
  color: rgba(0, 0, 0, 1) !important;
}

.menu-item:hover {
  background-color: rgb(7, 138, 7);
  color: white;
}

.menu-title {
  font-weight: 600;
}

</style>
