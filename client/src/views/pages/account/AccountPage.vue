<template>
    <div class="account">
        <div class="account-section">
            <div class="account-avatar">
                <div class="account-img">
                    <img
                        :src="urlImg ? urlImg : '@/assets/imgs/avatar.png'"
                        alt="Ảnh đại diện"
                    />
                    <input
                        @change="handleChangeImg($event)"
                        id="avatar"
                        type="file"
                        hidden
                    />
                    <label for="avatar">
                        <i class="fa-solid fa-pencil"></i>
                    </label>
                    <span>Ảnh đại diện</span>
                </div>
            </div>
            <div class="line-vertical"></div>
            <div class="account-info">
                <div class="account-info-title">
                    <h3>Thông tin tài khoản</h3>
                </div>
                <div class="account-info-row">
                    <div class="info-content-item user-name">
                        <label for="userName">Tên đăng nhập</label>
                        <b-input v-model="userInfor.userName" id="userName" />
                    </div>
                    <div class="info-content-item full-name">
                        <label for="fullName">Họ tên</label>
                        <b-input v-model="userInfor.fullName" id="fullName" />
                    </div>
                </div>
                <div class="account-info-row">
                    <div class="info-content-item email">
                        <label for="email">Email</label>
                        <b-input v-model="userInfor.email" id="email" />
                    </div>
                    <div class="info-content-item phone-number">
                        <label for="phoneNumber">Số điện thoại</label>
                        <b-input
                            v-model="userInfor.phoneNumber"
                            id="phoneNumber"
                        />
                    </div>
                </div>
                <div class="account-info-row">
                    <div class="info-content-item date-birth">
                        <label for="dateOfBirth">Ngày sinh</label>
                        <b-datepicker
                            v-model="userInfor.dateOfBirth"
                            id="dateOfBirth"
                        />
                    </div>
                </div>
                <div class="account-btn">
                    <b-button
                        @click="hadnleSaveChange()"
                        value="Lưu thay đổi"
                        type="primary"
                    />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { useUserStore } from "@/stores/user";
import { nextTick, ref } from "vue";

// ---------------------- Props ----------------------

// ---------------------- Khai báo biến --------------
const userStore = useUserStore();
const urlImg = ref(null);
const isChangeImg = ref(false);
const userInfor = ref({});
const fileImg = ref(null);

// ---------------------- Watcher --------------------

// ---------------------- Lifecycle ------------------
nextTick(async () => {
    await userStore.fetchGetById();
    userInfor.value = userStore.userInfor;
    urlImg.value = userStore.userInfor.image;
});

// ---------------------- Hàm xử lý ------------------
const handleChangeImg = (event) => {
    urlImg.value = URL.createObjectURL(event.target.files[0]);
    fileImg.value = event.target.files[0];
    console.log(event.target.files[0]);
    isChangeImg.value = true;
};

const hadnleSaveChange = async () => {
    if (isChangeImg.value) {
        let formData = new FormData();
        formData.append("image", fileImg.value);
        await userStore.fetchPostImage(formData);
    }
    await userStore.fetchEditProfile(userInfor.value);
};
</script>

<style scoped src="./account.css"></style>
