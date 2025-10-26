<script lang="js" setup>

const props = defineProps({
    item: Object
})

const calculateStar = (feedbacks) => {
    let totalStar = 0;
    feedbacks.forEach(feedback => {
        totalStar += feedback.star;
    })
    return totalStar / feedbacks?.length;
}
</script>

<template>
    <div class="product-tag__container">
        <div class="product-img">
            <p v-if="item?.discount > 0" class="discount">
                {{ item?.discount }}%
            </p>
            <img :src="item?.imageProducts[0]?.url" alt="" />
        </div>
        <div class="product-name">
            <p>{{ props?.item?.title }}</p>
        </div>
        <div class="product-star">
            <b-rating
                v-if="item?.feedbacks?.length > 0"
                :value="calculateStar(item?.feedbacks)"
                :stars="5"
                fontSize="12"
                isReadonly
            />
            <p class="number-sales">
                Đã bán: {{ $helper.formatNumber(item?.soldQuantity) }}
            </p>
        </div>
        <div class="product-price">
            <p class="price-new">
                {{ $formatValue.formatMoney(item?.salePrice) }}
            </p>
            <p class="price-old">
                {{
                    item?.discount > 0
                        ? $formatValue.formatMoney(item?.originPrice)
                        : ""
                }}
            </p>
        </div>
    </div>
</template>

<style lang="css" scoped>
.product-tag__container {
    border: 1px solid var(--color-border);
    border-radius: var(--border-radius-page);
    padding: 5px;
    display: block;
    transition: transform ease-in 0.1s;
    will-change: transform;
    box-shadow: 0 2px 5px var(--color-box-shadow);
    color: var(--color-text);
}

.product-item:hover {
    transform: translateY(-1px);
}

.product-img {
    position: relative;
}

.discount {
    position: absolute;
    padding: 5px;
    left: 0px;
    top: 0px;
    background-color: var(--color-pink);
    border-radius: var(--border-radius-page);
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 600;
    color: var(--color-red);
    font-size: 0.9rem;
}

.product-item .product-img > img {
    object-fit: cover;
    width: 100%;
    height: 130px;
}

.product-name {
    padding: 5px 10px;
    height: 50px;
    width: 100%;
    position: relative;
}

.product-name > p {
    width: inherit;
    display: -webkit-box;
    line-height: 20px;
    overflow: hidden;
    text-overflow: ellipsis;
    line-clamp: 2;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}

.product-star {
    display: flex;
    padding: 2px 10px;
    gap: 8px;
    flex-wrap: wrap;
}

.product-star .number-sales {
    font-size: 0.8rem;
}

.product-price {
    padding: 5px 10px;
    line-height: 20px;
}

.product-price .price-new {
    color: var(--color-red);
    font-weight: 600;
}

.product-price .price-old {
    text-decoration: line-through;
    height: 20px;
}
</style>
