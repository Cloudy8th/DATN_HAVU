<script setup>
import { useOrderStore } from '@/stores/order';
import { storeToRefs } from 'pinia';
import { nextTick, ref } from 'vue';
import { useRoute } from 'vue-router';


const orderStore = useOrderStore();
const { loadingOrder, codeStatusPayment } = storeToRefs(orderStore);
const route = useRoute();
const query = ref(route.query);

nextTick(async () => {
    if (query.value) {
        await orderStore.fetchPaymentReturn({ ...query.value });
        console.log(query.value);
    }
})

</script>

<template>
    <div class="loading" v-if="loadingOrder">
        <spinner-loader />
    </div>
    <div class="payment-success__container">
        <div
            :class="['payment-succes__section', { 'success': codeStatusPayment == 1 }, { 'error': codeStatusPayment == 0 }]">
            <i v-if="codeStatusPayment == 1" class="fa-solid fa-circle-check"></i>
            <i v-if="codeStatusPayment == 0" class="fa-solid fa-circle-xmark"></i>
            <p v-if="codeStatusPayment == 1">
                Thanh toán thành công
            </p>
            <p v-if="codeStatusPayment == 0">
                Thanh toán thất bại
            </p>
            <p>
                Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.
            </p>
            <router-link :to="{ name: 'HomePage' }">Quay lại trang chủ</router-link>
        </div>
    </div>
</template>

<style lang="css" scoped>
.loading {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: var(--bg-opacity);
    display: flex;
    align-items: center;
    justify-content: center;
}

.payment-success__container {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100vh;
    position: relative;
}

.payment-succes__section {
    display: flex;
    gap: 20px;
    flex-direction: column;
    justify-content: center;
    text-align: center;
}

.payment-succes__section>p {
    font-size: 1.8rem;
}

.payment-succes__section>i {
    font-size: 6rem;
}

.payment-succes__section>a {
    text-decoration: none;
    border-radius: 8px;
    background-color: var(--color-primary);
    padding: 15px 0;
    color: var(--color-white);
    font-weight: 700;
    text-transform: uppercase;
    display: block;
}

.payment-succes__section.success>i {
    color: green;
}

.payment-succes__section.error>i {
    color: red;
}
</style>
