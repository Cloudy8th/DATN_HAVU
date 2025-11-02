import axios from "axios";

export const chatbot = async (content, history) => {
  const payload = {
    prompt: content,
    history: Array.isArray(history) ? history : [],
  };
  const res = await axios.post(import.meta.env.VITE_CHATBOT_URL, payload);
  return res.data; // { answer, mode, ... }
};
