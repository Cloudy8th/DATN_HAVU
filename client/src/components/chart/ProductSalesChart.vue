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
import VueDatePicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

// ================= PINIA STORE =================
const orderStore = useOrderStore();
const { loadingOrder } = storeToRefs(orderStore);

// ================= FILTER TIME =================
const timeRange = ref("day");
const dateRange = ref([
  new Date(new Date().setDate(new Date().getDate() - 7)),
  new Date(),
]);

// Tự tính lại ngày khi đổi filter
const calculateDateRange = (filter) => {
  const now = new Date();
  let start = new Date(now);

  if (filter === "day") start.setHours(0, 0, 0, 0);
  else if (filter === "week") start.setDate(now.getDate() - 7);
  else if (filter === "month") start.setDate(now.getDate() - 30);

  return [start, now];
};

watch(timeRange, (newVal) => {
  dateRange.value = calculateDateRange(newVal);
});

// ================= FORMAT DATE =================
const formatTimestamp = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day} 00:00:00`;
};

// ================= CHART DATA =================
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
  scales: { y: { beginAtZero: true } },
});

// ================= FETCH DATA FROM DATABASE =================
const fetchProductData = async () => {
  if (!dateRange.value || dateRange.value.length < 2) return;

  const startDate = formatTimestamp(dateRange.value[0]);
  const endDate = formatTimestamp(dateRange.value[1]);

  try {
    // 🎯 GỌI API MỚI: LẤY THỐNG KÊ THEO DANH MỤC
    const statsData = await orderStore.fetchCategorySalesStats(startDate, endDate);

    if (!statsData || statsData.length === 0) {
      chartData.labels = [];
      chartData.datasets = [];
      return;
    }

    // ================= BUILD DATA DIRECTLY FROM DATABASE =================
    chartData.labels = statsData.map((item) => item.categoryName);

    // Tạo màu ngẫu nhiên nếu backend không có bảng màu
    const colors = statsData.map(
      () => "#" + Math.floor(Math.random() * 16777215).toString(16)
    );

    chartData.datasets = [
      {
        label: "Số lượng bán",
        backgroundColor: colors,
        data: statsData.map((item) => item.totalQuantitySold || 0),
        borderRadius: 4,
      },
    ];
  } catch (err) {
    console.error("❌ Lỗi khi fetch thống kê theo danh mục:", err);
  }
};

watch([timeRange, dateRange], fetchProductData, { deep: true });

onMounted(() => fetchProductData());
</script>

<template>
  <div class="product-chart-wrapper">
    <div class="chart-header">
      <h3>📦 Thống kê số lượng hàng hóa bán theo danh mục</h3>

      <div class="filters">
        <select v-model="timeRange" class="time-range-select">
          <option value="day">Theo ngày</option>
          <option value="week">Theo tuần</option>
          <option value="month">Theo tháng</option>
        </select>

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

    <div v-if="loadingOrder" class="loading">
      <spinner-loader />
    </div>

    <div v-if="!loadingOrder && chartData.datasets.length > 0" class="chart-container">
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
}

.chart-container {
  min-height: 400px;
  position: relative;
}

.filters {
  display: flex;
  gap: 12px;
  align-items: center;
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
</style>
