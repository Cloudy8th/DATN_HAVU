<script setup>
import { useFeedbackStore } from "@/stores/feedback";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { nextTick, reactive, toRaw, watch } from "vue";

const props = defineProps({
    data: Object,
    classify: String,
});

const userStore = useUserStore();
const { userId } = storeToRefs(userStore);
const feedbackStore = useFeedbackStore();

const feedbackData = reactive({
    star: 0,
    note: null,
    productId: props.data?.id,
    userId: userId.value,
});

nextTick(async () => {});

watch(feedbackData, () => {
    feedbackStore.fetchUpdateFeedback(
        feedbackData.productId,
        toRaw(feedbackData)
    );
});
</script>

<template>
    <div class="feedback__list">
        <div class="feedback__item">
            <img :src="data?.imageProducts[0]?.url" alt="" />
            <div class="feedback__info">
                <h5>{{ data?.title }}</h5>
                <p>Phân loại: {{ classify }}</p>
            </div>
        </div>
        <div class="feedback__rating">
            <label>Chất lượng sản phẩm: </label>
            <b-rating v-model="feedbackData.star"></b-rating>
        </div>
        <div class="feedback__comment">
            <label for="note">Nội dung đánh giá:</label>
            <textarea
                v-model="feedbackData.note"
                class="form-control"
                id="note"
            ></textarea>
        </div>
    </div>
</template>

<style lang="css" src="./feedback.css" scoped></style>
