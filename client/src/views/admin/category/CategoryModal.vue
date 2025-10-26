<script setup>
import { useCategoryStore } from "@/stores/category";
import { nextTick, reactive } from "vue";

const props = defineProps({
    statusForm: String,
    categoryId: String,
});

const emits = defineEmits(["closeModal"]);

const categoryStore = useCategoryStore();

const categoryData = reactive({
    name: null,
});

nextTick(async () => {
    console.log(props.statusForm);
    if (props.statusForm === "EDIT") {
        await categoryStore.fecthGetById(props.categoryId);
        categoryData.name = categoryStore.category.name;
    }
});

const handleCloseModal = () => {
    emits("closeModal");
};

const handleSubmit = async () => {
    if (props.statusForm === "ADD") {
        await categoryStore.fetchInsert(categoryData);
    } else if (props.statusForm === "EDIT") {
        await categoryStore.fetchUpdate(props.categoryId, categoryData);
    }
    emits("closeModal");
};
</script>

<template>
    <div class="admin-modal__container">
        <div class="admin-modal__section" v-click-outside="handleCloseModal">
            <div class="modal-header">
                <h5>
                    {{
                        statusForm === "ADD"
                            ? "Thêm mới danh mục"
                            : "Cập nhật danh mục"
                    }}
                </h5>
                <button @click="handleCloseModal()">
                    <i class="fa-solid fa-xmark"></i>
                </button>
            </div>
            <div class="modal-body">
                <form @submit.prevent="handleSubmit" class="row g-3 form-group">
                    <div class="form-item col-md-12">
                        <label for="name" class="form-label"
                            >Tên danh mục</label
                        >
                        <input
                            v-model="categoryData.name"
                            type="text"
                            class="form-control"
                            id="name"
                            required
                        />
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
