<script lang="js" setup>
import { Carousel, Navigation, Pagination } from 'vue3-carousel';
// ---------------------- Props -------------------------
const props = defineProps({
    autoplay: {
        type: Number,
        default: null
    },
    wrapAround: {
        type: Boolean,
        default: true
    },
    breakpoints: Object,
    navigator: {
        type: Boolean,
        default: true
    },
    pagination: {
        type: Boolean,
        default: true
    },
    id: String
})

// ---------------------- Ref, reactive, emits ----------


// ---------------------- Lifecycle ---------------------


// ---------------------- Methods -----------------------
</script>

<template>
    <div :id="id">
        <Carousel
            :autoplay="props.autoplay"
            :wrap-around="true"
            :breakpoints="props.breakpoints"
        >
            <slot></slot>

            <template #addons>
                <Navigation v-if="props.navigator" />
                <Pagination v-if="props.pagination" />
            </template>
        </Carousel>
    </div>
</template>

<style lang="css"></style>
