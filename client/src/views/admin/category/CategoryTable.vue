<script setup>
import { useCategoryStore } from '@/stores/category';
import { nextTick, ref } from 'vue';
import CategoryModal from './CategoryModal.vue';
import { dialogConfirm } from '@/helpers/swal';

const categoryStore = useCategoryStore();
const isShowForm = ref(false);
const statusForm = ref('ADD');
const categoryId = ref(null);


nextTick(async () => {
    await categoryStore.fecthGetAll();
})

const handleShowFormAdd = () => {
    statusForm.value = 'ADD';
    isShowForm.value = true;
}

const handleShowFormEdit = (id) => {
    categoryId.value = id;
    statusForm.value = 'EDIT';
    isShowForm.value = true;
}


const handleDelete = async (id) => {
    dialogConfirm('Xác nhận xóa', 'Bạn có chắc muốn xóa danh mục này', async () => {
        await categoryStore.fetchDelete(id);
    })
}

</script>

<template>
    <div class="admin-table">
        <div class="admin-header">
            <h1>Quản lý danh mục sản phẩm</h1>
            <div class="btn-group">
                <b-button @click="handleShowFormAdd" type="primary">Thêm danh mục</b-button>
            </div>
        </div>
        <table class="table table-bordered border-primary">
            <thead>
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">Tên danh mục</th>
                    <th scope="col">Ngày tạo</th>
                    <th scope="col">Người tạo</th>
                    <th class="action" scope="col">Chức năng</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(category, index) in categoryStore.categories.data" :key="category?.id">
                    <th width="50px" scope="row">{{ index + 1 }}</th>
                    <td>{{ category?.name }}</td>
                    <td>{{ $formatValue.formatDateTime(category?.createdAt) }}</td>
                    <td>{{ category?.createdBy }}</td>
                    <td class="action">
                        <button @click="handleShowFormEdit(category?.id)" class="btn-edit">
                            <i class="fa-solid fa-pencil"></i>
                        </button>
                        <button @click="handleDelete(category?.id)" class="btn-delete">
                            <i class="fa-solid fa-trash-can"></i>
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <CategoryModal v-if="isShowForm" :statusForm="statusForm" :categoryId="categoryId" @closeModal="isShowForm = false">
    </CategoryModal>
</template>

<style lang="css" src="../admin-table.css" scoped></style>
