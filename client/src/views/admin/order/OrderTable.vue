<script setup>
import { nextTick, ref, watch } from "vue";
import { storeToRefs } from "pinia";
import { useOrderStore } from "@/stores/order";
import { dialog, dialogConfirm } from "@/helpers/swal";
import OrderModal from "./OrderModal.vue";
import formatValue from "@/helpers/formatValue";

const orderStore = useOrderStore();
const { loadingOrder, orderList, pagination } = storeToRefs(orderStore);
const totalPage = ref(0);
const page = ref(1);
const perPage = ref(12);
const showModal = ref(false);
const orderId = ref(null);

const startDate = ref(null);
const endDate = ref(null);
const statusSearch = ref("");

nextTick(async () => {
    await orderStore.fetchGetAllOrder(page.value - 1, perPage.value);
    totalPage.value = pagination.value.lastPage + 1;
});

watch(
    () => page.value,
    async () => {
        await orderStore.fetchGetAllOrder(page.value - 1, perPage.value);
    }
);

const getStatusOrder = (status) => {
    switch (status) {
        case "PENDING":
            return "Chờ xác nhận";
        case "PAID":
            return "Đã thanh toán";
        case "UNPAID":
            return "Chưa thanh toán";
        case "PROCESSED":
            return "Đã xác nhận";
        case "SHIPPING":
            return "Đang vận chuyển";
        case "SHIPPED":
            return "Đã giao";
        case "CANCELLED":
            return "Đã huỷ";
        default:
            return "";
    }
};

const handleShowDetail = async (item) => {
    orderId.value = item?.id;
    showModal.value = true;
};

const handleCancelOrder = async (item) => {
    if (item?.status === "CANCELLED") {
        dialog("Thông báo", "warning", "Đơn hàng đã bị hủy");
        return;
    }
    if (item?.status === "SHIPPING" || item?.status === "SHIPPED") {
        dialog(
            "Thông báo",
            "warning",
            "Không thể hủy đơn hàng đã giao cho đơn vị vận chuyển"
        );
        return;
    }
    dialogConfirm("Xác nhận", "Hủy đơn hàng?", async () => {
        await orderStore.fetchUpdateOrder(
            item?.id,
            {
                status: "CANCELLED",
            },
            page.value - 1,
            perPage.value
        );
    });
};

const filterOrder = async () => {
    if (startDate.value !== null || startDate.value !== "") {
        startDate.value = formatValue.formatTimestamp(startDate.value, false);
    }
    if (endDate.value !== null || endDate.value !== "") {
        endDate.value = formatValue.formatTimestamp(endDate.value, false);
    }
    console.log(startDate.value, endDate.value, statusSearch.value);

    await orderStore.fetchFilterOrders(
        startDate.value,
        endDate.value,
        statusSearch.value,
        page.value - 1,
        perPage.value
    );
    totalPage.value = pagination.value.lastPage + 1;
};
</script>

<template>
    <div class="loading" v-if="loadingOrder">
        <spinner-loader></spinner-loader>
    </div>
    <div class="admin-table">
        <div class="admin-header">
            <h1>Quản lý đơn hàng</h1>
            <div class="btn-group btn-group-filter">
                <div class="btn-search">
                    <select
                        v-model="statusSearch"
                        class="form-select"
                        id="statusSearch"
                    >
                        <option value="" selected>Trạng thái</option>
                        <option value="PENDING">Chờ xác nhận</option>
                        <option value="PROCESSED">Đã xác nhận</option>
                        <option value="SHIPPING">Đang giao hàng</option>
                        <option value="SHIPPED">Đã giao hàng</option>
                        <option value="PAID">Đã thanh toán</option>
                        <option value="UNPAID">Chưa thanh toán</option>
                        <option value="CANCELLED">Đã hủy</option>
                    </select>
                    <b-datepicker
                        id="startDate"
                        v-model="startDate"
                        :timePicker="true"
                        placeholder="Từ ngày"
                    />
                    <b-datepicker
                        id="endDate"
                        v-model="endDate"
                        :timePicker="true"
                        placeholder="Đến ngày"
                    />
                    <div class="btn-search-button">
                        <b-button @click="filterOrder" type="secondary">
                            Tìm kiếm
                        </b-button>
                    </div>
                </div>
            </div>
        </div>
        <table class="table table-bordered border-primary">
            <thead>
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">Họ và tên</th>
                    <th scope="col">Số điện thoại</th>
                    <th scope="col">Địa chỉ</th>
                    <th scope="col">Mã giảm giá</th>
                    <th scope="col">Hình thức TT</th>
                    <th scope="col">Tổng tiền</th>
                    <th scope="col">Trạng thái</th>
                    <th scope="col">Chức năng</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(item, index) in orderList" :key="item?.id">
                    <th width="50px" scope="row">
                        {{ index + 1 + perPage * (page - 1) }}
                    </th>
                    <td>{{ item.fullName }}</td>
                    <td>{{ item?.phoneNumber }}</td>
                    <td class="value-too-long" :title="item?.address">
                        <span>
                            {{ item?.address }}
                        </span>
                    </td>
                    <td>{{ item?.coupon?.code }}</td>
                    <td>{{ item?.paymentMethod }}</td>
                    <td>{{ $formatValue.formatMoney(item?.totalMoney) }}</td>
                    <td class="status-order">
                        <span
                            :class="[
                                'status-order-item',
                                item?.status.toLowerCase(),
                            ]"
                        >
                            {{ getStatusOrder(item?.status) }}
                        </span>
                    </td>
                    <td class="action">
                        <button
                            @click="handleShowDetail(item)"
                            class="btn-confirm"
                        >
                            <i class="fa-regular fa-rectangle-list"></i>
                        </button>
                        <button
                            @click="handleCancelOrder(item)"
                            class="btn-cancel"
                        >
                            <i class="fa-solid fa-ban"></i>
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
        <v-pagination
            v-model="page"
            size="40"
            :length="totalPage"
            rounded="circle"
        ></v-pagination>
    </div>
    <OrderModal
        v-if="showModal"
        :orderId="orderId"
        @closeModal="showModal = false"
        :page="page - 1"
        :perPage="perPage"
    />
</template>

<style lang="css" src="../admin-table.css" scoped></style>
