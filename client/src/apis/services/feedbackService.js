import axios from "../axios";
import baseService from "./baseService";

class feedBackService extends baseService {
    endpoint = "/feedbacks";

    async getAllByProduct(id, page, perPage, star) {
        const res = await axios.get(`${this.endpoint}/product/${id}`,
            { params: { page: page, perPage: perPage, star: star } }
        );
        return res;
    }
}

export default new feedBackService();