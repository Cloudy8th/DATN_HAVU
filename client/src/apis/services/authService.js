import axios from '../axios';

class authService {

    async login(data) {
        const res = await axios.post('/users/login', data);
        return res;

    }

    async register(data) {
        const res = await axios.post('/users/register', data);
        return res;
    }
}

export default new authService();