import express from 'express';
import cors from 'cors';
import bodyParser from 'body-parser';
import 'dotenv/config';
import chatbot from './router/chatbot.js';

const app = express();
const port = process.env.PORT || 6060;

app.use(bodyParser.json({ limit: '30mb' }));
app.use(bodyParser.urlencoded({ extended: true, limit: '30mb' }));
app.use(cors());

app.use('/api/v1/chatbot', chatbot);

app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
})