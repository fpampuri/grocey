<script setup lang="ts">


    const props = defineProps({
      modelValue: { type: String, default: "" },
      placeholder: { type: String, required: true },
    });

    const emit = defineEmits(["update:modelValue"]);

    function handleInput(event: Event) {
        const target = event.target as HTMLInputElement;
        emit("update:modelValue", target.value);
    }

    function clearSearch() {
        emit("update:modelValue", "");
    }
</script>

<template>
    <div class="search-container">
        <div class="search-input-wrapper">
            <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                <path d="M9.5,3A6.5,6.5 0 0,1 16,9.5C16,11.11 15.41,12.59 14.44,13.73L14.71,14H15.5L20.5,19L19,20.5L14,15.5V14.71L13.73,14.44C12.59,15.41 11.11,16 9.5,16A6.5,6.5 0 0,1 3,9.5A6.5,6.5 0 0,1 9.5,3M9.5,5C7,5 5,7 5,9.5C5,12 7,14 9.5,14C12,14 14,12 14,9.5C14,7 12,5 9.5,5Z" />
            </svg>
            <input
                type="text"
                :value="modelValue"
                :placeholder="placeholder"
                @input="handleInput"
                class="search-input"
            />
            <button 
                v-if="modelValue" 
                @click="clearSearch" 
                class="clear-button"
                type="button"
            >
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                    <path d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z" />
                </svg>
            </button>
        </div>
    </div>
</template>

<style scoped>
    .search-container {
        position: relative;
        width: 100%;
    }

    .search-input-wrapper {
        position: relative;
        display: flex;
        align-items: center;
        background-color: rgb(201, 245, 193);
        border: 2px solid var(--primary-green-light);
        border-radius: 24px;
        transition: all 0.2s ease-in-out;
    }

    .search-input-wrapper:focus-within {
        background-color: rgb(201, 245, 193);
        border-color: var(--primary-green);
        box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
    }

    .search-icon {
        position: absolute;
        left: 16px;
        width: 20px;
        height: 20px;
        fill: black;
        pointer-events: none;
        z-index: 1;
    }

    .search-input {
        width: 100%;
        padding: 12px 16px 12px 48px;
        border: none;
        outline: none;
        background: transparent;
        font-size: 16px;
        color: black;
        border-radius: 24px;
    }

    .search-input::placeholder {
        color: #999;
    }

    .clear-button {
        position: absolute;
        right: 12px;
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

    .clear-button:hover {
        background-color: rgba(0, 0, 0, 0.1);
    }

    .clear-button svg {
        width: 16px;
        height: 16px;
        fill: black;
    }
</style>