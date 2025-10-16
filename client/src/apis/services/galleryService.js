import baseService from "./baseService";
import axios from "../axios";

class galleryService extends baseService {
    endpoint = "/galleries"

    async insert(productId, image) {
        const res = await axios.post(`${this.endpoint}`, image, { params: { productId: productId } }, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        return res;
    }

    async update(id, productId, image) {
        const res = await axios.put(`${this.endpoint}/${id}`, image, { params: { productId: productId } }, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        return res;
    }
}

export default new galleryService();