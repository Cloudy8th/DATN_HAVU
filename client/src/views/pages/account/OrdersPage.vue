<template>
    <div class="orders">
        <div class="order-section">
            <div class="order-title">
                <h3>Đơn hàng của tôi</h3>
            </div>
            <div class="order-header">
                <div class="order-menu">
                    <div
                        @click="handleFilter(null)"
                        :class="[
                            'order-menu-item',
                            'order-all',
                            { active: status === null },
                        ]"
                    >
                        <p>Tất cả đơn</p>
                    </div>
                    <div
                        @click="handleFilter('UNPAID')"
                        :class="[
                            'order-menu-item',
                            'order-peding',
                            { active: status === 'UNPAID' },
                        ]"
                    >
                        <p>Chưa thanh toán</p>
                    </div>
                    <div
                        @click="handleFilter('PENDING')"
                        :class="[
                            'order-menu-item',
                            'order-peding',
                            { active: status === 'PENDING' },
                        ]"
                    >
                        <p>Đang xử lý</p>
                    </div>
                    <div
                        @click="handleFilter('PROCESSED')"
                        :class="[
                            'order-menu-item',
                            'order-processed',
                            { active: status === 'PROCESSED' },
                        ]"
                    >
                        <p>Đã xử lý</p>
                    </div>
                    <div
                        @click="handleFilter('SHIPPING')"
                        :class="[
                            'order-menu-item',
                            'order-shipping',
                            { active: status === 'SHIPPING' },
                        ]"
                    >
                        <p>Đang vận chuyển</p>
                    </div>
                    <div
                        @click="handleFilter('SHIPPED')"
                        :class="[
                            'order-menu-item',
                            'order-done',
                            { active: status === 'SHIPPED' },
                        ]"
                    >
                        <p>Đã giao</p>
                    </div>
                    <div
                        @click="handleFilter('CANCELLED')"
                        :class="[
                            'order-menu-item',
                            'order-cancel',
                            { active: status === 'CANCELLED' },
                        ]"
                    >
                        <p>Đã hủy</p>
                    </div>
                </div>
            </div>
            <div class="order-search-box">
                <div class="search-icon">
                    <i class="fa-solid fa-magnifying-glass"></i>
                </div>
                <div class="search-input">
                    <b-input
                        v-model="keyword"
                        ref="refInputSearch"
                        placeholder="Tìm theo tên sản phẩm"
                    />
                </div>
                <div class="search-button">
                    <b-button @click="findByKeyword()" value="Tìm đơn hàng" />
                </div>
            </div>
            <div class="loading" v-if="loadingOrder">
                <spinner-loader />
            </div>
            <div
                class="order-body order-component"
                v-for="item in ordersByUser"
                :key="item"
            >
                <div class="order-body-content">
                    <div class="content-header">
                        <div
                            :class="[
                                'order-status',
                                item?.status.toLowerCase(),
                            ]"
                        >
                            <p>{{ statusOrder(item?.status) }}</p>
                        </div>
                    </div>
                    <div class="line"></div>
                    <div
                        @click="redirectOrderDetail(item.id)"
                        class="content-body"
                    >
                        <div
                            v-for="orderDetail in item.orderDetails"
                            :key="orderDetail.id"
                            class="product-info"
                        >
                            <img
                                :src="
                                    orderDetail?.product?.imageProducts[0]?.url
                                "
                                alt=""
                            />
                            <div>
                                <p class="product-name">
                                    {{ orderDetail?.product?.title }}
                                </p>
                                <p class="product-quantity">
                                    Số lượng: {{ orderDetail.quantity }}
                                </p>
                            </div>
                        </div>
                    </div>
                    <div class="line"></div>
                    <div class="content-footer">
                        <div class="total-amount">
                            <span>Tổng tiền:</span>
                            <p>
                                {{ $formatValue.formatMoney(item.totalMoney) }}
                            </p>
                        </div>
                        <div class="option">
                            <b-button
                                class="btn-cancel"
                                @click="handleCancelOrder(item)"
                                v-if="
                                    item.status === 'UNPAID' ||
                                    item.status === 'PENDING'
                                "
                                value="Hủy đơn"
                                type="secondary"
                            />
                            <b-button
                                @click="handleRouteFeedback(item?.id)"
                                v-if="
                                    item.status === 'SHIPPED' &&
                                    item?.feedback === false
                                "
                                value="Đánh giá"
                                type="secondary"
                            />
                            <b-button
                                @click="handleAddToCart(item.orderDetails)"
                                v-if="item.status === 'SHIPPED'"
                                value="Mua lại"
                                type="secondary"
                            />
                            <b-button
                                @click="redirectOrderDetail(item.id)"
                                value="Xem chi tiết"
                                type="secondary"
                            />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { dialog, dialogConfirm } from "@/helpers/swal";
import router from "@/routers/router";
import { useCartStore } from "@/stores/cart";
import { useOrderStore } from "@/stores/order";
import { useUserStore } from "@/stores/user";
import _ from "lodash";
import { storeToRefs } from "pinia";
import { nextTick, onMounted, ref, watch } from "vue";

// ---------------------- Props ----------------------

// ---------------------- Khai báo biến --------------
const refInputSearch = ref(null);
const orderStore = useOrderStore();
const userStore = useUserStore();
const cartStore = useCartStore();
const { ordersByUser, loadingOrder } = storeToRefs(orderStore);
const { userId } = storeToRefs(userStore);
const keyword = ref(null);
const status = ref(null);

// ---------------------- Watcher --------------------
watch(
    () => keyword.value,
    async () => {
        await findByKeyword();
    }
);

// ---------------------- Lifecycle ------------------
nextTick(async () => {
    await orderStore.fetchGetAllByUser(userId.value);
});

onMounted(() => {
    refInputSearch.value.focus();
});

// ---------------------- Hàm xử lý ------------------
const redirectOrderDetail = (id) => {
    router.push({ name: "OrderDetail", params: { id: id } });
};

const statusOrder = (status) => {
    switch (status) {
        case "PENDING":
            return "Đang xử lý";
        case "PAID":
            return "Đã thanh toán";
        case "UNPAID":
            return "Chưa thanh toán";
        case "PROCESSED":
            return "Đã xử lý";
        case "SHIPPING":
            return "Đang vận chuyển";
        case "SHIPPED":
            return "Đã giao";
        case "CANCELLED":
            return "Đã hủy";
        default:
            return "";
    }
};

const findByKeyword = _.debounce(async function () {
    await orderStore.fetchGetAllByUser(
        userId.value,
        status.value,
        keyword.value
    );
}, 500);

const handleFilter = (statusOrder) => {
    status.value = statusOrder;
    findByKeyword();
};

const handleAddToCart = async (orderDetails) => {
    let data = [];
    orderDetails.forEach((element) => {
        data.push({
            productId: element?.product?.id,
            quantity: element?.quantity,
            userId: userId.value,
            classify: element?.classify,
        });
    });
    await cartStore.fetchInsertMultiple(data);
};

const handleRouteFeedback = (id) => {
    router.push({ name: "Feedback", params: { id: id } });
};

const handleCancelOrder = async (item) => {
    if (item?.status === "CANCELLED") {
        dialog("Thông báo", "warning", "Đơn hàng đã bị hủy");
        return;
    }
    dialogConfirm("Xác nhận", "Hủy đơn hàng?", async () => {
        await orderStore.fetchCancelOrder(item?.id);
    });
};
</script>

<style scoped src="./orders.css"></style>
