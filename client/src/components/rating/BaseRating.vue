<template>
    <div class="rating">
        <div
            :class="['star', isReadonly ? '' : 'pointer']"
            v-for="(item, index) in props.stars"
            :key="index"
            @click="isReadonly ? undefined : changeRating(item)"
        >
            <i
                :class="[
                    'fa-solid fa-star',
                    starValue >= item ? 'isCheck' : '',
                    isReadonly ? '' : 'hover',
                ]"
                :style="[
                    Math.ceil(starValue) == item && starValue > 0
                        ? halfStar(starValue)
                        : '',
                    `font-size: ${fontSize}px;`,
                ]"
            ></i>
        </div>
    </div>
</template>

<script setup>
import { ref, watch } from "vue";

const props = defineProps({
    modelValue: {
        type: [Number, String],
        default: 0,
    },
    value: {
        type: [Number, String], //Số sao đã vote
        default: 0,
    },
    stars: {
        type: [Number, String], //Số lượng sao
        default: 5,
    },
    isReadonly: {
        type: Boolean,
        default: false,
    },
    fontSize: {
        type: String, //Size icon sao (px)
        default: null,
    },
});

// Khai báo biến
const emit = defineEmits(["update:modelValue"]);
const starValue = ref(props.value);

// --------------------- Life cycle vue -------------------------

watch(
    () => starValue.value,
    (newVal) => {
        emit("update:modelValue", newVal);
    }
);

watch(
    () => props.modelValue,
    (newVal) => {
        starValue.value = newVal;
    }
);

// ----------------------- Hàm xử lý ---------------------------

const halfStar = (val) => {
    const floatVal = (val * 10) % 10 > 0 ? (val * 10) % 10 : 100;
    return `background: linear-gradient(90deg, var(--color-yellow) ${
        floatVal * 10
    }%, var(--color-greyish) ${floatVal * 10}%);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;`;
};
const changeRating = (val) => {
    starValue.value = val;
};
</script>

<style scoped>
.rating {
    display: flex;
    align-items: center;
    width: fit-content;
    gap: 1px;
}

.star {
    display: flex;
    align-items: center;
}

.star.pointer {
    cursor: pointer;
}

.star > i {
    color: var(--color-greyish);
}

.star .isCheck {
    background: linear-gradient(
        90deg,
        var(--color-yellow) 100%,
        var(--color-greyish) 100%
    );
    background-clip: text;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
}

.star > i.hover:hover {
    transform: scale(1.2);
    transition: 0.1s linear;
}
</style>
