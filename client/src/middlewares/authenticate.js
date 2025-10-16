import { dialog } from '@/helpers/swal';
import { verifyJwt } from '@/helpers/verifyJwt';
import { useAuthStore } from '@/stores/auth';

export const authenticate = async () => {
    const authStore = useAuthStore();
    const user = JSON.parse(localStorage.getItem('user'));
    const token = user?.token;
    if (token) {
        const payloadData = await verifyJwt(token);
        if (!payloadData || payloadData?.exp < Math.floor(Date.now() / 1000)) {
            dialog('Phiên đăng nhập hết hạn', 'error', 'Vui lòng đăng nhập lại');
            authStore.fetchLogout();
        }
    }
}

export const requireAuth = (to, from, next) => {
    const authStore = useAuthStore();
    if (!authStore.isLoggedIn) {
        next({ name: 'Login', query: { redirect: to.fullPath } });
    }
    else {
        next();
    }
}

export const requireAdmin = async (to, from, next) => {
    const user = JSON.parse(localStorage.getItem('user'));
    const token = user?.token;
    if (token) {
        const payloadData = await verifyJwt(token);
        if (payloadData?.role === 'ADMIN') {
            next();
        }
        next({ name: 'Forbidden' });
    }
    else {
        next({ name: 'Forbidden' });
    }
}

