<template>
    <div class="forbidden">
        <i class="fa-regular fa-face-frown"></i>
        <p class="title">403</p>
        <p class="description">Bạn không có quyền truy cập vào chức năng này!</p>
        <router-link :to="{ name: 'HomePage' }">Quay lại trang chủ</router-link>
    </div>
</template>

<script setup>

</script>

<style scoped>
.forbidden {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100vh;
    flex-direction: column;
}

.forbidden>i {
    font-size: 6rem;
}

.title {
    font-size: 5rem;
    font-weight: 600;
    color: var(--color-orange);
    letter-spacing: 6px;
    font-family: cursive;
}

.description {
    font-size: 2rem;
}

.forbidden>a {
    margin: 20px;
    border: 1px solid var(--color-primary);
    padding: 10px;
    text-decoration: none;
    color: var(--color-primary);
    border-radius: var(--border-radius);
    font-weight: 600;
    background-color: var(--color-white);
}

.forbidden>a:hover {
    background-color: var(--color-primary);
    color: var(--color-white);
}
</style>