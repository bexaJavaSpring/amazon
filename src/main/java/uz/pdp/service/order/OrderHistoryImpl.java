package uz.pdp.service.order;

import com.google.gson.Gson;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uz.pdp.library.Util;
import uz.pdp.model.abtract.User;
import uz.pdp.model.enums.Role;
import uz.pdp.model.order.OrderHistory;
import uz.pdp.model.order.OrderItem;
import uz.pdp.service.interfaces.OrderHistoryService;

import java.awt.*;
import java.io.*;
import java.time.format.DateTimeFormatter;

import static uz.pdp.DataBase.orderHistoryList;
import static uz.pdp.library.SessionMessage.*;
import static uz.pdp.library.Util.*;

public class OrderHistoryImpl implements OrderHistoryService {

    @Override
    public void orderHistory(User user) {

        switch (user.getRole()) {
            case SUPER_ADMIN:
                print(CYAN, "|----------------------------------------------------");
                orderHistoryList.forEach(order -> {
                    print(CYAN, "User     : " + order.getCustomer().getUsername());
                    print(CYAN, "PayType  : " + order.getPayType().getName());
                    print(CYAN, "Total    : " + order.getPrice());
                    print(CYAN, "TotalFee : " + order.getCommissionFeeSum());
                    print(CYAN, "Clothes");
                    order.getItems().stream().forEach(cloth -> print(CYAN, "NAME : " + cloth.getCloth().getName() + ", QUANTITY : " + cloth.getQuantity()));
                    print(CYAN, "----------------------------------------------------");
                });
                break;
            case CUSTOMER:
                print(RED, SUCCESS);
                print(CYAN, "|----------------------------------------------------");
                orderHistoryList.stream().filter(order -> order.getCustomer().getUsername().equals(user.getUsername())).forEach(order -> {
                    print(CYAN, "User     : " + order.getCustomer().getUsername());
                    print(CYAN, "PayType  : " + order.getPayType().getName());
                    print(CYAN, "Total    : " + order.getPrice());
                    print(CYAN, "TotalFee : " + order.getCommissionFeeSum());
                    print(CYAN, "Clothes");
                    order.getItems().stream().forEach(cloth -> print(CYAN, "NAME : " + cloth.getCloth().getName() + ", QUANTITY : " + cloth.getQuantity()));
                    print(CYAN, "----------------------------------------------------");
                });
                break;


        }
    }

    @Override
    public void orderHistoryPayType(User user) {

    }

    @Override
    public void convertExcel(User user) {

        try {
            switch (user.getRole()) {
                case SUPER_ADMIN: {
                    FileOutputStream out = new FileOutputStream(new File("src/main/resources/order/order.xlsx"));
                    XSSFWorkbook workbook = new XSSFWorkbook();
                    XSSFSheet spreadsheet = workbook.createSheet(" Cloth Data ");
                    XSSFRow row;
                    XSSFRow row2;

                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
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
                        row = spreadsheet.createRow(rowid++);
                        row.createCell(0).setCellValue(i++);
                        row.getCell(0).setCellStyle(cellStyle);
                        row.createCell(1).setCellValue(orderHistory.getCustomer().getUsername());
                        row.getCell(1).setCellStyle(cellStyle);
                        row.createCell(2).setCellValue(orderHistory.getPayType().getName());
                        row.getCell(2).setCellStyle(cellStyle);

                        row.createCell(7).setCellValue(orderHistory.getPrice());
                        row.getCell(7).setCellStyle(cellStyle);

                        row.createCell(8).setCellValue(orderHistory.getCommissionFeeSum() - orderHistory.getPrice());
                        row.getCell(8).setCellStyle(cellStyle);

                        row.createCell(9).setCellValue(orderHistory.getCommissionFeeSum());
                        row.getCell(9).setCellStyle(cellStyle);
                        row.createCell(10).setCellValue(orderHistory.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy , HH:mm:ss")));
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

                    workbook.write(out);
                    out.close();
                    print(CYAN, SUCCESS);
                    Desktop d = Desktop.getDesktop();
                    d.open(new File("src/main/resources/order/order.xlsx"));
                    break;
                }
                case CUSTOMER: {
                    FileOutputStream out = new FileOutputStream(new File("src/main/resources/order/order.xlsx"));
                    XSSFWorkbook workbook = new XSSFWorkbook();
                    XSSFSheet spreadsheet = workbook.createSheet(" Cloth Data ");
                    XSSFRow row;
                    XSSFRow row2;

                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
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
                            row.createCell(1).setCellValue(orderHistory.getCustomer().getUsername());
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
                    print(CYAN, SUCCESS);
//                    Desktop d = Desktop.getDesktop();
//                    d.open(new File("src/main/resources/order/order.xlsx"));
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void convertPDF(User user) {
        if (user.getRole().equals(Role.CUSTOMER)) {
            try (PdfWriter writer = new PdfWriter("src/main/resources/order/order.pdf")) {
                PdfDocument pdfDocument = new PdfDocument(writer);

                pdfDocument.setDefaultPageSize(PageSize.A4);
                pdfDocument.addNewPage();

                Document document = new Document(pdfDocument);

                for (OrderHistory orderHistory : orderHistoryList) {
                    if (orderHistory.getCustomer().getUsername().equalsIgnoreCase(user.getUsername())) {
                        float[] pointTablePay = {100F, 100F, 150F};
                        Table table2 = new Table(pointTablePay);
                        table2.setTextAlignment(TextAlignment.CENTER);

                        table2.addCell(new Cell().add("USER").setBackgroundColor(com.itextpdf.kernel.color.Color.BLUE));
                        table2.addCell(new Cell().add("PAYMENT").setBackgroundColor(com.itextpdf.kernel.color.Color.BLUE));
                        table2.addCell(new Cell().add("DATE").setBackgroundColor(com.itextpdf.kernel.color.Color.BLUE));
                        table2.addCell(new Cell().add("" + orderHistory.getCustomer().getUsername().toUpperCase()));
                        table2.addCell(new Cell().add("" + orderHistory.getPayType().getName()));
                        table2.addCell(new Cell().add("" + orderHistory.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy , HH:mm:ss"))));
                        table2.setMarginTop(40);
                        Paragraph paragraph = new Paragraph("----------------------------------").setHorizontalAlignment(com.itextpdf.layout.property.HorizontalAlignment.CENTER).setFontColor(Color.WHITE);

                        float[] pointColumn = {60F, 120F, 120F, 120F, 120F};
                        Table table = new Table(pointColumn);
                        table.setMarginTop(10);
                        table.setTextAlignment(TextAlignment.CENTER).setHorizontalAlignment(com.itextpdf.layout.property.HorizontalAlignment.CENTER);

                        table.addCell(new Cell().add("ID").setBackgroundColor(com.itextpdf.kernel.color.Color.BLUE));
                        table.addCell(new Cell().add("NAME").setBackgroundColor(com.itextpdf.kernel.color.Color.BLUE));
                        table.addCell(new Cell().add("SIZE").setBackgroundColor(com.itextpdf.kernel.color.Color.BLUE));
                        table.addCell(new Cell().add("PRICE").setBackgroundColor(com.itextpdf.kernel.color.Color.BLUE));
                        table.addCell(new Cell().add("QUANTITY").setBackgroundColor(Color.BLUE));

                        int i = 1;
                        int sum = 0;
                        for (OrderItem item : orderHistory.getItems()) {
                            if (i % 2 == 0) {
                                table.addCell(new Cell().add("" + (i++)).setBackgroundColor(Color.LIGHT_GRAY));
                                table.addCell(new Cell().add(item.getCloth().getName()).setBackgroundColor(Color.LIGHT_GRAY));
                                table.addCell(new Cell().add(item.getCloth().getSize().name()).setBackgroundColor(Color.LIGHT_GRAY));
                                table.addCell(new Cell().add("" + item.getCloth().getPrice()).setBackgroundColor(Color.LIGHT_GRAY));
                                table.addCell(new Cell().add("" + item.getQuantity()).setBackgroundColor(Color.LIGHT_GRAY));
                            } else {
                                table.addCell(new Cell().add("" + (i++)).setBackgroundColor(Color.GRAY));
                                table.addCell(new Cell().add(item.getCloth().getName()).setBackgroundColor(Color.GRAY));
                                table.addCell(new Cell().add(item.getCloth().getSize().name()).setBackgroundColor(Color.GRAY));
                                table.addCell(new Cell().add("" + item.getCloth().getPrice()).setBackgroundColor(Color.GRAY));
                                table.addCell(new Cell().add("" + item.getQuantity()).setBackgroundColor(Color.GRAY));
                            }
                            sum += item.getQuantity() * item.getCloth().getPrice();

                        }

                        table.addCell(new Cell().add(""));
                        table.addCell(new Cell().add(""));
                        table.addCell(new Cell().add(""));
                        table.addCell(new Cell().add("TOTAL PRICE").setBackgroundColor(Color.RED));
                        table.addCell(new Cell().add("" + sum).setBackgroundColor(Color.RED));
                        table.addCell(new Cell().add(""));
                        table.addCell(new Cell().add(""));
                        table.addCell(new Cell().add(""));
                        table.addCell(new Cell().add("FEE : ").setBackgroundColor(Color.LIGHT_GRAY));
                        table.addCell(new Cell().add("" + (orderHistory.getCommissionFeeSum() - orderHistory.getPrice())).setBackgroundColor(Color.LIGHT_GRAY));
                        table.addCell(new Cell().add(""));
                        table.addCell(new Cell().add(""));
                        table.addCell(new Cell().add(""));
                        table.addCell(new Cell().add("TOTAL COST").setBackgroundColor(Color.RED));
                        table.addCell(new Cell().add("" + (sum + sum * orderHistory.getPayType().getCommissionFee() / 100)).setBackgroundColor(Color.RED));

                        document.add(table2);
                        document.add(table);
                    }
                }

                document.close();
                Desktop d = Desktop.getDesktop();
                d.open(new File("src/main/resources/order/order.pdf"));

            } catch (IOException e) {
                print(RED, "FILE NOT FOUND");
            }
        } else {
            try (PdfWriter writer = new PdfWriter("src/main/resources/order/order.pdf")) {
                PdfDocument pdfDocument = new PdfDocument(writer);

                pdfDocument.setDefaultPageSize(PageSize.A4);
                pdfDocument.addNewPage();

                Document document = new Document(pdfDocument);

                for (OrderHistory orderHistory : orderHistoryList) {
                    float[] pointTablePay = {100F, 100F, 150F};
                    Table table2 = new Table(pointTablePay);
                    table2.setTextAlignment(TextAlignment.CENTER);

                    table2.addCell(new Cell().add("USER").setBackgroundColor(com.itextpdf.kernel.color.Color.BLUE));
                    table2.addCell(new Cell().add("PAYMENT").setBackgroundColor(com.itextpdf.kernel.color.Color.BLUE));
                    table2.addCell(new Cell().add("DATE").setBackgroundColor(com.itextpdf.kernel.color.Color.BLUE));
                    table2.addCell(new Cell().add("" + orderHistory.getCustomer().getUsername().toUpperCase()));
                    table2.addCell(new Cell().add("" + orderHistory.getPayType().getName()));
                    table2.addCell(new Cell().add("" + orderHistory.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy , HH:mm:ss"))));
                    table2.setMarginTop(40);
                    Paragraph paragraph = new Paragraph("----------------------------------").setHorizontalAlignment(com.itextpdf.layout.property.HorizontalAlignment.CENTER).setFontColor(Color.WHITE);

                    float[] pointColumn = {60F, 120F, 120F, 120F, 120F};
                    Table table = new Table(pointColumn);
                    table.setMarginTop(10);
                    table.setTextAlignment(TextAlignment.CENTER).setHorizontalAlignment(com.itextpdf.layout.property.HorizontalAlignment.CENTER);

                    table.addCell(new Cell().add("ID").setBackgroundColor(com.itextpdf.kernel.color.Color.BLUE));
                    table.addCell(new Cell().add("NAME").setBackgroundColor(com.itextpdf.kernel.color.Color.BLUE));
                    table.addCell(new Cell().add("SIZE").setBackgroundColor(com.itextpdf.kernel.color.Color.BLUE));
                    table.addCell(new Cell().add("PRICE").setBackgroundColor(com.itextpdf.kernel.color.Color.BLUE));
                    table.addCell(new Cell().add("QUANTITY").setBackgroundColor(Color.BLUE));

                    int i = 1;
                    int sum = 0;
                    for (OrderItem item : orderHistory.getItems()) {
                        if (i % 2 == 0) {
                            table.addCell(new Cell().add("" + (i++)).setBackgroundColor(Color.LIGHT_GRAY));
                            table.addCell(new Cell().add(item.getCloth().getName()).setBackgroundColor(Color.LIGHT_GRAY));
                            table.addCell(new Cell().add(item.getCloth().getSize().name()).setBackgroundColor(Color.LIGHT_GRAY));
                            table.addCell(new Cell().add("" + item.getCloth().getPrice()).setBackgroundColor(Color.LIGHT_GRAY));
                            table.addCell(new Cell().add("" + item.getQuantity()).setBackgroundColor(Color.LIGHT_GRAY));
                        } else {
                            table.addCell(new Cell().add("" + (i++)).setBackgroundColor(Color.GRAY));
                            table.addCell(new Cell().add(item.getCloth().getName()).setBackgroundColor(Color.GRAY));
                            table.addCell(new Cell().add(item.getCloth().getSize().name()).setBackgroundColor(Color.GRAY));
                            table.addCell(new Cell().add("" + item.getCloth().getPrice()).setBackgroundColor(Color.GRAY));
                            table.addCell(new Cell().add("" + item.getQuantity()).setBackgroundColor(Color.GRAY));
                        }
                        sum += item.getQuantity() * item.getCloth().getPrice();

                    }

                    RemoveBorder(table);
                    table.addCell(new Cell().add(""));
                    table.addCell(new Cell().add(""));
                    table.addCell(new Cell().add(""));
                    table.addCell(new Cell().add("TOTAL PRICE").setBackgroundColor(Color.RED));
                    table.addCell(new Cell().add("" + sum).setBackgroundColor(Color.RED));
                    table.addCell(new Cell().add(""));
                    table.addCell(new Cell().add(""));
                    table.addCell(new Cell().add(""));
                    table.addCell(new Cell().add("FEE : ").setBackgroundColor(Color.LIGHT_GRAY));
                    table.addCell(new Cell().add("" + (orderHistory.getCommissionFeeSum() - orderHistory.getPrice())).setBackgroundColor(Color.LIGHT_GRAY));
                    table.addCell(new Cell().add(""));
                    table.addCell(new Cell().add(""));
                    table.addCell(new Cell().add(""));
                    table.addCell(new Cell().add("TOTAL COST").setBackgroundColor(Color.RED));
                    table.addCell(new Cell().add("" + (sum + sum * orderHistory.getPayType().getCommissionFee() / 100)).setBackgroundColor(Color.RED));
                    RemoveBorder(table);
                    document.add(table2);
                    document.add(table);
                }

                document.close();
                Desktop d = Desktop.getDesktop();
                d.open(new File("src/main/resources/order/order.pdf"));

            } catch (IOException e) {
                print(RED, "FILE NOT FOUND");
            }
        }
    }

    private static void RemoveBorder(Table table) {
        for (IElement iElement : table.getChildren()) {
            ((Cell) iElement).setBorder(Border.NO_BORDER);
        }
    }


    @Override
    public void writeJson() {
        try (Writer writer = new FileWriter("src/main/resources/order/orderhistory.json")) {
            Gson gson = new Gson();
            String s = gson.toJson(orderHistoryList);
            writer.write(s);
            print(BLUE, WRITED);
        } catch (IOException e) {
            print(RED, NOT_FOUND);
        }
    }

    @Override
    public void orderHistoryMenu(User user) {
        writeJson();
        print(BLUE_BOLD, "|---|--------------------------");
        print(CYAN, "| 1 | ORDER HISTORY \uD83D\uDCD1");
        print(CYAN, "| 2 | BY PAY-TYPE \uD83D\uDCB5");
        print(CYAN, "| 3 | EXPORT EXCEL \uD83D\uDCC2");
        print(CYAN, "| 4 | EXPORT PDF \uD83D\uDDC2");
        print(CYAN, "| 0 | BACK                ");
        print(BLUE_BOLD, "|---|--------------------------");
        int option = Util.inputInt();
        switch (option) {
            case 1:
                print(RED, "ORDERS");
                orderHistory(user);
                break;
            case 2:
                orderHistoryPayType(user);
                break;
            case 3:
                convertExcel(user);
                break;
            case 4:
                convertPDF(user);
                break;
            case 0:
                return;
        }


        orderHistoryMenu(user);
    }
}
