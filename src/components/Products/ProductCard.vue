<script setup lang="ts">
import { defineProps, defineEmits, ref, watch, nextTick } from 'vue';

const props = defineProps({
  title: { type: String, required: true },
  icon: { type: String, default: 'mdi-tag-outline' },
  itemsCount: { type: Number, default: 0 },
});

const emits = defineEmits(["click", "edit", "delete", "rename"]);

// Inline rename state
const editing = ref(false);
const localTitle = ref(props.title);
const inputRef = ref<HTMLInputElement | null>(null);

watch(() => props.title, (v) => { if (!editing.value) localTitle.value = v; });

function startEdit(e?: Event) {
  if (e) e.stopPropagation();
  editing.value = true;
  localTitle.value = props.title;
  nextTick(() => inputRef.value?.focus());
}

function commitEdit() {
  const newName = localTitle.value.trim();
  if (newName && newName !== props.title) {
    emits('rename', newName);
  }
  editing.value = false;
}

function cancelEdit() {
  localTitle.value = props.title;
  editing.value = false;
}
</script>

<template>
  <v-card class="product-card" outlined @click="$emit('click')">
    <!-- Main content row with all elements in one line -->
    <v-row class="card-main-row" align="center" no-gutters>

      <v-col cols="auto">
        <v-icon class="category-icon text-green">{{ icon }}</v-icon>
      </v-col>

      <v-col>
        <div class="category-body">
          <div class="category-title-row">
            <template v-if="editing">
              <input
                ref="inputRef"
                v-model="localTitle"
                class="rename-input"
                type="text"
                @click.stop
                @keydown.enter.stop.prevent="commitEdit"
                @keydown.esc.stop.prevent="cancelEdit"
                @blur="commitEdit"
              />
            </template>
            <template v-else>
              <span class="category-title">{{ title }}</span>
              <button class="action-btn edit-btn" @click.stop="startEdit" aria-label="Rename category">
                <v-icon>mdi-pencil</v-icon>
              </button>
            </template>
          </div>
        </div>
      </v-col>

      <v-col cols="auto">
        <div class="actions" @click.stop>
          <v-menu offset-y>
            <template #activator="{ props: activator }">
              <button v-bind="activator" class="action-btn dots-btn" aria-label="more actions" @click.stop>
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

              <v-list-item @click="$emit('delete')" class="menu-item delete-item">
                <template #prepend>
                  <v-icon class="menu-icon">mdi-delete</v-icon>
                </template>
                <v-list-item-title class="menu-title">Delete category</v-list-item-title>
              </v-list-item>

            </v-list>
          </v-menu>
        </div>
      </v-col>
    </v-row>

    <!-- Item count row spanning full width -->
    <v-row no-gutters>
      <v-col cols="12">
        <div class="category-count">
          {{ itemsCount }} {{ itemsCount === 1 ? 'product' : 'products' }}
        </div>
      </v-col>
    </v-row>
  </v-card>
  
</template>

<style scoped>
.product-card {
  border-radius: 12px;
  padding: 12px;
  box-sizing: border-box;
  background: var(--v-surface, #fff);
  border: 2px solid var(--primary-green-light);
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.02);
  cursor: pointer;
  transition: box-shadow 0.2s ease, transform 0.1s ease;
}

.product-card:hover {
  box-shadow: 0 2px 8px var(--primary-green-light);
  transform: translateY(-1px);
}

.card-main-row {
  gap: 12px;
  margin-bottom: 18px;
}

.category-body {
  flex: 1 1 auto;
  min-width: 0;
}

.category-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.category-icon {
  font-size: 26px;
}

.category-title {
  font-weight: 600;
  font-size: 1rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--text-primary);
}

.rename-input {
  width: 100%;
  max-width: 320px;
  padding: 6px 10px;
  border: 2px solid var(--primary-green-light);
  border-radius: 8px;
  font-size: 0.95rem;
}

.category-count {
  text-align: center;
  color: var(--text-secondary);
  font-weight: 600;
  width: 100%;
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

.edit-btn {
  border: 1px solid var(--v-border-color, #e0e0e0);
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