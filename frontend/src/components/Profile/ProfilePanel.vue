<script setup lang="ts">
import { ref, computed, watch } from 'vue'
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

// Password change form data
const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const showCurrentPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

// Toast notifications
const toastMessage = ref('')
const toastType = ref<'success' | 'error'>('success')
const showToastNotification = ref(false)

// Computed to check if there are changes (only for editable fields)
const hasChanges = computed(() => {
  return (
    firstName.value !== userStore.user.firstName ||
    lastName.value !== userStore.user.lastName
  )
})

// Computed to check if password can be changed
const canChangePassword = computed(() => {
  return (
    currentPassword.value.trim() !== '' &&
    newPassword.value.trim() !== '' &&
    confirmPassword.value.trim() !== '' &&
    newPassword.value === confirmPassword.value &&
    newPassword.value.length >= 8
  )
})

// Computed to get password validation messages
const passwordValidationMessage = computed(() => {
  if (currentPassword.value.trim() === '' && newPassword.value.trim() === '' && confirmPassword.value.trim() === '') {
    return 'Fill in all password fields to continue'
  }
  
  if (currentPassword.value.trim() === '') {
    return 'Enter your current password'
  }
  
  if (newPassword.value.trim() === '') {
    return 'Enter a new password'
  }
  
  if (newPassword.value.length > 0 && newPassword.value.length < 8) {
    return 'New password must be at least 8 characters long'
  }
  
  if (confirmPassword.value.trim() === '') {
    return 'Confirm your new password'
  }
  
  if (newPassword.value !== confirmPassword.value) {
    return 'Passwords do not match'
  }
  
  return 'Ready to change password'
})

watch(
  () => userStore.user.firstName,
  (value) => {
    if (!hasChanges.value) {
      firstName.value = value
    }
  },
  { immediate: true }
)

watch(
  () => userStore.user.lastName,
  (value) => {
    if (!hasChanges.value) {
      lastName.value = value
    }
  },
  { immediate: true }
)

watch(
  () => props.modelValue,
  (isOpen) => {
    if (isOpen) {
      if (!userStore.profileLoaded && !userStore.loading) {
        userStore.fetchUserProfile()
      }
      // Only reset form fields when opening, not password fields
      firstName.value = userStore.user.firstName
      lastName.value = userStore.user.lastName
    } else {
      // Reset everything when closing
      firstName.value = userStore.user.firstName
      lastName.value = userStore.user.lastName
      currentPassword.value = ''
      newPassword.value = ''
      confirmPassword.value = ''
      showCurrentPassword.value = false
      showNewPassword.value = false
      showConfirmPassword.value = false
    }
  },
  { immediate: true }
)

function showToast(message: string, type: 'success' | 'error') {
  toastMessage.value = message
  toastType.value = type
  showToastNotification.value = true
  setTimeout(() => {
    showToastNotification.value = false
  }, 2000)
}

function closePanel() {
  emit('update:modelValue', false)
  // Reset form to original values
  firstName.value = userStore.user.firstName
  lastName.value = userStore.user.lastName
  // Reset password fields
  currentPassword.value = ''
  newPassword.value = ''
  confirmPassword.value = ''
  showCurrentPassword.value = false
  showNewPassword.value = false
  showConfirmPassword.value = false
}

function saveChanges() {
  userStore
    .saveChanges({
      firstName: firstName.value,
      lastName: lastName.value,
    })
    .then(() => {
      // Show success toast
      showToast('Profile updated successfully!', 'success')
    })
    .catch((err) => {
      console.error('Failed to update profile', err)
      showToast('Failed to update profile. Please try again.', 'error')
    })
}

async function changePassword() {
  // Check if new password is same as current password
  if (currentPassword.value === newPassword.value) {
    showToast('New password must be different from your current password', 'error')
    return
  }

  try {
    const { UserApi } = await import('@/services')
    await UserApi.changePassword({
      currentPassword: currentPassword.value,
      newPassword: newPassword.value,
    })
    // Reset password fields on success
    currentPassword.value = ''
    newPassword.value = ''
    confirmPassword.value = ''
    showCurrentPassword.value = false
    showNewPassword.value = false
    showConfirmPassword.value = false
    showToast('Password changed successfully!', 'success')
  } catch (err) {
    console.error('Failed to change password', err)
    showToast('Failed to change password. Please check your current password.', 'error')
  }
}

async function logout() {
  await userStore.logout()
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
        <!-- User Information -->
        <div class="user-info mb-6">
          <h3 class="user-info-title mb-3">Personal Information</h3>
          
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
          <div class="mb-4">
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
            class="save-button w-100"
            :disabled="!hasChanges || userStore.loading"
            :loading="userStore.loading"
            @click="saveChanges"
          >
            Save Changes
          </v-btn>
        </div>

        <!-- Change Password -->
        <div class="password-change mb-6">
          <h3 class="password-change-title mb-3">Change Password</h3>
          
          <div class="mb-4">
            <label class="form-label">Current Password</label>
            <v-text-field
              v-model="currentPassword"
              :type="showCurrentPassword ? 'text' : 'password'"
              :append-inner-icon="showCurrentPassword ? 'mdi-eye' : 'mdi-eye-off'"
              @click:append-inner="showCurrentPassword = !showCurrentPassword"
              variant="outlined"
              density="compact"
              hide-details
              class="form-field"
              placeholder="Enter your current password"
            />
          </div>

          <div class="mb-4">
            <label class="form-label">New Password</label>
            <v-text-field
              v-model="newPassword"
              :type="showNewPassword ? 'text' : 'password'"
              :append-inner-icon="showNewPassword ? 'mdi-eye' : 'mdi-eye-off'"
              @click:append-inner="showNewPassword = !showNewPassword"
              variant="outlined"
              density="compact"
              hide-details
              class="form-field"
              placeholder="Enter your new password"
            />
          </div>

          <div class="mb-4">
            <label class="form-label">Confirm New Password</label>
            <v-text-field
              v-model="confirmPassword"
              :type="showConfirmPassword ? 'text' : 'password'"
              :append-inner-icon="showConfirmPassword ? 'mdi-eye' : 'mdi-eye-off'"
              @click:append-inner="showConfirmPassword = !showConfirmPassword"
              variant="outlined"
              density="compact"
              hide-details
              class="form-field"
              placeholder="Confirm your new password"
            />
          </div>

          <!-- Password Validation Message -->
          <div class="password-validation-message mb-3">
            <v-icon 
              :icon="canChangePassword ? 'mdi-check-circle' : 'mdi-information'"
              :color="canChangePassword ? 'success' : 'warning'"
              size="small"
              class="mr-2"
            />
            <span 
              :class="canChangePassword ? 'text-success' : 'text-warning'"
              class="validation-text"
            >
              {{ passwordValidationMessage }}
            </span>
          </div>

          <v-btn
            class="change-password-button w-100"
            :disabled="!canChangePassword || userStore.loading"
            :loading="userStore.loading"
            @click="changePassword"
          >
            Change Password
          </v-btn>
        </div>
      </div>

      <!-- Bottom Actions -->
      <div class="profile-actions pa-6">

        <v-btn
          class="logout-button w-100"
          prepend-icon="mdi-logout"
          @click="logout"
        >
          Log out
        </v-btn>
      </div>
    </div>

    <!-- Toast Notification -->
    <v-snackbar
      v-model="showToastNotification"
      :color="toastType === 'success' ? 'success' : 'error'"
      location="bottom right"
      timeout="2000"
    >
      {{ toastMessage }}
      <template #actions>
        <v-btn
          color="white"
          variant="text"
          @click="showToastNotification = false"
        >
          Close
        </v-btn>
      </template>
    </v-snackbar>
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
  background-color: rgba(0, 0, 0, 0.12) !important;
  color: var(--text-primary) !important;
}

.user-info {
  padding: 16px;
  background-color: var(--primary-lightgrey);
  border-radius: 8px;
}

.user-info-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.password-change {
  padding: 16px;
  background-color: var(--primary-lightgrey);
  border-radius: 8px;
}

.password-change-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.change-password-button {
  background-color: var(--primary-green) !important;
  color: white !important;
  font-weight: 600;
  text-transform: none;
  height: 48px;
  border-radius: 8px;
  letter-spacing: normal;
}

.change-password-button:disabled {
  background-color: rgba(0, 0, 0, 0.12) !important;
  color: var(--text-primary) !important;
}

.password-validation-message {
  display: flex;
  align-items: center;
  font-size: 14px;
  line-height: 1.4;
}

.validation-text {
  font-weight: 500;
}

.text-success {
  color: var(--primary-green) !important;
}

.text-warning {
  color: var(--delete-red) !important;
}

.profile-actions {
  border-top: 1px solid var(--text-terciary);
}

.logout-button {
  background-color: var(--delete-red) !important;
  color: white !important;
  font-weight: 600;
  text-transform: none;
  height: 48px;
  border-radius: 8px;
  letter-spacing: normal;
}

.logout-button:hover {
  background-color: rgba(244, 67, 54, 0.8) !important;
}
</style>
