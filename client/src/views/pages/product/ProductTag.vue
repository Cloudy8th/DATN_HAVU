<script setup lang="js">
const props = defineProps({
  item: { type: Object, required: true },
  // Chiều rộng cứng cho mỗi thẻ (px). Có thể override từ cha.
  widthPx: { type: Number, default: 180 }
});

const calculateStar = (feedbacks = []) => {
  if (!Array.isArray(feedbacks) || feedbacks.length === 0) return 0;
  const total = feedbacks.reduce((s, f) => s + (Number(f?.star) || 0), 0);
  return total / feedbacks.length;
};
</script>

<template>
  <!-- set cứng bằng CSS var, đồng thời min/max để đảm bảo không nở/teo -->
  <div
    class="product-tag__container"
    :style="{ '--card-w': props.widthPx + 'px' }"
  >
    <div class="product-img">
      <p v-if="item?.discount > 0" class="discount">{{ item?.discount }}%</p>
      <img
        :src="item?.imageProducts?.[0]?.url"
        :alt="item?.title || 'product image'"
      />
    </div>

    <div class="product-name">
      <p>{{ item?.title }}</p>
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
      <p class="price-new">{{ $formatValue.formatMoney(item?.salePrice) }}</p>
      <p class="price-old">
        {{
          item?.discount > 0 ? $formatValue.formatMoney(item?.originPrice) : ""
        }}
      </p>
    </div>
  </div>
</template>

<style scoped>
.product-tag__container {
  width: var(--card-w);
  min-width: var(--card-w);
  max-width: var(--card-w);
  border: 1px solid var(--color-border);
  border-radius: var(--border-radius-page);
  padding: 6px;
  display: block;
  transition: transform 0.12s ease-in;
  will-change: transform;
  box-shadow: 0 2px 5px var(--color-box-shadow);
  color: var(--color-text);
  background: var(--color-white);
}

.product-tag__container:hover {
  transform: translateY(-1px);
}

.product-img {
  position: relative;
  /* Giữ ảnh vuông, bao trọn khung */
  aspect-ratio: 1 / 1;
  overflow: hidden;
  border-radius: calc(var(--border-radius-page) - 2px);
}

.product-img > img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.discount {
  position: absolute;
  left: 6px;
  top: 6px;
  padding: 4px 6px;
  background-color: var(--color-pink);
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  color: var(--color-red);
  font-size: clamp(10px, 1.8vw, 12px);
}

.product-name {
  padding: 6px 8px 2px;
  height: auto;
  width: 100%;
}

.product-name > p {
  display: -webkit-box;
  line-height: 1.2rem;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  font-size: clamp(12px, 2.1vw, 14px);
  min-height: calc(1.2rem * 2);
}

.product-star {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  flex-wrap: wrap;
}

.product-star .number-sales {
  font-size: clamp(11px, 1.9vw, 12px);
  opacity: 0.9;
}

.product-price {
  padding: 4px 8px 6px;
  line-height: 1.25rem;
  display: grid;
  gap: 2px;
}

.product-price .price-new {
  color: var(--color-red);
  font-weight: 700;
  font-size: clamp(13px, 2.3vw, 16px);
}

.product-price .price-old {
  text-decoration: line-through;
  height: 1.1rem;
  font-size: clamp(11px, 1.9vw, 12px);
  opacity: 0.8;
}
</style>
