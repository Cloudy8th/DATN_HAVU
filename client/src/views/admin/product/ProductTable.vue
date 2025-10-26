<script setup>
import { dialogConfirm } from "@/helpers/swal";
import ProductModal from "./ProductModal.vue";
import { useProductStore } from "@/stores/product";
import { useCategoryStore } from "@/stores/category";
import { nextTick, ref, watch } from "vue";
import { storeToRefs } from "pinia";
import { dialog } from "@/helpers/swal";

const productStore = useProductStore();
const categoryStore = useCategoryStore();
const { loading } = storeToRefs(productStore);
const { categories } = storeToRefs(categoryStore);
const page = ref(1);
const perPage = ref(6);
const totalPage = ref(0);
const productsData = ref([]);
const statusForm = ref("ADD");
const showModal = ref(false);
const productId = ref(null);

const keyword = ref(null);
const price = ref(null);
const title = ref(null);
const discount = ref(null);
const categoryId = ref(null);
const isStock = ref(false);
const productIdSearch = ref(null);

const priceSort = ref(0);
const titleSort = ref(0);
const discountSort = ref(0);

nextTick(async () => {
    await categoryStore.fecthGetAll();
    await productStore.fetchGetAll(page.value - 1, perPage.value);
    productsData.value = productStore.products?.data;
    page.value = productStore.products?.pagination?.page + 1;
    totalPage.value = productStore.products?.pagination?.lastPage + 1;
});

watch(
    () => page.value,
    async () => {
        await productStore.fetchGetAll(page.value - 1, perPage.value);
        productsData.value = productStore.products?.data;
        page.value = productStore.products?.pagination?.page + 1;
        totalPage.value = productStore.products?.pagination?.lastPage + 1;
    }
);

watch(
    () => [discount.value, price.value, title.value],
    () => {
        filterProduct();
    }
);

const handleAddProduct = () => {
    statusForm.value = "ADD";
    showModal.value = true;
};

const handleEditProduct = (id) => {
    statusForm.value = "EDIT";
    showModal.value = true;
    productId.value = id;
};

const handleCloseModal = () => {
    page.value = 1;
    showModal.value = false;
};

const handleDeleteProduct = async (id) => {
    dialogConfirm(
        "Xác nhận xóa",
        "Bạn có chắc muốn xóa sản phẩm này",
        async () => {
            page.value = 1;
            await productStore.fetchDelete(id, 0, perPage.value);
        }
    );
    await productStore.fetchGetAll(page.value - 1, perPage.value);
};

const filterProduct = async () => {
    if (productIdSearch.value) {
        const uuidRegex = /^PRODUCT-\d+$/;
        if (!uuidRegex.test(productIdSearch.value)) {
            dialog(
                "Lỗi",
                "error",
                "Mã sản phẩm không hợp lệ, vui lòng kiểm tra lại"
            );
            return;
        }
    }
    await productStore.fetchFilterProduct(
        keyword.value,
        page.value - 1,
        perPage.value,
        discount.value,
        title.value,
        price.value,
        categoryId.value,
        isStock.value,
        productIdSearch.value
    );
    totalPage.value = productStore.products?.pagination?.lastPage + 1;
};

const sortBy = (column) => {
    if (column === "title") {
        if (titleSort.value === 0) {
            title.value = "desc";
            titleSort.value = 1;
        } else if (titleSort.value === 1) {
            title.value = "asc";
            titleSort.value = 2;
        } else if (titleSort.value === 2) {
            title.value = null;
            titleSort.value = 0;
        }
        price.value = null;
        priceSort.value = 0;
        discount.value = null;
        discountSort.value = 0;
    }
    if (column === "salePrice") {
        if (priceSort.value === 0) {
            price.value = "desc";
            priceSort.value = 1;
        } else if (priceSort.value === 1) {
            price.value = "asc";
            priceSort.value = 2;
        } else if (priceSort.value === 2) {
            price.value = null;
            priceSort.value = 0;
        }
        title.value = null;
        titleSort.value = 0;
        discount.value = null;
        discountSort.value = 0;
    }
    if (column === "discount") {
        if (discountSort.value === 0) {
            discount.value = "desc";
            discountSort.value = 1;
        } else if (discountSort.value === 1) {
            discount.value = "asc";
            discountSort.value = 2;
        } else if (discountSort.value === 2) {
            discount.value = null;
            discountSort.value = 0;
        }
        title.value = null;
        titleSort.value = 0;
        price.value = null;
        priceSort.value = 0;
    }
};
</script>

<template>
    <div class="loading" v-if="loading">
        <spinner-loader></spinner-loader>
    </div>
    <div class="admin-table">
        <div class="admin-header">
            <h1>Quản lý sản phẩm</h1>
            <div class="btn-group btn-group-filter">
                <div class="btn-add">
                    <b-button @click="handleAddProduct" type="primary">
                        Thêm sản phẩm
                    </b-button>
                </div>
                <div class="btn-search">
                    <select
                        v-model="categoryId"
                        class="form-select"
                        id="categoryId"
                    >
                        <option :value="null" selected>Danh mục</option>
                        <option
                            v-for="item in categories.data"
                            :value="item?.id"
                            :key="item?.id"
                        >
                            {{ item?.name }}
                        </option>
                    </select>
                    <b-input
                        v-model="productIdSearch"
                        ref="refInputSearch"
                        placeholder="Tìm theo mã sản phẩm"
                    />
                    <b-input
                        v-model="keyword"
                        ref="refInputSearch"
                        placeholder="Tìm theo tên sản phẩm"
                    />
                    <div class="check-is-stock">
                        <label for="isStock">Tồn kho: </label>
                        <br />
                        <input
                            v-model="isStock"
                            type="checkbox"
                            class="form-check-input"
                            style="width: 18px; height: 18px"
                            id="isStock"
                        />
                    </div>
                    <div class="btn-search-button">
                        <b-button @click="filterProduct" type="secondary">
                            Tìm kiếm
                        </b-button>
                    </div>
                </div>
            </div>
        </div>
        <table class="table table-bordered border-primary">
            <thead>
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">Mã sản phẩm</th>
                    <th scope="col">Ảnh</th>
                    <th scope="col" class="th-sort" @click="sortBy('title')">
                        Tên sản phẩm
                        <i v-if="title === null" class="fa-solid fa-sort"></i>
                        <i
                            v-else
                            :class="[
                                'fa-solid',
                                title === 'desc'
                                    ? 'fa-sort-down'
                                    : 'fa-sort-up',
                            ]"
                        ></i>
                    </th>
                    <th scope="col">Tên danh mục</th>
                    <th scope="col">Giá gốc</th>
                    <th
                        scope="col"
                        class="th-sort"
                        @click="sortBy('salePrice')"
                    >
                        Giá bán
                        <i v-if="price === null" class="fa-solid fa-sort"></i>
                        <i
                            v-else
                            :class="[
                                'fa-solid',
                                price === 'desc'
                                    ? 'fa-sort-down'
                                    : 'fa-sort-up',
                            ]"
                        ></i>
                    </th>
                    <th scope="col" class="th-sort" @click="sortBy('discount')">
                        Giảm giá
                        <i
                            v-if="discount === null"
                            class="fa-solid fa-sort"
                        ></i>
                        <i
                            v-else
                            :class="[
                                'fa-solid',
                                discount === 'desc'
                                    ? 'fa-sort-down'
                                    : 'fa-sort-up',
                            ]"
                        ></i>
                    </th>
                    <th scope="col">Số lượng</th>
                    <th class="action" scope="col">Chức năng</th>
                </tr>
            </thead>
            <tbody>
                <tr
                    v-for="(item, index) in productStore.products.data"
                    :key="item?.id"
                >
                    <th width="50px" scope="row">
                        {{ index + 1 + perPage * (page - 1) }}
                    </th>
                    <td>{{ item?.id }}</td>
                    <td style="text-align: center">
                        <img
                            :src="
                                item?.imageProducts.length > 0
                                    ? item?.imageProducts[0]?.url
                                    : '@/assets/imgs/TMart-logo.png'
                            "
                            alt=""
                            style="width: 55px; height: 55px"
                        />
                    </td>
                    <td class="value-too-long" :title="item?.title">
                        <span>{{ item?.title }}</span>
                    </td>
                    <td>{{ item?.category?.name }}</td>
                    <td>
                        {{ $formatValue.formatMoney(item?.originPrice) }}
                    </td>
                    <td>{{ $formatValue.formatMoney(item?.salePrice) }}</td>
                    <td>{{ item?.discount }}%</td>
                    <td width="80px">{{ item?.quantity }}</td>

                    <td class="action">
                        <button
                            @click="handleEditProduct(item?.id)"
                            class="btn-edit"
                        >
                            <i class="fa-solid fa-pencil"></i>
                        </button>
                        <button
                            @click="handleDeleteProduct(item?.id)"
                            class="btn-delete"
                        >
                            <i class="fa-solid fa-trash-can"></i>
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
        <v-pagination
            v-model="page"
            size="40"
            :length="totalPage"
            rounded="circle"
        ></v-pagination>
    </div>
    <ProductModal
        :productId="productId"
        v-if="showModal"
        @closeModal="handleCloseModal"
        :statusForm="statusForm"
    >
    </ProductModal>
</template>

<style lang="css" src="../admin-table.css" scoped></style>
