<script lang="js" setup>
import { dialog } from '@/helpers/swal';
import { useUserStore } from '@/stores/user';
import { reactive } from 'vue';

const userStore = useUserStore();

const userInfor = reactive({
    oldPassword: null,
    newPassword: null,
    confirmPassword: null
})

const handleSubmit = async () => {
    if (userInfor.newPassword !== userInfor.confirmPassword) {
        dialog('Lỗi', 'error', 'Xác nhận mật khẩu không đúng!');
        return;
    }
    await userStore.fetchChangePassword({
        oldPassword: userInfor.oldPassword,
        newPassword: userInfor.newPassword
    })
}

</script>

<template>
    <div class="change-password__container container">
        <div class="change-password__section">
            <form @submit.prevent="handleSubmit" class="row g-3 form-group">
                <div class="form-item col-md-12">
                    <label for="oldPassword" class="form-label">Mật khẩu cũ</label>
                    <input v-model="userInfor.oldPassword" type="password" class="form-control" id="oldPassword"
                        required>
                </div>
                <div class="form-item col-md-12">
                    <label for="newPassword" class="form-label">Mật khẩu mới</label>
                    <input v-model="userInfor.newPassword" type="password" class="form-control" id="newPassword"
                        required>
                </div>
                <div class="form-item col-md-12">
                    <label for="confirmPassword" class="form-label">Xác nhận mật khẩu</label>
                    <input v-model="userInfor.confirmPassword" type="password" class="form-control" id="confirmPassword"
                        required>
                </div>
                <div class="btn-submit col-12">
                    <button class="btn btn-primary" type="submit">Cập nhật</button>
                </div>
            </form>
        </div>
    </div>
</template>

<style lang="css" scoped>
.change-password__container {
    width: 100%;
    padding: 40px;
    border-radius: var(--border-radius-page);
    background-color: var(--color-white);
    box-shadow: 0 3px 5px var(--color-box-shadow);
}

.change-password__section {
    width: 50%;
    margin: 0 auto;
}

.change-password__section label {
    display: block;
}
</style>
