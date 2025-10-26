<script lang="js" setup>
import { Carousel, Navigation, Pagination, Slide } from 'vue3-carousel';
import { ref, reactive } from "vue";
// ---------------------- Props -------------------------
const props = defineProps({
    slides: {
        type: Array,
        default: null
    },
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
    }
})

// ---------------------- Ref, reactive, emits ----------
const currentSlide = ref(0);
const breakpoints = reactive({
    // 700px and up
    700: {
        itemsToShow: 4,
        snapAlign: 'center',
    },
    // 1024 and up
    1024: {
        itemsToShow: 4,
        snapAlign: 'start',
    },
})

// ---------------------- Lifecycle ---------------------


// ---------------------- Methods -----------------------
const slideTo = (val) => {
    currentSlide.value = val;
}
</script>

<template>
    <div id="carousel-gallery">
        <Carousel
            id="gallery"
            :items-to-show="1"
            :autoplay="props.autoplay"
            :wrap-around="props.wrapAround"
            v-model="currentSlide"
        >
            <Slide v-for="slide in props.slides" :key="slide">
                <img class="carousel__item" :src="slide?.url" alt="" />
            </Slide>
        </Carousel>

        <Carousel
            id="thumbnails"
            :breakpoints="breakpoints"
            :autoplay="props.autoplay"
            :wrap-around="props.wrapAround"
            v-model="currentSlide"
        >
            <Slide v-for="(slide, index) in props.slides" :key="slide">
                <img
                    class="carousel__item"
                    @click="slideTo(index)"
                    :src="slide?.url"
                    alt=""
                />
            </Slide>

            <template #addons>
                <Navigation v-if="props.navigator" />
                <Pagination v-if="props.pagination" />
            </template>
        </Carousel>
    </div>
</template>

<style lang="css">
#carousel-gallery #gallery {
    border: 1px solid var(--color-border);
    border-radius: var(--border-radius-page);
}

#carousel-gallery #thumbnails .carousel__slide {
    border-radius: var(--border-radius-page);
    padding: 5px;
    cursor: pointer;
    box-sizing: border-box;
}

#carousel-gallery #thumbnails .carousel__item {
    border: 1px solid var(--color-border);
}

#carousel-gallery #thumbnails .carousel__slide--active .carousel__item {
    border: 1px solid var(--color-primary-focus);
}

#carousel-gallery #thumbnails .carousel__prev {
    left: -25px;
}

#carousel-gallery #thumbnails .carousel__next {
    right: -25px;
}

#carousel-gallery #thumbnails .carousel__prev,
#carousel-gallery #thumbnails .carousel__next {
    background-color: transparent;
    border: none;
}
</style>
