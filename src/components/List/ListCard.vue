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
            <v-btn
              icon
              small
              class="no-shrink"
              @click.stop="toggleStar()"
              :aria-pressed="localStarred"
              aria-label="toggle star"
            >
              <v-icon :class="localStarred ? 'text-yellow' : ''">{{
                localStarred ? 'mdi-star' : 'mdi-star-outline'
              }}</v-icon>
            </v-btn>

            <v-menu offset-y>
              <template #activator="{ props: activator }">
                <v-btn v-bind="activator" icon class="no-shrink" aria-label="more actions">
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

.actions {
  display: flex;
  gap: 8px;
  align-items: center;
  flex: 0 0 auto;
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
}

.green-border-checkbox :deep(.mdi-checkbox-blank-outline):before {
  color: green !important;
}

</style>
