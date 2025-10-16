<template>
    <div class="loading" v-if="loadingOrder">
        <spinner-loader />
    </div>
    <div class="payments-page">
        <div class="address component-page" v-if="showChangeAddress == false">
            <div class="address-title">
                <i class="fa-solid fa-location-dot"></i>
                <h3>Địa chỉ nhận hàng</h3>
            </div>
            <div class="address-content">
                <b>{{ userInfor.fullName }} - {{ userInfor.phoneNumber }}</b>
                <p>{{ userInfor.address }}</p>
                <b-button @click="handleChangeAddress()" value="Thay đổi" />
            </div>
        </div>

        <div class="address component-page" v-if="showChangeAddress == true">
            <div class="address-title">
                <i class="fa-solid fa-location-dot"></i>
                <h3>Địa chỉ nhận hàng</h3>
            </div>
            <div class="address-content-change">
                <div class="row">
                    <div class="col">
                        <label for="fullName">Họ và tên người nhận</label>
                        <b-input v-model="userInfor.fullName" id="fullName" />
                    </div>
                    <div class="col">
                        <label for="phoneNumber"
                            >Số điện thoại người nhận</label
                        >
                        <b-input
                            type="text"
                            v-model="userInfor.phoneNumber"
                            id="phoneNumber"
                            @keypress="validatePhoneNumber($event)"
                        />
                    </div>
                    <div class="col">
                        <label for="address">Địa chỉ</label>
                        <b-input v-model="userInfor.address" id="address" />
                    </div>
                </div>

                <b-button
                    @click="handleChangeAddress()"
                    type="primary"
                    value="Thay đổi"
                />
            </div>
        </div>

        <div class="product-info component-page">
            <div class="product-info-title">
                <div class="title-name product-info-name">
                    <span>Sản phẩm</span>
                </div>
                <div class="title-classify product-info-classify"></div>
                <div class="title-price product-info-price">
                    <span>Đơn giá</span>
                </div>
                <div class="title-quantity product-info-quantity">
                    <span>Số lượng</span>
                </div>
                <div class="title-total product-info-total">
                    <span>Thành tiền</span>
                </div>
            </div>
            <div
                class="product-info-content"
                v-for="item in orderItems"
                :key="item?.product?.id"
            >
                <div class="content-name product-info-name">
                    <img :src="item?.product?.imageProducts[0]?.url" alt="" />
                    <span :title="item?.product?.title">
                        {{ item?.product?.title }}
                    </span>
                </div>
                <div class="content-classify product-info-classify">
                    <span>Màu: {{ item?.classify }}</span>
                </div>
                <div class="content-price product-info-price">
                    <span>
                        {{ $formatValue.formatMoney(item?.product?.salePrice) }}
                    </span>
                </div>
                <div class="title-quantity product-info-quantity">
                    <span>{{ item?.quantity }}</span>
                </div>
                <div class="title-total product-info-total">
                    <span>
                        {{
                            $formatValue.formatMoney(
                                item?.product?.salePrice * item?.quantity
                            )
                        }}
                    </span>
                </div>
            </div>
        </div>
        <div class="payments-method component-page">
            <div class="payments-method-header">
                <h3>Phương thức thanh toán</h3>
            </div>
            <div class="line"></div>
            <div class="payments-method-body">
                <div class="payments-method-item">
                    <input
                        id="cod"
                        type="radio"
                        value="COD"
                        name="paymentsMethod"
                        v-model="paymentMethod"
                    />
                    <label for="cod">
                        <i class="fa-solid fa-coins"></i>
                        Thanh toán khi nhận hàng
                    </label>
                </div>
                <div class="payments-method-item">
                    <input
                        id="vnpay"
                        type="radio"
                        value="VNPAY"
                        name="paymentsMethod"
                        v-model="paymentMethod"
                    />
                    <label for="vnpay">
                        <i class="fa-solid fa-wallet"></i>
                        Thanh toán với VNPay
                    </label>
                </div>
            </div>
            <div class="line"></div>
            <div class="payments-method-footer">
                <div class="payments-info">
                    <div class="payments-title">
                        <p>Tạm tính</p>
                        <p>Phí vận chuyển</p>
                        <p>Giảm giá</p>
                        <p>Tổng thanh toán</p>
                    </div>
                    <div class="payments-value">
                        <p>
                            {{
                                $formatValue.formatMoney(
                                    totalMoney / ((100 - discount) / 100)
                                )
                            }}
                        </p>
                        <p>0 ₫</p>
                        <p>
                            -
                            {{
                                $formatValue.formatMoney(
                                    totalMoney / ((100 - discount) / 100) -
                                        totalMoney
                                )
                            }}
                        </p>
                        <p>{{ $formatValue.formatMoney(totalMoney) }}</p>
                    </div>
                </div>
                <div class="line"></div>
                <div class="payments-btn">
                    <b-button
                        @click="handleOrder()"
                        value="ĐẶT HÀNG"
                        type="primary"
                    />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { dialog } from "@/helpers/swal";
import { useCartStore } from "@/stores/cart";
import { useCouponStore } from "@/stores/coupon";
import { useOrderStore } from "@/stores/order";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { nextTick, onMounted, onUpdated, reactive, ref } from "vue";

const orderStore = useOrderStore();
const couponStore = useCouponStore();
const cartStore = useCartStore();
const userStore = useUserStore();
const { ordersByUser, loadingOrder } = storeToRefs(orderStore);
const orderItems = ref([]);
const cartItems = ref([]);
const ordersData = ref([]);
const couponCode = ref(null);
const discount = ref(0);
const totalMoney = ref(0);
const userInfor = reactive({
    fullName: null,
    address: null,
    phoneNumber: null,
});
const userId = ref(null);
const showChangeAddress = ref(false);
const paymentMethod = ref("COD");

nextTick(async () => {
    userId.value = userStore.userId;
    await cartStore.fetchGetAllByUser(userStore.userId);
    orderItems.value = orderStore.orderItem;
    couponCode.value = orderStore.coupon;
    let data = [];
    orderItems?.value?.forEach(async (item) => {
        data.push({
            id: item.id,
            quantity: item.quantity,
            productId: item.product.id,
            classify: item.classify,
        });
    });
    cartItems.value = data;
    if (couponCode.value) {
        await couponStore.fetchUseCoupon(couponCode.value);
        discount.value = couponStore.coupon.discount;
    }
    await orderStore.fetchGetAllByUser(userStore.userId);
    ordersData.value = orderStore.ordersByUser;
    userInfor.fullName = ordersByUser?.value[0]?.fullName;
    userInfor.address = ordersByUser?.value[0]?.address;
    userInfor.phoneNumber = ordersByUser?.value[0]?.phoneNumber;
});

onMounted(() => {});

onUpdated(() => {
    nextTick(async () => {
        await orderStore.fetchCalculateTotalMoney(
            orderItems.value,
            discount.value
        );
        totalMoney.value = orderStore.totalMoney;
    });
});

const handleOrder = async () => {
    console.log(paymentMethod.value);
    if (
        userInfor.fullName == null ||
        userInfor.address == null ||
        userInfor.phoneNumber == null
    ) {
        dialog("Vui lòng điền đầy đủ thông tin", "error");
        return;
    }
    await orderStore.fetchInsertOrder({
        fullName: userInfor.fullName,
        address: userInfor.address,
        phoneNumber: userInfor.phoneNumber,
        cartItems: cartItems.value,
        couponId: couponCode.value,
        userId: userId.value,
        paymentMethod: paymentMethod.value,
    });
    await cartStore.fetchGetAllByUser();
};

const handleChangeAddress = () => {
    showChangeAddress.value = !showChangeAddress.value;
};

const validatePhoneNumber = (e) => {
    let regexNumber = /^[0-9]+$/;
    if (!regexNumber.test(e.key)) {
        e.preventDefault();
    }
};
</script>

<style src="./payments.css" scoped></style>
