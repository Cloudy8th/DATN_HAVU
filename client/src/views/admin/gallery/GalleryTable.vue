<script setup>
import { useGalleryStore } from '@/stores/gallery';
import { storeToRefs } from 'pinia'
import { nextTick, ref } from 'vue';
import GalleryModal from './GalleryModal.vue';
import { dialogConfirm } from '@/helpers/swal';

const galleryStore = useGalleryStore();
const showModal = ref(false);
const statusForm = ref('ADD');
const galleryId = ref(null);

const { isLoading, galleryList } = storeToRefs(galleryStore);
nextTick(async () => {
    await galleryStore.fetchGetAll();
})

const handleAddGallery = () => {
    showModal.value = true;
    statusForm.value = 'ADD';
}

const handleEditGallery = (id) => {
    showModal.value = true;
    statusForm.value = 'EDIT';
    galleryId.value = id;
}

const handleCloseModal = () => {
    showModal.value = false;
}

const handleDeleteGallery = async (id) => {
    dialogConfirm('Xác nhận xóa', 'Bạn có chắc muốn xóa trưng bày này?', async () => {
        await galleryStore.fetchDelete(id);
    })
}
</script>

<template>
    <div class="loading" v-if="isLoading">
        <spinner-loader></spinner-loader>
    </div>
    <div class="admin-table">
        <div class="admin-header">
            <h1>Quản lý trưng bày</h1>
            <div class="btn-group">
                <b-button @click="handleAddGallery" type="primary">Thêm trưng bày</b-button>
            </div>
        </div>
        <table class="table table-bordered border-primary">
            <thead>
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">Tên sản phẩm</th>
                    <th scope="col">Ngày tạo</th>
                    <th class="image-gallery" scope="col">Ảnh</th>
                    <th class="action" scope="col">Chức năng</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(item, index) in galleryList" :key="item?.id">
                    <th width="50px" scope="row">{{ index + 1 }}</th>
                    <td class="value-too-long gallery-title" :title="item?.title">
                        <span>{{ item?.product.title }}</span>
                    </td>
                    <td>{{ $formatValue.formatDateTime(item?.createdAt) }}</td>
                    <td class="image-gallery">
                        <img :src="item?.image" :alt="item?.title" />
                    </td>
                    <td class="action">
                        <button @click="handleEditGallery(item?.id)" class="btn-edit">
                            <i class="fa-solid fa-pencil"></i>
                        </button>
                        <button @click="handleDeleteGallery(item?.id)" class="btn-delete">
                            <i class="fa-solid fa-trash-can"></i>
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <GalleryModal :galleryId="galleryId" v-if="showModal" @closeModal="handleCloseModal" :statusForm="statusForm">
    </GalleryModal>
</template>

<style lang="css" src="../admin-table.css" scoped></style>
