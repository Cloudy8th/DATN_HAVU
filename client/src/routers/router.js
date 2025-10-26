import { authenticate, requireAdmin, requireAuth } from "@/middlewares/authenticate";
import { createRouter, createWebHistory } from "vue-router";

const routes = [
    {
        path: '/',
        name: 'FrontSide',
        component: () => import('@/views/layouts/TheFrontSide.vue'),
        children: [
            {
                path: '/',
                name: 'Container',
                component: () => import('@/views/layouts/TheContainer.vue'),
                children: [
                    {
                        path: '/',
                        name: 'HomePage',
                        component: () => import('@/views/pages/home/HomePage.vue'),
                        meta: {
                            title: 'TMart - Mua sắm online giá tốt',
                        }
                    },
                    {
                        path: '/product/:id',
                        name: 'DetailProduct',
                        component: () => import('@/views/pages/product/DetailProduct.vue'),
                        meta: {
                            title: 'TMart - Mua sắm online giá tốt',
                        }
                    },
                    {
                        path: '/catalogry/:id',
                        name: 'ProductCatalogry',
                        component: () => import('@/views/pages/catalogry/ProductCatalogry.vue'),
                        meta: {
                            title: 'TMart - Mua sắm online giá tốt',
                        }
                    },
                    {
                        path: '/payments',
                        name: 'Payments',
                        component: () => import('@/views/pages/payments/PaymentsPage.vue'),
                        meta: {
                            title: 'Thanh toán',
                            authenticate: true
                        }
                    },
                    {
                        path: '/cart',
                        name: 'CartPage',
                        component: () => import('@/views/pages/cart/CartPage.vue'),
                        meta: {
                            title: 'Giỏ hàng',
                            authenticate: true,
                        }
                    },
                    {
                        path: '/account/edit',
                        name: 'AccountPage',
                        component: () => import('@/views/pages/account/AccountPage.vue'),
                        meta: {
                            title: 'Tài khoản của tôi',
                            authenticate: true
                        }
                    },
                    {
                        path: '/orders',
                        name: 'OrdersPage',
                        component: () => import('@/views/pages/account/OrdersPage.vue'),
                        meta: {
                            title: 'Đơn hàng của tôi',
                            authenticate: true
                        }
                    },
                    {
                        path: '/notifications',
                        name: 'NotifyPage',
                        component: () => import('@/views/pages/account/NotifyPage.vue'),
                        meta: {
                            title: 'Thông báo của tôi',
                            authenticate: true
                        }
                    },
                    {
                        path: '/order/detail/:id',
                        name: 'OrderDetail',
                        component: () => import('@/views/pages/account/OrderDetail.vue'),
                        meta: {
                            title: 'Chi tiết đơn hàng',
                            authenticate: true
                        }
                    },
                    {
                        path: '/product/best-seller',
                        name: 'ProductBestSeller',
                        component: () => import('@/views/pages/product/ProductBestSeller.vue'),
                        meta: {
                            title: 'Sản phẩm bán chạy',
                            authenticate: false
                        }
                    },
                    {
                        path: '/product/sale',
                        name: 'ProductSale',
                        component: () => import('@/views/pages/product/ProductSale.vue'),
                        meta: {
                            title: 'Sản phẩm giảm giá',
                            authenticate: false
                        }
                    },
                    {
                        path: '/product/search/:keyword',
                        name: 'ProductSearch',
                        component: () => import('@/views/pages/product/ProductSearch.vue'),
                        meta: {
                            title: 'TMart - Mua sắm online giá tốt',
                            authenticate: false
                        }
                    },
                    {
                        path: '/user/change-password',
                        name: 'ChangePassword',
                        component: () => import('@/views/pages/account/ChangePassword.vue'),
                        meta: {
                            title: 'Thay đổi mật khẩu',
                            authenticate: true
                        }
                    },
                    {
                        path: '/feedback/:id',
                        name: 'Feedback',
                        component: () => import('@/views/pages/product/FeedbackProduct.vue'),
                        meta: {
                            title: 'Đánh giá sản phẩm',
                            authenticate: true
                        }
                    },
                ]
            },
        ]
    },
    {
        path: '/admin',
        name: 'AdminSide',
        component: () => import('@/views/layouts/TheAdminSide.vue'),
        meta: {
            title: 'Hệ thống quản trị',
            authenticate: true,
            requireAdmin: true
        },
        children: [
            {
                path: '',
                name: 'HomeAdmin',
                component: () => import('@/views/admin/home/HomeAdmin.vue'),
                meta: {
                    title: 'Hệ thống quản trị',
                }
            },
            {
                path: 'category',
                name: 'CategoryAdmin',
                component: () => import('@/views/admin/category/CategoryTable.vue'),
                meta: {
                    title: 'Quản lý danh mục',
                }
            },
            {
                path: 'coupon',
                name: 'CouponAdmin',
                component: () => import('@/views/admin/coupon/CouponTable.vue'),
                meta: {
                    title: 'Quản lý mã giảm giá',
                }
            },
            {
                path: 'gallery',
                name: 'GalleryAdmin',
                component: () => import('@/views/admin/gallery/GalleryTable.vue'),
                meta: {
                    title: 'Quản lý trưng bày',
                }
            },
            {
                path: 'order',
                name: 'OrderAdmin',
                component: () => import('@/views/admin/order/OrderTable.vue'),
                meta: {
                    title: 'Quản lý đơn hàng',
                }
            },
            {
                path: 'product',
                name: 'ProductAdmin',
                component: () => import('@/views/admin/product/ProductTable.vue'),
                meta: {
                    title: 'Quản lý sản phẩm',
                }
            },
            {
                path: 'user',
                name: 'UserAdmin',
                component: () => import('@/views/admin/user/UserTable.vue'),
                meta: {
                    title: 'Quản lý người dùng',
                }
            },
        ]
    },
    {
        path: '/payment-success',
        name: 'PaymentSuccess',
        component: () => import('@/views/pages/payments/PaymentSuccess.vue'),
        meta: {
            title: 'Thanh toán'
        }
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/pages/auth/LoginPage.vue'),
        meta: {
            title: 'Đăng nhập'
        }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/pages/auth/RegisterPage.vue'),
        meta: {
            title: 'Đăng ký'
        }
    },
    {
        path: '/forbidden',
        name: 'Forbidden',
        component: () => import('@/views/pages/forbidden/ForbiddenPage.vue'),
        meta: {
            title: '403 - FORBIDDEN'
        }
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('@/views/pages/not-found/NotFoundPage.vue'),
        meta: {
            title: '404 - PAGE NOT FOUND'
        }
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition
        } else {
            return {
                top: 0, behavior: 'smooth',
            }
        }
    }
});

router.beforeEach(async (to, from, next) => {
    document.title = to.meta.title;
    await authenticate()
    if (to.meta.authenticate && !localStorage.getItem('user')) {
        requireAuth(to, from, next);
    }
    if (to.meta.requireAdmin) {
        await requireAdmin(to, from, next);
    }
    next();
})

export default router;