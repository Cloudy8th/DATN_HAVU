<script setup>
import { useCouponStore } from "@/stores/coupon";
import { storeToRefs } from "pinia";
import { nextTick, reactive } from "vue";

const emits = defineEmits(["closeModal"]);

const props = defineProps({
    statusForm: String,
    couponCode: String,
    page: Number,
    perPage: Number,
});

const couponStore = useCouponStore();
const { isLoading, isSuccess, coupon } = storeToRefs(couponStore);
const couponData = reactive({
    code: null,
    discount: null,
    expirationDate: null,
    expired: false,
    quantity: null,
});

nextTick(async () => {
    if (props.statusForm === "EDIT") {
        await couponStore.fetchGetById(props.couponCode);
        Object.assign(couponData, coupon.value);
    }
});

const handleOutsideClick = () => {
    const datepickerEl = document.querySelector(".dp__outer_menu_wrap");
    const dialog = document.querySelector(".swal2-popup");
    if (datepickerEl || dialog) {
        return;
    }
    handleCloseModal();
};

const handleCloseModal = () => {
    emits("closeModal");
};

const handleSubmit = async () => {
    if (props.statusForm === "ADD") {
        await couponStore.fetchInsert(couponData, props.page, props.perPage);
    } else if (props.statusForm === "EDIT") {
        await couponStore.fetchUpdate(
            props.couponCode,
            couponData,
            props.page,
            props.perPage
        );
    }
    if (isLoading.value === false && isSuccess.value === true) {
        emits("closeModal");
    }
};
</script>

<template>
    <div class="admin-modal__container">
        <div v-if="isLoading" class="loading">
            <spinner-loader></spinner-loader>
        </div>
        <div class="admin-modal__section" v-click-outside="handleOutsideClick">
            <div class="modal-header">
                <h5>
                    {{
                        statusForm === "ADD"
                            ? "Thêm mã giảm giá"
                            : "Cập nhật giảm giá"
                    }}
                </h5>
                <button @click="handleCloseModal()">
                    <i class="fa-solid fa-xmark"></i>
                </button>
            </div>
            <div class="modal-body">
                <form @submit.prevent="handleSubmit" class="row g-3 form-group">
                    <div class="form-item col-md-4">
                        <label for="code" class="form-label">Mã giảm giá</label>
                        <input
                            :readonly="props.statusForm === 'EDIT'"
                            v-model="couponData.code"
                            type="text"
                            class="form-control"
                            id="code"
                            required
                        />
                    </div>
                    <div class="form-item col-md-4">
                        <label for="discount" class="form-label"
                            >Tỉ lệ giảm giá</label
                        >
                        <input
                            v-model="couponData.discount"
                            type="number"
                            class="form-control"
                            id="discount"
                            required
                        />
                    </div>
                    <div class="form-item col-md-4">
                        <label for="quantity" class="form-label"
                            >Số lượng</label
                        >
                        <input
                            v-model="couponData.quantity"
                            type="number"
                            class="form-control"
                            id="quantity"
                            required
                        />
                    </div>
                    <div class="form-item col-md-6">
                        <label for="expirationDate" class="form-label">
                            Ngày hết hạn
                        </label>
                        <b-datepicker
                            id="expirationDate"
                            v-model="couponData.expirationDate"
                            :timePicker="true"
                        />
                    </div>
                    <div class="form-item col-md-6">
                        <label for="status" class="form-label">
                            Trạng thái
                        </label>
                        <select
                            v-model="couponData.expired"
                            class="form-select"
                            id="status"
                            required
                        >
                            <option :value="false">Đang sử dụng</option>
                            <option :value="true">Ngưng sử dụng</option>
                        </select>
                    </div>
                    <div class="btn-submit col-12">
                        <button class="btn btn-primary" type="submit">
                            Lưu
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<style lang="css" src="../admin-modal.css" scoped></style>
