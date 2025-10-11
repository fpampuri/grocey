<script setup lang="ts">
import { ref } from "vue";
import StandardButton from "@/components/StandardButton.vue";
import BaseDialog from "@/components/dialog/BaseDialog.vue";

const props = defineProps({
  modelValue: { type: Boolean, default: false },
});

const emit = defineEmits(["update:modelValue", "create-list"]);

// Form data
const listName = ref("");
const isRecurring = ref(false);
const selectedIcon = ref("mdi-cart");
const selectedDay = ref("Monday");
const selectedHour = ref(9);
const selectedMinute = ref(0);

// Available days for recurring lists
const dayOptions = [
  "Monday",
  "Tuesday", 
  "Wednesday",
  "Thursday",
  "Friday",
  "Saturday",
  "Sunday"
];

// Available icons for selection
const iconOptions = [
  'mdi-cart',
  'mdi-apple',
  'mdi-food-apple',
  'mdi-food',
  'mdi-carrot',
  'mdi-cheese',
  'mdi-fish',
  'mdi-egg',
  'mdi-bread-slice',
  'mdi-baguette',
  'mdi-grain',
  'mdi-pasta',
  'mdi-rice',
  'mdi-bottle-wine',
  'mdi-coffee',
  'mdi-tea',
  'mdi-beer',
  'mdi-glass-cocktail',
  'mdi-cupcake',
  'mdi-cake',
  'mdi-ice-cream',
  'mdi-candy',
  'mdi-fruit-cherries',
  'mdi-fruit-grapes',
  'mdi-fruit-watermelon',
  'mdi-leaf',
  'mdi-tree',
  'mdi-sprout',
  'mdi-flower',
  'mdi-home',
  'mdi-fridge',
  'mdi-microwave',
  'mdi-silverware-fork-knife',
  'mdi-grill',
  'mdi-pot',
  'mdi-broom',
  'mdi-gift',
  'mdi-party-popper',
  'mdi-star',
  'mdi-heart',
  'mdi-tag',
  'mdi-tag-outline',
  'mdi-bookmark',
  'mdi-shopping',
  'mdi-basket',
  'mdi-package',
  'mdi-package-variant',
  'mdi-snowflake',
];

function closeDialog() {
  emit("update:modelValue", false);
  // Reset form
  listName.value = "";
  isRecurring.value = false;
  selectedIcon.value = "mdi-cart";
  selectedDay.value = "Monday";
  selectedHour.value = 9;
  selectedMinute.value = 0;
  iconOptionsOpen.value = false;
}

function handleModelValueUpdate(value: boolean) {
  if (!value) {
    // Reset form when dialog closes
    listName.value = "";
    isRecurring.value = false;
    selectedIcon.value = "mdi-cart";
    selectedDay.value = "Monday";
    selectedHour.value = 9;
    selectedMinute.value = 0;
    iconOptionsOpen.value = false;
  }
  emit("update:modelValue", value);
}

function createList() {
  if (!listName.value.trim()) {
    return; // Don't create if required fields missing
  }

  const listData = {
    name: listName.value.trim(),
    description: "-", // Minimal placeholder to satisfy backend requirement
    recurring: isRecurring.value,
    icon: selectedIcon.value,
  };

  // Add scheduling info if recurring is enabled
  if (isRecurring.value) {
    (listData as any).recurringSchedule = {
      day: selectedDay.value,
      hour: selectedHour.value,
      minute: selectedMinute.value,
      time: `${String(selectedHour.value).padStart(2, '0')}:${String(selectedMinute.value).padStart(2, '0')}`
    };
  }

  emit("create-list", listData);
  closeDialog();
}

function selectIcon(iconValue: string) {
  selectedIcon.value = iconValue;
  iconOptionsOpen.value = false;
}

function validateHour() {
  if (selectedHour.value < 0) selectedHour.value = 0;
  if (selectedHour.value > 23) selectedHour.value = 23;
}

function validateMinute() {
  if (selectedMinute.value < 0) selectedMinute.value = 0;
  if (selectedMinute.value > 59) selectedMinute.value = 59;
}

// Toggle icon options on click instead of hover
const iconOptionsOpen = ref(false);
</script>

<template>
  <BaseDialog
    :model-value="modelValue"
    @update:model-value="handleModelValueUpdate"
  >
    <!-- Dialog Header -->
    <div class="dialog-header">
      <h2 class="dialog-title">Create New List</h2>
      <button class="close-button" @click="closeDialog">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
          <path
            d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z"
          />
        </svg>
      </button>
    </div>

    <!-- Dialog Body -->
    <div class="dialog-body">
      <!-- List Name Field -->
      <div class="form-field">
        <label class="field-label">List Name</label>
        <input
          v-model="listName"
          type="text"
          placeholder="Enter list name"
          class="text-input"
          @keyup.enter="createList"
        />
      </div>

      <div class="form-field toggle-row">
        <label class="field-label">Recurring List</label>
        <v-switch
          v-model="isRecurring"
          inset
          color="var(--primary-green)"
          hide-details
        />
      </div>

      <!-- Recurring Schedule (only show when recurring is enabled) -->
      <div v-if="isRecurring" class="recurring-schedule">
        <!-- Day Selection -->
        <div class="form-field">
          <label class="field-label">Day of Week</label>
          <select v-model="selectedDay" class="select-input">
            <option v-for="day in dayOptions" :key="day" :value="day">
              {{ day }}
            </option>
          </select>
        </div>

        <!-- Time Selection -->
        <div class="form-field">
          <label class="field-label">Time</label>
          <div class="time-inputs">
            <div class="time-input-group">
              <label class="time-label">Hour</label>
              <input
                v-model.number="selectedHour"
                type="number"
                min="0"
                max="23"
                class="time-input"
                @input="validateHour"
              />
            </div>
            <span class="time-separator">:</span>
            <div class="time-input-group">
              <label class="time-label">Minute</label>
              <input
                v-model.number="selectedMinute"
                type="number"
                min="0"
                max="59"
                step="5"
                class="time-input"
                @input="validateMinute"
              />
            </div>
          </div>
          <div class="time-preview">
            Scheduled for {{ selectedDay }} at {{ String(selectedHour).padStart(2, '0') }}:{{ String(selectedMinute).padStart(2, '0') }}
          </div>
        </div>
      </div>

      <!-- Icon Selection -->
      <div class="form-field">
        <label class="field-label">Icon</label>
        <div class="icon-selector">
          <div
            class="selected-icon"
            @click="iconOptionsOpen = !iconOptionsOpen"
          >
            <v-icon :icon="selectedIcon" size="24" />
            <svg
              class="dropdown-arrow"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
            >
              <path d="M7,10L12,15L17,10H7Z" />
            </svg>
          </div>

          <div class="icon-options" v-if="iconOptionsOpen">
            <button
              v-for="iconValue in iconOptions"
              :key="iconValue"
              @click="selectIcon(iconValue)"
              class="icon-option"
              :class="{ active: selectedIcon === iconValue }"
            >
              <v-icon :icon="iconValue" size="24" />
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Dialog Footer -->
    <div class="dialog-footer">
      <button @click="closeDialog" class="cancel-button">Cancel</button>
      <StandardButton title="Create List" icon="mdi-plus" @click="createList" />
    </div>
  </BaseDialog>
</template>

<style scoped>
/* Custom styles specific to this dialog - icon selector */

.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 20px;
}

.text-area {
  width: 100%;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  padding: 12px 16px;
  font-size: 14px;
  resize: vertical;
  min-height: 96px;
  outline: none;
  transition: border-color 0.2s ease;
}

.text-area:focus {
  border-color: var(--primary-green);
}

.toggle-row {
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

.icon-selector {
  position: relative;
}

.selected-icon {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: border-color 0.2s ease;
  background: white;
}

.selected-icon:hover {
  border-color: var(--primary-green);
}

.dropdown-arrow {
  width: 16px;
  height: 16px;
  fill: #666;
  margin-left: auto;
}

.icon-options {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 1001;
  max-height: 300px;
  overflow-y: auto;
  padding: 12px;
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 8px;
}

.icon-option {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  aspect-ratio: 1;
  padding: 8px;
  border: 2px solid transparent;
  border-radius: 8px;
  background: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.icon-option:hover {
  background-color: #f5f5f5;
  border-color: var(--primary-green);
}

.icon-option.active {
  background-color: #e8f5e8;
  border-color: var(--primary-green);
  color: var(--primary-green);
}

.dialog-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e0e0e0;
  background-color: white;
}

.cancel-button {
  padding: 10px 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: white;
  color: #666;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s ease;
}

.cancel-button:hover {
  background-color: white;
  border-color: #999;
}

/* Recurring Schedule Styles */
.recurring-schedule {
  background: #f8f9fa;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  padding: 16px;
  margin: 16px 0;
}

.select-input {
  width: 100%;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  padding: 12px 16px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s ease;
  background: white;
}

.select-input:focus {
  border-color: var(--primary-green);
}

.time-inputs {
  display: flex;
  align-items: flex-end;
  gap: 12px;
}

.time-input-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.time-label {
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

.time-input {
  width: 70px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  padding: 12px 8px;
  font-size: 14px;
  text-align: center;
  outline: none;
  transition: border-color 0.2s ease;
}

.time-input:focus {
  border-color: var(--primary-green);
}

.time-separator {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0 4px;
  padding-bottom: 12px;
}

.time-preview {
  margin-top: 8px;
  font-size: 13px;
  color: var(--primary-green);
  font-weight: 500;
}

/* Remove number input arrows */
.time-input::-webkit-outer-spin-button,
.time-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.time-input[type=number] {
  appearance: textfield;
  -moz-appearance: textfield;
}
</style>
