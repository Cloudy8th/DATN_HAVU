import OpenAI from "openai";


const openai = new OpenAI({
    apiKey: process.env.OPENAI_API_KEY
});

export const chatbot = async (req, res) => {
    try {
        const prompt = req.body.prompt || 'Xin chào';
        const model = "ft:gpt-3.5-turbo-0125:personal:tmartbot:9QeT8Ohh";
        const completion = await openai.chat.completions.create({
            messages: [{ role: "user", content: prompt }],
            model: model,
        });
        res.status(200).json(completion.choices[0].message.content);
        console.log(completion);
    } catch (error) {
        console.log(error);
        res.status(500).json({ error: error });
    }
}