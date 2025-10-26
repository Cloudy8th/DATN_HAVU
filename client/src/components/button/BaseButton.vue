<script setup>
import { ref } from "vue";
const props = defineProps({
    value: {
        type: String,
        default: null,
    },
    type: {
        type: String,
        default: null,
    },
    icon: {
        type: String,
        default: null,
    },
    tooltip: {
        type: String,
        default: null,
    },
    color: {},
});

// Khai báo biến
const refBtn = ref(null);
const focusVisible = ref(false);

const btnType = (type) => {
    switch (type) {
        case "secondary":
            return "btn-secondary";
        case "primary":
            return "btn-primary";
    }
};

// ---------------------- Hàm xử lý ----------------------------

const focus = (visible = true) => {
    refBtn.value.focus();
    focusVisible.value = visible;
};

defineExpose({
    focus,
});
</script>

<template>
    <button
        :style="'color:' + props.color"
        ref="refBtn"
        :class="[btnType(props.type), { 'focus-visible': focusVisible }]"
    >
        <i
            v-if="icon != null"
            :class="[icon]"
            :style="'color:' + props.color"
        ></i>
        {{ props.value }}
        <slot></slot>
    </button>
</template>

<style scoped>
button {
    cursor: pointer;
    padding: 0 15px;
    height: var(--height-btn-default);
    box-sizing: border-box;
    border-radius: var(--border-radius);
    font-weight: 600;
    width: fit-content;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 5px;
    border: 1px solid transparent;
    position: relative;
    line-height: 0;
}

button > i {
    font-size: 15px;
}

button.btn-secondary {
    background-color: var(--color-primary-opacity);
    color: var(--color-primary);
    border: 1px solid var(--color-primary);
}

button.btn-secondary:hover {
    background-color: var(--color-white);
}

button.btn-primary {
    background-color: var(--color-primary);
    color: var(--color-white);
}

button.btn-primary:hover {
    background-color: var(--color-primary-hover);
}

button.btn-primary:focus,
button.btn-primary:active {
    background-color: var(--color-primary-focus);
    color: var(--color-white);
}

button:focus-visible {
    outline: 2px solid var(--color-outline);
    outline-offset: var(--outline-offset);
}

.tooltip {
    position: absolute;
    border: 1px solid var(--color-greyish);
    width: fit-content;
    padding: 5px 10px;
    border-radius: var(--border-radis-components);
    background-color: var(--color-white);
}

.focus-visible:focus-visible {
    outline: 2px solid var(--color-outline);
    outline-offset: var(--outline-offset);
}
</style>
