package uz.pdp.bot.helper;



import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uz.pdp.model.order.OrderHistory;
import uz.pdp.model.order.OrderItem;
import uz.pdp.model.payment.PayType;
import uz.pdp.model.user.Customer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static uz.pdp.DataBase.orderHistoryList;

public class DocGenerator {

    public File checkPdf(Customer customer, PayType payType) {
        File file = null;
        try (PdfWriter writer = new PdfWriter("src/main/resources/files/check.pdf")) {

            PdfDocument pdfDocument = new PdfDocument(writer);

            pdfDocument.setDefaultPageSize(PageSize.A6);
            pdfDocument.addNewPage();


            Document document = new Document(pdfDocument);
            Paragraph paragraph = new Paragraph("CHECK " + customer.getFullName() + " - DATE " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy , HH:mm:ss"))).setFontSize(15);

            document.add(paragraph);


            float[] pointColumn = {40F, 80F, 80F, 80F, 80F};
            Table table = new Table(pointColumn);

            table.setTextAlignment(TextAlignment.CENTER).setHorizontalAlignment(HorizontalAlignment.CENTER);
            table.addCell(new Cell().add("ID").setBackgroundColor(Color.BLUE));
            table.addCell(new Cell().add("NAME").setBackgroundColor(Color.BLUE));
            table.addCell(new Cell().add("SIZE").setBackgroundColor(Color.BLUE));
            table.addCell(new Cell().add("PRICE").setBackgroundColor(Color.BLUE));
            table.addCell(new Cell().add("QUANTITY").setBackgroundColor(Color.BLUE));


            int i = 1;
            int sum = 0;
            for (OrderItem orderItem : customer.getMyCart()) {
                if (i % 2 == 0) {
                    table.addCell(new Cell().add("" + (i++)).setBackgroundColor(Color.LIGHT_GRAY));
                    table.addCell(new Cell().add(orderItem.getCloth().getName()).setBackgroundColor(Color.LIGHT_GRAY));
                    table.addCell(new Cell().add(orderItem.getCloth().getSize().name()).setBackgroundColor(Color.LIGHT_GRAY));
                    table.addCell(new Cell().add("" + orderItem.getCloth().getPrice()).setBackgroundColor(Color.LIGHT_GRAY));
                    table.addCell(new Cell().add("" + orderItem.getQuantity()).setBackgroundColor(Color.LIGHT_GRAY));
                } else {
                    table.addCell(new Cell().add("" + (i++)).setBackgroundColor(Color.GRAY));
                    table.addCell(new Cell().add(orderItem.getCloth().getName()).setBackgroundColor(Color.GRAY));
                    table.addCell(new Cell().add(orderItem.getCloth().getSize().name()).setBackgroundColor(Color.GRAY));
                    table.addCell(new Cell().add("" + orderItem.getCloth().getPrice()).setBackgroundColor(Color.GRAY));
                    table.addCell(new Cell().add("" + orderItem.getQuantity()).setBackgroundColor(Color.GRAY));
                }

                sum += orderItem.getQuantity() * orderItem.getCloth().getPrice();
            }


            table.addCell(new Cell().add(""));
            table.addCell(new Cell().add(""));
            table.addCell(new Cell().add(""));
            table.addCell(new Cell().add("TOTAL PRICE").setBackgroundColor(Color.RED));
            table.addCell(new Cell().add("" + sum).setBackgroundColor(Color.RED));
            table.addCell(new Cell().add(""));
            table.addCell(new Cell().add(""));
            table.addCell(new Cell().add(""));
            table.addCell(new Cell().add("FEE : " + payType.getName()).setBackgroundColor(Color.LIGHT_GRAY));
            table.addCell(new Cell().add("" + (sum * payType.getCommissionFee() / 100)).setBackgroundColor(Color.LIGHT_GRAY));
            table.addCell(new Cell().add(""));
            table.addCell(new Cell().add(""));
            table.addCell(new Cell().add(""));
            table.addCell(new Cell().add("TOTAL COST").setBackgroundColor(Color.RED));
            table.addCell(new Cell().add("" + (sum + sum * payType.getCommissionFee() / 100)).setBackgroundColor(Color.RED));


            document.add(table);

            document.close();
            file = new File("src/main/resources/files/check.pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public File historyXls(Customer user) {
        File file = null;
        try {
            FileOutputStream out = new FileOutputStream(new File("src/main/resources/order/order.xlsx"));
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet spreadsheet = workbook.createSheet(" Cloth Data ");
            XSSFRow row;
            XSSFRow row2;

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);


            int rowid = 0;
            row = spreadsheet.createRow(rowid++);
            row.createCell(0).setCellValue("#");
            row.getCell(0).setCellStyle(cellStyle);
            row.createCell(1).setCellValue("User");
            row.getCell(1).setCellStyle(cellStyle);
            row.createCell(2).setCellValue("PayType");
            row.getCell(2).setCellStyle(cellStyle);

            row.createCell(3).setCellValue("--------");
            row.getCell(3).setCellStyle(cellStyle);
            row.createCell(4).setCellValue("Name");
            row.getCell(4).setCellStyle(cellStyle);
            row.createCell(5).setCellValue("QUANTITY");
            row.getCell(5).setCellStyle(cellStyle);
            row.createCell(6).setCellValue("PRICE");
            row.getCell(6).setCellStyle(cellStyle);

            row.createCell(7).setCellValue("Total");
            row.getCell(7).setCellStyle(cellStyle);
            row.createCell(8).setCellValue("Fee");
            row.getCell(8).setCellStyle(cellStyle);
            row.createCell(9).setCellValue("TotalWithCommissionSum");
            row.getCell(9).setCellStyle(cellStyle);

            row.createCell(10).setCellValue("Date");
            row.getCell(10).setCellStyle(cellStyle);

            int i = 1;
            for (OrderHistory orderHistory : orderHistoryList) {
                if (orderHistory.getCustomer().getUserId() != null && orderHistory.getCustomer().getUserId().longValue() == user.getUserId().longValue()) {
                    row = spreadsheet.createRow(rowid++);
                    row.createCell(0).setCellValue(i++);
                    row.getCell(0).setCellStyle(cellStyle);
                    row.createCell(1).setCellValue(orderHistory.getCustomer().getFullName());
                    row.getCell(1).setCellStyle(cellStyle);
                    row.createCell(2).setCellValue(orderHistory.getPayType().getName());
                    row.getCell(2).setCellStyle(cellStyle);

                    row.createCell(7).setCellValue(orderHistory.getPrice());
                    row.getCell(7).setCellStyle(cellStyle);

                    row.createCell(8).setCellValue(orderHistory.getPayType().getCommissionFee());
                    row.getCell(8).setCellStyle(cellStyle);

                    row.createCell(9).setCellValue(orderHistory.getCommissionFeeSum());
                    row.getCell(9).setCellStyle(cellStyle);
                    row.createCell(10).setCellValue(orderHistory.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    row.getCell(10).setCellStyle(cellStyle);

                    for (OrderItem item : orderHistory.getItems()) {
                        row.createCell(3).setCellValue("--------");
                        row.getCell(3).setCellStyle(cellStyle);
                        row.createCell(4).setCellValue(item.getCloth().getName());
                        row.getCell(4).setCellStyle(cellStyle);
                        row.createCell(5).setCellValue(item.getQuantity());
                        row.getCell(5).setCellStyle(cellStyle);
                        row.createCell(6).setCellValue("" + item.getCloth().getPrice());
                        row.getCell(6).setCellStyle(cellStyle);
                        row = spreadsheet.createRow(rowid++);
                    }
                }
            }


            workbook.write(out);
            out.close();
            file = new File("src/main/resources/order/order.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}
