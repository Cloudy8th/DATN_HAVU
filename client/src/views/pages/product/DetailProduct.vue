<template>
    <div class="loading" v-if="loading">
        <spinner-loader />
    </div>
    <div class="detail-product">
        <div class="product-infor">
            <div class="product-imgs">
                <carousel-gallery
                    v-if="product?.imageProducts?.length > 0"
                    :pagination="false"
                    :wrapAround="false"
                    :slides="product?.imageProducts"
                >
                </carousel-gallery>
            </div>
            <div class="product-more-infor">
                <h3 class="title">
                    {{ product?.title }}
                </h3>
                <div class="product-rating">
                    <div
                        v-if="product?.feedbacks?.length > 0"
                        class="product-star"
                    >
                        <ins>
                            {{ calculateStar(product?.feedbacks) }}
                        </ins>
                        <b-rating
                            :value="calculateStar(product?.feedbacks)"
                            isReadonly
                        />
                    </div>
                    <span class="line"></span>
                    <div class="quantity-rating">
                        <ins>{{
                            $helper.formatNumber(product?.feedbacks?.length)
                        }}</ins>
                        <span>Đã đánh giá</span>
                    </div>
                    <span class="line"></span>
                    <div class="product-sold">
                        <ins>{{
                            $helper.formatNumber(product?.soldQuantity)
                        }}</ins>
                        <span>Đã bán</span>
                    </div>
                </div>
                <div class="product-price">
                    <p v-if="product?.discount > 0" class="price-old">
                        {{ $formatValue.formatMoney(product?.originPrice) }}
                    </p>
                    <p class="price-new">
                        {{ $formatValue.formatMoney(product?.salePrice) }}
                    </p>
                    <div v-if="product?.discount > 0" class="discount">
                        <p>{{ product?.discount }}% GIẢM</p>
                    </div>
                </div>
                <div class="product-option">
                    <div class="options">
                        <h5>Màu sắc</h5>
                        <div class="option-item">
                            <div
                                @click="changeClassify(item)"
                                :class="[
                                    'option-value',
                                    { 'option-active': item === classify },
                                ]"
                                v-for="item in ['Đen', 'Xanh', 'Trắng']"
                                :key="item"
                            >
                                <p class="option-label">{{ item }}</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="product-quantity">
                    <p class="quantity-tile">Số lượng</p>
                    <div class="select-quantity">
                        <b-button @click="reduce" icon="fa-solid fa-minus" />
                        <input
                            min="1"
                            :max="quantityProduct"
                            @input="validInputQuantity"
                            type="number"
                            v-model="quantity"
                        />
                        <b-button @click="increment" icon="fa-solid fa-plus" />
                    </div>
                    <p class="quantity">
                        {{ $helper.formatNumber(product.quantity) }} sản phẩm có
                        sẵn
                    </p>
                </div>
                <div class="button-handle">
                    <b-button
                        @click="handleAddToCart"
                        type="secondary"
                        icon="fa-solid fa-cart-plus"
                        value="Thêm vào giỏ hàng"
                    />
                </div>
            </div>
        </div>
        <div class="product-description">
            <h3>MÔ TẢ SẢN PHẨM</h3>
            <pre class="description-content">{{ product.description }}</pre>
        </div>
        <ReviewProduct :avg-star="avgStar" />
    </div>
</template>

<script setup>
import { nextTick, ref } from "vue";
import ReviewProduct from "./ReviewProduct.vue";
import { useRoute } from "vue-router";
import { useProductStore } from "@/stores/product";
import { useCartStore } from "@/stores/cart";
import { useUserStore } from "@/stores/user";
import { useAuthStore } from "@/stores/auth";
import { storeToRefs } from "pinia";
import router from "@/routers/router";
import { dialog } from "@/helpers/swal";

// --------------------- Khai báo biến ----------------------
const quantity = ref(1);
const product = ref({});
const route = useRoute();
const productId = ref(route.params.id);
const quantityProduct = ref(0);
const avgStar = ref(0);
const classify = ref("Đen");
const cartStore = useCartStore();
const userStore = useUserStore();
const { userId } = storeToRefs(userStore);
const authStore = useAuthStore();
const { isLoggedIn } = storeToRefs(authStore);

// --------------------- Lifecycle vue ----------------------

const productStore = useProductStore();
const { loading } = storeToRefs(productStore);

nextTick(async () => {
    await productStore.fetchGetById(productId.value);
    product.value = productStore.product;
    quantityProduct.value = product.value.quantity;
    calculateStar(product.value?.feedbacks);
});

// --------------------- Hàm xử lý --------------------------
const changeClassify = (item) => {
    classify.value = item;
};

const calculateStar = (feedbacks) => {
    let totalStar = 0;
    feedbacks.forEach((feedback) => {
        totalStar += feedback.star;
    });
    avgStar.value = (totalStar / feedbacks?.length).toFixed(1);
    return (totalStar / feedbacks?.length).toFixed(1);
};

const validInputQuantity = () => {
    const maxQuantity = product.value.quantity;
    if (quantity.value > maxQuantity) {
        quantity.value = maxQuantity;
    } else {
        quantity.value = Number.parseInt(quantity.value);
    }
};

const reduce = () => {
    quantity.value = quantity.value > 1 ? (quantity.value -= 1) : 1;
};

const increment = () => {
    quantity.value =
        quantity.value < quantityProduct.value
            ? (quantity.value += 1)
            : quantityProduct.value;
};

const handleAddToCart = () => {
    if (product.value.quantity === 0) {
        dialog(
            "Thêm vào giỏ hàng thất bại",
            "error",
            "Sản phẩm bạn chọn đã hết hàng"
        );
        return;
    }
    if (!isLoggedIn.value) {
        router.push({
            name: "Login",
            query: { redirect: router.currentRoute.value.fullPath },
        });
        return;
    }
    cartStore.fetchInsert({
        productId: productId.value,
        quantity: quantity.value,
        userId: userId.value,
        classify: classify.value,
    });
};
</script>

<style src="./detailProduct.css" scoped></style>
