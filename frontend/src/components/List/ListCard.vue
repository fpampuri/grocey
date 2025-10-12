<script setup lang="ts">
  import { ref, watch } from 'vue'

  const props = defineProps({
    title: { type: String, required: true },
    icon: { type: String, default: 'mdi-format-list-bulleted' },
    itemsCount: { type: Number, default: 0 },
    starred: { type: Boolean, default: false },
    selected: { type: Boolean, default: false },
    showEdit: { type: Boolean, default: true },
    showShare: { type: Boolean, default: true },
    showSendToHistory: { type: Boolean, default: true },
    showMarkUncompleted: { type: Boolean, default: false },
  })

  const emits = defineEmits(['click', 'toggle-star', 'toggle-selection', 'edit', 'delete', 'share', 'send-to-history', 'mark-uncompleted'])

  // local reactive starred state so UI updates immediately
  const localStarred = ref(props.starred)

  // keep local state in sync when prop changes
  watch(() => props.starred, v => (localStarred.value = v))

  function toggleStar () {
    localStarred.value = !localStarred.value
    emits('toggle-star', localStarred.value)
  }

  const selectedForAction = ref(false)

  watch(() => props.selected, v => (selectedForAction.value = v))

  function toggleSelection () {
    selectedForAction.value = !selectedForAction.value
    emits('toggle-selection', selectedForAction.value)
  }

</script>

<template>
  <v-card class="list-card" outlined @click="$emit('click')">
    <!-- Main content row with all elements in one line -->
    <v-row align="center" class="card-main-row" no-gutters>
      <v-col cols="auto">
        <v-checkbox
          v-model="selectedForAction"
          aria-label="select list"
          class="green-border-checkbox"
          color="green"
          dense
          hide-details
          @click.stop="toggleSelection"
        />
      </v-col>

      <v-col cols="auto">
        <v-icon class="list-icon text-green">{{ icon }}</v-icon>
      </v-col>

      <v-col>
        <div class="list-body">
          <div class="list-title">{{ title }}</div>
        </div>
      </v-col>

      <v-col cols="auto">
        <div class="actions" @click.stop>
          <button
            aria-label="toggle star"
            :aria-pressed="localStarred"
            class="action-btn star-btn"
            @click.stop="toggleStar()"
          >
            <v-icon :class="localStarred ? 'text-star-gold' : ''">
              {{ localStarred ? 'mdi-star' : 'mdi-star-outline' }}
            </v-icon>
          </button>

          <v-menu offset-y>
            <template #activator="{ props: activator }">
              <button v-bind="activator" aria-label="more actions" class="action-btn dots-btn" @click.stop>
                <v-icon>mdi-dots-horizontal</v-icon>
              </button>
            </template>
            <v-list class="action-menu">

              <v-list-item v-if="showEdit" class="menu-item" @click="$emit('edit')">
                <template #prepend>
                  <v-icon class="menu-icon">mdi-pencil</v-icon>
                </template>
                <v-list-item-title class="menu-title">Edit</v-list-item-title>
              </v-list-item>

              <v-list-item v-if="showShare" class="menu-item" @click="$emit('share')">
                <template #prepend>
                  <v-icon class="menu-icon">mdi-share-variant</v-icon>
                </template>
                <v-list-item-title class="menu-title">Share</v-list-item-title>
              </v-list-item>

              <v-list-item v-if="showSendToHistory" class="menu-item" @click="$emit('send-to-history')">
                <template #prepend>
                  <v-icon class="menu-icon">mdi-checkbox-marked-circle</v-icon>
                </template>
                <v-list-item-title class="menu-title">Mark as Completed</v-list-item-title>
              </v-list-item>

              <v-list-item v-if="showMarkUncompleted" class="menu-item" @click="$emit('mark-uncompleted')">
                <template #prepend>
                  <v-icon class="menu-icon">mdi-backup-restore</v-icon>
                </template>
                <v-list-item-title class="menu-title">Mark as Uncompleted</v-list-item-title>
              </v-list-item>

              <v-list-item class="menu-item delete-item" @click="$emit('delete')">
                <template #prepend>
                  <v-icon class="menu-icon">mdi-delete</v-icon>
                </template>
                <v-list-item-title class="menu-title">Delete list</v-list-item-title>
              </v-list-item>

            </v-list>
          </v-menu>
        </div>
      </v-col>
    </v-row>

    <!-- Item count row spanning full width -->
    <v-row no-gutters>
      <v-col cols="12">
        <div class="list-count">
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
  border: 2px solid var(--primary-green-light);
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.02);
  cursor: pointer;
  transition: box-shadow 0.2s ease, transform 0.1s ease;
}

.list-card:hover {
  box-shadow: 0 2px 8px var(--primary-green-light);
  transform: translateY(-1px);
}

.card-main-row {
  gap: 12px;
  margin-bottom: 18px;
}

.list-body {
  flex: 1 1 auto;
  min-width: 0;
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
  color: var(--text-primary);
}

.list-count {
  text-align: center;
  color: var(--text-secondary);
  font-weight: 600;
  width: 100%;
}

.green-border-checkbox :deep(.mdi-checkbox-blank-outline):before {
  color: var(--primary-green) !important;
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
  color: var(--text-primary);
}

.action-btn:hover {
  background-color: var(--primary-green);
  color: white;
}

.text-star-gold {
  color: var(--star-gold) !important;
}

.text-green {
  color: var(--primary-green) !important;
}

.actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.action-menu {
  min-width: 150px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.menu-item {
  padding: 12px 16px;
  color: var(--text-primary);
  transition: background-color 0.2s ease, color 0.2s ease;
}

.menu-icon {
  margin-right: 12px;
  transition: color 0.2s ease;
  color: var(--text-primary) !important;
}

.menu-item:hover {
  background-color: var(--primary-green);
  color: white;
}

.menu-title {
  font-weight: 600;
}

</style>
