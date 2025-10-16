import couponService from '@/apis/services/couponService';
import { dialog } from '@/helpers/swal';
import { toastify } from '@/helpers/toastify';
import { defineStore } from 'pinia'

export const useCouponStore = defineStore('coupon', {
    state: () => ({
        coupon: {},
        couponsList: [],
        pagination: {},
        isLoading: false,
        isSuccess: false,
    }),
    getters: {},
    actions: {
        async fetchUseCoupon(id) {
            try {
                const res = await couponService.useCoupon(id);
                if (res.status === 200) {
                    this.coupon = res.data;
                    return res;
                }
            } catch (error) {
                dialog('Lấy mã giảm giá thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            }
        },

        async fetchGetAll(page, perPage) {
            try {
                this.isLoading = true;
                this.isSuccess = false;
                const res = await couponService.getAll(page, perPage);
                if (res.status === 200) {
                    this.couponsList = res.data.data;
                    this.pagination = res.data.pagination;
                    this.isSuccess = true;
                }
            } catch (error) {
                toastify('Lấy dữ liệu mã giảm giá thất bại', 'error');
                console.error(error);
            } finally {
                this.isLoading = false;
            }
        },

        async fetchGetById(id) {
            try {
                this.isLoading = true;
                this.isSuccess = false;
                const res = await couponService.getById(id);
                if (res.status === 200) {
                    this.coupon = res.data;
                    this.isSuccess = true;
                }
            } catch (error) {
                toastify('Lấy dữ liệu thất bại', 'error');
                console.error(error);
            } finally {
                this.isLoading = false;
            }
        },

        async fetchInsert(data, page, perPage) {
            try {
                this.isLoading = true;
                this.isSuccess = false;
                const res = await couponService.insert(data);
                if (res.status === 201) {
                    await this.fetchGetAll(page, perPage);
                    this.isSuccess = true;
                }
            } catch (error) {
                dialog('Thêm mã khuyến mãi thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            } finally {
                this.isLoading = false;
            }
        },

        async fetchUpdate(id, data, page, perPage) {
            try {
                this.isLoading = true;
                this.isSuccess = false;
                const res = await couponService.update(id, data);
                if (res.status === 200) {
                    await this.fetchGetAll(page, perPage);
                    this.isSuccess = true;
                }
            } catch (error) {
                dialog('Cập nhật mã khuyến mãi thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            } finally {
                this.isLoading = false;
            }
        },

        async fetchDelete(id, page, perPage) {
            try {
                this.isLoading = true;
                this.isSuccess = false;
                const res = await couponService.delete(id);
                if (res.status === 200) {
                    await this.fetchGetAll(page, perPage);
                    this.isSuccess = true;
                }
            } catch (error) {
                dialog('Xóa mã khuyến mãi thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            } finally {
                this.isLoading = false;
            }
        },
    },
})
