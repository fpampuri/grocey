<script setup lang="ts">
  import { ref, computed } from "vue";
  import { useRouter } from "vue-router";
  import { isAxiosError } from "axios";
  import apiClient from "@/services/api";
  import { useUserStore } from "@/stores/user";

  type VuetifyForm = {
    validate: () => Promise<{ valid: boolean }>;
    resetValidation: () => void;
  };
  type Mode = "login" | "register" | "verify";

  const router = useRouter();
  const userStore = useUserStore();

  const formRef = ref<VuetifyForm | null>(null);
  const mode = ref<Mode>("login");
  const email = ref("");
  const password = ref("");
  const confirmPassword = ref("");
  const firstName = ref("");
  const lastName = ref("");
  const rememberMe = ref(false);
  const showPassword = ref(false);
  const isSubmitting = ref(false);
  const authError = ref("");
  const successMessage = ref("");
  const verificationCode = ref("");
  const verificationEmail = ref("");
  const isResending = ref(false);
  const resendMessage = ref("");
  const resendError = ref("");

  const isLoginMode = computed(() => mode.value === "login");
  const isRegisterMode = computed(() => mode.value === "register");
  const isVerifyMode = computed(() => mode.value === "verify");
  const formSubtitle = computed(() => {
    if (isRegisterMode.value) {
      return "Fill out the details below to start using Grocey.";
    }
    if (isVerifyMode.value) {
      return verificationEmail.value
        ? `Enter the verification code we sent to ${verificationEmail.value}.`
        : "Enter the verification code we emailed you.";
    }
    return "Enter your credentials to continue.";
  });

  const emailRules = [
    (value: string) => !!value || "Enter your email address",
    (value: string) => /.+@.+\..+/.test(value) || "Enter a valid email",
  ];

  const passwordRules = [
    (value: string) => !!value || "Enter your password",
    (value: string) => value.length >= 6 || "Must be at least 6 characters",
  ];

  const confirmPasswordRules = [
    (value: string) => !!value || "Confirm your password",
    (value: string) => value === password.value || "Passwords must match",
  ];

  const firstNameRules = [(value: string) => !!value || "Enter your first name"];
  const lastNameRules = [(value: string) => !!value || "Enter your last name"];
  const verificationCodeRules = [
    (value: string) => !!value || "Enter the verification code",
    (value: string) => value.length >= 4 || "Code must be at least 4 characters",
  ];

  function changeMode(next: Mode, { keepSuccess = false } = {}) {
    mode.value = next;
    authError.value = "";
    if (!keepSuccess) {
      successMessage.value = "";
    }
    isSubmitting.value = false;
    formRef.value?.resetValidation();

    if (next === "register") {
      rememberMe.value = false;
      password.value = "";
      confirmPassword.value = "";
    }

    if (next !== "register") {
      firstName.value = "";
      lastName.value = "";
      confirmPassword.value = "";
    }

    if (next !== "verify") {
      verificationCode.value = "";
      resendMessage.value = "";
      resendError.value = "";
      isResending.value = false;
    }
  }

  function switchToRegister() {
    changeMode("register");
  }

  function switchToLogin() {
    changeMode("login");
  }

  function openVerification(emailAddress?: string, message?: string) {
    changeMode("verify", { keepSuccess: true });
    if (emailAddress) {
      verificationEmail.value = emailAddress;
    } else if (!verificationEmail.value) {
      verificationEmail.value = email.value;
    }
    verificationCode.value = "";
    resendMessage.value = "";
    resendError.value = "";
    if (message) {
      successMessage.value = message;
    } else if (verificationEmail.value) {
      successMessage.value = `Enter the verification code we sent to ${verificationEmail.value}.`;
    } else if (!successMessage.value) {
      successMessage.value = "Enter the verification code we emailed you.";
    }
  }

  async function resendVerification() {
    resendMessage.value = "";
    resendError.value = "";

    const targetEmail = verificationEmail.value || email.value;
    if (!targetEmail) {
      resendError.value = "Enter your email to resend the verification code.";
      return;
    }

    if (!/.+@.+\..+/.test(targetEmail)) {
      resendError.value = "Enter a valid email address.";
      return;
    }

    isResending.value = true;
    try {
      await apiClient.post("/users/send-verification", {}, {
        params: { email: targetEmail },
      });
      verificationEmail.value = targetEmail;
      resendMessage.value = `We sent a new verification code to ${targetEmail}.`;
    } catch (error: unknown) {
      console.error("Resend verification error", error);
      if (isAxiosError(error)) {
        resendError.value =
          (error.response?.data as { message?: string })?.message ??
          "Unable to resend the verification code.";
      } else if (error instanceof Error) {
        resendError.value = error.message;
      } else {
        resendError.value = "Unable to resend the verification code.";
      }
    } finally {
      isResending.value = false;
    }
  }

  async function handleSubmit() {
    authError.value = "";

    const validation = await formRef.value?.validate();
    if (!validation?.valid) return;

    isSubmitting.value = true;

    try {
      if (isRegisterMode.value) {
        await apiClient.post("/users/register", {
          name: firstName.value,
          surname: lastName.value,
          email: email.value,
          password: password.value,
        });

        successMessage.value =
          "Account created! Enter the verification code we emailed to activate your account.";
        openVerification(email.value, successMessage.value);
        password.value = "";
        confirmPassword.value = "";
      } else if (isVerifyMode.value) {
        await apiClient.post("/users/verify-account", {
          code: verificationCode.value,
        });

        successMessage.value =
          "Account verified! You can now sign in with your email and password.";
        changeMode("login", { keepSuccess: true });
        verificationCode.value = "";
      } else {
        const { data } = await apiClient.post("/users/login", {
          email: email.value,
          password: password.value,
        });

        if (!data?.token) {
          throw new Error("Invalid response from server.");
        }

        userStore.setToken(data.token);
        await userStore.fetchUserProfile();

        if (!userStore.profileLoaded) {
          throw new Error(userStore.error ?? "Unable to load your profile.");
        }

        router.push({ name: "lists" });
      }
    } catch (error: unknown) {
      console.error("Authentication error", error);
      if (isLoginMode.value) {
        userStore.setToken(null);
      }

      let message = "We couldn't complete your request. Please try again.";
      if (isAxiosError(error)) {
        message =
          (error.response?.data as { message?: string })?.message ?? message;
      } else if (error instanceof Error) {
        message = error.message;
      }

      if (
        isLoginMode.value &&
        message.toLowerCase().includes("not verified")
      ) {
        authError.value = message;
        openVerification(email.value);
        return;
      }

      authError.value = message;
    } finally {
      isSubmitting.value = false;
    }
  }
</script>

<template>
  <v-container class="login-view" fluid>
    <div class="login-content">
      <section class="login-hero">
        <div class="hero-heading">
          <v-icon icon="mdi-cart" class="hero-logo" />
          <h1>Welcome to Grocey</h1>
        </div>
        <p>Organize your lists and shopping with a simple and smart experience.</p>
      </section>

      <v-card class="login-card" elevation="10">
        <v-card-title class="text-h5 font-weight-bold text-primary">
          {{ isRegisterMode ? "Create Account" : isVerifyMode ? "Verify Account" : "Sign In" }}
        </v-card-title>
        <v-card-subtitle class="text-subtitle-1 mb-4">
          {{ formSubtitle }}
        </v-card-subtitle>

        <v-card-text>
          <v-alert
            v-if="successMessage && !isRegisterMode"
            type="success"
            variant="tonal"
            class="mb-4"
          >
            {{ successMessage }}
          </v-alert>

          <v-alert
            v-if="authError"
            type="error"
            variant="tonal"
            class="mb-4"
          >
            {{ authError }}
          </v-alert>

          <v-form ref="formRef" @submit.prevent="handleSubmit">
            <div v-if="isRegisterMode" class="name-fields mb-2">
              <v-text-field
                v-model="firstName"
                label="First name"
                prepend-inner-icon="mdi-account"
                density="comfortable"
                variant="outlined"
                color="primary"
                :rules="firstNameRules"
                required
              />

              <v-text-field
                v-model="lastName"
                label="Last name"
                prepend-inner-icon="mdi-account"
                density="comfortable"
                variant="outlined"
                color="primary"
                :rules="lastNameRules"
                required
              />
            </div>

            <v-text-field
              v-model="email"
              label="Email address"
              type="email"
              prepend-inner-icon="mdi-email"
              density="comfortable"
              variant="outlined"
              class="mb-4"
              color="primary"
              clearable
              :rules="emailRules"
              required
            />

            <v-text-field
              v-if="isVerifyMode"
              v-model="verificationCode"
              label="Verification code"
              prepend-inner-icon="mdi-shield-check"
              density="comfortable"
              variant="outlined"
              class="mb-2"
              color="primary"
              autocomplete="one-time-code"
              :rules="verificationCodeRules"
              required
            />

            <v-text-field
              v-if="!isVerifyMode"
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              label="Password"
              prepend-inner-icon="mdi-lock"
              :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
              @click:append-inner="showPassword = !showPassword"
              density="comfortable"
              variant="outlined"
              class="mb-2"
              color="primary"
              :rules="passwordRules"
              required
            />

            <v-text-field
              v-if="isRegisterMode"
              v-model="confirmPassword"
              :type="showPassword ? 'text' : 'password'"
              label="Confirm password"
              prepend-inner-icon="mdi-lock-check"
              :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
              @click:append-inner="showPassword = !showPassword"
              density="comfortable"
              variant="outlined"
              class="mb-2"
              color="primary"
              :rules="confirmPasswordRules"
              required
            />

            <div v-if="isVerifyMode" class="resend-section">
              <span class="resend-label">Didn't receive the email?</span>
              <v-btn
                type="button"
                variant="text"
                class="resend-button"
                :loading="isResending"
                @click.prevent="resendVerification"
              >
                Resend verification code
              </v-btn>
              <div v-if="resendMessage" class="resend-feedback success">
                {{ resendMessage }}
              </div>
              <div v-if="resendError" class="resend-feedback error">
                {{ resendError }}
              </div>
            </div>

            <div v-if="isLoginMode" class="helpers">
              <v-checkbox
                v-model="rememberMe"
                label="Remember me"
                hide-details
                density="compact"
              />

              <RouterLink to="/forgot-password" class="forgot-link">
                Forgot your password?
              </RouterLink>
            </div>

            <v-btn
              type="submit"
              class="login-button mt-6"
              block
              height="48"
              :loading="isSubmitting"
              :disabled="isSubmitting"
            >
              {{
                isRegisterMode
                  ? "Create Account"
                  : isVerifyMode
                    ? "Verify Account"
                    : "Sign In"
              }}
            </v-btn>
          </v-form>

          <div class="toggle-account mt-6 text-center">
            <template v-if="isVerifyMode">
              <span>Already verified?</span>
              <v-btn
                variant="text"
                class="toggle-button"
                @click="switchToLogin"
              >
                Sign In
              </v-btn>
            </template>
            <template v-else>
              <span>
                {{
                  isRegisterMode
                    ? "Already have an account?"
                    : "Don't have an account yet?"
                }}
              </span>
              <v-btn
                variant="text"
                class="toggle-button"
                @click="isRegisterMode ? switchToLogin() : switchToRegister()"
              >
                {{ isRegisterMode ? "Sign In" : "Create one" }}
              </v-btn>
            </template>
          </div>
        </v-card-text>
      </v-card>
    </div>
  </v-container>
</template>

<style scoped>
  .login-view {
    min-height: calc(100vh - 72px);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 48px 16px;
    background: linear-gradient(135deg, rgba(7, 138, 7, 0.12), rgba(76, 175, 80, 0.08));
  }

  .login-hero {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .login-hero p {
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

  .login-content {
    width: 100%;
    max-width: 960px;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 48px;
    align-items: center;
  }

  .login-card {
    padding: 28px 32px 36px;
    border-radius: 24px;
    box-shadow: 0 12px 32px rgba(7, 138, 7, 0.12);
  }

  .login-card :deep(.text-primary) {
    color: var(--primary-green) !important;
  }

  .helpers {
    margin-top: 8px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }

  .forgot-link {
    color: var(--primary-green);
    font-weight: 600;
    text-decoration: none;
    transition: color 0.2s ease;
  }

  .forgot-link:hover {
    color: var(--primary-green-dark);
    text-decoration: underline;
  }

  .name-fields {
    display: grid;
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .toggle-account {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    font-size: 0.95rem;
    color: var(--text-secondary);
  }

  .toggle-button {
    text-transform: none;
    font-weight: 600;
    color: var(--primary-green) !important;
    padding: 0;
    min-width: unset;
  }

  .resend-section {
    margin-top: 8px;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .resend-label {
    color: var(--text-secondary);
    font-size: 0.9rem;
  }

  .resend-button {
    text-transform: none;
    font-weight: 600;
    color: var(--primary-green) !important;
    padding: 0;
    min-width: unset;
  }

  .resend-feedback {
    font-size: 0.9rem;
  }

  .resend-feedback.success {
    color: var(--primary-green);
  }

  .resend-feedback.error {
    color: #d32f2f;
  }

  .login-button {
    background-color: var(--primary-green);
    color: #fff;
    font-weight: 600;
    letter-spacing: 0.025em;
  }

  .login-button:hover {
    background-color: var(--primary-green-dark);
  }

  @media (max-width: 768px) {
    .login-view {
      padding: 32px 12px;
    }

    .login-card {
      padding: 24px;
    }

    .login-hero h1 {
      font-size: 28px;
    }

    .login-hero p {
      font-size: 16px;
    }
  }

  @media (min-width: 600px) {
    .name-fields {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }
</style>
