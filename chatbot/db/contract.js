export const CONTRACT = {
  resources: {
    Product: {
      view: "products",
      description: "Sản phẩm đang bán trên trang web",
      aliases: ["products", "san_pham", "sản phẩm", "sp"],
      fields: {
        id: { type: "text", description: "Mã sản phẩm" },
        category_id: { type: "uuid", description: "Mã loại sản phẩm" },
        title: { type: "text", description: "Tên sản phẩm" },
        origin_price: { type: "number", description: "Giá gốc" },
        sale_price: { type: "number", description: "Giá bán" },
        discount: { type: "number", description: "Phần trăm giảm giá (%)" },
        description: { type: "text", description: "Mô tả" },
        quantity: { type: "number", description: "Số lượng" },
        deleted: { type: "boolean", description: "Trạng thái đã xóa" },
        created_at: { type: "timestamp", description: "Thời gian tạo" },
      },
      defaultSelect: ["id", "title", "sale_price", "discount"],
      filterable: true,
      sortable: true,
      relations: {
        Category: {
          type: "inner", // "inner" | "left"
          localField: "category_id",
          foreignResource: "Category",
          foreignField: "id",
          alias: "c", // alias bảng join
        },
        FeedbackStat: {
          type: "left", // dùng LEFT để vẫn thấy sp chưa có review
          localField: "id",
          foreignResource: "FeedbackStat",
          foreignField: "product_id",
          alias: "fs",
        },
      },
    },
    Category: {
      view: "categories",
      description: "Danh sách loại sản phẩm",
      aliases: ["category", "categories", "danh_muc", "danh mục"],
      fields: {
        id: { type: "uuid", description: "Mã loại sản phẩm" },
        name: { type: "text", description: "Tên loại sản phẩm" },
        created_at: { type: "timestamp", description: "Thời gian tạo" },
      },
      defaultSelect: ["id", "name"],
      filterable: true,
      sortable: true,
    },
    Coupon: {
      view: "coupons",
      description: "Danh sách khuyến mãi",
      aliases: [
        "coupons",
        "voucher",
        "vouchers",
        "khuyen_mai",
        "khuyến mãi",
        "km",
      ],
      fields: {
        code: { type: "text", description: "Mã khuyến mãi" },
        discount: { type: "number", description: "Phần trăm giảm giá (%)" },
        expiration_date: { type: "timestamp", description: "Ngày hết hạn" },
        expired: { type: "boolean", description: "Trạng thái ngưng sử dụng" },
        quantity: { type: "number", description: "Số lượng" },
        created_at: { type: "timestamp", description: "Thời gian tạo" },
      },
      defaultSelect: ["code", "discount", "expiration_date"],
      filterable: true,
      sortable: true,
    },
    FeedbackStat: {
      view: "v_feedback_stats",
      fields: {
        product_id: { type: "text" },
        avg_star: { type: "number" },
        review_count: { type: "number" },
        last_review_at: { type: "timestamp" },
      },
      defaultSelect: ["product_id", "avg_star", "review_count"],
    },
  },
};
