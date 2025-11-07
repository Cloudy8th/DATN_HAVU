<script setup>
import { ref, reactive, watch, onMounted } from "vue";
import { useOrderStore } from "@/stores/order";
import { storeToRefs } from "pinia";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import { Line } from "vue-chartjs";
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

const orderStore = useOrderStore();
const { loadingOrder } = storeToRefs(orderStore);

// Bộ lọc
const timeRange = ref("day");
const dateRange = ref([
  new Date(new Date().setDate(new Date().getDate() - 7)),
  new Date(),
]);

const chartData = reactive({
  labels: [],
  datasets: [
    {
      label: "Doanh thu (VNĐ)",
      backgroundColor: "#3b82f6",
      borderColor: "#3b82f6",
      data: [],
      tension: 0.4,
      fill: false,
      borderWidth: 3,
      pointRadius: 4,
      pointHoverRadius: 6,
    },
  ],
});

const chartOptions = ref({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: true,
      position: "top",
    },
    tooltip: {
      callbacks: {
        label: function(context) {
          let label = context.dataset.label || '';
          if (label) label += ': ';
          if (context.parsed.y !== null) {
            label += new Intl.NumberFormat('vi-VN', {
              style: 'currency',
              currency: 'VND'
            }).format(context.parsed.y);
          }
          return label;
        }
      }
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        callback: function(value) {
          return (value / 1000000).toFixed(0) + 'M';
        }
      }
    }
  }
});

const formatTimestamp = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day} 00:00:00`;
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return `${String(date.getDate()).padStart(2, '0')}/${String(date.getMonth() + 1).padStart(2, '0')}`;
};

const fetchRevenueData = async () => {
  if (!dateRange.value || dateRange.value.length < 2) {
    console.warn('⚠️ Chưa chọn khoảng thời gian');
    return;
  }

  const startDate = formatTimestamp(dateRange.value[0]);
  const endDate = formatTimestamp(dateRange.value[1]);

  console.log('🔍 Fetching revenue data...');
  console.log('Start:', startDate);
  console.log('End:', endDate);

  try {
    let statsData;
    
    if (timeRange.value === "day") {
      statsData = await orderStore.fetchDailyStats(startDate, endDate);
      console.log('✅ Daily stats:', statsData);
      
      if (statsData && statsData.length > 0) {
        chartData.labels = statsData.map(item => formatDate(item.date));
        chartData.datasets[0].data = statsData.map(item => item.totalMoney || 0);
      } else {
        console.warn('⚠️ Không có dữ liệu');
        chartData.labels = [];
        chartData.datasets[0].data = [];
      }
    } else if (timeRange.value === "week") {
      statsData = await orderStore.fetchWeeklyStats(startDate, endDate);
      console.log('✅ Weekly stats:', statsData);
      
      if (statsData && statsData.length > 0) {
        chartData.labels = statsData.map(item => `Tuần ${item.week}`);
        chartData.datasets[0].data = statsData.map(item => item.totalMoney || 0);
      } else {
        console.warn('⚠️ Không có dữ liệu');
        chartData.labels = [];
        chartData.datasets[0].data = [];
      }
    }
  } catch (error) {
    console.error("❌ Error:", error);
  }
};

watch([timeRange, dateRange], fetchRevenueData, { deep: true });

onMounted(() => {
  fetchRevenueData();
});
</script>

<template>
  <div class="revenue-chart-wrapper">
    <div class="chart-header">
      <h3>📈 Thống kê doanh thu</h3>
      <div class="filters">
        <select v-model="timeRange" class="time-range-select">
          <option value="day">Theo ngày</option>
          <option value="week">Theo tuần</option>
        </select>
        <VueDatePicker 
          v-model="dateRange" 
          range 
          placeholder="Chọn khoảng thời gian"
          format="dd/MM/yyyy"
          :enable-time-picker="false"
          auto-apply
        />
      </div>
    </div>

    <div class="loading" v-if="loadingOrder">
      <spinner-loader />
    </div>

    <div class="chart-container" v-if="!loadingOrder && chartData.datasets[0].data.length > 0">
      <Line :options="chartOptions" :data="chartData" />
    </div>

    <div v-if="!loadingOrder && chartData.datasets[0].data.length === 0" class="no-data">
      <p>Không có dữ liệu trong khoảng thời gian này</p>
    </div>
  </div>
</template>

<style scoped>
.revenue-chart-wrapper {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
  width: 70%;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 16px;
}

.chart-header h3 {
  margin: 0;
  font-size: 20px;
  color: #1f2937;
  font-weight: 600;
}

.filters {
  display: flex;
  gap: 12px;
  align-items: center;
}

.time-range-select {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  cursor: pointer;
  background: white;
}

.time-range-select:focus {
  border-color: #3b82f6;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.chart-container {
  height: 500px;
  position: relative;
}

.no-data {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  color: #6b7280;
}
</style>