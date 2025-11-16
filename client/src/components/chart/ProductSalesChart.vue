<script setup>
import { ref, watch, onMounted, nextTick, computed } from "vue";
import { storeToRefs } from "pinia";
import { Bar } from "vue-chartjs";
import {
  Chart as ChartJS,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend,
  Title
} from "chart.js";

import { useOrderStore } from "@/stores/order";
import orderService from "@/apis/services/orderService";
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title);

const orderStore = useOrderStore();
const { loadingOrder } = storeToRefs(orderStore);

// Khai báo ref
const startDateRef = ref(new Date(new Date().setDate(new Date().getDate() - 30)));
const endDateRef = ref(new Date());
const chartRef = ref(null);
const chartKey = ref(0); // ✅ Key để force re-render

// ✅ Computed để tính độ rộng tối thiểu dựa vào số lượng categories
const chartMinWidth = computed(() => {
  const numCategories = chartData.value.labels.length;
  if (numCategories === 0) return '100%';
  
  // Mỗi cột cần ~80px, tối thiểu 100%
  const minWidth = Math.max(800, numCategories * 80);
  return `${minWidth}px`;
});

const chartData = ref({
  labels: [],
  datasets: [
    {
      label: "Số lượng bán",
      data: [],
      backgroundColor: [
        'rgba(72, 187, 168, 0.85)',
        'rgba(255, 99, 132, 0.85)',
        'rgba(255, 182, 193, 0.85)',
        'rgba(99, 132, 255, 0.85)',
        'rgba(153, 142, 195, 0.85)',
        'rgba(222, 184, 135, 0.85)',
        'rgba(47, 79, 79, 0.85)',
        'rgba(230, 230, 250, 0.85)',
        'rgba(64, 224, 208, 0.85)',
        'rgba(255, 140, 0, 0.85)'
      ],
      borderWidth: 0,
      borderRadius: 6,
      barPercentage: 0.7
    }
  ]
});

const chartOptions = ref({
  responsive: true,
  maintainAspectRatio: false,
  aspectRatio: 2,
  plugins: {
    legend: { 
      display: true,
      position: 'top',
      align: 'end',
      labels: {
        boxWidth: 15,
        boxHeight: 15,
        padding: 15,
        font: {
          size: 13
        }
      }
    },
    tooltip: {
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      padding: 12,
      titleFont: {
        size: 13,
        weight: 'bold'
      },
      bodyFont: {
        size: 12
      },
      callbacks: {
        label: function(context) {
          return 'Đã bán: ' + context.parsed.y + ' sản phẩm';
        }
      }
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        stepSize: 1,
        callback: function(value) {
          if (Number.isInteger(value)) {
            return value;
          }
        },
        font: {
          size: 11
        }
      },
      grid: {
        color: 'rgba(0, 0, 0, 0.05)',
        drawBorder: false
      }
    },
    x: {
      grid: {
        display: false
      },
      ticks: {
        font: {
          size: 11
        },
        maxRotation: 45,
        minRotation: 0
      }
    }
  }
});

// Hàm format timestamp
const formatTimestamp = (date) => {
  if (!date) return null;
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day} 00:00:00`;
};

// ✅ FIX: Hàm load dữ liệu với force re-render
const loadData = async () => {
  if (!startDateRef.value || !endDateRef.value) {
    console.warn('⚠️ Chưa chọn khoảng thời gian');
    return;
  }

  const startDate = formatTimestamp(startDateRef.value);
  const endDate = formatTimestamp(endDateRef.value);

  console.log('🔍 Fetching category sales stats...');
  console.log('Start:', startDate);
  console.log('End:', endDate);

  try {
    const response = await orderService.getCategorySalesStats(startDate, endDate);
    const data = response.data;

    console.log('📊 API Response:', response);
    console.log('📊 Raw data:', data);

    if (!data || data.length === 0) {
      console.warn('⚠️ Không có dữ liệu danh mục');
      chartData.value = {
        labels: ["Không có dữ liệu"],
        datasets: [{
          label: "Số lượng bán",
          data: [0],
          backgroundColor: ['rgba(200, 200, 200, 0.6)'],
          borderWidth: 0,
          borderRadius: 6,
          barPercentage: 0.7
        }]
      };
    } else {
      // ✅ SẮP XẾP GIẢM DẦN theo totalQuantitySold
      const sortedData = [...data].sort((a, b) => b.totalQuantitySold - a.totalQuantitySold);
      
      const labels = sortedData.map((x) => x.categoryName);
      const quantities = sortedData.map((x) => x.totalQuantitySold);
      
      console.log('📊 Sorted data:', sortedData);
      console.log('📊 Labels:', labels);
      console.log('📊 Quantities:', quantities);
      
      chartData.value = {
        labels: labels,
        datasets: [{
          label: "Số lượng bán",
          data: quantities,
          backgroundColor: [
            'rgba(72, 187, 168, 0.85)',
            'rgba(255, 99, 132, 0.85)',
            'rgba(255, 182, 193, 0.85)',
            'rgba(99, 132, 255, 0.85)',
            'rgba(153, 142, 195, 0.85)',
            'rgba(222, 184, 135, 0.85)',
            'rgba(47, 79, 79, 0.85)',
            'rgba(230, 230, 250, 0.85)',
            'rgba(64, 224, 208, 0.85)',
            'rgba(255, 140, 0, 0.85)'
          ],
          borderWidth: 0,
          borderRadius: 6,
          barPercentage: 0.7
        }]
      };
      
      console.log('✅ Chart data updated:', chartData.value);
    }

    // ✅ FORCE RE-RENDER bằng cách tăng key
    chartKey.value++;
    console.log('🔄 Chart key updated to:', chartKey.value);
    
    // ✅ Đợi Vue render xong rồi update chart
    await nextTick();
    if (chartRef.value?.chart) {
      chartRef.value.chart.update('active');
    }
    
  } catch (error) {
    console.error('❌ Error loading category sales:', error);
    console.error('❌ Error details:', error?.response?.data);
    chartData.value = {
      labels: ["Lỗi tải dữ liệu"],
      datasets: [{
        label: "Số lượng bán",
        data: [0],
        backgroundColor: ['rgba(255, 0, 0, 0.6)'],
        borderWidth: 0,
        borderRadius: 6,
        barPercentage: 0.7
      }]
    };
    chartKey.value++;
  }
};

// ✅ Watch với immediate để load ngay khi mount
watch([startDateRef, endDateRef], () => {
  loadData();
}, { deep: true });

onMounted(() => {
  loadData();
});
</script>

<template>
  <div class="category-sales-wrapper">
    <!-- Hàng 1: Title -->
    <div class="chart-title">
      <div class="title-icon">📦</div>
      <h3>Thống kê số lượng hàng hóa bán theo danh mục</h3>
    </div>

    <!-- Hàng 2: Date Picker -->
    <div class="chart-controls">
      <div class="date-range-group">
        <label class="date-label">Chọn ngày:</label>
        
        <VueDatePicker 
          v-model="startDateRef"
          placeholder="Ngày bắt đầu"
          format="dd/MM/yyyy"
          :enable-time-picker="false"
          auto-apply
          class="compact-date-input"
        />
        
        <VueDatePicker 
          v-model="endDateRef"
          placeholder="Ngày kết thúc"
          format="dd/MM/yyyy"
          :enable-time-picker="false"
          auto-apply
          class="compact-date-input"
        />
      </div>
    </div>

    <!-- Loading State -->
    <div class="loading" v-if="loadingOrder">
      <div class="spinner"></div>
      <p>Đang tải dữ liệu...</p>
    </div>

    <!-- Chart Container - ✅ Bỏ v-if để tránh unmount/remount -->
    <div class="chart-container" v-show="!loadingOrder">
      <Bar 
        :data="chartData" 
        :options="chartOptions" 
        :key="chartKey" 
        ref="chartRef"
      />
    </div>
  </div>
</template>

<style scoped>
.category-sales-wrapper {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* Hàng 1: Title */
.chart-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f3f4f6;
}

.title-icon {
  font-size: 24px;
}

.chart-title h3 {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
  font-weight: 600;
}

/* Hàng 2: Controls */
.chart-controls {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 24px;
}

.date-range-group {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #f9fafb;
  padding: 12px 16px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.date-label {
  font-weight: 500;
  color: #374151;
  font-size: 14px;
  white-space: nowrap;
}

.compact-date-input {
  width: 150px;
  height: 38px;
}

/* Loading */
.loading {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  flex: 1;
  min-height: 350px;
  color: #6b7280;
  font-size: 14px;
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

/* Chart Container */
.chart-container {
  flex: 1;
  position: relative;
  height: 400px;
  overflow-x: auto;
  overflow-y: hidden;
  padding-top: 10px;
}

.chart-wrapper {
  height: 100%;
  position: relative;
}

/* Custom scrollbar */
.chart-container::-webkit-scrollbar {
  height: 8px;
}

.chart-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.chart-container::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
}

.chart-container::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style>