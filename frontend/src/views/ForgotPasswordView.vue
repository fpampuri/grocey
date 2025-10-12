<script setup lang="ts">
  import { computed, ref } from 'vue'
  import { useRouter } from 'vue-router'
  import { UserApi } from '@/services'

  type VuetifyForm = {
    validate: () => Promise<{ valid: boolean }>
    resetValidation: () => void
  }

  const router = useRouter()

  const formRef = ref<VuetifyForm | null>(null)
  const email = ref('')
  const isSubmitting = ref(false)
  const error = ref('')
  const successMessage = ref('')
  const resetCode = ref('')
  const newPassword = ref('')
  const confirmPassword = ref('')
  const showPassword = ref(false)
  const mode = ref<'request' | 'reset'>('request')

  const isRequestMode = computed(() => mode.value === 'request')
  const isResetMode = computed(() => mode.value === 'reset')

  const emailRules = [
    (value: string) => !!value || 'Enter your email address',
    (value: string) => /.+@.+\..+/.test(value) || 'Enter a valid email',
  ]

  const resetCodeRules = [
    (value: string) => !!value || 'Enter the reset code',
    (value: string) => value.length >= 4 || 'Code must be at least 4 characters',
  ]

  const passwordRules = [
    (value: string) => !!value || 'Enter your new password',
    (value: string) => value.length >= 6 || 'Must be at least 6 characters',
  ]

  const confirmPasswordRules = [
    (value: string) => !!value || 'Confirm your new password',
    (value: string) => value === newPassword.value || 'Passwords must match',
  ]

  function switchToResetMode () {
    mode.value = 'reset'
    error.value = ''
    formRef.value?.resetValidation()
  }

  function backToLogin () {
    router.push({ name: 'login' })
  }

  async function handleRequestReset () {
    error.value = ''
    successMessage.value = ''

    const validation = await formRef.value?.validate()
    if (!validation?.valid) return

    isSubmitting.value = true

    try {
      await UserApi.requestPasswordRecovery({
        email: email.value,
      })

      successMessage.value = `We sent a password reset code to ${email.value}. Check your email and enter the code below.`
      switchToResetMode()
    } catch (error_: unknown) {
      console.error('Password recovery request error', error_)
      error.value = error_ instanceof Error ? error_.message : 'Unable to send password reset email. Please try again.'
    } finally {
      isSubmitting.value = false
    }
  }

  async function handleResetPassword () {
    error.value = ''
    successMessage.value = ''

    const validation = await formRef.value?.validate()
    if (!validation?.valid) return

    isSubmitting.value = true

    try {
      await UserApi.resetPassword({
        code: resetCode.value,
        password: newPassword.value,
      })

      successMessage.value = 'Password reset successfully! You can now sign in with your new password.'

      // Redirect to login after 2 seconds
      setTimeout(() => {
        router.push({ name: 'login' })
      }, 2000)
    } catch (error_: unknown) {
      console.error('Password reset error', error_)
      error.value = error_ instanceof Error ? error_.message : 'Unable to reset password. Please check your code and try again.'
    } finally {
      isSubmitting.value = false
    }
  }

  function handleSubmit () {
    if (isRequestMode.value) {
      handleRequestReset()
    } else {
      handleResetPassword()
    }
  }
</script>

<template>
  <v-container class="forgot-password-view" fluid>
    <div class="forgot-password-content">
      <section class="forgot-password-hero">
        <div class="hero-heading">
          <v-icon class="hero-logo" icon="mdi-cart" />
          <h1>Reset Your Password</h1>
        </div>
        <p>{{ isRequestMode ? "Enter your email to receive a password reset code." : "Enter the code and your new password." }}</p>
      </section>

      <v-card class="forgot-password-card" elevation="10">
        <v-card-title class="text-h5 font-weight-bold text-primary">
          {{ isRequestMode ? "Forgot Password" : "Reset Password" }}
        </v-card-title>
        <v-card-subtitle class="text-subtitle-1 mb-4">
          {{ isRequestMode
            ? "We'll send you a code to reset your password."
            : "Enter the code from your email and choose a new password."
          }}
        </v-card-subtitle>

        <v-card-text>
          <v-alert
            v-if="successMessage"
            class="mb-4"
            type="success"
            variant="tonal"
          >
            {{ successMessage }}
          </v-alert>

          <v-alert
            v-if="error"
            class="mb-4"
            type="error"
            variant="tonal"
          >
            {{ error }}
          </v-alert>

          <v-form ref="formRef" @submit.prevent="handleSubmit">
            <v-text-field
              v-if="isRequestMode"
              v-model="email"
              class="mb-4"
              clearable
              color="primary"
              density="comfortable"
              label="Email address"
              prepend-inner-icon="mdi-email"
              required
              :rules="emailRules"
              type="email"
              variant="outlined"
            />

            <template v-if="isResetMode">
              <v-text-field
                v-model="resetCode"
                autocomplete="one-time-code"
                class="mb-4"
                color="primary"
                density="comfortable"
                label="Reset code"
                prepend-inner-icon="mdi-shield-key"
                required
                :rules="resetCodeRules"
                variant="outlined"
              />

              <v-text-field
                v-model="newPassword"
                :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                class="mb-2"
                color="primary"
                density="comfortable"
                label="New password"
                prepend-inner-icon="mdi-lock"
                required
                :rules="passwordRules"
                :type="showPassword ? 'text' : 'password'"
                variant="outlined"
                @click:append-inner="showPassword = !showPassword"
              />

              <v-text-field
                v-model="confirmPassword"
                :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                class="mb-4"
                color="primary"
                density="comfortable"
                label="Confirm new password"
                prepend-inner-icon="mdi-lock-check"
                required
                :rules="confirmPasswordRules"
                :type="showPassword ? 'text' : 'password'"
                variant="outlined"
                @click:append-inner="showPassword = !showPassword"
              />
            </template>

            <v-btn
              block
              class="submit-button mt-2"
              :disabled="isSubmitting"
              height="48"
              :loading="isSubmitting"
              type="submit"
            >
              {{ isRequestMode ? "Send Reset Code" : "Reset Password" }}
            </v-btn>
          </v-form>

          <div class="actions mt-6 text-center">
            <span>Remember your password?</span>
            <v-btn
              class="back-button"
              variant="text"
              @click="backToLogin"
            >
              Back to Sign In
            </v-btn>
          </div>
        </v-card-text>
      </v-card>
    </div>
  </v-container>
</template>

<style scoped>
  .forgot-password-view {
    min-height: calc(100vh - 72px);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 48px 16px;
    background: linear-gradient(135deg, rgba(7, 138, 7, 0.12), rgba(76, 175, 80, 0.08));
  }

  .forgot-password-hero {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .forgot-password-hero p {
    color: var(--text-secondary);
    font-size: 18px;
    line-height: 1.5;
    max-width: 420px;
    margin-top: 12px;
  }

  .hero-heading {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 12px;
  }

  .hero-heading h1 {
    color: var(--primary-green-dark);
    font-size: 36px;
    font-weight: 700;
    margin: 0;
  }

  .hero-logo {
    font-size: 64px;
    color: var(--primary-green);
  }

  .forgot-password-content {
    width: 100%;
    max-width: 960px;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 48px;
    align-items: center;
  }

  .forgot-password-card {
    padding: 28px 32px 36px;
    border-radius: 24px;
    box-shadow: 0 12px 32px rgba(7, 138, 7, 0.12);
  }

  .forgot-password-card :deep(.text-primary) {
    color: var(--primary-green) !important;
  }

  .actions {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    font-size: 0.95rem;
    color: var(--text-secondary);
  }

  .back-button {
    text-transform: none;
    font-weight: 600;
    color: var(--primary-green) !important;
    padding: 0;
    min-width: unset;
  }

  .submit-button {
    background-color: var(--primary-green);
    color: #fff;
    font-weight: 600;
    letter-spacing: 0.025em;
  }

  .submit-button:hover {
    background-color: var(--primary-green-dark);
  }

  @media (max-width: 768px) {
    .forgot-password-view {
      padding: 32px 12px;
    }

    .forgot-password-card {
      padding: 24px;
    }

    .forgot-password-hero h1 {
      font-size: 28px;
    }

    .forgot-password-hero p {
      font-size: 16px;
    }
  }
</style>
