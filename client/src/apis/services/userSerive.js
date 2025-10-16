import baseService from "./baseService";
import axios from '../axios';

class userService extends baseService {
    endpoint = "/users";

    async getByToken(token) {
        const res = await axios.get(`${this.endpoint}/token/${token}`);
        return res;
    }

    async uploadFile(id, file) {
        const res = await axios.post(`${this.endpoint}/upload-image/${id}`, file, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        return res;
    }

    async editProfile(id, data) {
        const res = await axios.put(`${this.endpoint}/edit-profile/${id}`, data);
        return res;
    }

    async changePassword(id, data) {
        const res = await axios.put(`${this.endpoint}/change-password/${id}`, data);
        return res;
    }

    async getAllByFilter(fullName, userName, dateOfBirth, roleId, page, perPage) {
        const res = await axios.get(`${this.endpoint}/filter`, {
            params: {
                fullName: fullName,
                userName: userName,
                dateOfBirth: dateOfBirth,
                roleId: roleId,
                page: page,
                perPage: perPage
            }
        });
        return res;
    }
}

export default new userService();