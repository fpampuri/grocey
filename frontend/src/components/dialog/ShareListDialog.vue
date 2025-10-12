<script setup lang="ts">
  import { computed, ref } from 'vue'
  import StandardButton from '../StandardButton.vue'
  import BaseDialog from './BaseDialog.vue'

  interface Props {
    modelValue: boolean
    listName?: string
    listId?: number
  }

  interface ShareData {
    emails: string[]
  }

  const props = withDefaults(defineProps<Props>(), {
    listName: 'Untitled List',
  })

  const emit = defineEmits<{
    'update:modelValue': [value: boolean]
    'share-list': [data: ShareData]
  }>()

  const dialogModel = computed({
    get: () => props.modelValue,
    set: (value: boolean) => emit('update:modelValue', value),
  })

  const currentEmail = ref('')
  const emailList = ref<string[]>([])
  const isSharing = ref(false)

  const isCurrentEmailValid = computed(() => {
    return currentEmail.value.trim() !== '' && isValidEmail(currentEmail.value)
  })

  const canShare = computed(() => {
    return emailList.value.length > 0 && !isSharing.value
  })

  function isValidEmail (email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return emailRegex.test(email)
  }

  function handleCancel () {
    resetForm()
    emit('update:modelValue', false)
  }

  function addEmail () {
    if (!isCurrentEmailValid.value) return

    const email = currentEmail.value.trim()
    if (!emailList.value.includes(email)) {
      emailList.value.push(email)
    }
    currentEmail.value = ''
  }

  function removeEmail (index: number) {
    emailList.value.splice(index, 1)
  }

  function resetForm () {
    currentEmail.value = ''
    emailList.value = []
    isSharing.value = false
  }

  async function handleShare () {
    if (!canShare.value) return

    isSharing.value = true

    const shareData: ShareData = {
      emails: [...emailList.value],
    }

    try {
      emit('share-list', shareData)
      resetForm()
      emit('update:modelValue', false)
    } catch (error) {
      console.error('Error sharing list:', error)
    } finally {
      isSharing.value = false
    }
  }

  function handleKeyPress (event: KeyboardEvent) {
    if (event.key === 'Enter') {
      event.preventDefault()
      addEmail()
    }
  }

  function handleClose () {
    if (!isSharing.value) {
      handleCancel()
    }
  }
</script>

<template>
  <BaseDialog v-model="dialogModel" max-width="500px" :persistent="isSharing">
    <!-- Dialog Header -->
    <div class="dialog-header">
      <h2 class="dialog-title">Share "{{ listName }}"</h2>
      <button
        v-if="!isSharing"
        aria-label="Close dialog"
        class="close-button"
        @click="handleClose"
      >
        <svg viewBox="0 0 24 24">
          <path
            d="M19 6.41L17.59 5L12 10.59L6.41 5L5 6.41L10.59 12L5 17.59L6.41 19L12 13.41L17.59 19L19 17.59L13.41 12L19 6.41Z"
          />
        </svg>
      </button>
    </div>

    <!-- Dialog Body -->
    <div class="dialog-body">
      <div class="share-info">
        <p class="share-description">
          Share your list with others by entering their email addresses. Click the + button to add each email to the list.
        </p>
      </div>

      <form @submit.prevent="addEmail">
        <!-- Email Input Field -->
        <div class="form-field">
          <label class="field-label" for="share-email">Add Email Address</label>
          <div class="email-input-container">
            <input
              id="share-email"
              v-model="currentEmail"
              class="text-input email-input"
              :disabled="isSharing"
              placeholder="Enter email address..."
              type="email"
              @keypress="handleKeyPress"
            >
            <button
              class="add-email-button"
              :disabled="!isCurrentEmailValid || isSharing"
              type="button"
              @click="addEmail"
            >
              <svg class="add-icon" viewBox="0 0 24 24">
                <path d="M19,13H13V19H11V13H5V11H11V5H13V11H19V13Z" />
              </svg>
            </button>
          </div>
          <div v-if="currentEmail && !isValidEmail(currentEmail)" class="error-text">
            Please enter a valid email address
          </div>
        </div>

        <!-- Email List -->
        <div v-if="emailList.length > 0" class="form-field">
          <label class="field-label">People to share with ({{ emailList.length }})</label>
          <div class="email-list">
            <div
              v-for="(email, index) in emailList"
              :key="index"
              class="email-item"
            >
              <span class="email-text">{{ email }}</span>
              <button
                aria-label="Remove email"
                class="remove-email-button"
                :disabled="isSharing"
                type="button"
                @click="removeEmail(index)"
              >
                <svg class="remove-icon" viewBox="0 0 24 24">
                  <path d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </form>
    </div>

    <!-- Dialog Footer -->
    <div class="dialog-footer">
      <button class="cancel-button" :disabled="isSharing" @click="handleCancel">
        Cancel
      </button>
      <StandardButton
        :disabled="!canShare"
        icon="mdi-share"
        :title="isSharing ? 'Sharing...' : `Share with ${emailList.length} ${emailList.length === 1 ? 'person' : 'people'}`"
        @click="handleShare"
      />
    </div>
  </BaseDialog>
</template>

<style scoped>
.share-description {
  margin-bottom: 24px;
  color: #666;
  line-height: 1.5;
}

.share-info {
  margin-bottom: 20px;
}

.error-text {
  color: #d32f2f;
  font-size: 14px;
  margin-top: 4px;
}

.email-input-container {
  display: flex;
  gap: 8px;
  align-items: stretch;
}

.email-input {
  flex: 1;
}

.add-email-button {
  background: var(--primary-green, #4caf50);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  min-width: 48px;
}

.add-email-button:hover:not(:disabled) {
  background: var(--primary-green-dark, #45a049);
  transform: translateY(-1px);
}

.add-email-button:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}

.add-icon {
  width: 20px;
  height: 20px;
  fill: currentColor;
}

.email-list {
  background: #f8f9fa;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 12px;
  max-height: 200px;
  overflow-y: auto;
}

.email-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  margin-bottom: 8px;
}

.email-item:last-child {
  margin-bottom: 0;
}

.email-text {
  font-size: 14px;
  color: #333;
  flex: 1;
}

.remove-email-button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s ease;
}

.remove-email-button:hover:not(:disabled) {
  background-color: #f5f5f5;
}

.remove-email-button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.remove-icon {
  width: 16px;
  height: 16px;
  fill: #666;
}

/* Form styling enhancements */
.form-field:last-child {
  margin-bottom: 0;
}

.text-input:invalid {
  border-color: #d32f2f;
}

.text-input:invalid:focus {
  box-shadow: 0 0 0 3px rgba(211, 47, 47, 0.1);
}

/* Responsive design */
@media (max-width: 480px) {
  .dialog-footer {
    flex-direction: column-reverse;
    gap: 8px;
  }

  .share-button,
  .cancel-button {
    width: 100%;
    justify-content: center;
  }
}
</style>
