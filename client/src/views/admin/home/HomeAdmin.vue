<script setup>
import { useProductStore } from "@/stores/product";
import ChartMoney from "./ChartMoney.vue";
import { storeToRefs } from "pinia";
import { onMounted } from "vue";

const productStore = useProductStore();
const { productListBestSeller } = storeToRefs(productStore);

onMounted(async () => {
    await productStore.fetchGetAllBestSeller(0, 10);
});
</script>

<template>
    <div class="home-admin">
        <div class="home-admin-header">
            <h1>Hệ thống quản trị</h1>
        </div>
        <div class="home-admin-section">
            <ChartMoney />
            <div class="product-best-seller">
                <h3>Sản phẩm bán chạy</h3>
                <div class="product-best-seller-body">
                    <div
                        v-for="item in productListBestSeller"
                        :key="item?.id"
                        class="detail-body-content"
                    >
                        <div class="body-content-product detail-body-product">
                            <img :src="item.imageProducts[0]?.url" alt="" />
                            <div class="product-info">
                                <p title="" class="product-name">
                                    {{ item?.title }}
                                </p>
                                <p class="product-sold">
                                    Đã bán: {{ item?.soldQuantity }}
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="css" src="./home-admin.css" scoped></style>
