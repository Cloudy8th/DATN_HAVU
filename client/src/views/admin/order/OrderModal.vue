<script setup>
import { useOrderStore } from "@/stores/order";
import { storeToRefs } from "pinia";
import { nextTick, reactive } from "vue";

const emits = defineEmits(["closeModal"]);

const props = defineProps({
    orderId: String,
    page: Number,
    perPage: Number,
});

const orderStore = useOrderStore();
const { loadingOrder, order, successOrder } = storeToRefs(orderStore);
const orderData = reactive({
    address: null,
    coupon: null,
    fullName: null,
    note: null,
    orderDetails: [],
    paymentMethod: null,
    phoneNumber: null,
    status: null,
    totalMoney: null,
    createdAt: null,
});

nextTick(async () => {
    await orderStore.fetchGetById(props.orderId);
    Object.assign(orderData, order.value);
});

const handleCloseModal = () => {
    const dialog = document.querySelector(".swal2-popup");
    if (dialog) {
        return;
    }
    emits("closeModal");
};

const handleSubmit = async () => {
    await orderStore.fetchUpdateOrder(
        props.orderId,
        {
            status: orderData.status,
        },
        props.page,
        props.perPage
    );
    if (successOrder.value === true) {
        emits("closeModal");
    }
};

const handleExportOrder = async () => {
    await orderStore.fetchExportOrder(props.orderId);
};
</script>

<template>
    <div class="admin-modal__container">
        <div v-if="loadingOrder" class="loading">
            <spinner-loader></spinner-loader>
        </div>
        <div class="admin-modal__section" v-click-outside="handleCloseModal">
            <div class="modal-header">
                <h5>Chi tiết đơn hàng</h5>
                <button @click="handleCloseModal()">
                    <i class="fa-solid fa-xmark"></i>
                </button>
            </div>
            <div class="modal-body">
                <form @submit.prevent="handleSubmit" class="row g-3 form-group">
                    <div class="form-item col-md-6">
                        <h6>Địa chỉ nhận hàng</h6>
                        <p>
                            {{ orderData.fullName }} -
                            {{ orderData.phoneNumber }}
                        </p>
                        <p>{{ orderData.address }}</p>
                    </div>
                    <div class="form-item col-md-6">
                        <label for="coupon" class="form-label px-2"
                            >Mã giảm giá:
                        </label>
                        <span>{{ orderData?.coupon?.code ?? "Không có" }}</span
                        ><br />
                        <label for="paymentMethod" class="form-label px-2"
                            >Phương thức thanh toán:
                        </label>
                        <span>{{ orderData.paymentMethod }}</span>
                    </div>
                    <div class="form-item col-md-12">
                        <div class="order-detail-body">
                            <div class="detailt-body-title">
                                <div class="detail-body-product">
                                    <p>Sản phẩm</p>
                                </div>
                                <div class="detail-body-price">
                                    <p>Giá</p>
                                </div>
                                <div class="detail-body-quantity">
                                    <p>Số lượng</p>
                                </div>
                                <div class="detail-body-discount">
                                    <p>Giảm giá</p>
                                </div>
                                <div class="detailt-body-total">
                                    <p>Tạm tính</p>
                                </div>
                            </div>
                            <div class="line"></div>
                            <div
                                v-for="item in orderData.orderDetails"
                                :key="item?.id"
                                class="detail-body-content"
                            >
                                <div
                                    class="body-content-product detail-body-product"
                                >
                                    <img
                                        :src="
                                            item.product?.imageProducts[0]?.url
                                        "
                                        alt=""
                                    />
                                    <div class="product-info">
                                        <p title="" class="product-name">
                                            {{ item?.product?.title }}
                                        </p>
                                        <p class="product-classify">
                                            Phân loại: {{ item?.classify }}
                                        </p>
                                    </div>
                                </div>
                                <div class="detail-body-price">
                                    <p>
                                        {{
                                            $formatValue.formatMoney(
                                                item?.product?.salePrice
                                            )
                                        }}
                                    </p>
                                </div>
                                <div class="detail-body-quantity">
                                    <p>{{ item?.quantity }}</p>
                                </div>
                                <div class="detail-body-discount">
                                    <p>
                                        {{
                                            $formatValue.formatMoney(
                                                item?.product?.salePrice -
                                                    item?.price
                                            )
                                        }}
                                    </p>
                                </div>
                                <div class="detailt-body-total">
                                    <p>
                                        {{
                                            $formatValue.formatMoney(
                                                item?.price * item?.quantity
                                            )
                                        }}
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="form-item col-md-6">
                        <label for="status" class="form-label"
                            >Trạng thái</label
                        >
                        <select
                            :disabled="
                                orderData.status === 'UNPAID' ||
                                orderData.status === 'CANCELLED'
                            "
                            v-model="orderData.status"
                            class="form-select"
                            id="status"
                        >
                            <option hidden value="PENDING">Chờ xác nhận</option>
                            <option hidden value="PAID">Đã thanh toán</option>
                            <option hidden value="UNPAID">
                                Chưa thanh toán
                            </option>
                            <option hidden value="CANCELLED">Đã hủy</option>
                            <option value="PROCESSED">Đã xác nhận</option>
                            <option value="SHIPPING">Đang giao hàng</option>
                            <option value="SHIPPED">Đã giao hàng</option>
                        </select>
                    </div>
                    <div
                        class="form-item col-md-6"
                        style="display: flex; align-items: center; gap: 30px"
                    >
                        <div>
                            <label for="description" class="form-label"
                                >Tổng tiền</label
                            >
                            <p>
                                {{
                                    $formatValue.formatMoney(
                                        orderData.totalMoney
                                    )
                                }}
                            </p>
                        </div>
                        <div>
                            <label for="description" class="form-label"
                                >Ngày đặt hàng</label
                            >
                            <p>
                                {{
                                    $formatValue.formatDateTime(
                                        orderData.createdAt
                                    )
                                }}
                            </p>
                        </div>
                    </div>
                    <div class="btn-submit order-modal-btn col-12">
                        <button class="btn btn-primary" type="submit">
                            Lưu
                        </button>
                        <button
                            @click="handleExportOrder()"
                            class="btn btn-secondary"
                            type="button"
                        >
                            Xuất hóa đơn
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<style lang="css" src="../admin-modal.css" scoped></style>
