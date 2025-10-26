<template>
    <div v-if="type === 'checkbox'" class="form-check">
        <input
            :class="[`form-check-input`]"
            :indeterminate="indeterminate"
            :value="value"
            v-model="inputValue"
            type="checkbox"
            :id="id !== null ? id : uid"
            :name="name"
        />
        <label
            v-if="label !== null"
            :class="[`form-check-label`]"
            :for="id !== null ? id : uid"
            >{{ label }}</label
        >
    </div>
    <div v-else-if="type === 'radio'" class="form-check">
        <input
            :class="[`form-check-input`]"
            :value="value"
            v-model="inputValue"
            type="radio"
            :id="id !== null ? id : uid"
            :name="name"
        />
        <label
            v-if="label !== null"
            :class="[`form-check-label`]"
            :for="id !== null ? id : uid"
            >{{ label }}</label
        >
    </div>
</template>

<script setup>
import { watch, ref } from "vue";
import _ from "lodash";

// ------------------------------ Props ---------------------------------
const props = defineProps({
    type: {
        type: String,
        default: "checkbox",
    },
    label: {
        type: String,
        default: null,
    },
    modelValue: {},
    name: {
        type: String,
        default: null,
    },
    value: null,
    size: {
        type: String,
        default: "18",
    },
    id: {
        type: String,
        default: null,
    },
    checked: {
        type: Boolean,
        default: false,
    },
    indeterminate: {
        type: Boolean,
        default: false,
    },
});

// ------------------------------ Khai báo biến --------------------------
const uid = _.uniqueId();
const inputValue = ref(props.modelValue);
const emits = defineEmits(["update:modelValue"]);

// ------------------------------ Watcher --------------------------------

watch(
    () => props.modelValue,
    (newVal) => {
        inputValue.value = newVal;
    }
);

watch(
    () => inputValue.value,
    (newVal) => {
        emits("update:modelValue", newVal);
    }
);

// ------------------------------ Lifecycle ------------------------------

// ------------------------------ Hàm xử lý ------------------------------
</script>

<style scoped>
.form-check {
    display: flex;
    gap: 5px;
    align-items: center;
}

.form-check-input {
    width: 18px;
    height: 18px;
    cursor: pointer;
}

.form-check-label {
    font-size: 1rem;
    padding-top: 5px;
    cursor: pointer;
}
</style>
