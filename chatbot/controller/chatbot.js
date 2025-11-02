import OpenAI from "openai";
import promptSystem from "../prompt/prompt-system.js";
import { contractToPrompt } from "../prompt/contract-to-prompt.js";
import { validatePlan } from "../prompt/plan-validate.js";
import { planToSQL } from "../prompt/plan-to-sql.js";
import { runReadOnly } from "../db/executor.js";
import { CONTRACT } from "../db/contract.js";

const openai = new OpenAI({ apiKey: process.env.OPENAI_API_KEY });

/* ✅ Convert FE history -> OpenAI messages */
function formatHistory(history) {
  return history.map((m) => ({
    role: m.role === "bot" ? "assistant" : "user",
    content: m.content,
  }));
}

/* ✅ gọi OpenAI tạo kế hoạch truy vấn DB có history */
// ✅ generatePlan: ưu tiên truy vấn DB, có few-shot ví dụ, dùng history
async function generatePlan(question, history) {
  const schemaText = contractToPrompt();
  const allowed = Object.keys(CONTRACT.resources).join(", ");

  const resp = await openai.chat.completions.create({
    model: "o4-mini",
    response_format: { type: "json_object" },
    messages: [
      {
        role: "system",
        content: `Bạn là trợ lý lập kế hoạch truy vấn DB của cửa hàng.
- Luôn cố gắng lập kế hoạch truy vấn vào dữ liệu cửa hàng trước.
- Trả về JSON: {"resource":"<${allowed}>","joins":[],"select":[],"where":[],"sort":[],"limit":number}
- CHỈ được JOIN theo "relations" trong CONTRACT.
- Nếu câu hỏi nhắc tên DANH MỤC (vd "điện thoại", "laptop", "tivi") khi resource=Product:
  • phải thêm joins:[{"resource":"Category"}]
  • và lọc theo field "Category.name" (contains/ILIKE).
- "đánh giá tốt nhất" = sắp xếp theo FeedbackStat.avg_star DESC, tie-break FeedbackStat.review_count DESC.
- Ý định mua/bán/sản phẩm/giá → coi là truy vấn Product (không hỏi lại).
- Nếu thực sự KHÔNG phù hợp truy vấn DB (chit-chat thuần), trả {"message":"<gợi ý hỏi lại rõ ràng>"}.
- Không sinh SQL. Không bịa tên bảng/field.`,
      },
      { role: "system", content: schemaText },

      // ✅ Few-shot ví dụ rõ ràng
      {
        role: "system",
        content: `Ví dụ:
Q: "Cửa hàng có những loại điện thoại nào"
A: {"resource":"Product",
    "joins":[{"resource":"Category"}],
    "select":["id","title","sale_price","discount","Category.name"],
    "where":[{"field":"Category.name","op":"contains","value":"điện thoại"}],
    "limit":5}

Q: "Tôi muốn mua điện thoại"
A: {"resource":"Product",
    "joins":[{"resource":"Category"}],
    "select":["id","title","sale_price","discount","Category.name"],
    "where":[{"field":"Category.name","op":"contains","value":"điện thoại"}],
    "limit":5}

Q: "Laptop nào đang giảm trên 10%"
A: {"resource":"Product",
    "joins":[{"resource":"Category"}],
    "select":["id","title","sale_price","discount","Category.name"],
    "where":[{"field":"Category.name","op":"contains","value":"laptop"},
             {"field":"discount","op":"gte","value":10}],
    "limit":5}

Q: "Sản phẩm nào có đánh giá tốt nhất"
A: {"resource":"Product",
    "joins":[{"resource":"FeedbackStat"}],
    "select":["id","title","sale_price","FeedbackStat.avg_star","FeedbackStat.review_count"],
    "where":[{"field":"FeedbackStat.review_count","op":"gte","value":1}],
    "sort":[{"field":"FeedbackStat.avg_star","dir":"desc"},{"field":"FeedbackStat.review_count","dir":"desc"}],
    "limit":5}

Q: "Top 5 điện thoại có điểm đánh giá trung bình cao nhất"
A: {"resource":"Product",
    "joins":[{"resource":"Category"},{"resource":"FeedbackStat"}],
    "select":["id","title","sale_price","discount","Category.name","FeedbackStat.avg_star","FeedbackStat.review_count"],
    "where":[{"field":"Category.name","op":"contains","value":"điện thoại"},
             {"field":"FeedbackStat.review_count","op":"gte","value":1}],
    "sort":[{"field":"FeedbackStat.avg_star","dir":"desc"},{"field":"FeedbackStat.review_count","dir":"desc"}],
    "limit":5}`,
      },

      // ✅ NHÚNG LỊCH SỬ CHAT
      ...formatHistory(history),

      // ✅ Câu hỏi hiện tại
      { role: "user", content: question },
    ],
  });

  let obj = {};
  try {
    obj = JSON.parse(resp.choices[0].message.content || "{}");
  } catch {}
  if (obj && obj.resource) return { mode: "db", plan: obj };

  // Fallback gợi ý lịch sự (chỉ khi thực sự không thể lập plan)
  const hint =
    obj?.message?.trim() ||
    "Câu hỏi này chưa đủ thông tin để truy vấn. Bạn muốn xem Product, Category, Coupon hay Feedback?";
  return { mode: "non_db", message: hint };
}

/* ✅ Controller chính */
export const chatbot = async (req, res) => {
  try {
    const userPrompt = (req.body.prompt || "").trim();
    const history = Array.isArray(req.body.history) ? req.body.history : [];

    if (!userPrompt) return res.status(400).json({ error: "empty_prompt" });

    const { mode, plan, message } = await generatePlan(userPrompt, history);
    console.log(plan);

    /* ✅ Non DB mode: trả lời gợi ý hoặc chat thuần */
    if (mode === "non_db") {
      const ans = await openai.chat.completions.create({
        model: "o4-mini",
        messages: [
          { role: "system", content: `${promptSystem.system}` },

          // ✅ include chat history when answering chit-chat
          ...formatHistory(history),

          { role: "user", content: message || userPrompt },
        ],
      });

      return res.status(200).json({
        answer: ans.choices[0].message.content,
        mode,
      });
    }

    /* ✅ DB mode */
    const valid = validatePlan(plan);
    const { sql, params } = planToSQL(valid);
    const result = await runReadOnly(sql, params, 6000);

    const payload = {
      question: userPrompt,
      plan: valid,
      sql,
      columns: result.columns,
      rows: result.rows,
    };

    const summary = await openai.chat.completions.create({
      model: "o4-mini",
      messages: [
        {
          role: "system",
          content: `${promptSystem.system}
- Chỉ dùng DATA; nếu không có data, báo Chưa có dữ liệu phù hợp và gợi ý người dùng liên hệ với quản trị viên.`,
        },

        // ✅ giữ lịch sử
        ...formatHistory(history),

        { role: "user", content: JSON.stringify(payload) },
      ],
    });

    return res.status(200).json({
      answer: summary.choices[0].message.content,
      mode,
      plan: valid,
      sql,
      params,
      rowCount: result.rowCount,
      rows: result.rows,
    });
  } catch (e) {
    console.error(e);
    if (
      e?.message === "RESOURCE_NOT_ALLOWED" ||
      e?.message === "SELECT_EMPTY"
    ) {
      return res.status(200).json({
        answer:
          "Câu hỏi chưa rõ. Bạn muốn xem Product, Category, Coupon hay Feedback?",
        mode: "non_db",
      });
    }
    return res.status(500).json({ error: e.message });
  }
};
