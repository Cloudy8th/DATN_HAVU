import axios from '../axios';
import baseService from './baseService';

class productService extends baseService {
    endpoint = "/products";

    async getAll(page, perPage, keyword) {
        try {
            const res = await axios.get('/products',
                {
                    params: {
                        page: page,
                        perPage: perPage,
                        keyword: keyword
                    }
                }
            );
            return res.data;
        } catch (error) {
            console.error(error);
        }
    }

    async getAllByCategory(id, page, perPage) {
        try {
            const res = await axios.get(`/products/category/${id}`, { params: { page, perPage } });
            return res.data;
        } catch (error) {
            console.error(error);
        }
    }

    async getAllDeleted(page, perPage) {
        const res = await axios.get('/products/deleted',
            {
                params: {
                    page: page,
                    perPage: perPage
                }
            }
        );
        return res;
    }

    async uploadImage(id, data) {
        const res = await axios.post(`${this.endpoint}/upload-image/${id}`, data, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        return res;
    }

    async getAllBestSeller(page, perPage) {
        const res = await axios.get('/products/best-seller', { params: { page, perPage } });
        return res;
    }

    async getAllSale(page, perPage) {
        const res = await axios.get('/products/sale', { params: { page, perPage } });
        return res;
    }

    async searchProduct(keyword, page, perPage, price, feedback) {
        const res = await axios.get('/products/search',
            {
                params: {
                    keyword, page, perPage, price, feedback
                }
            });
        return res;
    }

    async filterProduct(keyword, page, perPage, discount, title, price, categoryId, isStock, productId) {
        const res = await axios.get(`${this.endpoint}/filter`,
            {
                params: {
                    keyword, page, perPage, discount, title, price, categoryId, isStock, productId
                }
            });
        return res;
    }

    async deleteImages(uuids) {
        const res = await axios.delete('/products/delete-images', { data: uuids });
        return res;
    }
}

export default new productService();