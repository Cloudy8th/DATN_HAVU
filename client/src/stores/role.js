import roleService from '@/apis/services/roleService';
import { toastify } from '@/helpers/toastify';
import { defineStore } from 'pinia'

export const useRoleStore = defineStore('role', {
    state: () => ({
        roles: [],
        role: {},
    }),
    getters: {},
    actions: {
        async fetchGetAll() {
            try {
                const res = await roleService.getAll();
                if (res.status === 200) {
                    this.roles = res.data.data;
                }
            } catch (error) {
                toastify('Lấy dữ liệu quyền truy cập thất bại', 'error');
                console.error(error);
            }
        },
        async fetchGetById(id) {
            try {
                const res = await roleService.getById(id);
                if (res.status === 200) {
                    this.role = res.data;
                }
            } catch (error) {
                toastify('Lấy dữ liệu quyền truy cập thất bại', 'error');
                console.error(error);
            }
        }
    },
})
