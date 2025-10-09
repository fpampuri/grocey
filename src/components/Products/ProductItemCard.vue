<script setup lang="ts">
import { ref, watch, nextTick } from 'vue';

const props = defineProps({
  title: { type: String, required: true },
  completed: { type: Boolean, default: false },
  id: { type: [String, Number], required: true },
});

const emits = defineEmits(["toggle-complete", "delete", "move", "add-to-list", "rename"]);

const localCompleted = ref(props.completed);
watch(() => props.completed, (v) => (localCompleted.value = v));

function toggleComplete() {
  localCompleted.value = !localCompleted.value;
  emits('toggle-complete', localCompleted.value);
}

function deleteItem() {
  emits('delete', props.id);
}

function moveItem() {
  emits('move', props.id);
}

function addToList() {
  emits('add-to-list', props.id);
}

// Inline rename state
const editing = ref(false);
const localTitle = ref(props.title);
const inputRef = ref<HTMLInputElement | null>(null);

watch(() => props.title, (v) => { if (!editing.value) localTitle.value = v; });

function startEdit() {
  editing.value = true;
  localTitle.value = props.title;
  nextTick(() => inputRef.value?.focus());
}

function commitEdit() {
  const newName = localTitle.value.trim();
  if (newName && newName !== props.title) {
    emits('rename', { id: props.id, name: newName });
  }
  editing.value = false;
}

function cancelEdit() {
  localTitle.value = props.title;
  editing.value = false;
}
</script>

<template>
  <v-card class="product-item-card" outlined>
    <v-row class="item-row" align="center" no-gutters>
      <!-- Completion checkbox -->
      <v-col cols="auto">
        <v-checkbox
          v-model="localCompleted"
          hide-details
          color="green"
          class="green-border-checkbox"
          aria-label="mark item as completed"
          @click.stop="toggleComplete"
        />
      </v-col>

      <!-- Item title -->
      <v-col>
        <div class="title-row">
          <template v-if="editing">
            <input
              ref="inputRef"
              v-model="localTitle"
              class="rename-input"
              type="text"
              @keydown.enter.stop.prevent="commitEdit"
              @keydown.esc.stop.prevent="cancelEdit"
              @blur="commitEdit"
            />
          </template>
          <template v-else>
            <span 
              class="item-title"
              :class="{ 'completed': localCompleted }"
            >
              {{ title }}
            </span>
            <button class="icon-btn edit-btn" @click.stop="startEdit" aria-label="Rename product">
              <v-icon icon="mdi-pencil" size="small" />
            </button>
          </template>
        </div>
      </v-col>

      <!-- Actions: move icon + add-to-list button + delete -->
      <v-col cols="auto">
        <div class="actions">
          <button class="icon-btn" @click="moveItem" aria-label="Move to category">
            <v-icon icon="mdi-folder-move" size="small" />
          </button>

          <button class="add-list-btn" @click="addToList" aria-label="Add to list">
            <v-icon icon="mdi-plus" size="small" class="mr-1" />
            Add to List
          </button>

          <button class="delete-btn" @click="deleteItem" aria-label="delete item">
            <v-icon icon="mdi-delete" size="small" />
          </button>
        </div>
      </v-col>
    </v-row>
  </v-card>
</template>

<style scoped>
.green-border-checkbox :deep(.mdi-checkbox-blank-outline):before {
  color: var(--primary-green) !important;
}

.product-item-card {
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-sizing: border-box;
  background: var(--v-surface, #fff);
  border: 1px solid var(--v-border-color, #e0e0e0);
  transition: box-shadow 0.2s ease;
}

.product-item-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.item-row { gap: 16px; }

.item-title {
  font-weight: 500;
  font-size: 1rem;
  color: var(--text-primary);
  transition: all 0.2s ease;
}

.item-title.completed {
  text-decoration: line-through;
  color: var(--text-secondary);
  opacity: 0.7;
}

.actions { display: flex; align-items: center; gap: 8px; }

.icon-btn {
  background: none;
  border: 1px solid var(--v-border-color, #e0e0e0);
  border-radius: 6px;
  width: 32px;
  height: 32px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  color: var(--text-primary);
}
.icon-btn:hover { background-color: var(--primary-green); color: white; border-color: var(--primary-green); }

.edit-btn {
  border-color: var(--v-border-color, #e0e0e0);
}

.title-row {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.rename-input {
  width: 100%;
  max-width: 360px;
  padding: 6px 10px;
  border: 2px solid var(--primary-green-light);
  border-radius: 8px;
  font-size: 0.95rem;
}

.add-list-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  background-color: var(--primary-green);
  color: white;
  border: 1px solid var(--primary-green);
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease;
}
.add-list-btn:hover { background-color: #0f7a0f; }

.delete-btn {
  background: none;
  border: 1px solid var(--delete-red);
  border-radius: 6px;
  width: 32px;
  height: 32px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  color: var(--delete-red);
  margin-left: 4px;
}
.delete-btn:hover { background-color: var(--delete-red); color: white; }
</style>