<script setup>
import { ref, reactive, watch, onMounted, computed } from "vue";
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

// ================= PINIA STORE =================
const orderStore = useOrderStore();
const { loadingOrder } = storeToRefs(orderStore);

// ✅ FIX: Đảm bảo startDate < endDate
const startDateRef = ref(new Date(new Date().setDate(new Date().getDate() - 7)));
const endDateRef = ref(new Date());

// ✅ Computed để đảm bảo thứ tự đúng
const validStartDate = computed(() => {
  const start = startDateRef.value;
  const end = endDateRef.value;
  if (start && end && start > end) {
    console.warn('⚠️ Start date > End date, swapping...');
    return end;
  }
  return start;
});

const validEndDate = computed(() => {
  const start = startDateRef.value;
  const end = endDateRef.value;
  if (start && end && start > end) {
    return start;
  }
  return end;
});

// ================= FORMAT DATE =================
const formatTimestamp = (date) => {
  if (!date) return null;
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day} 00:00:00`;
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return `${String(date.getDate()).padStart(2, '0')}/${String(date.getMonth() + 1).padStart(2, '0')}`;
};

// ================= CHART DATA =================
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

// ================= FETCH DATA =================
const fetchRevenueData = async () => {
  if (!validStartDate.value || !validEndDate.value) {
    console.warn('⚠️ Chưa chọn khoảng thời gian');
    return;
  }

  // ✅ Sử dụng validStartDate và validEndDate
  const startDate = formatTimestamp(validStartDate.value);
  const endDate = formatTimestamp(validEndDate.value);

  console.log('🔍 Fetching revenue data...');
  console.log('Start:', startDate, '(', validStartDate.value, ')');
  console.log('End:', endDate, '(', validEndDate.value, ')');

  try {
    let statsData = await orderStore.fetchDailyStats(startDate, endDate);
    console.log('✅ Daily stats:', statsData);
    
    if (statsData && statsData.length > 0) {
      chartData.labels = statsData.map(item => formatDate(item.date));
      chartData.datasets[0].data = statsData.map(item => item.totalMoney || 0);
      console.log('📊 Chart labels:', chartData.labels);
      console.log('📊 Chart data:', chartData.datasets[0].data);
    } else {
      console.warn('⚠️ Không có dữ liệu');
      chartData.labels = [];
      chartData.datasets[0].data = [];
    }
  } catch (error) {
    console.error("❌ Error:", error);
    chartData.labels = [];
    chartData.datasets[0].data = [];
  }
};

// ================= WATCH & MOUNT =================
watch([startDateRef, endDateRef], fetchRevenueData, { deep: true });
onMounted(() => fetchRevenueData());
</script>

<template>
  <div class="revenue-chart-wrapper">
    <div class="chart-header">
      <h3>📈 Thống kê doanh thu</h3>
      
      <!-- Hàng 2: Date Picker -->
      <div class="chart-controls">
        <div class="date-range-group">
          <label class="date-label">Chọn ngày:</label>
          
          <VueDatePicker 
            v-model="startDateRef"
            placeholder="Ngày bắt đầu"
            format="dd/MM/yyyy"
            :enable-time-picker="false"
            :max-date="endDateRef"
            auto-apply
            class="compact-date-input"
          />
          
          <VueDatePicker 
            v-model="endDateRef"
            placeholder="Ngày kết thúc"
            format="dd/MM/yyyy"
            :enable-time-picker="false"
            :min-date="startDateRef"
            auto-apply
            class="compact-date-input"
          />
        </div>
      </div>
    </div>

    <div class="loading" v-if="loadingOrder">
      <div class="spinner"></div>
      <p>Đang tải dữ liệu...</p>
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

.date-range-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-label {
  font-weight: 500;
  color: #1f2937;
}

.compact-date-input {
  max-width: 140px;
  height: 38px;
}

.loading {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  color: #6b7280;
  gap: 12px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f4f6;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.chart-container {
  height: 400px;
  position: relative;
  margin-top: 20px;
}

.no-data {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  color: #6b7280;
}
</style>