import cartService from '@/apis/services/cartService'
import { dialog } from '@/helpers/swal';
import { toastify } from '@/helpers/toastify';
import { defineStore } from 'pinia'

export const useCartStore = defineStore('cart', {
    state: () => ({
        carts: {},
        cart: {},
        cartByUser: {},
        cartItems: [],
        loadingCart: false,
    }),
    getters: {},
    actions: {
        async fetchGetAll() {
            try {
                const res = await cartService.getAll();
                this.carts = res.data;
            } catch (error) {
                toastify('Lỗi không lấy được đơn hàng', 'error');
                console.error(error);
            }
        },

        async fetchGetAllByUser() {
            try {
                this.loadingCart = true;
                const userId = JSON.parse(localStorage.getItem('user'))?.id
                const res = await cartService.getAllByUser(userId);
                this.cartByUser = res.data;
            } catch (error) {
                toastify('Lỗi không lấy được đơn hàng', 'error');
                console.error(error);
            } finally {
                this.loadingCart = false;
            }
        },

        async fetchGetById(id) {
            try {
                const res = await cartService.getById(id);
                this.cart = res.data;
            } catch (error) {
                toastify('Lỗi không lấy được đơn hàng', 'error');
                console.error(error);
            }
        },

        async fetchDelete(id) {
            try {
                const res = await cartService.delete(id);
                if (res.status === 200) {
                    await this.fetchGetAllByUser();
                }
            } catch (error) {
                dialog('Xóa đơn hàng thất bại', 'error', error.response.data.userMessage);
                console.error(error);
            }
        },

        async fetchInsert(data) {
            try {
                const res = await cartService.insert(data);
                if (res.status === 201) {
                    toastify('Thêm vào giỏ hàng thành công!', 'success');
                    this.fetchGetAllByUser();
                }
                this.carts = res.data;
            } catch (error) {
                dialog('Thêm vào giỏ hàng thất bại', 'error', error.response.data.userMessage);
                console.error(error);
            }
        },

        async fetchUpdate(id, data) {
            try {
                const res = await cartService.update(id, data);
                this.cart = res.data;
            } catch (error) {
                dialog('Cập nhật giỏ hàng thất bại', 'error', error.response.data.userMessage);
                console.error(error);
            }
        },

        fetchGetCartItems(cartItems) {
            this.cartItems = cartItems;
        },

        async fetchInsertMultiple(data) {
            try {
                const res = await cartService.insertMultiple(data);
                if (res.status === 201) {
                    toastify('Thêm vào giỏ hàng thành công!', 'success');
                    await this.fetchGetAllByUser();
                }
                this.carts = res.data;
            } catch (error) {
                dialog('Thêm vào giỏ hàng thất bại', 'error', error.response.data.userMessage);
                console.error(error);
            }
        },

        async fetchDeleteMultiple(ids) {
            try {
                this.loadingCart = true;
                const res = await cartService.deleteMultiple(ids);
                if (res.status === 200) {
                    await this.fetchGetAllByUser();
                }
            } catch (error) {
                console.error(error);
            } finally {
                this.loadingCart = false;
            }
        }
    },
})
