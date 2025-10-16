import userSerive from '@/apis/services/userSerive'
import { defineStore } from 'pinia'
import { toastify } from '@/helpers/toastify';
import { useAuthStore } from './auth';
import { dialog } from '@/helpers/swal';

export const useUserStore = defineStore('user', {
    state: () => ({
        userInfor: {},
        user: {},
        usersData: [],
        pagination: {},
        userId: JSON.parse(localStorage.getItem('user'))?.id ? JSON.parse(localStorage.getItem('user'))?.id : null,
        fullName: JSON.parse(localStorage.getItem('user'))?.fullName,
        isLoading: false,
        isSuccess: false
    }),
    getters: {},
    actions: {
        fetchSetUserId() {
            this.userId = JSON.parse(localStorage.getItem('user'))?.id;
        },
        async fetchGetAll(page, perPage) {
            try {
                this.isLoading = true;
                const res = await userSerive.getAll(page, perPage);
                if (res.status === 200) {
                    this.usersData = res.data.data;
                    this.pagination = res.data.pagination;
                }
            } catch (error) {
                toastify('Lỗi không lấy được danh sách người dùng', 'error');
                console.error(error);
            } finally {
                this.isLoading = false;
            }
        },
        async fetchGetUser(id) {
            try {
                this.isLoading = true;
                const res = await userSerive.getById(id);
                this.user = res;
            } catch (error) {
                dialog('Lỗi', 'error', error?.response?.data?.userMessage);
                console.error(error);
            } finally {
                this.isLoading = false;
            }
        },

        async fetchEditUser(id, data, page, perPage) {
            try {
                this.isLoading = true;
                this.isSuccess = false;
                const res = await userSerive.update(id, data);
                if (res.status === 200) {
                    toastify('Cập nhật thành công', 'success');
                    this.fetchGetAll(page, perPage);
                    this.isSuccess = true;
                }
            } catch (error) {
                dialog('Cập nhật tài khoản thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            } finally {
                this.isLoading = false;
            }
        },

        async fetchGetById() {
            const user = JSON.parse(localStorage.getItem('user'));
            const id = user?.id;
            const authStore = useAuthStore();
            if (localStorage.getItem('user')) {
                try {
                    const res = await userSerive.getById(id);
                    this.userInfor = res.data;
                } catch (error) {
                    if (error?.response?.status === 401) {
                        authStore.fetchLogout();
                        toastify("Phiên đăng nhập đã hết hạn", 'error')
                        return;
                    }
                }
            }
        },
        async fetchPostImage(data) {
            try {
                this.isLoading = true;
                const res = await userSerive.uploadFile(this.userId, data);
                this.userInfor = res.data;
            } catch (error) {
                dialog('Cập nhật ảnh đại diện thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            } finally {
                this.isLoading = false;
            }
        },

        async fetchChangePassword(data) {
            try {
                this.isLoading = true;
                const res = await userSerive.changePassword(this.userId, data);
                if (res.status === 200) {
                    dialog('Thông báo', 'success', 'Thay đổi mật khẩu thành công!');
                }
            } catch (error) {
                dialog('Đổi mật khẩu thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            } finally {
                this.isLoading = false;
            }
        },

        async fetchEditProfile(data) {
            try {
                this.isLoading = true;
                const res = await userSerive.editProfile(this.userId, data);
                if (res.status === 200) {
                    toastify('Cập nhật thành công', 'success');
                }
            } catch (error) {
                dialog('Cập nhật tài khoản thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            } finally {
                this.isLoading = false;
            }
        },

        async fetchDelete(id, page, perPage) {
            try {
                this.isLoading = true;
                const res = await userSerive.delete(id);
                if (res.status === 200) {
                    toastify('Xóa tài khoản thành công', 'success');
                    this.fetchGetAll(page, perPage);
                }
            } catch (error) {
                dialog('Xóa tài khoản thất bại', 'error', error?.response?.data?.userMessage);
                console.error(error);
            } finally {
                this.isLoading = false;
            }
        },

        async fetchInsert(data, page, perPage) {
            try {
                this.isLoading = true;
                this.isSuccess = false;
                const res = await userSerive.insert(data);
                if (res.status === 201) {
                    this.fetchGetAll(page, perPage);
                    this.isSuccess = true;
                    dialog('Thêm tài khoản thành công', 'success', null);
                }
            } catch (error) {
                dialog('Thêm tài khoản thất bại', 'error');
                console.error(error);
            } finally {
                this.isLoading = false;
            }
        },

        async fetchFilterUser(fullName, userName, dateOfBirth, roleId, page, perPage) {
            try {
                this.isLoading = true;
                const res = await userSerive.getAllByFilter(fullName, userName, dateOfBirth, roleId, page, perPage);
                if (res.status === 200) {
                    this.usersData = res.data.data;
                    this.pagination = res.data.pagination;
                }
            } catch (error) {
                toastify('Lỗi khi lấy danh sách người dùng', 'error');
                console.error(error);
            } finally {
                this.isLoading = false;
            }
        },

        fetchLogout() {
            this.userInfor = {};
        }
    },
})
