import orderService from '@/apis/services/orderService'
import { dialog } from '@/helpers/swal';
import { toastify } from '@/helpers/toastify';
import router from '@/routers/router';
import { defineStore } from 'pinia'
import { useUserStore } from './user';

export const useOrderStore = defineStore('order', {
    state: () => ({
        totalMoney: 0,
        orderItem: [],
        coupon: null,
        orderList: [],
        pagination: {},
        order: {},
        ordersByUser: [],
        loadingOrder: false,
        successOrder: false,
        codeStatusPayment: null,
        statistical: []
    }),
    getters: {},
    actions: {

        async fetchGetAllOrder(page, perPage) {
            try {
                this.loadingOrder = true;
                this.successOrder = false;
                const res = await orderService.getAll(page, perPage);
                if (res.status === 200) {
                    this.orderList = res.data.data;
                    this.pagination = res.data.pagination;
                    this.successOrder = true;
                }
            } catch (error) {
                dialog('Lấy đơn hàng thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            } finally {
                this.loadingOrder = false;
            }
        },

        async fetchGetById(id) {
            try {
                this.loadingOrder = true;
                this.successOrder = false;
                const res = await orderService.getById(id);
                if (res.status === 200) {
                    this.order = res.data;
                    this.successOrder = true;
                }
            } catch (error) {
                dialog('Lấy đơn hàng thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            } finally {
                this.loadingOrder = false;
            }
        },

        async fetchUpdateOrder(id, data, page, perPage) {
            try {
                this.loadingOrder = true;
                const res = await orderService.update(id, data);
                if (res.status === 200) {
                    toastify('Cập nhật đơn hàng thành công', 'success');
                    await this.fetchGetAllOrder(page, perPage);
                }
            } catch (error) {
                dialog('Cập nhật đơn hàng thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            } finally {
                this.loadingOrder = false;
            }
        },

        async fetchCancelOrder(id) {
            try {
                this.loadingOrder = true;
                const userStore = useUserStore();
                const res = await orderService.update(id, {
                    status: 'CANCELLED'
                });
                if (res.status === 200) {
                    toastify('Hủy đơn hàng thành công!', 'success');
                    await this.fetchGetAllByUser(userStore.userId);
                }
            } catch (error) {
                dialog('Cập nhật đơn hàng thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            } finally {
                this.loadingOrder = false;
            }
        },

        async fetchInsertOrder(data) {
            try {
                this.loadingOrder = true;
                const res = await orderService.insert(data);
                if (res.status === 201) {
                    if (data.paymentMethod === 'VNPAY') {
                        window.location.href = res.data.url;
                        return;
                    }
                    router.push({ name: 'HomePage' })
                    dialog('Đặt hàng thành công', 'success', null);
                }
            } catch (error) {
                dialog('Đặt hàng thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            } finally {
                this.loadingOrder = false;
            }
        },

        async fetchCalculateTotalMoney(data, discount) {
            try {
                const total = await orderService.calculateTotalPrice(data, discount);
                this.totalMoney = total.data;
            } catch (error) {
                toastify('Lỗi tính tiền', 'error');
                console.error(error);
            }
        },

        async fetchGetAllByUser(id, status, keyword) {
            try {
                this.loadingOrder = true;
                const res = await orderService.getAllByUser(id, status, keyword);
                if (res.status === 200) {
                    this.ordersByUser = res.data;
                }
            } catch (error) {
                toastify('Lỗi không lấy được đơn hàng', 'error');
                console.error(error);
            } finally {
                this.loadingOrder = false;
            }
        },

        fetchGetCoupon(orderItem, coupon) {
            this.orderItem = orderItem;
            this.coupon = coupon;
        },

        async fetchPaymentReturn(params) {
            try {
                this.loadingOrder = true;
                const res = await orderService.paymentReturn(params);
                if (res.status === 200) {
                    this.codeStatusPayment = res.data;
                }
            } catch (error) {
                console.error(error);
            } finally {
                this.loadingOrder = false;
            }
        },

        async fetchFeedbackOrder(id) {
            try {
                this.loadingOrder = true;
                const res = await orderService.feedbackOrder(id);
                if (res.status === 200) {
                    console.log(res);
                }
            } catch (error) {
                console.error(error);
            } finally {
                this.loadingOrder = false;
            }
        },

        async fetchStatisticOrder(year) {
            try {
                this.loadingOrder = true;
                const res = await orderService.statisticalOrder(year);
                if (res.status === 200) {
                    this.statistical = res.data;
                }
            } catch (error) {
                console.error(error);
            } finally {
                this.loadingOrder = false;
            }
        },

        async fetchExportOrder(id) {
            try {
                this.loadingOrder = true;
                const res = await orderService.exportOrder(id);
                if (res.status === 200) {
                    const blob = new Blob([res.data], { type: res.headers['content-type'] });
                    const timestamp = new Date().getTime();
                    const fileName = res.headers['content-disposition']
                        ?.split('filename=')[1]
                        ?.replace(/['"]/g, '') || `bill_${timestamp}.pdf`;

                    const link = document.createElement('a');
                    link.href = window.URL.createObjectURL(blob);
                    link.download = fileName;
                    link.click();

                    window.URL.revokeObjectURL(link.href);
                    toastify('Tạo hóa đơn thành công!', 'success');
                    console.log(res.headers);
                }
            } catch (error) {
                dialog('Lỗi', 'error', error?.response?.data?.userMessage);
                console.error(error);
            } finally {
                this.loadingOrder = false;
            }
        },

        async fetchFilterOrders(startDate, endDate, status, page, perPage) {
            try {
                this.loadingOrder = true;
                const res = await orderService.getAllByFilter(startDate, endDate, status, page, perPage);
                if (res.status === 200) {
                    this.orderList = res.data.data;
                    this.pagination = res.data.pagination;
                }
            } catch (error) {
                toastify('Lỗi khi lấy danh sách đơn hàng', 'error');
                console.error(error);
            } finally {
                this.loadingOrder = false;
            }
        }
    },
})
