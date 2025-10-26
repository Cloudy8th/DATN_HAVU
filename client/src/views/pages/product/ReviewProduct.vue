<template>
    <div id="review-product">
        <div class="loading" v-if="loadingFeedback">
            <spinner-loader></spinner-loader>
        </div>
        <div class="review-header">
            <h3>ĐÁNH GIÁ SẢN PHẨM</h3>
            <div class="review-filter">
                <div class="filter-title">
                    <label
                        >{{
                            feedbackList.length == 0
                                ? 0
                                : Number(props.avgStar)
                        }}/5</label
                    >
                </div>
                <div class="filter-star">
                    <div
                        @click="handleFilter(null)"
                        :class="[
                            'filter-star-item',
                            'all-star',
                            { active: star === null },
                        ]"
                    >
                        Tất cả
                    </div>
                    <div
                        @click="handleFilter(5)"
                        :class="[
                            'filter-star-item',
                            'five-star',
                            { active: star === 5 },
                        ]"
                    >
                        5 Sao
                    </div>
                    <div
                        @click="handleFilter(4)"
                        :class="[
                            'filter-star-item',
                            'four-star',
                            { active: star === 4 },
                        ]"
                    >
                        4 Sao
                    </div>
                    <div
                        @click="handleFilter(3)"
                        :class="[
                            'filter-star-item',
                            'three-star',
                            { active: star === 3 },
                        ]"
                    >
                        3 Sao
                    </div>
                    <div
                        @click="handleFilter(2)"
                        :class="[
                            'filter-star-item',
                            'two-star',
                            { active: star === 2 },
                        ]"
                    >
                        2 Sao
                    </div>
                    <div
                        @click="handleFilter(1)"
                        :class="[
                            'filter-star-item',
                            'one-star',
                            { active: star === 1 },
                        ]"
                    >
                        1 Sao
                    </div>
                </div>
            </div>
        </div>
        <div class="review-body">
            <div
                class="review-content"
                v-for="item in feedbackList"
                :key="item.id"
            >
                <div class="review-item">
                    <div class="reviewer-avt">
                        <img
                            :src="
                                item?.user?.image
                                    ? item?.user?.image
                                    : '@/assets/imgs/avatar.png'
                            "
                            :alt="item?.user?.userName"
                        />
                    </div>
                    <div class="reviewer-info">
                        <label>{{ item?.user?.userName }}</label>
                        <b-rating :value="item?.star" isReadonly />
                        <div class="reviewer-more-info">
                            <p class="reviewer-time">
                                {{
                                    $formatValue.formatDateTime(item?.createdAt)
                                }}
                            </p>
                            <div class="line"></div>
                        </div>
                        <div class="item-content">
                            <p>
                                {{ item?.note }}
                            </p>
                            <!-- <div class="item-content-img">
                                <img src='@/assets/imgs/Iphone15-promax.webp' alt="">
                            </div> -->
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="review-footer">
            <div class="paging">
                <v-pagination
                    v-model="page"
                    :length="totalPage"
                    :size="40"
                    color="#006fff"
                    active-color="#0138ff"
                    theme="#ffa500"
                ></v-pagination>
            </div>
        </div>
    </div>
</template>

<script setup>
import { useFeedbackStore } from "@/stores/feedback";
import { storeToRefs } from "pinia";
import { nextTick, ref, watch } from "vue";
import { useRoute } from "vue-router";

const props = defineProps({
    avgStar: {
        type: [Number, String],
        default: 0,
    },
});

const feedbackStore = useFeedbackStore();
const { loadingFeedback, feedbackList, pagination } =
    storeToRefs(feedbackStore);
const totalPage = ref(pagination.value.lastPage + 1);
const page = ref(1);
const perPage = ref(10);
const star = ref(null);

const route = useRoute();
const productId = ref(route.params.id);

nextTick(async () => {
    await feedbackStore.fetchGetAllByProduct(
        productId.value,
        page.value - 1,
        perPage.value,
        star.value
    );
    totalPage.value = pagination.value.lastPage + 1;
});

watch(
    () => page.value,
    async () => {
        await feedbackStore.fetchGetAllByProduct(
            productId.value,
            page.value - 1,
            perPage.value,
            star.value
        );
    }
);

const handleFilter = async (value) => {
    star.value = value;
    await feedbackStore.fetchGetAllByProduct(
        productId.value,
        page.value - 1,
        perPage.value,
        star.value
    );
};
</script>

<style scoped>
.loading {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    width: 100%;
    position: absolute;
    top: 0;
    left: 0;
    background-color: var(--bg-opacity);
}

#review-product {
    width: 100%;
    background-color: var(--color-white);
    border-radius: var(--border-radius-page);
    padding: 20px;
    box-shadow: 0 3px 5px var(--color-greyish);
    position: relative;
}

.review-header > h3 {
    font-size: 1.3rem;
    font-weight: 500;
    padding-bottom: 10px;
}

.review-header .review-filter {
    padding: 30px;
    background-color: var(--bg-main);
    display: flex;
    gap: 50px;
    border-radius: var(--border-radius-page);
    align-items: center;
}

.review-filter .filter-title {
    display: flex;
    gap: 10px;
}

.review-filter .filter-title > label {
    font-size: 1.2rem;
    font-weight: 500;
}

.review-filter .filter-star {
    display: flex;
    gap: 10px;
}

.review-filter .filter-star .filter-star-item {
    background-color: var(--color-white);
    border: 1px solid var(--color-border);
    padding: 5px 20px;
    cursor: pointer;
    border-radius: var(--border-radius);
}

.review-filter .filter-star .filter-star-item.active {
    color: var(--color-primary);
    border-color: var(--color-primary);
}

.review-body .review-content {
    border-bottom: 1px solid var(--color-border);
    padding: 20px 10px;
}

.review-body .review-content .review-item {
    padding: 0 20px;
    display: flex;
    align-items: flex-start;
    gap: 10px;
}

.review-body .review-content .review-item .reviewer-avt > img {
    object-fit: cover;
    width: 40px;
    border-radius: 50%;
}

.review-item .reviewer-info {
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.review-item .reviewer-info .reviewer-more-info {
    display: flex;
    gap: 5px;
    color: var(--color-grey);
}

.review-item .reviewer-info .reviewer-more-info > p {
    font-size: 0.9rem;
}

.review-item .reviewer-info .reviewer-more-info .line {
    height: auto;
    width: 1px;
    background-color: var(--color-border);
}

.review-item .reviewer-info .item-content {
    padding: 10px 0;
}

.review-item .reviewer-info .item-content > p {
    padding-bottom: 10px;
    line-height: 20px;
}

.review-item .reviewer-info .item-content .item-content-img {
    display: flex;
    gap: 10px;
}

.review-item .reviewer-info .item-content .item-content-img > img {
    object-fit: cover;
    width: 80px;
    border: 1px solid var(--color-border);
}

.review-footer {
    padding-top: 20px;
    display: flex;
    justify-content: center;
}

.review-footer .paging {
    width: 40%;
}
</style>
