<template>
    <div class="not-found">
        <i class="fa-regular fa-face-frown"></i>
        <p class="title">404</p>
        <p class="description">Đường dẫn này đã bị hỏng hoặc không tồn tại!</p>
        <router-link :to="{ name: 'HomePage' }">Quay lại trang chủ</router-link>
    </div>
</template>

<script setup>

</script>

<style scoped>
.not-found {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100vh;
    flex-direction: column;
}

.not-found>i {
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

.not-found>a {
    margin: 20px;
    border: 1px solid var(--color-primary);
    padding: 10px;
    text-decoration: none;
    color: var(--color-primary);
    border-radius: var(--border-radius);
    font-weight: 600;
    background-color: var(--color-white);
}

.not-found>a:hover {
    background-color: var(--color-primary);
    color: var(--color-white);
}
</style>