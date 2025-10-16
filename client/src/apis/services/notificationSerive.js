import baseService from "./baseService";
import axios from "../axios";

class notificationService extends baseService {
    endpoint = "/notifications";

    async readNotification(id) {
        const res = await axios.put(`${this.endpoint}/read/${id}`);
        return res;
    }

    async getAllByUser(id, page, perPage) {
        const res = await axios.get(`${this.endpoint}/user/${id}`, {
            params: {
                page: page,
                perPage: perPage
            }
        });
        return res;
    }

    async getAllByUserAndRead(id) {
        const res = await axios.get(`${this.endpoint}/user-read/${id}`);
        return res;
    }

    async readAllNotification(id) {
        const res = await axios.put(`${this.endpoint}/read-all/${id}`);
        return res;
    }
}

export default new notificationService();