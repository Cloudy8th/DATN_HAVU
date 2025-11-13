<script setup>
import { useProductStore } from "@/stores/product";
import { storeToRefs } from "pinia";
import { onMounted } from "vue";

import RevenueChart from "@/components/chart/RevenueChart.vue";
import ProductSalesChart from "@/components/chart/ProductSalesChart.vue";

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

        <!-- Hàng 1: Biểu đồ doanh thu -->
        <div class="stat-row">
            <RevenueChart />
        </div>

        <!-- Hàng 2: Biểu đồ sản phẩm + Sản phẩm bán chạy -->
        <div class="stat-row-columns">
            <!-- Cột 1: Chart sản phẩm -->
            <div class="product-chart-column">
                <ProductSalesChart />
            </div>

            <!-- Cột 2: Sản phẩm bán chạy -->
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
                                    <p class="product-sold">Đã bán: {{ item?.soldQuantity }}</p>
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
.home-admin {
    display: flex;
    flex-direction: column;
    gap: 24px;
}

.home-admin-header h1 {
    font-size: 28px;
    font-weight: bold;
    color: #1f2937;
    margin: 0;
}

.stat-row {
    width: 100%;
}

/* ============ FIX CHIỀU CAO BẰNG NHAU ============ */
.stat-row-columns {
    display: flex;
    flex-direction: row;
    gap: 24px;
    width: 100%;
    align-items: stretch; /* Căn chỉnh chiều cao */
}

.product-chart-column {
    flex: 1;
    min-width: 65%;
    display: flex; /* Thêm flex */
}

.best-seller-column {
    flex-basis: 35%;
    max-width: 35%;
    display: flex; /* Thêm flex */
}

/* ============ CHIỀU CAO CỐ ĐỊNH ============ */
.product-best-seller {
    background: white;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    width: 100%;
    display: flex;
    flex-direction: column;
    min-height: 550px; /* Chiều cao tối thiểu */
}

.product-best-seller h3 {
    margin: 0 0 20px 0;
    font-size: 20px;
    color: #1f2937;
    font-weight: 600;
}

.product-best-seller-body {
    flex: 1;
    overflow-y: auto; /* Scroll nếu quá nhiều sản phẩm */
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