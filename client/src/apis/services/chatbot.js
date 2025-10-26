import axios from "axios";

export const chatbot = async (content) => {
    try {
        const res = await axios.post(import.meta.env.VITE_CHATBOT_URL, { prompt: content });
        return res.data;
    } catch (error) {
        throw new Error(error);
    }
}