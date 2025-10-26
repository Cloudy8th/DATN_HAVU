<script setup>
import { dialog } from "@/helpers/swal";
import { toastify } from "@/helpers/toastify";
import { useCategoryStore } from "@/stores/category";
import { useProductStore } from "@/stores/product";
import _ from "lodash";
import { nextTick, reactive, ref, toRaw } from "vue";

const props = defineProps({
    statusForm: String,
    productId: String,
});

const emits = defineEmits(["closeModal"]);

const categoryStore = useCategoryStore();
const categoriesData = ref([]);
const productStore = useProductStore();
const productImages = reactive([]);
const idImagesRemove = ref([]);

const isChangeImg = ref(false);
const isRemoveImg = ref(false);

const productData = reactive({
    title: null,
    categoryId: null,
    originPrice: 0,
    quantity: 1,
    salePrice: 0,
    description: null,
    images: [],
});

nextTick(async () => {
    await categoryStore.fecthGetAll();
    categoriesData.value = categoryStore.categories.data;
    if (props.statusForm === "EDIT") {
        await productStore.fetchGetById(props.productId);
        productData.title = productStore.product.title;
        productData.categoryId = productStore.product.category.id;
        productData.originPrice = productStore.product.originPrice;
        productData.quantity = productStore.product.quantity;
        productData.salePrice = productStore.product.salePrice;
        productData.description = productStore.product.description;
        productData.images = productStore.product.imageProducts;
        if (productStore.product.imageProducts) {
            productImages.push(
                ...productStore.product.imageProducts.map((image) => ({
                    id: image.id,
                    url: image.url,
                    isFromDb: true,
                }))
            );
        }
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
    let files = event.target.files;
    let urlImages = productImages.map((image) => image.url);
    if (urlImages.length + files.length > 5) {
        toastify("Chỉ chọn tối đa 5 ảnh", "warning");
        return;
    } else {
        for (let i = 0; i < files.length; i++) {
            const url = URL.createObjectURL(files[i]);
            let id = _.uniqueId();
            productImages.push({
                id: id,
                url: url,
                isFromDb: false,
            });
            productData.images.push({
                id: id,
                file: files[i],
            });
        }
        isChangeImg.value = true;
    }
};

const handleSubmit = async () => {
    if (productData.originPrice < productData.salePrice) {
        dialog("Lỗi", "error", "Giá giảm giá phải nhỏ hơn giá bán");
        return;
    }
    let formData = new FormData();
    if (productData.images) {
        for (let i = 0; i < productData.images.length; i++) {
            formData.append("images", productData.images[i].file);
        }
    }
    let res = null;
    if (props.statusForm === "ADD") {
        res = await productStore.fetchInsert(toRaw(productData));
    } else if (props.statusForm === "EDIT") {
        res = await productStore.fetchUpdate(
            props.productId,
            toRaw(productData)
        );
    }
    if (res !== null && isChangeImg.value) {
        await productStore.fetchPostImage(res?.id, formData);
    }
    if (isRemoveImg.value) {
        await productStore.fetchDeleteImages(idImagesRemove.value);
    }
    emits("closeModal");
};

const removeImage = (id) => {
    const index = productImages.findIndex((image) => image.id === id);
    if (index !== -1) {
        const removedImage = productImages.splice(index, 1)[0];

        if (removedImage?.isFromDb) {
            idImagesRemove.value.push(id);
            isRemoveImg.value = true;
        } else {
            const filesArray = Array.from(productData.images);
            filesArray.splice(index, 1);
            productData.images = filesArray;
        }
    }
};
</script>

<template>
    <div class="admin-modal__container">
        <div v-if="productStore.loading" class="loading">
            <spinner-loader></spinner-loader>
        </div>
        <div class="admin-modal__section" v-click-outside="handleCloseModal">
            <div class="modal-header">
                <h5>
                    {{
                        statusForm === "ADD"
                            ? "Thêm mới sản phẩm"
                            : "Sửa sản phẩm"
                    }}
                </h5>
                <button @click="handleCloseModal()" title="Đóng">
                    <i class="fa-solid fa-xmark"></i>
                </button>
            </div>
            <div class="modal-body">
                <form @submit.prevent="handleSubmit" class="row g-3 form-group">
                    <div class="form-item col-md-6">
                        <label for="title" class="form-label"
                            >Tên sản phẩm</label
                        >
                        <input
                            v-model="productData.title"
                            type="text"
                            class="form-control"
                            id="title"
                            required
                        />
                    </div>
                    <div class="form-item col-md-6">
                        <label for="category" class="form-label"
                            >Danh mục sản phẩm</label
                        >
                        <select
                            v-model="productData.categoryId"
                            class="form-select"
                            id="category"
                            required
                        >
                            <option
                                v-for="category in categoriesData"
                                :value="category.id"
                                :key="category.id"
                            >
                                {{ category.name }}
                            </option>
                        </select>
                    </div>
                    <div class="form-item col-md-4">
                        <label for="originPrice" class="form-label"
                            >Giá sản phẩm</label
                        >
                        <input
                            v-model="productData.originPrice"
                            type="number"
                            class="form-control"
                            id="originPrice"
                            required
                        />
                    </div>
                    <div class="form-item col-md-4">
                        <label for="sale" class="form-label"
                            >Giá giảm giá</label
                        >
                        <input
                            v-model="productData.salePrice"
                            type="number"
                            class="form-control"
                            id="sale"
                            required
                        />
                    </div>
                    <div class="form-item col-md-4">
                        <label for="quantity" class="form-label"
                            >Số lượng</label
                        >
                        <input
                            v-model="productData.quantity"
                            type="number"
                            class="form-control"
                            id="quantity"
                            required
                        />
                    </div>

                    <div class="form-item col-md-12">
                        <label for="description" class="form-label"
                            >Mô tả</label
                        >
                        <textarea
                            v-model="productData.description"
                            type="text"
                            class="form-control"
                            id="description"
                            required
                        ></textarea>
                    </div>
                    <div class="form-item product-choose-images col-md-12">
                        <label for="images" class="form-label"
                            >Ảnh sản phẩm</label
                        >
                        <input
                            @change="changeImg($event)"
                            multiple="multiple"
                            type="file"
                            class="form-control"
                            id="images"
                            style="display: none"
                        />
                        <label for="images" class="form-label btn-choose-img">
                            <i class="fa-regular fa-folder-open"></i>
                            <span>Chọn ảnh</span>
                        </label>
                    </div>
                    <div class="form-item product-images col-md-12">
                        <div
                            class="product-images-item"
                            v-for="image in productImages"
                            :key="image.id"
                        >
                            <img :src="image.url" alt="" />
                            <div
                                class="remove-image"
                                @click="removeImage(image.id)"
                            >
                                <i class="fa-solid fa-xmark"></i>
                            </div>
                        </div>
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

<style lang="css" src="../admin-modal.css" scoped></style>
