<script setup>
import { useProductStore } from "@/stores/product";
import { storeToRefs } from "pinia";
import { onMounted } from "vue";

// Các component thống kê
import RevenueChart from "@/components/chart/RevenueChart.vue";
import ProductSalesChart from "@/components/chart/ProductSalesChart.vue";

const productStore = useProductStore();
const { productListBestSeller } = storeToRefs(productStore);

onMounted(async () => {
    // Lấy 10 sản phẩm bán chạy nhất
    await productStore.fetchGetAllBestSeller(0, 5);
});
</script>

<template>
    <div class="home-admin">
        <div class="home-admin-header">
            <h1>Hệ thống quản trị</h1>
        </div>

        <div class="stat-row">
            <RevenueChart />
        </div>

        <div class="stat-row-columns">
            
            <div class="product-chart-column">
                <ProductSalesChart />
            </div>

            <div class="best-seller-column">
                <div class="product-best-seller">
                    <h3>Sản phẩm bán chạy</h3>
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
    </div>
</template>

<style scoped>
/* Thêm CSS này để tạo layout 2 hàng và 2 cột */
.home-admin {
    display: flex;
    flex-direction: column;
    gap: 24px; /* Khoảng cách giữa các hàng */
}

.stat-row {
    width: 100%;
}

.stat-row-columns {
    display: flex;
    flex-direction: row; /* Sắp xếp 2 cột theo hàng ngang */
    gap: 24px; /* Khoảng cách giữa 2 cột */
    width: 100%;
}

.product-chart-column {
    flex: 1; /* Cho phép cột này co giãn */
    min-width: 65%; /* Đảm bảo cột biểu đồ luôn rộng hơn */
}

.best-seller-column {
    flex-basis: 35%; /* Cột này có chiều rộng cơ bản 35% */
    max-width: 35%;
}

/* CSS cho .product-best-seller của bạn
  (Bạn có thể đã có CSS này trong file home-admin.css) 
*/
.product-best-seller {
    background: white;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    height: 100%; /* Làm cho nó cao bằng biểu đồ */
}

.product-best-seller h3 {
    margin: 0 0 20px 0;
    font-size: 20px;
    color: #1f2937;
    font-weight: 600;
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
    
    /* Giới hạn tên sản phẩm trên 2 dòng */
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 1; /* Số dòng tối đa */
    -webkit-box-orient: vertical;
}
.product-sold {
    font-size: 14px;
    color: #6b7280;
    margin: 4px 0 0 0;
}

</style>