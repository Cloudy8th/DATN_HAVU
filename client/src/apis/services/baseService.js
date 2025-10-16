import axios from '../axios';

class baseService {
    constructor() { }

    endpoint = "";

    async getAll(page, perPage) {
        const res = await axios.get(this.endpoint, { params: { page: page, perPage: perPage } });
        return res;
    }

    async getById(id) {
        const res = await axios.get(`${this.endpoint}/${id}`);
        return res;

    }

    async insert(data) {
        const res = await axios.post(this.endpoint, data);
        return res;
    }

    async update(id, data) {
        const res = await axios.put(`${this.endpoint}/${id}`, data);
        return res;
    }

    async delete(id) {
        const res = await axios.delete(`${this.endpoint}/${id}`);
        return res;
    }

    async insertMultiple(data) {
        const res = await axios.post(`${this.endpoint}/multiple`, data);
        return res;
    }

    async updateMultiple(data) {
        const res = await axios.put(`${this.endpoint}/multiple`, data);
        return res;
    }

    async deleteMultiple(data) {
        const res = await axios.delete(`${this.endpoint}/multiple`, { data: data });
        return res;
    }
}

export default baseService;