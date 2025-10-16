<template>
    <div id="flash-sale">
        <div class="loading" v-if="loading">
            <spinner-loader />
        </div>
        <div class="header">
            <h4>Sản phẩm giảm giá</h4>
            <router-link :to="{ name: 'ProductSale' }">
                Xem thêm <i class="fa-solid fa-chevron-right"></i>
            </router-link>
        </div>
        <div class="product row sm-gutter">
            <b-carousel id="carousel" :breakpoints="breakpoints" :pagination="false">
                <V-Slide v-for="(item) in productListSale" :key="item?.id">
                    <router-link :to="{ name: 'DetailProduct', params: { id: item?.id } }" :title="item?.title"
                        class="product-item">
                        <ProductTag :item="item"></ProductTag>
                    </router-link>
                </V-Slide>
            </b-carousel>
        </div>
    </div>
</template>

<script setup>
import { useProductStore } from '@/stores/product';
import ProductTag from '@/views/pages/product/ProductTag.vue';
import { storeToRefs } from 'pinia';
import { nextTick, ref } from 'vue';

const breakpoints = ref({
    700: {
        itemsToShow: 4,
        snapAlign: 'center'
    },
    1024: {
        itemsToShow: 6,
        snapAlign: 'start'
    }
})

const productStore = useProductStore();
const { loading, productListSale } = storeToRefs(productStore);

const page = ref(0);
const perPage = ref(12);

nextTick(async () => {
    await productStore.fetchGetAllSale(page.value, perPage.value);
})

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

#flash-sale {
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

.header>a {
    text-decoration: none;
    font-size: 1.2rem;
    color: var(--color-primary);
}

.header>a>i {
    padding-left: 10px;
}

.product {
    display: flex;
    padding: 20px 0;
}

.product .product-item {
    text-decoration: none;
}

.product #carousel .carousel__slide {
    padding: 0 5px;
}
</style>