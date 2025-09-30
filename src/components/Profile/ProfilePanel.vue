<script setup lang="ts">
import { ref, computed } from 'vue'
import { defineProps, defineEmits } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
})

const emit = defineEmits(['update:modelValue'])

const userStore = useUserStore()
const router = useRouter()

// Local form data
const firstName = ref(userStore.user.firstName)
const lastName = ref(userStore.user.lastName)
// Email is not editable, so we don't need a ref for it

// Computed to check if there are changes (only for editable fields)
const hasChanges = computed(() => {
  return firstName.value !== userStore.user.firstName ||
         lastName.value !== userStore.user.lastName
})

function closePanel() {
  emit('update:modelValue', false)
  // Reset form to original values
  firstName.value = userStore.user.firstName
  lastName.value = userStore.user.lastName
}

function saveChanges() {
  userStore.updateProfile({
    firstName: firstName.value,
    lastName: lastName.value
  })
  userStore.saveChanges()
}

function logout() {
  userStore.logout()
  closePanel()
  router.push({ name: 'login' })
}
</script>

<template>
  <v-navigation-drawer
    :model-value="modelValue"
    @update:model-value="emit('update:modelValue', $event)"
    location="end"
    temporary
    width="400"
    class="profile-panel"
  >
    <div class="d-flex flex-column h-100">
      <!-- Header -->
      <div class="profile-header pa-6">
        <div class="d-flex justify-space-between align-center mb-4">
          <h2 class="text-h5 font-weight-bold">Profile</h2>
          <v-btn
            icon="mdi-close"
            variant="text"
            size="small"
            @click="closePanel"
          />
        </div>
        
        <!-- Avatar -->
        <div class="d-flex justify-center mb-6">
          <v-avatar
            size="80"
            class="profile-avatar"
          >
            <v-icon
              icon="mdi-account"
              size="50"
              color="white"
            />
          </v-avatar>
        </div>
      </div>

      <!-- Form Content -->
      <div class="profile-content pa-6 flex-grow-1">
        <!-- First Name -->
        <div class="mb-4">
          <label class="form-label">First Name</label>
          <v-text-field
            v-model="firstName"
            variant="outlined"
            density="compact"
            hide-details
            class="form-field"
          />
        </div>

        <!-- Last Name -->
        <div class="mb-4">
          <label class="form-label">Last Name</label>
          <v-text-field
            v-model="lastName"
            variant="outlined"
            density="compact"
            hide-details
            class="form-field"
          />
        </div>

        <!-- Email -->
        <div class="mb-6">
          <label class="form-label">Email</label>
          <v-text-field
            :value="userStore.user.email"
            readonly
            variant="outlined"
            density="compact"
            hide-details
            class="form-field email-field"
          />
        </div>

        <!-- Save Changes Button -->
        <v-btn
          class="save-button w-100 mb-6"
          :disabled="!hasChanges"
          @click="saveChanges"
        >
          Save Changes
        </v-btn>

        <!-- Account Information -->
        <div class="account-info mb-6">
          <h3 class="account-info-title mb-3">Account Information</h3>
          <div class="info-row">
            <span class="info-label">Member since:</span>
            <span class="info-value">{{ userStore.user.memberSince }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Lists created:</span>
            <span class="info-value">{{ userStore.user.listsCreated }}</span>
          </div>
        </div>
      </div>

      <!-- Bottom Actions -->
      <div class="profile-actions pa-6">
        <v-btn
          class="action-button logout-button"
          variant="text"
          prepend-icon="mdi-logout"
          @click="logout"
        >
          Log out
        </v-btn>
      </div>
    </div>
  </v-navigation-drawer>
</template>

<style scoped>
.profile-panel {
  border-left: 1px solid var(--text-terciary);
}

.profile-header {
  background-color: var(--primary-lightgrey);
  border-bottom: 1px solid var(--text-terciary);
}

.profile-avatar {
  background-color: var(--primary-green);
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.form-field :deep(.v-field__outline) {
  --v-field-border-opacity: 0.3;
}

.form-field :deep(.v-field--focused .v-field__outline) {
  --v-field-border-opacity: 1;
  color: var(--primary-green);
}

.email-field :deep(.v-field__input) {
  background-color: var(--primary-lightgrey);
  color: var(--text-secondary);
}

.save-button {
  background-color: var(--primary-green) !important;
  color: white !important;
  font-weight: 600;
  text-transform: none;
  height: 48px;
  border-radius: 8px;
  letter-spacing: normal;
}

.save-button:disabled {
  background-color: var(--text-terciary) !important;
  color: var(--text-secondary) !important;
}

.account-info {
  padding: 16px;
  background-color: var(--primary-lightgrey);
  border-radius: 8px;
}

.account-info-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-label {
  color: var(--text-secondary);
  font-size: 14px;
}

.info-value {
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 500;
}

.profile-actions {
  border-top: 1px solid var(--text-terciary);
}

.action-button {
  width: 100%;
  justify-content: flex-start;
  text-transform: none;
  font-weight: 500;
  height: 48px;
}

.logout-button {
  color: var(--delete-red) !important;
}
</style>