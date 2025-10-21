// File: OrderExportService.java (Ví dụ về cách sửa)

package com.project.hmartweb.application.services.order;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import com.project.hmartweb.domain.entities.Order;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.lowagie.text.DocumentException;

// ... Các import và annotation khác

public class OrderExportService implements IOrderExportService {

    @Override
    public void exportBillOrder(Order order, HttpServletResponse response) {
        // FIX: Khai báo Document ngoài khối try
        Document document = null; 
        try {
            document = new Document(PageSize.A4);
            
            // Đảm bảo response headers được thiết lập đúng trước khi lấy OutputStream
            response.setContentType("application/pdf");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=bill_order_" + order.getId() + ".pdf";
            response.setHeader(headerKey, headerValue);
            
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // 1. Logic tạo nội dung PDF của bạn ở đây...
            // Ví dụ: document.add(new Paragraph("Hóa đơn đơn hàng " + order.getId()));
            
        } catch (DocumentException e) {
            // Xử lý lỗi liên quan đến iText/OpenPDF
            throw new RuntimeException("Error generating PDF document: " + e.getMessage(), e);
        } catch (IOException e) {
            // Xử lý lỗi I/O
            throw new RuntimeException("Error writing PDF to response: " + e.getMessage(), e);
        } finally {
            // FIX: Đóng document trong khối finally
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }
}