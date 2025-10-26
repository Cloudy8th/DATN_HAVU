<script setup>
import { useGalleryStore } from "@/stores/gallery";
import { storeToRefs } from "pinia";
import { nextTick, ref } from "vue";

const props = defineProps({
    statusForm: String,
    galleryId: String,
});

const emits = defineEmits(["closeModal"]);

const galleryStore = useGalleryStore();

const { gallery, isLoading, isSuccess } = storeToRefs(galleryStore);

const productId = ref(null);
const image = ref(null);
const imagePreview = ref(null);

nextTick(async () => {
    if (props.statusForm === "EDIT") {
        await galleryStore.fetchGetById(props.galleryId);
        imagePreview.value = gallery?.value?.image;
        productId.value = gallery?.value?.product?.id;
    }
});

const handleCloseModal = () => {
    const dialog = document.querySelector(".swal2-popup");
    if (dialog) {
        return;
    }
    emits("closeModal");
};

const changeImg = (event) => {
    image.value = event.target.files[0];
    imagePreview.value = URL.createObjectURL(event.target.files[0]);
};

const handleSubmit = async () => {
    let formData = new FormData();
    formData.append("image", image.value);
    if (props.statusForm === "ADD") {
        await galleryStore.fetchInsert(productId.value, formData);
    } else {
        await galleryStore.fetchUpdate(
            props.galleryId,
            productId.value,
            formData
        );
    }
    if (isSuccess.value === true) {
        emits("closeModal");
    }
};
</script>

<template>
    <div class="admin-modal__container">
        <div v-if="isLoading" class="loading">
            <spinner-loader></spinner-loader>
        </div>
        <div class="admin-modal__section" v-click-outside="handleCloseModal">
            <div class="modal-header">
                <h5>Thêm mới sản phẩm</h5>
                <button @click="handleCloseModal()">
                    <i class="fa-solid fa-xmark"></i>
                </button>
            </div>
            <div class="modal-body">
                <form
                    @submit.prevent="handleSubmit"
                    class="form-gallery row g-3 form-group"
                >
                    <div class="form-item col-md-12">
                        <label for="productId" class="form-label"
                            >Mã sản phẩm</label
                        >
                        <input
                            v-model="productId"
                            type="text"
                            class="form-control"
                            id="productId"
                            required
                        />
                    </div>
                    <div class="form-item col-md-12">
                        <label for="images" class="form-label"
                            >Ảnh sản phẩm</label
                        >
                        <input
                            @change="changeImg($event)"
                            multiple="multiple"
                            type="file"
                            class="form-control"
                            id="images"
                            required
                        />
                        <img class="mt-3" :src="imagePreview" alt="" />
                    </div>
                    <div class="btn-submit col-12">
                        <button class="btn btn-primary" type="submit">
                            Lưu
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<style lang="css" src="../admin-modal.css" scoped>
.form-gallery table tbody tr {
    cursor: pointer;
}
</style>
