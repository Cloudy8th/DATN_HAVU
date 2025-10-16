<template>
    <div id="home-page">
        <TheSidebar />
        <div class="home-page-content">
            <HomeCover />
            <HomeFlashSale />
            <HomeBestseller />
            <HomeProducts />
        </div>
    </div>
</template>

<script setup>
import HomeCover from './HomeCover.vue';
import HomeFlashSale from './HomeFlashSale.vue';
import HomeBestseller from './HomeBestseller.vue';
import HomeProducts from './HomeProducts.vue';
import TheSidebar from '@/views/layouts/TheSidebar.vue';

</script>

<style scoped>
#home-page {
    width: 100%;
    height: 100%;
    display: flex;
    gap: 20px;
}

.home-page-content {
    width: calc(100% - var(--width-sidebar));
    display: flex;
    flex-direction: column;
    gap: 20px;
}
</style>