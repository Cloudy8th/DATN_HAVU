<script setup>
import { ref, reactive, nextTick } from "vue";
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
ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);
const chartData = reactive({
    labels: [
        "Tháng 1",
        "Tháng 2",
        "Tháng 3",
        "Tháng 4",
        "Tháng 5",
        "Tháng 6",
        "Tháng 7",
        "Tháng 8",
        "Tháng 9",
        "Tháng 10",
        "Tháng 11",
        "Tháng 12",
    ],
    datasets: [
        {
            label: "Doanh thu",
            backgroundColor: "#006fff",
            data: [],
        },
    ],
});

const chartOptions = ref({
    responsive: true,
});

const orderStore = useOrderStore();
const { loadingOrder, statistical } = storeToRefs(orderStore);
const year = ref(new Date().getFullYear());

nextTick(async () => {
    year.value = new Date().getFullYear();
    await orderStore.fetchStatisticOrder(year.value);
    for (let i = 0; i < statistical.value.length; i++) {
        chartData.datasets[0].data[i] = statistical.value[i].totalMoney;
    }
});
</script>

<template>
    <div class="loading" v-if="loadingOrder">
        <spinner-loader />
    </div>
    <div
        class="chart-container"
        v-if="!loadingOrder && chartData.datasets[0].data.length > 0"
    >
        <h3>Thống kê doanh thu</h3>
        <Line id="my-chart-id" :options="chartOptions" :data="chartData" />
    </div>
</template>

<style lang="css" scoped>
.loading {
    position: absolute;
    top: 0;
    left: 0;
    background-color: rgba(0, 0, 0, 0.5);
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}

.chart-container {
    width: 70%;
}
</style>
