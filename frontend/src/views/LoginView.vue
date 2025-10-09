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
  type Mode = "login" | "register";

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

  const isRegisterMode = computed(() => mode.value === "register");

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
    } else {
      confirmPassword.value = "";
      firstName.value = "";
      lastName.value = "";
    }
  }

  function switchToRegister() {
    changeMode("register");
  }

  function switchToLogin() {
    changeMode("login");
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

        changeMode("login", { keepSuccess: true });
        successMessage.value =
          "Account created! Check your email for the verification code before signing in.";
        password.value = "";
        confirmPassword.value = "";
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
      console.error("Login error", error);
      userStore.setToken(null);

      if (isAxiosError(error)) {
        authError.value =
          (error.response?.data as { message?: string })?.message ??
          "We couldn't log you in. Please check your credentials.";
      } else if (error instanceof Error) {
        authError.value = error.message;
      } else {
        authError.value = "We couldn't log you in. Please try again.";
      }
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
          {{ isRegisterMode ? "Create Account" : "Sign In" }}
        </v-card-title>
        <v-card-subtitle class="text-subtitle-1 mb-4">
          {{
            isRegisterMode
              ? "Fill out the details below to start using Grocey."
              : "Enter your credentials to continue."
          }}
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

            <div class="helpers">
              <v-checkbox
                v-if="!isRegisterMode"
                v-model="rememberMe"
                label="Remember me"
                hide-details
                density="compact"
              />

              <RouterLink v-if="!isRegisterMode" to="/forgot-password" class="forgot-link">
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
              {{ isRegisterMode ? "Create Account" : "Sign In" }}
            </v-btn>
          </v-form>

          <div class="toggle-account mt-6 text-center">
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
