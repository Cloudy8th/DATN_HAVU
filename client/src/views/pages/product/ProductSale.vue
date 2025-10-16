<script lang="js" setup>
import { useProductStore } from '@/stores/product';
import { storeToRefs } from 'pinia';
import { nextTick, ref, watch } from 'vue';
import ProductTag from './ProductTag.vue';
import TheSidebar from '@/views/layouts/TheSidebar.vue';

// ------------------------- Ref, reactive, emits, computed ----------------------
const productStore = useProductStore();
const { loading, productListSale, pagination } = storeToRefs(productStore);
const page = ref(1);
const perPage = ref(12);
const totalPage = ref(0);
const productsData = ref([]);
const price = ref('default');
const feedback = ref('default');

// ------------------------- Lifecycle ----------------------
nextTick(async () => {
    await productStore.fetchGetAllSale(page.value - 1, perPage.value, price.value, feedback.value);
    totalPage.value = pagination.value.lastPage + 1;
    productsData.value = productListSale.value;
    console.log(productsData.value);
})

// ------------------------- Methods -----------------------
const handleLoadMore = async () => {
    if (page.value < totalPage.value) {
        page.value += 1;
        await productStore.fetchGetAllSale(page.value - 1, perPage.value, price.value, feedback.value);
        productsData.value = [...productsData.value, ...productListSale.value];
    }
}

const getAvgStar = (feedbacks) => {
    if (!Array.isArray(feedbacks) || feedbacks.length === 0) return 0;
    let totalStars = 0;
    feedbacks.forEach((feedback) => {
        totalStars += feedback.star;
    });
    console.log(totalStars);

    return totalStars / feedbacks.length;
};

const sortProductsByPrice = () => {
    productsData.value.sort((a, b) => {
        if (price.value !== "default") {
            if (price.value === "asc") {
                return a.salePrice - b.salePrice;
            } else if (price.value === "desc") {
                return b.salePrice - a.salePrice;
            }
        }
    });
};

const sortProductsByFeedback = () => {
    productsData.value.sort((a, b) => {
        const avgStarA = getAvgStar(a.feedbacks);
        const avgStarB = getAvgStar(b.feedbacks);
        if (feedback.value !== "default") {
            if (feedback.value === "asc") {
                return avgStarA - avgStarB;
            } else if (feedback.value === "desc") {
                return avgStarB - avgStarA;
            }
        }
    });
}

watch(() => [price.value, feedback.value],  () => {
    sortProductsByPrice();
    sortProductsByFeedback();
})
</script>

<template>
    <div class="product-catalogry__container">
        <TheSidebar />
        <div class="product-catalogry">
            <div class="loading" v-if="loading">
                <spinner-loader></spinner-loader>
            </div>
            <div class="header">
                <h4>Sản phẩm giảm giá</h4>
                <div class="sort-by">
                    Sắp xếp theo:
                    <select v-model="price" class="price form-select">
                        <option value="default" hidden selected>Giá bán</option>
                        <option value="desc">Giá: Cao đến thấp</option>
                        <option value="asc">Giá: Thấp đến cao</option>
                    </select>
                    <select v-model="feedback" class="feedback form-select">
                        <option value="default" hidden selected>
                            Đánh giá
                        </option>
                        <option value="desc">Đánh giá: Cao đến thấp</option>
                        <option value="asc">Đánh giá: Thấp đến cao</option>
                    </select>
                </div>
            </div>
            <div class="product row sm-gutter">
                <div
                    class="col-2 product__item"
                    v-for="item in productsData"
                    :key="item.id"
                >
                    <router-link
                        :to="{ name: 'DetailProduct', params: { id: item.id } }"
                        :title="item?.title"
                        class="product-item"
                    >
                        <ProductTag :item="item"></ProductTag>
                    </router-link>
                </div>
            </div>
            <div class="link-more">
                <b-button @click="handleLoadMore" class="btn-more"
                    >Xem thêm</b-button
                >
            </div>
        </div>
    </div>
</template>

<style lang="css" scoped>
.product-catalogry__container {
    display: flex;
    width: 100%;
    height: 100%;
    gap: 10px;
}

.loading {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 100;
}

.product-catalogry {
    position: relative;
    width: 100%;
    background-color: var(--color-white);
    border-radius: var(--border-radius-page);
    padding: 10px 20px;
    box-shadow: 0 2px 5px var(--color-box-shadow);
}

.header {
    padding-top: 10px;
}

.header > h4 {
    font-size: 1.8rem;
    font-weight: 500;
}

.header > a > i {
    padding-left: 10px;
}

.header .sort-by {
    display: flex;
    padding: 10px 0;
    align-items: center;
    gap: 10px;
}

.header .sort-by > select {
    width: fit-content;
}

.product {
    display: flex;
    padding: 20px 0;
}

.product a {
    text-decoration: none;
}

.product .product__item {
    padding: 5px;
}

.link-more {
    display: flex;
    justify-content: center;
    padding-bottom: 10px;
}

.link-more .btn-more {
    text-decoration: none;
    background-color: var(--color-white);
    border: 1px solid var(--color-primary);
    color: var(--color-primary);
    display: block;
    padding: 10px 40px;
    width: fit-content;
    border-radius: var(--border-radius);
    font-size: 1.1rem;
    font-weight: 600;
}

.link-more .btn-more:hover {
    background-color: var(--color-primary);
    color: var(--color-white);
}
</style>
