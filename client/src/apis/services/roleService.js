import axios from "../axios";

class roleService {
    endpoint = '/roles';

    async getAll() {
        const res = await axios.get(this.endpoint);
        return res;
    }

    async getById(id) {
        const res = await axios.get(`${this.endpoint}/${id}`);
        return res;
    }
}

export default new roleService();