package com.project.tmartweb.application.services.order;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.project.tmartweb.application.repositories.OrderDetailRepository;
import com.project.tmartweb.domain.entities.Order;
import com.project.tmartweb.domain.entities.OrderDetail;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderExportService implements IOrderExportService {
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public void exportBillOrder(Order order, HttpServletResponse response) {
        try (Document document = new Document(PageSize.A4)) {

            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();
            String fontPath = "src/main/resources/fonts/OpenSans.ttf";
            BaseFont baseFont = BaseFont.createFont(
                    fontPath,
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );
            Font titleFont = new Font(baseFont, 18, Font.BOLD, Color.BLUE);
            Paragraph title = new Paragraph("Hóa đơn mua hàng", titleFont);
            title.setSpacingAfter(15);

            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            Paragraph p = new Paragraph("Hóa đơn", titleFont);
            p.setAlignment(Paragraph.ALIGN_CENTER);
            p.setSpacingAfter(15);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss, dd-MM-yyyy");
            LocalDateTime dateTime = Instant.ofEpochMilli(order.getCreatedAt().getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            document.add(new Paragraph("Mã đơn hàng: " + order.getId()));
            document.add(new Paragraph("Ngày tạo: " + dateTime.format(dateTimeFormatter)));
            document.add(new Paragraph("Tên người nhận: " + order.getFullName()));
            document.add(new Paragraph("Số điện thoại người nhận: " + order.getPhoneNumber()));
            document.add(new Paragraph("Địa chỉ người nhận: " + order.getAddress()));
            document.add(new Paragraph(" "));

            document.add(p);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100f);
            table.setWidths(new float[]{1.0f, 5.0f, 2.0f, 2.0f, 2.5f, 2.5f});
            table.setSpacingBefore(10);
            Font contentFont = new Font(baseFont, 12, Font.NORMAL);
            writeTableHeader(table, baseFont);
            writeTableData(table, order, contentFont);

            document.add(table);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Error occurred while generating PDF", e);
        }
    }

    private void writeTableHeader(PdfPTable table, BaseFont baseFont) {
        PdfPCell cell = new PdfPCell();

        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);

        Font font = new Font(baseFont, 12, Font.BOLD);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("STT", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Tên sản phẩm", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Phân loại", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Số lượng", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Giá tiền", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Tổng tiền", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table, Order order, Font contentFont) {
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderId(order.getId());
        int index = 1;
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        for (OrderDetail orderDetail : orderDetails) {
            table.addCell(new Phrase(Integer.toString(index), contentFont));
            table.addCell(new Phrase(orderDetail.getProduct().getTitle(), contentFont));
            table.addCell(new Phrase(orderDetail.getClassify(), contentFont));
            table.addCell(new Phrase(Integer.toString(orderDetail.getQuantity()), contentFont));
            table.addCell(new Phrase(decimalFormat.format(orderDetail.getPrice()) + "₫", contentFont));
            table.addCell(new Phrase(decimalFormat.format(orderDetail.getTotalMoney()) + "₫", contentFont));
            index++;
        }
    }
}
