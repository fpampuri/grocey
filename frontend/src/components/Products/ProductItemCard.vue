<script setup lang="ts">
  import { nextTick, ref, watch } from 'vue'
  import StandardButton from '@/components/StandardButton.vue'

  const props = defineProps({
    title: { type: String, required: true },
    completed: { type: Boolean, default: false },
    id: { type: [String, Number], required: true },
  })

  const emits = defineEmits(['toggle-complete', 'delete', 'move', 'add-to-list', 'add-to-pantry', 'rename'])

  const localCompleted = ref(props.completed)
  watch(() => props.completed, v => (localCompleted.value = v))

  function toggleComplete () {
    localCompleted.value = !localCompleted.value
    emits('toggle-complete', localCompleted.value)
  }

  function deleteItem () {
    emits('delete', props.id)
  }

  function moveItem () {
    emits('move', props.id)
  }

  function addToList () {
    emits('add-to-list', props.id)
  }

  function addToPantry () {
    emits('add-to-pantry', props.id)
  }

  // Inline rename state
  const editing = ref(false)
  const localTitle = ref(props.title)
  const inputRef = ref<HTMLInputElement | null>(null)

  watch(() => props.title, v => {
    if (!editing.value) localTitle.value = v
  })

  function startEdit () {
    editing.value = true
    localTitle.value = props.title
    nextTick(() => inputRef.value?.focus())
  }

  function commitEdit () {
    const newName = localTitle.value.trim()
    if (newName && newName !== props.title) {
      emits('rename', { id: props.id, name: newName })
    }
    editing.value = false
  }

  function cancelEdit () {
    localTitle.value = props.title
    editing.value = false
  }
</script>

<template>
  <v-card class="product-item-card" outlined>
    <v-row align="center" class="item-row" no-gutters>
      <!-- Item title -->
      <v-col>
        <div class="title-row">
          <template v-if="editing">
            <input
              ref="inputRef"
              v-model="localTitle"
              class="rename-input"
              type="text"
              @blur="commitEdit"
              @keydown.enter.stop.prevent="commitEdit"
              @keydown.esc.stop.prevent="cancelEdit"
            >
          </template>
          <template v-else>
            <span
              class="item-title"
              :class="{ 'completed': localCompleted }"
            >
              {{ title }}
            </span>
            <button aria-label="Rename product" class="icon-btn edit-btn" @click.stop="startEdit">
              <v-icon icon="mdi-pencil" size="small" />
            </button>
          </template>
        </div>
      </v-col>

      <!-- Actions: move button + add-to-list button + add-to-pantry button + delete -->
      <v-col cols="auto">
        <div class="actions">
          <div class="compact-button">
            <StandardButton
              icon="mdi-folder-move"
              title="Move to Category"
              @click="moveItem"
            />
          </div>

          <div class="compact-button">
            <StandardButton
              icon="mdi-plus"
              title="Add to List"
              @click="addToList"
            />
          </div>

          <div class="compact-button">
            <StandardButton
              icon="mdi-fridge-variant"
              title="Add to Pantry"
              @click="addToPantry"
            />
          </div>

          <button aria-label="delete item" class="delete-btn" @click="deleteItem">
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

.compact-button :deep(.standard-button) {
  padding: 6px 12px;
  font-size: 14px;
  min-width: auto;
  border-radius: 8px;
}

.compact-button :deep(.button-icon) {
  font-size: 16px;
}

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
