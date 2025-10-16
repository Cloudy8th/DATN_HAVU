<script lang="js" setup>
import { useProductStore } from '@/stores/product';
import { storeToRefs } from 'pinia';
import { nextTick, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import ProductTag from './ProductTag.vue';

// ------------------------- Ref, reactive, emits, computed ----------------------
const productStore = useProductStore();
const { loading, products } = storeToRefs(productStore);
const page = ref(1);
const perPage = ref(24);
const totalPage = ref(0);
const route = useRoute();
const keyword = ref(route.params.keyword);
const price = ref('default');
const feedback = ref('default');

// ------------------------- Lifecycle ----------------------
nextTick(async () => {
    await productStore.fetchSearchProduct(keyword.value, page.value - 1, perPage.value);
    totalPage.value = products.value.pagination.lastPage + 1;
})

// ------------------------- Methods -----------------------

// ------------------------- Watchers -----------------------
watch(() => [price.value, feedback.value, page.value], async () => {
    await productStore.fetchSearchProduct(keyword.value, page.value - 1, perPage.value, price.value, feedback.value);
})

</script>

<template>
    <div class="product-catalogry">
        <div class="loading" v-if="loading">
            <spinner-loader></spinner-loader>
        </div>
        <div class="header">
            <h4>Tìm kiếm: {{ keyword }}</h4>
            <div class="sort-by">
                Sắp xếp theo:
                <select v-model="price" class="price form-select">
                    <option value="default" hidden selected>Giá bán</option>
                    <option value="desc">Giá: Cao đến thấp</option>
                    <option value="asc">Giá: Thấp đến cao</option>
                </select>
                <select v-model="feedback" class="feedback form-select">
                    <option value="default" hidden selected>Đánh giá</option>
                    <option value="desc">Đánh giá: Cao đến thấp</option>
                    <option value="asc">Đánh giá: Thấp đến cao</option>
                </select>
            </div>
        </div>
        <div class="product row sm-gutter">
            <div class="col-2 product__item" v-for="item in products.data" :key="item?.id">
                <router-link :to="{ name: 'DetailProduct', params: { id: item.id } }" :title="item?.title"
                    class="product-item">
                    <ProductTag :item="item"></ProductTag>
                </router-link>
            </div>
        </div>
        <v-pagination v-model="page" size="35" :length="totalPage" rounded="circle"></v-pagination>
    </div>
</template>

<style lang="css" scoped>
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

.header>h4 {
    font-size: 1.8rem;
    font-weight: 500;
}

.header>a>i {
    padding-left: 10px;
}

.header .sort-by {
    display: flex;
    padding: 10px 0;
    align-items: center;
    gap: 10px;
}

.header .sort-by>select {
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
    padding: 10px;
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
