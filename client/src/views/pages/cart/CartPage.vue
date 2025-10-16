<template>
    <div class="loading" v-if="loadingCart">
        <spinner-loader></spinner-loader>
    </div>
    <div class="cart">
        <h3>GIỎ HÀNG</h3>
        <div class="cart-section">
            <div class="cart-info">
                <div class="cart-header">
                    <div class="title-name cart-name">
                        <b-checkbox
                            v-model="selectAll"
                            :indeterminate="indeterminate"
                            :label="`Tất cả (${cartData?.length} sản phẩm)`"
                        />
                    </div>
                    <div class="title-price cart-price">
                        <p>Đơn giá</p>
                    </div>
                    <div class="title-quantity cart-quantity">
                        <p>Số lượng</p>
                    </div>
                    <div class="title-total cart-total">
                        <p>Thành tiền</p>
                    </div>
                    <div class="title-option cart-option">
                        <b-button
                            @click="handleDeleteMultipleCart()"
                            class="btn"
                            icon="fa-solid fa-trash-can"
                            color="var(--color-grey)"
                        />
                    </div>
                </div>
                <div class="cart-body">
                    <div
                        class="cart-content"
                        v-for="item in cartData"
                        :key="item?.id"
                    >
                        <div class="cart-content-name cart-name">
                            <b-checkbox
                                v-model="selectedItem"
                                :value="item"
                                @change="handleSelect(item)"
                            />
                            <img
                                :src="item?.product?.imageProducts[0]?.url"
                                alt=""
                            />
                            <span :title="item?.product?.title">
                                {{ item?.product?.title }}
                            </span>
                        </div>
                        <div class="cart-content-price cart-price">
                            <p>
                                {{
                                    $formatValue.formatMoney(
                                        item?.product?.salePrice
                                    )
                                }}
                            </p>
                        </div>
                        <div class="cart-content-quantity cart-quantity">
                            <div class="select-quantity">
                                <b-button
                                    @click="reduce(item)"
                                    icon="fa-solid fa-minus"
                                />
                                <input
                                    @change="validInputQuantity(item)"
                                    type="number"
                                    min="1"
                                    :max="item?.product?.quantity"
                                    v-model="item.quantity"
                                />
                                <b-button
                                    @click="increment(item)"
                                    icon="fa-solid fa-plus"
                                />
                            </div>
                            <p>Còn {{ item?.product?.quantity }} sản phẩm</p>
                        </div>
                        <div class="cart-content-total cart-total">
                            <p>
                                {{
                                    $formatValue.formatMoney(
                                        item?.product?.salePrice *
                                            item?.quantity
                                    )
                                }}
                            </p>
                        </div>
                        <div class="cart-content-option cart-option">
                            <b-button
                                @click="handleDeleteCart(item?.id)"
                                class="btn"
                                icon="fa-solid fa-trash-can"
                                color="var(--color-grey)"
                            />
                        </div>
                    </div>
                </div>
            </div>
            <div class="cart-sell">
                <div class="cart-sell-voucher">
                    <h5>Mã khuyến mãi</h5>
                    <div class="voucher-content">
                        <b-input
                            @keydown.enter="handleAddCoupon()"
                            v-model="couponCode"
                            placeholder="Nhập mã khuyến mãi"
                        />
                        <b-button
                            @click="handleAddCoupon()"
                            type="secondary"
                            value="Áp dụng"
                            color="var(--color-primary)"
                        />
                    </div>
                </div>
                <div class="cart-sell-buy-now">
                    <div class="buy-now-bill">
                        <div class="bill-title">
                            <p>Tạm tính</p>
                            <p>Giảm giá</p>
                        </div>
                        <div class="bill-value">
                            <p>
                                {{
                                    $formatValue.formatMoney(
                                        totalMoney / ((100 - discount) / 100)
                                    )
                                }}
                            </p>
                            <p>
                                -
                                {{
                                    $formatValue.formatMoney(
                                        totalMoney / ((100 - discount) / 100) -
                                            totalMoney
                                    )
                                }}
                            </p>
                        </div>
                    </div>
                    <div class="line"></div>
                    <div class="total-amount">
                        <p>Tổng tiền</p>
                        <p>{{ $formatValue.formatMoney(totalMoney) }}</p>
                    </div>
                    <div class="buy-now-btn">
                        <b-button
                            @click="handleRedrectPayments()"
                            value="Đặt hàng"
                            type="primary"
                        />
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed, nextTick, onUpdated, ref, watch } from "vue";
import router from "@/routers/router";
import { useCartStore } from "@/stores/cart";
import { useOrderStore } from "@/stores/order";
import _ from "lodash";
import { useCouponStore } from "@/stores/coupon";
import { dialog, dialogConfirm } from "@/helpers/swal";
import { storeToRefs } from "pinia";

// ------------------------- Khai báo biến ----------------------
const cartData = ref([]);
const selectedItem = ref([]);
const indeterminate = ref(false);
const cartStore = useCartStore();
const totalMoney = ref(0);
const discount = ref(0);
const orderStore = useOrderStore();
const couponCode = ref(null);
const couponStore = useCouponStore();
const cartSelected = ref([]);
const { loadingCart } = storeToRefs(cartStore);

const selectAll = computed({
    get() {
        if (cartData.value?.length > 0) {
            let allChecked = true;
            for (let item of cartData.value) {
                if (!selectedItem.value.includes(item)) {
                    allChecked = false;
                    break;
                }
            }
            return allChecked;
        }
        return false;
    },
    set(value) {
        let selected = [];
        if (value) {
            cartData.value.forEach((cart) => {
                selected.push(cart);
            });
        }
        selectedItem.value = selected;
    },
});

// ------------------------ Lifecle -----------------------------
watch(
    () => couponCode.value,
    (newValue) => {
        if ((newValue === null || newValue === "") && discount.value > 0) {
            discount.value = 0;
            handleCaculatorTotalMoney();
        }
    }
);

nextTick(async () => {
    await cartStore.fetchGetAllByUser();
    cartData.value = cartStore.cartByUser.data;
    cartSelected.value = cartData.value;
    selectAll.value = true;
});

onUpdated(async () => {
    nextTick(async () => {
        let data = [];
        for (let item of selectedItem.value) {
            data.push({
                ...item,
            });
        }
        cartSelected.value = data;

        if (data.length > 0) {
            handleCaculatorTotalMoney();
        } else {
            totalMoney.value = 0;
        }
    });
});

// ------------------------ Watcher -----------------------------

// ------------------------ Hàm xử lý ---------------------------
const handleDeleteMultipleCart = async () => {
    let ids = [];
    cartSelected.value.forEach((item) => {
        ids.push(item?.id);
    });
    if (ids.length === 0) {
        dialog("Vui là chọn ít nhất 1 sản phâm", "error");
        return;
    }
    dialogConfirm(
        "Xác nhận xóa",
        "Bạn có muốn xóa nhưng sản phẩm đang được chọn",
        async () => {
            await cartStore.fetchDeleteMultiple(ids);
            cartData.value = cartStore.cartByUser.data;
        }
    );
};

const validInputQuantity = (item) => {
    handleUpdateQuantityCart(item);
    if (item.quantity > item.product?.quantity) {
        item.quantity = item.product?.quantity;
    } else {
        item.quantity = Number.parseInt(item.quantity);
    }
};

const reduce = (item) => {
    item.quantity = item.quantity > 1 ? (item.quantity -= 1) : 1;
    handleUpdateQuantityCart(item);
};

const increment = (item) => {
    item.quantity =
        item.quantity < item.product?.quantity
            ? (item.quantity += 1)
            : item.product?.quantity;
    handleUpdateQuantityCart(item);
};
const handleRedrectPayments = async () => {
    if (!cartSelected.value.length) {
        dialog("Vui lòng chọn ít nhất 1 sản phẩm", "error");
        return;
    }
    if (couponCode.value !== null && couponCode.value !== "") {
        const res = await couponStore.fetchUseCoupon(couponCode.value);
        if (res) {
            orderStore.fetchGetCoupon(cartSelected.value, couponCode.value);
            router.push({ name: "Payments" });
        }
        return;
    }
    orderStore.fetchGetCoupon(cartSelected.value, couponCode.value);
    router.push({ name: "Payments" });
};

const handleSelect = () => {
    for (let cart of cartData.value) {
        if (selectAll.value) {
            indeterminate.value = false;
            break;
        }
        if (selectedItem.value.includes(cart)) {
            indeterminate.value = true;
            break;
        } else {
            indeterminate.value = false;
        }
    }
};

const handleCaculatorTotalMoney = _.debounce(async function () {
    await orderStore.fetchCalculateTotalMoney(
        cartSelected.value,
        discount.value
    );
    totalMoney.value = orderStore.totalMoney;
}, 500);

const handleUpdateQuantityCart = _.debounce(async function (data) {
    await cartStore.fetchUpdate(data?.id, {
        quantity: data?.quantity,
    });
}, 500);

const handleAddCoupon = async () => {
    if (couponCode.value === null || couponCode.value === "") {
        discount.value = 0;
        return;
    }
    const res = await couponStore.fetchUseCoupon(
        couponCode.value.toUpperCase()
    );
    if (res) {
        discount.value = couponStore.coupon?.discount;
        await orderStore.fetchCalculateTotalMoney(
            cartSelected.value,
            discount.value
        );
        totalMoney.value = orderStore.totalMoney;
    }
};

const handleDeleteCart = (id) => {
    dialogConfirm(
        "Xác nhận xóa",
        "Bạn có chắc muốn xóa sản phẩm này?",
        async () => {
            await cartStore.fetchDelete(id);
            cartData.value = cartStore.cartByUser.data;
        }
    );
};
</script>

<style scoped src="./cart.css"></style>
