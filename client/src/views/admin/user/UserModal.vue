<script setup>
import { dialog } from "@/helpers/swal";
import { useRoleStore } from "@/stores/role";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { nextTick, reactive, ref } from "vue";

const emits = defineEmits(["closeModal"]);

const props = defineProps({
    statusForm: String,
    userId: String,
    page: Number,
    perPage: Number,
});

const userStore = useUserStore();
const roleStore = useRoleStore();
const { isLoading, user, isSuccess } = storeToRefs(userStore);
const { roles } = storeToRefs(roleStore);

const userData = reactive({
    fullName: null,
    userName: null,
    email: null,
    phoneNumber: null,
    address: null,
    dateOfBirth: null,
    password: null,
    roleId: null,
});
const confirmPassword = ref("");

nextTick(async () => {
    await roleStore.fetchGetAll();
    console.log(roles.value);
    if (props.statusForm === "EDIT") {
        console.log(props.userId);
        await userStore.fetchGetUser(props.userId);
        Object.assign(userData, user.value.data);
        userData.roleId = user.value.data.role.id;
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
    if (
        userData.password !== null &&
        userData.password !== confirmPassword.value
    ) {
        dialog("Lỗi", "error", "Xác nhận mật khẩu không khớp");
        return;
    }
    if (props.statusForm === "ADD") {
        await userStore.fetchInsert(userData, props.page, props.perPage);
    } else if (props.statusForm === "EDIT") {
        await userStore.fetchEditUser(
            props.userId,
            userData,
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
        <div class="loading" v-if="isLoading">
            <spinner-loader></spinner-loader>
        </div>
        <div class="admin-modal__section" v-click-outside="handleOutsideClick">
            <div class="modal-header">
                <h5>
                    {{
                        statusForm === "ADD"
                            ? "Thêm mới người dùng"
                            : "Cập nhật người dùng"
                    }}
                </h5>
                <button @click="handleCloseModal()">
                    <i class="fa-solid fa-xmark"></i>
                </button>
            </div>
            <div class="modal-body">
                <form @submit.prevent="handleSubmit" class="row g-3 form-group">
                    <div class="form-item col-md-6">
                        <label for="fullName" class="form-label"
                            >Họ và tên</label
                        >
                        <input
                            v-model="userData.fullName"
                            type="text"
                            class="form-control"
                            id="fullName"
                        />
                    </div>
                    <div class="form-item col-md-6">
                        <label for="userName" class="form-label"
                            >Tên người dùng</label
                        >
                        <input
                            v-model="userData.userName"
                            type="text"
                            class="form-control"
                            id="userName"
                            required
                        />
                    </div>
                    <div class="form-item col-md-6">
                        <label for="email" class="form-label">Email</label>
                        <input
                            v-model="userData.email"
                            type="text"
                            class="form-control"
                            id="email"
                            required
                        />
                    </div>
                    <div class="form-item col-md-6">
                        <label for="phoneNumber" class="form-label"
                            >Số điện thoại</label
                        >
                        <input
                            v-model="userData.phoneNumber"
                            type="text"
                            class="form-control"
                            id="phoneNumber"
                        />
                    </div>
                    <div class="form-item col-md-6">
                        <label for="dateOfBirth" class="form-label"
                            >Ngày sinh</label
                        >
                        <b-datepicker
                            v-model="userData.dateOfBirth"
                            id="dateOfBirth"
                        />
                    </div>
                    <div class="form-item col-md-6">
                        <label for="category" class="form-label"
                            >Quyền người dùng</label
                        >
                        <select
                            v-model="userData.roleId"
                            class="form-select"
                            id="category"
                            required
                        >
                            <option
                                :value="role.id"
                                v-for="role in roles"
                                :key="role.id"
                            >
                                {{
                                    role?.id == "ADMIN"
                                        ? "Quản trị viên"
                                        : "Người dùng"
                                }}
                            </option>
                        </select>
                    </div>
                    <div v-if="statusForm === 'ADD'" class="form-item col-md-6">
                        <label for="password" class="form-label"
                            >Mật khẩu</label
                        >
                        <input
                            v-model="userData.password"
                            type="password"
                            class="form-control"
                            id="password"
                            required
                        />
                    </div>
                    <div v-if="statusForm === 'ADD'" class="form-item col-md-6">
                        <label for="confirmPassword" class="form-label"
                            >Xác nhận mật khẩu</label
                        >
                        <input
                            v-model="confirmPassword"
                            type="password"
                            class="form-control"
                            id="confirmPassword"
                            required
                        />
                    </div>
                    <div class="form-item col-md-12">
                        <label for="address" class="form-label">Địa chỉ</label>
                        <input
                            v-model="userData.address"
                            type="text"
                            class="form-control"
                            id="address"
                        />
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
