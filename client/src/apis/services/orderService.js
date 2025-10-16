import axios from '../axios';
import baseService from './baseService';

class orderService extends baseService {

    endpoint = "/orders";

    async calculateTotalPrice(data, discount) {
        const res = await axios.post(`${this.endpoint}/calculate`, data, { params: { discount: discount } });
        return res;
    }

    async getAllByUser(id, status, keyword) {
        const res = await axios.get(`${this.endpoint}/user/${id}`,
            { params: { status: status, keyword: keyword } }
        );
        return res;
    }

    async paymentReturn(params) {
        const res = await axios.get(`${this.endpoint}/return`, { params: { ...params } });
        return res;
    }

    async feedbackOrder(id) {
        const res = await axios.put(`${this.endpoint}/feedback/${id}`);
        return res;
    }

    async statisticalOrder(year) {
        const res = await axios.get(`${this.endpoint}/statistical`, { params: { year: year } });
        return res;
    }

    async exportOrder(id) {
        const res = await axios.get(`${this.endpoint}/export/${id}`, {
            responseType: 'blob'
        });
        return res;
    }

    async getAllByFilter(startDate, endDate, status, page, perPage) {
        const res = await axios.get(`${this.endpoint}/filter`, {
            params: {
                startDate: startDate,
                endDate: endDate,
                status: status,
                page: page,
                perPage: perPage
            }
        });
        return res;
    }
}

export default new orderService();