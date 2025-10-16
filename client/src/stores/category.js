import categoryService from '@/apis/services/categoryService';
import { dialog } from '@/helpers/swal';
import { toastify } from '@/helpers/toastify';
import { defineStore } from 'pinia';

export const useCategoryStore = defineStore('category', {
    state: () => ({
        categories: [],
        category: {}
    }),
    getters: {},
    actions: {
        async fecthGetAll() {
            try {
                const res = await categoryService.gettAll();
                this.categories = res;
            } catch (error) {
                toastify('Lấy dữ liệu thất bại', 'error');
                console.error(error);
            }
        },

        async fecthGetById(id) {
            const res = await categoryService.getById(id);
            this.category = res;
        },

        async fetchInsert(data) {
            try {
                const res = await categoryService.insert(data);
                if (res.status === 201) {
                    await this.fecthGetAll();
                    toastify('Thêm thành công', 'success');
                }
            } catch (error) {
                dialog('Thêm thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            }
        },

        async fetchUpdate(id, data) {
            try {
                const res = await categoryService.update(id, data);
                if (res.status === 200) {
                    await this.fecthGetAll();
                    toastify('Cập nhật thành công', 'success');
                }
            } catch (error) {
                dialog('Cập nhật thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            }
        },

        async fetchDelete(id) {
            try {
                const res = await categoryService.delete(id);
                if (res.status === 200) {
                    await this.fecthGetAll();
                    toastify('Xóa thành công', 'success');
                }
            } catch (error) {
                dialog('Xóa thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            }
        }
    },
})
