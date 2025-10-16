import baseService from "./baseService";
import axios from '../axios';

class cartService extends baseService {
    endpoint = "/carts";

    async getAllByUser(id) {
        const res = await axios.get(`${this.endpoint}/user/${id}`);
        return res;
    }
}

export default new cartService();