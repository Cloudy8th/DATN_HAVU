import axios from "../axios";
import baseService from "./baseService";

class couponService extends baseService {
    endpoint = "/coupons";

    async useCoupon(id) {
        const res = await axios.get(`${this.endpoint}/use/${id}`);
        return res;
    }
}

export default new couponService();