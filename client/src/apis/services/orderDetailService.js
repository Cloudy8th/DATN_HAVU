import axios from "../axios";
import baseService from "./baseService";

class orderDetailService extends baseService {
    endpoint = "/order-details";

    async getAllByOrderId(id) {
        const res = await axios.get(`${this.endpoint}/order/${id}`);
        return res;
    }
}

export default new orderDetailService();