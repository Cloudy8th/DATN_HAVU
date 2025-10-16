import notificationSerive from '@/apis/services/notificationSerive';
import { toastify } from '@/helpers/toastify';
import { defineStore } from 'pinia'
import { useUserStore } from './user';

export const useNotificationStore = defineStore('notification', {
    state: () => ({
        notificationList: [],
        notificationListNoRead: [],
        notification: {},
        pagination: {},
        loadingNotifi: false,
        successNotifi: false
    }),
    getters: {},
    actions: {
        async fetchGetAllByUser(page, perPage) {
            try {
                const userStore = useUserStore();
                this.loadingNotifi = true;
                this.successNotifi = false;
                const res = await notificationSerive.getAllByUser(userStore.userId, page, perPage);
                if (res.status === 200) {
                    this.notificationList = res.data.data;
                    this.pagination = res.data.pagination;
                    this.successNotifi = true;
                }
            } catch (error) {
                toastify('Lấy dữ liệu thông báo thất bại', 'error');
                console.error(error);
            } finally {
                this.loadingNotifi = false;
            }
        },

        async fetchGetById(id) {
            try {
                this.loadingNotifi = true;
                this.successNotifi = false;
                const res = await notificationSerive.getById(id);
                if (res.status === 200) {
                    this.notification = res.data;
                    this.successNotifi = true;
                }
            } catch (error) {
                toastify('Lấy dữ liệu thông báo thất bại', 'error');
                console.error(error);
            } finally {
                this.loadingNotifi = false;
            }
        },

        async fetchDelete(id, page, perPage) {
            try {
                this.loadingNotifi = true;
                this.successNotifi = false;
                const res = await notificationSerive.delete(id);
                if (res.status === 200) {
                    await this.fetchGetAllByUser(page, perPage);
                    this.successNotifi = true;
                }
            } catch (error) {
                toastify('Xóa thông báo thất bại', 'error');
                console.error(error);
            } finally {
                this.loadingNotifi = false;
            }
        },

        async fetchReadNotification(id, page, perPage) {
            try {
                this.loadingNotifi = true;
                this.successNotifi = false;
                const res = await notificationSerive.readNotification(id);
                if (res.status === 200) {
                    this.successNotifi = true;
                    this.fetchGetAllByUserAndRead();
                    this.fetchGetAllByUser(page, perPage);
                }
            } catch (error) {
                toastify('Xem thông báo thất bại', 'error');
                console.error(error);
            } finally {
                this.loadingNotifi = false;
            }
        },

        async fetchGetAllByUserAndRead() {
            try {
                const userStore = useUserStore();
                const res = await notificationSerive.getAllByUserAndRead(userStore.userId);
                if (res.status === 200) {
                    this.notificationListNoRead = res.data;
                }
            } catch (error) {
                console.error(error);
            }
        },

        async fetchReadAllNotification() {
            try {
                const userStore = useUserStore();
                const res = await notificationSerive.readAllNotification(userStore.userId);
                if (res.status === 200) {
                    this.fetchGetAllByUserAndRead();
                }
            } catch (error) {
                toastify('Đọc thông báo thất bại', 'error');
                console.error(error);
            }
        }
    },
})
