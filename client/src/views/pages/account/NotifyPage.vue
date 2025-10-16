<template>
    <div class="notify-page">
        <div class="notify-section">
            <div class="notify-title">
                <h3>Thông báo của tôi</h3>
            </div>
            <div class="notify-content">
                <div class="content-header">
                    <b-button @click="handleReadAllNotifi">Đánh dấu đã đọc tất cả</b-button>
                </div>
                <div class="line"></div>
                <div class="content-body">
                    <router-link @click="handleReadNotifi(item?.id)"
                        :to="{ name: 'OrderDetail', params: { id: item?.order?.id } }"
                        :class="['content-body-item', { 'active': item?.read === false }]"
                        v-for="item in notificationList" :key="item.id">
                        <div class="product-info">
                            <img :src="item?.order?.orderDetails[0]?.product?.imageProducts[0]?.url" alt="">
                            <div class="notify-detail">
                                <div class="notify-detail-title">
                                    <p>{{ item?.title }}</p>
                                </div>
                                <div class="notify-detail-content">
                                    <p>{{ item?.content }}</p>
                                </div>
                                <div class="notify-detail-time">
                                    <p>{{ $formatValue.formatDateTime(item?.createdAt) }}</p>
                                </div>
                            </div>
                        </div>
                        <router-link @click="handleReadNotifi(item?.id)"
                            :to="{ name: 'OrderDetail', params: { id: item?.order?.id } }">Xem chi tiết</router-link>
                    </router-link>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { useNotificationStore } from '@/stores/notification';
import { storeToRefs } from 'pinia';
import { nextTick, ref } from 'vue';


// ---------------------- Props ----------------------


// ---------------------- Khai báo biến --------------
const notifyStore = useNotificationStore();
const { notificationList, pagination } = storeToRefs(notifyStore);

const page = ref(1);
const perPage = ref(15);
const totalPage = ref(0);

// ---------------------- Watcher --------------------


// ---------------------- Lifecycle ------------------
nextTick(async () => {
    await notifyStore.fetchGetAllByUser(page.value - 1, perPage.value);
    totalPage.value = pagination.value.lastPage + 1;
})

// ---------------------- Hàm xử lý ------------------
const handleReadNotifi = async (id) => {
    await notifyStore.fetchReadNotification(id, page.value - 1, perPage.value);
}

const handleReadAllNotifi = async () => {
    let isRead = false;
    for (let item of notificationList.value) {
        if (item.read === false) {
            isRead = true;
            break;
        }
    }
    if (isRead === true) {
        await notifyStore.fetchReadAllNotification();
        for (let item of notificationList.value) {
            item.read = true;
        }
    }
}

</script>

<style scoped src="./notify.css"></style>