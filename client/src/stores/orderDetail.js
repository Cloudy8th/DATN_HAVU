import orderDetailService from '@/apis/services/orderDetailService';
import { defineStore } from 'pinia'

export const useOrderDetailStore = defineStore('orderDetail', {
    state: () => ({
        orderDetailList: [],
        orderDetail: {},
        loadingOrderDetail: false
    }),
    getters: {},
    actions: {
        async fetchGetAllByOrderId(id) {
            this.loadingOrderDetail = true;
            try {
                const res = await orderDetailService.getAllByOrderId(id);
                if (res.status === 200) {
                    this.orderDetailList = res.data;
                    this.loadingOrderDetail = false;
                }
            } catch (error) {
                console.error(error);
            } finally {
                this.loadingOrderDetail = false;
            }
        },

        async fetchGetById(id) {
            this.loadingOrderDetail = true;
            try {
                const res = await orderDetailService.getById(id);
                if (res.status === 200) {
                    this.orderDetail = res.data;
                    this.loadingOrderDetail = false;
                }
            } catch (error) {
                console.error(error);
            } finally {
                this.loadingOrderDetail = false;
            }
        }
    },
})
