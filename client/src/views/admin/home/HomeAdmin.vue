<script setup>
import { useProductStore } from "@/stores/product";
import { storeToRefs } from "pinia";
import { onMounted } from "vue";

import RevenueChart from "@/components/chart/RevenueChart.vue";
import ProductSalesChart from "@/components/chart/ProductSalesChart.vue";

const productStore = useProductStore();
const { productListBestSeller } = storeToRefs(productStore);

onMounted(async () => {
    // FIX: chỉ lấy TOP 7 sản phẩm bán chạy nhất
    await productStore.fetchGetAllBestSeller(0, 7);
});
</script>

<template>
    <div class="home-admin">
        <div class="home-admin-header">
            <h1>Hệ thống quản trị</h1>
        </div>

        <!-- Hàng 1 -->
        <div class="stat-row">
            <RevenueChart />
        </div>

        <!-- Hàng 2 -->
        <div class="stat-row-columns">

            <!-- Cột trái: Chart -->
            <div class="product-chart-column">
                <ProductSalesChart />
            </div>

            <!-- Cột phải: Best Seller -->
            <div class="best-seller-column">
                <div class="product-best-seller">
                    <h3>Sản phẩm bán chạy</h3>

                    <div class="product-best-seller-body">
                        <div
                            v-for="item in productListBestSeller"
                            :key="item?.id"
                            class="detail-body-content"
                        >
                            <div class="body-content-product">
                                <img :src="item.imageProducts[0]?.url" alt="" />
                                <div class="product-info">
                                    <p class="product-name">{{ item?.title }}</p>
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
    </div>
</template>

<style scoped>
/* Căn giữa tiêu đề */
.home-admin-header {
    text-align: center;
    margin-bottom: 30px;
}

.stat-row {
    width: 100%;
}

/* FIX: chiều cao bằng nhau và giảm gap */
.stat-row-columns {
    display: flex;
    flex-direction: row;
    gap: 16px; /* FIX: giảm khoảng cách */
    width: 100%;
    align-items: stretch;
}

/* Hai cột chia đều 50% */
.product-chart-column{
    flex: 6;
    min-width: unset;
    max-width: unset;
    display: flex;
    flex-direction: column;
}

.best-seller-column {
    flex: 4;
    min-width: unset;
    max-width: unset;
    displat: flex;
    flex-direction: column;
}

/* Đảm bảo biểu đồ chiếm toàn bộ không gian trong cột */
.product-chart-column > * {
    height: 100%;
}

/* Card sản phẩm bán chạy */
.product-best-seller {
    background: white;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    width: 100%;
    display: flex;
    flex-direction: column;
    min-height: 550px;
}

.product-best-seller h3 {
    margin: 0 0 20px 0;
    font-size: 20px;
    color: #1f2937;
    font-weight: 600;
}

.product-best-seller-body {
    flex: 1;
    overflow-y: auto;
}

.detail-body-content {
    margin-bottom: 16px;
}

.body-content-product {
    display: flex;
    align-items: center;
    gap: 12px;
}

.body-content-product img {
    width: 50px;
    height: 50px;
    border-radius: 6px;
    object-fit: cover;
    border: 1px solid #eee;
}

.product-name {
    font-weight: 500;
    color: #1f2937;
    margin: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}

.product-sold {
    font-size: 14px;
    color: #6b7280;
    margin: 4px 0 0 0;
}
</style>
