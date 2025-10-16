import axios from "axios";

export const chatbot = async (content) => {
    try {
        const res = await axios.post(process.env.VUE_APP_CHATBOT, { prompt: content });
        return res.data;
    } catch (error) {
        throw new Error(error);
    }
}