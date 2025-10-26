<script lang="js" setup>
import { reactive, ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
// ---------------------- Props -------------------------

// ---------------------- Props -------------------------


// ---------------------- Ref, reactive, emits ----------
const user = reactive({
    email: '',
    password: '',
})
const confirmPassword = ref('');

// ---------------------- Lifecycle ---------------------
const authStore = useAuthStore();

// ---------------------- Methods -----------------------
const onSubmit = () => {
    if (user.password != confirmPassword.value) {
        alert("Confirm password not match with password");
        return;
    }
    authStore.fetchRegister(user);
}
</script>

<template>
    <div class="register__container">
        <!-- Login 1 - Bootstrap Brain Component -->
        <div class="container">
            <div class="row justify-content-md-center">
                <div class="col-12 col-md-11 col-lg-8 col-xl-7 col-xxl-6">
                    <div class="bg-white p-4 p-md-5 rounded shadow-sm">
                        <div class="row">
                            <div class="col-12">
                                <div class="text-center mb-5">
                                    <router-link :to="{ name: 'HomePage' }">
                                        <img
                                            src="@/assets/imgs/TMart-logo.png"
                                            alt="Logo"
                                            width="150"
                                        />
                                    </router-link>
                                </div>
                            </div>
                        </div>
                        <form @submit.prevent="onSubmit">
                            <div class="row gy-3 gy-md-4 overflow-hidden">
                                <div class="col-12">
                                    <label
                                        for="email"
                                        class="d-block form-label"
                                        >Email
                                        <span class="text-danger"
                                            >*</span
                                        ></label
                                    >
                                    <div class="input-group">
                                        <span class="input-group-text">
                                            <i class="fa-solid fa-user"></i>
                                        </span>
                                        <input
                                            v-model="user.email"
                                            placeholder="Email"
                                            type="email"
                                            class="form-control"
                                            name="email"
                                            id="email"
                                            required
                                        />
                                    </div>
                                </div>
                                <div class="col-12">
                                    <label
                                        for="password"
                                        class="d-block form-label"
                                        >Mật khẩu
                                        <span class="text-danger"
                                            >*</span
                                        ></label
                                    >
                                    <div class="input-group">
                                        <span class="input-group-text">
                                            <i class="fa-solid fa-key"></i>
                                        </span>
                                        <input
                                            v-model="user.password"
                                            placeholder="********"
                                            type="password"
                                            class="form-control"
                                            name="password"
                                            id="password"
                                            value=""
                                            required
                                        />
                                    </div>
                                </div>
                                <div class="col-12">
                                    <label
                                        for="confirmPassword"
                                        class="d-block form-label"
                                        >Nhập lại mật khẩu
                                        <span class="text-danger"
                                            >*</span
                                        ></label
                                    >
                                    <div class="input-group">
                                        <span class="input-group-text">
                                            <i class="fa-solid fa-key"></i>
                                        </span>
                                        <input
                                            v-model="confirmPassword"
                                            placeholder="********"
                                            type="password"
                                            class="form-control"
                                            name="confirmPassword"
                                            id="confirmPassword"
                                            value=""
                                            required
                                        />
                                    </div>
                                </div>
                                <div class="col-12">
                                    <div class="d-grid">
                                        <button
                                            class="btn btn-primary btn-lg"
                                            type="submit"
                                        >
                                            Đăng ký
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </form>
                        <div class="row">
                            <div class="col-12">
                                <hr class="mt-5 mb-4 border-secondary-subtle" />
                                <div
                                    class="d-flex gap-1 gap-md-1 flex-column flex-md-row justify-content-md-center"
                                >
                                    Đã có tài khoản?
                                    <router-link
                                        :to="{ name: 'Login' }"
                                        class="link-secondary text-decoration-none"
                                        >Đăng nhập</router-link
                                    >
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="css" scoped>
.register__container {
    width: 100%;
    height: 100vh;
    background-color: var(--color-primary-opacity);
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>
