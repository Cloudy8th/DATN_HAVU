import feedbackService from '@/apis/services/feedbackService';
import { dialog } from '@/helpers/swal';
import { toastify } from '@/helpers/toastify';
import router from '@/routers/router';
import { defineStore } from 'pinia'

export const useFeedbackStore = defineStore('feedback', {
    state: () => ({
        feedbackList: [],
        pagination: {},
        feedback: {},
        loadingFeedback: false,
        feedbackListData: []
    }),
    getters: {},
    actions: {
        async fetchGetAll(page, perPage) {
            try {
                this.loadingFeedback = true;
                const res = await feedbackService.getAll(page, perPage);
                if (res.status === 200) {
                    this.feedbackList = res.data.data;
                    this.pagination = res.data.pagination;
                }
            } catch (error) {
                console.error(error);
            } finally {
                this.loadingFeedback = false;
            }
        },

        async fetchGetById(id) {
            try {
                this.loadingFeedback = true;
                const res = await feedbackService.getById(id);
                if (res.status === 200) {
                    this.feedback = res.data;
                }
            } catch (error) {
                console.error(error);
            } finally {
                this.loadingFeedback = false;
            }
        },

        async fetchInsert(data) {
            try {
                this.loadingFeedback = true;
                const res = await feedbackService.insert(data);
                if (res.status === 201) {
                    dialog('Đánh giá thành công', 'success', null);
                    router.push({ name: 'OrdersPage' });
                }
            } catch (error) {
                console.error(error);
            } finally {
                this.loadingFeedback = false;
            }
        },

        async fetchUpdate(id, data) {
            try {
                this.loadingFeedback = true;
                const res = await feedbackService.update(id, data);
                if (res.status === 200) {
                    toastify('Đánh giá thành công', 'success');
                }
            } catch (error) {
                console.error(error);
            } finally {
                this.loadingFeedback = false;
            }
        },

        async fetchInsertMultiple(data) {
            try {
                this.loadingFeedback = true;
                const res = await feedbackService.insertMultiple(data);
                if (res.status === 201) {
                    dialog('Đánh giá thành công', 'success', null);
                    router.push({ name: 'OrdersPage' });
                }
            } catch (error) {
                console.error(error);
            } finally {
                this.loadingFeedback = false;
            }
        },

        fetchPushDataFeedback(data) {
            this.feedbackListData.push(data);
        },

        fetchUpdateFeedback(id, data) {
            const feedbackData = this.feedbackListData.find(item => item.productId === id);
            if (feedbackData) {
                Object.assign(feedbackData, data);
            }
        },

        fetchResetDataFeedback() {
            this.feedbackListData = [];
        },

        async fetchGetAllByProduct(id, page, perPage, star) {
            try {
                this.loadingFeedback = true;
                const res = await feedbackService.getAllByProduct(id, page, perPage, star);
                if (res.status === 200) {
                    this.feedbackList = res.data.data;
                    this.pagination = res.data.pagination;
                }
            } catch (error) {
                console.error(error);
            } finally {
                this.loadingFeedback = false;
            }
        }
    },

})
