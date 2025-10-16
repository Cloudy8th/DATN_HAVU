import OpenAI from "openai";
import promptSytem from "../prompt/prompt-system.js";

const openai = new OpenAI({
    apiKey: process.env.OPENAI_API_KEY,
});

export const chatbot = async (req, res) => {
    try {
        const prompt = req.body.prompt || "Xin chào";
        const model = "o4-mini";
        const completion = await openai.chat.completions.create({
            messages: [
                {
                    role: "system",
                    content: promptSytem.data,
                },
                { role: "user", content: prompt },
            ],
            model: model,
        });
        res.status(200).json(completion.choices[0].message.content);
        console.log(completion);
    } catch (error) {
        console.log(error);
        res.status(500).json({ error: error });
    }
};
