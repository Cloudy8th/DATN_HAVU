<template>
    <div id="products">
        <div class="loading" v-if="loading">
            <spinner-loader />
        </div>
        <div class="header">
            <h4>Sản phẩm đề xuất</h4>
        </div>
        <div class="product row sm-gutter">
            <div class="col-xxl-2 product-item" v-for="(item) in productsData" :key="item?.id">
                <router-link :to="{ name: 'DetailProduct', params: { id: item?.id } }" :title="item?.title"
                    class="product-item">
                    <ProductTag :item="item"></ProductTag>
                </router-link>
            </div>
        </div>
        <div class="link-more">
            <b-button @click="handleLoadMore" class="btn-more">Xem thêm</b-button>
        </div>
    </div>
</template>

<script setup>
import { nextTick, ref } from 'vue';
import ProductTag from '../product/ProductTag.vue';
import { useProductStore } from '@/stores/product';
import { storeToRefs } from 'pinia';
const page = ref(0);
const perPage = ref(6);
const totalPage = ref(0);
const productsData = ref([]);
const productStore = useProductStore();
const { loading } = storeToRefs(productStore);

nextTick(async () => {
    await productStore.fetchGetAll(page.value, perPage.value);
    totalPage.value = productStore?.products?.pagination?.lastPage;
    productsData.value = productStore?.products?.data;
})

// --------------------------- Methods ------------------------------
const handleLoadMore = async () => {
    if (page.value < totalPage.value) {
        page.value++;
        await productStore.fetchGetAll(page.value, perPage.value);
        productsData.value = [...productsData?.value, ...productStore.getProducts];
    }
}

</script>

<style scoped>
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

#products {
    width: 100%;
    background-color: var(--color-white);
    border-radius: var(--border-radius-page);
    padding: 10px;
    position: relative;
}

.header {
    display: flex;
    justify-content: space-between;
    padding-top: 10px;
}

.header>h4 {
    font-size: 1.2rem;
}

.header>a>i {
    padding-left: 10px;
}

.product {
    display: flex;
    padding: 20px 0;
}

.product-item {
    margin-top: 10px;
}

.product .product-item>a {
    text-decoration: none;
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