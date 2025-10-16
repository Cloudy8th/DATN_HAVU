<script setup>
import { useOrderDetailStore } from "@/stores/orderDetail";
import FeedbackItem from "./FeedbackItem.vue";
import { storeToRefs } from "pinia";
import { nextTick, ref, reactive, toRaw } from "vue";
import { useRoute } from "vue-router";
import router from "@/routers/router";
import { useFeedbackStore } from "@/stores/feedback";
import { dialog } from "@/helpers/swal";
import { useOrderStore } from "@/stores/order";
import { useUserStore } from "@/stores/user";

const orderDetailStore = useOrderDetailStore();
const { orderDetailList, loadingOrderDetail } = storeToRefs(orderDetailStore);
const feedbackStore = useFeedbackStore();
const { feedbackListData } = storeToRefs(feedbackStore);
const orderStore = useOrderStore();

const userStore = useUserStore();
const route = useRoute();
const orderId = ref(route.params.id);
const { userId } = storeToRefs(userStore);

const feedbackData = reactive({
    star: 0,
    note: null,
    productId: null,
    userId: userId.value,
});

nextTick(async () => {
    await orderDetailStore.fetchGetAllByOrderId(orderId.value);
    orderDetailList.value.forEach((item) => {
        feedbackData.productId = item?.product?.id;
        feedbackStore.fetchPushDataFeedback(toRaw(feedbackData));
    });
});

const backToOrderPage = () => {
    router.push({ name: "OrdersPage" });
};

const handleFeedback = async () => {
    for (let i = 0; i < feedbackListData.value.length; i++) {
        if (feedbackListData.value[i].star == 0) {
            dialog("Lỗi", "error", "Vui lý đánh giá cho sản phẩm");
            console.log(feedbackListData.value);
            return;
        }
    }
    await orderStore.fetchFeedbackOrder(orderId.value);
    await feedbackStore.fetchInsertMultiple(feedbackListData.value);
    feedbackStore.fetchResetDataFeedback();
};

const handlePushData = () => {
    console.log(feedbackListData.value);
};
</script>

<template>
    <div class="loading" v-if="loadingOrderDetail">
        <spinner-loader />
    </div>
    <div class="feedback__container container">
        <div class="feedback__section">
            <div class="feedback__header">
                <button @click="backToOrderPage()">
                    <i class="fa-solid fa-arrow-left"></i>
                </button>
                <h4>Đánh giá sản phẩm</h4>
            </div>
            <div class="feedback__body">
                <div class="feedback__content">
                    <FeedbackItem
                        v-for="item in orderDetailList"
                        :key="item"
                        :classify="item.classify"
                        :data="item.product"
                        @pushData="handlePushData()"
                    />
                </div>
            </div>
            <div class="feedback__footer">
                <b-button @click="backToOrderPage()" type="secondary"
                    >Trở lại</b-button
                >
                <b-button @click="handleFeedback()" type="primary"
                    >Đánh giá</b-button
                >
            </div>
        </div>
    </div>
</template>

<style lang="css" src="./feedback.css" scoped></style>
