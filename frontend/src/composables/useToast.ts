import { ref } from 'vue';

export function useToast() {
  const showToast = ref(false);
  const toastMessage = ref('');
  const toastType = ref<'success' | 'error'>('success');

  function showSuccess(message: string) {
    toastMessage.value = message;
    toastType.value = 'success';
    showToast.value = true;
  }

  function showError(message: string) {
    toastMessage.value = message;
    toastType.value = 'error';
    showToast.value = true;
  }

  return {
    showToast,
    toastMessage,
    toastType,
    showSuccess,
    showError,
  };
}