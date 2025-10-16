<script setup>
import { nextTick, ref } from "vue";
import { dialogConfirm } from "@/helpers/swal";
import { useCouponStore } from "@/stores/coupon";
import { storeToRefs } from "pinia";
import CouponModal from "./CouponModal.vue";

const couponStore = useCouponStore();
const { isLoading, couponsList, pagination } = storeToRefs(couponStore);
const totalPage = ref(pagination.value.lastPage + 1);
const isShowForm = ref(false);
const statusForm = ref("ADD");
const couponCode = ref(null);
const page = ref(1);
const perPage = ref(12);

nextTick(async () => {
    await couponStore.fetchGetAll(page.value - 1, perPage.value);
    totalPage.value = pagination.value.lastPage + 1;
});

const handleShowFormAdd = () => {
    statusForm.value = "ADD";
    isShowForm.value = true;
};

const handleShowFormEdit = (id) => {
    couponCode.value = id;
    statusForm.value = "EDIT";
    isShowForm.value = true;
};

const handleDelete = async (id) => {
    dialogConfirm(
        "Xác nhận xóa",
        "Bạn có chắc muốn xóa mã giảm giá này?",
        async () => {
            await couponStore.fetchDelete(id, 0, perPage.value);
        }
    );
};
</script>

<template>
    <div class="loading" v-if="isLoading">
        <spinner-loader></spinner-loader>
    </div>
    <div class="admin-table">
        <div class="admin-header">
            <h1>Quản lý mã giảm giá</h1>
            <div class="btn-group">
                <b-button @click="handleShowFormAdd" type="primary"
                    >Thêm mã giảm giá</b-button
                >
            </div>
        </div>
        <table class="table table-bordered border-primary">
            <thead>
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">Mã giảm giá</th>
                    <th scope="col">Giảm giá</th>
                    <th scope="col">Ngày hết hạn</th>
                    <th scope="col">Số lượng</th>
                    <th scope="col">Trạng thái</th>
                    <th class="action" scope="col">Chức năng</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(item, index) in couponsList" :key="item?.code">
                    <th width="50px" scope="row">
                        {{ index + 1 + perPage * (page - 1) }}
                    </th>
                    <td>{{ item?.code }}</td>
                    <td>{{ item?.discount }}%</td>
                    <td>
                        {{
                            item?.expirationDate
                                ? $formatValue.formatDateTime(
                                      item?.expirationDate
                                  )
                                : ""
                        }}
                    </td>
                    <td>{{ item?.quantity }}</td>
                    <td>
                        <span :class="['status', { expired: item?.expired }]">
                            {{
                                item?.expired == false
                                    ? "Đang hoạt động"
                                    : "Ngưng hoạt động"
                            }}
                        </span>
                    </td>
                    <td class="action">
                        <button
                            @click="handleShowFormEdit(item?.code)"
                            class="btn-edit"
                        >
                            <i class="fa-solid fa-pencil"></i>
                        </button>
                        <button
                            @click="handleDelete(item?.code)"
                            class="btn-delete"
                        >
                            <i class="fa-solid fa-trash-can"></i>
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
        <v-pagination
            v-model="page"
            :length="totalPage"
            rounded="circle"
        ></v-pagination>
    </div>
    <CouponModal
        v-if="isShowForm"
        :page="page - 1"
        :perPage="perPage"
        :statusForm="statusForm"
        :couponCode="couponCode"
        @closeModal="isShowForm = false"
    >
    </CouponModal>
</template>

<style lang="css" src="../admin-table.css" scoped></style>
