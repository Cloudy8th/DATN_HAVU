<template>
    <input
        ref="input"
        type="text"
        v-model="inputValue"
        :required="props.required"
    />
</template>

<script setup>
import { ref, watch } from "vue";

const props = defineProps({
    required: {
        type: Boolean,
        default: false,
    },
    modelValue: {},
});
const emit = defineEmits(["update:modelValue"]);
const input = ref(null);
const inputValue = ref(props.modelValue);

watch(
    () => inputValue.value,
    (newValue) => {
        emit("update:modelValue", newValue);
    }
);
watch(
    () => props.modelValue,
    (newValue) => {
        inputValue.value = newValue;
    }
);

// ------------- Hàm xử lý -------------------

const focus = () => {
    input.value.focus();
};
defineExpose({
    focus,
});
</script>

<style scoped>
input {
    height: var(--height-btn-default);
    box-sizing: border-box;
    border-radius: var(--border-radius);
    border: 1px solid var(--color-border);
    outline: none;
    padding: 0 10px;
    width: 100%;
}

input:focus {
    border: 1px solid var(--color-primary);
}
</style>
