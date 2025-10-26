<template>
    <div id="home-cover">
        <div class="loading" v-if="isLoading">
            <spinner-loader />
        </div>
        <b-carousel :autoplay="4000" :navigator="false" :pagination="false">
            <V-Slide v-for="slide in galleryList" :key="slide">
                <RouterLink
                    class="carousel__item"
                    :to="{
                        name: 'DetailProduct',
                        params: { id: slide?.product?.id },
                    }"
                >
                    <img :src="slide?.image" alt="" />
                </RouterLink>
            </V-Slide>
        </b-carousel>
    </div>
</template>

<script setup>
import { useGalleryStore } from "@/stores/gallery";
import { storeToRefs } from "pinia";
import { nextTick } from "vue";

// ------------------------- Khai báo biến ---------------------------
const galleryStore = useGalleryStore();
const { galleryList, isLoading } = storeToRefs(galleryStore);

// ------------------------- Lifecycle --------------------------------
nextTick(async () => {
    await galleryStore.fetchGetAll();
});

// ------------------------- Hàm xử lý -------------------------------
</script>

<style scoped>
#home-cover {
    position: relative;
    min-height: 100px;
}

.loading {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}
</style>
