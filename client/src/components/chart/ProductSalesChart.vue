<script setup>
import { ref, reactive, watch, onMounted } from "vue";
import { useOrderStore } from "@/stores/order";
import { storeToRefs } from "pinia";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import { Bar } from "vue-chartjs";
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const orderStore = useOrderStore();
const { loadingOrder } = storeToRefs(orderStore);

/* ========================================
   DANH MỤC SẢN PHẨM
======================================== */
const PRODUCT_CATEGORIES = [
  "tai nghe", "chuột", "bàn phím", "camera", "máy tính bảng",
  "linh kiện", "màn hình", "pc", "laptop", "điện thoại"
];

const CATEGORY_COLORS = {
  "tai nghe": "#3b82f6",
  "chuột": "#ef4444",
  "bàn phím": "#10b981",
  "camera": "#f59e0b",
  "máy tính bảng": "#8b5cf6",
  "linh kiện": "#ec4899",
  "màn hình": "#06b6d4",
  "pc": "#f97316",
  "laptop": "#14b8a6",
  "điện thoại": "#6366f1",
};

/* ========================================
   THỜI GIAN LỌC (FILTER)
======================================== */
const timeRange = ref("day");
const dateRange = ref([
  new Date(new Date().setDate(new Date().getDate() - 7)),
  new Date(),
]);

// Hàm sinh start/end ngày
const calculateDateRange = (filter) => {
  const now = new Date();
  let start = new Date(now);

  if (filter === "day") {
    start.setHours(0, 0, 0, 0);
  } else if (filter === "week") {
    start.setDate(now.getDate() - 7);
  } else if (filter === "month") {
    start.setDate(now.getDate() - 30);
  }

  return [start, now];
};

// Tự đổi date theo filter
watch(timeRange, (newFilter) => {
  dateRange.value = calculateDateRange(newFilter);
});

/* ========================================
   CHART
======================================== */
const chartData = reactive({
  labels: [],
  datasets: [],
});

const chartOptions = ref({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: true, position: "top" },
    tooltip: {
      callbacks: {
        label: (ctx) => `${ctx.dataset.label}: ${ctx.parsed.y} sản phẩm`,
      },
    },
  },
  scales: {
    y: { beginAtZero: true, ticks: { stepSize: 1 } },
  },
});

const formatTimestamp = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day} 00:00:00`;
};

/* ========================================
   PHÂN LOẠI SẢN PHẨM
======================================== */
const categorizeProduct = (productTitle) => {
  const title = productTitle.toLowerCase();

  for (const category of PRODUCT_CATEGORIES) {
    if (title.includes(category)) return category;
  }

  const phoneBrands = ["iphone", "samsung", "xiaomi", "oppo", "realme", "vsmart", "mobile", "phone"];
  if (phoneBrands.some((kw) => title.includes(kw))) return "điện thoại";

  return "khác";
};

/* ========================================
   FETCH DATA
======================================== */
const fetchProductData = async () => {
  if (!dateRange.value || dateRange.value.length < 2) return;

  const startDate = formatTimestamp(dateRange.value[0]);
  const endDate = formatTimestamp(dateRange.value[1]);

  try {
    const statsData = await orderStore.fetchProductSalesStats(startDate, endDate);

    if (!statsData || statsData.length === 0) {
      chartData.labels = [];
      chartData.datasets = [];
      return;
    }

    const categoryData = {};
    PRODUCT_CATEGORIES.forEach((cat) => (categoryData[cat] = 0));

    statsData.forEach((item) => {
      const category = categorizeProduct(item.productTitle);
      if (category !== "khác") {
        categoryData[category] += item.totalQuantitySold || 0;
      }
    });

    chartData.labels = PRODUCT_CATEGORIES.map(
      (cat) => cat.charAt(0).toUpperCase() + cat.slice(1)
    );

    chartData.datasets = [
      {
        label: "Số lượng bán",
        backgroundColor: Object.values(CATEGORY_COLORS),
        data: PRODUCT_CATEGORIES.map((cat) => categoryData[cat]),
        borderRadius: 4,
      },
    ];
  } catch (error) {
    console.error("❌ Lỗi fetch thống kê:", error);
  }
};

watch([timeRange, dateRange], fetchProductData, { deep: true });
onMounted(() => fetchProductData());
</script>

<template>
  <div class="product-chart-wrapper">
    <div class="chart-header">
      <h3>📦 Thống kê số lượng hàng hóa bán</h3>

      <div class="filters">
        <div class="filter-inline">
          <select v-model="timeRange" class="time-range-select">
            <option value="day">Theo ngày</option>
            <option value="week">Theo tuần</option>
            <option value="month">Theo tháng</option>
          </select>
        </div>

        <VueDatePicker
          v-model="dateRange"
          range
          placeholder="Chọn khoảng thời gian"
          format="dd/MM/yyyy"
          :enable-time-picker="false"
          auto-apply
          class="date-picker-compact"
        />
      </div>
    </div>

    <div class="loading" v-if="loadingOrder">
      <spinner-loader />
    </div>

    <div class="chart-container" v-if="!loadingOrder && chartData.datasets.length > 0">
      <Bar :options="chartOptions" :data="chartData" />
    </div>

    <div v-if="!loadingOrder && chartData.datasets.length === 0" class="no-data">
      <p>Không có dữ liệu trong khoảng thời gian này</p>
    </div>
  </div>
</template>

<style scoped>
.product-chart-wrapper {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  display: flex;
  flex-direction: column;
  height: 100%;
}

.chart-container {
  flex: 1;
  min-height: 400px;
  position: relative;
}

/* FILTER UI */
.filters {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-inline {
  display: flex;
  align-items: center;
  gap: 6px;
}

.time-range-select {
  height: 36px;
  padding: 6px 10px;
  border-radius: 6px;
  border: 1px solid #d1d5db;
  background: white;
  font-size: 14px;
}

.date-picker-compact {
  max-width: 260px;
}

@media (max-width: 480px) {
  .filters {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
