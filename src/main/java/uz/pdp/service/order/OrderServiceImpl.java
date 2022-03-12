package uz.pdp.service.order;

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
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import uz.pdp.library.Util;
import uz.pdp.model.abtract.User;
import uz.pdp.model.order.OrderHistory;
import uz.pdp.model.order.OrderItem;
import uz.pdp.model.order.StoreItem;
import uz.pdp.model.payment.PayType;
import uz.pdp.model.products.Cloth;
import uz.pdp.model.user.Customer;
import uz.pdp.service.AuthServiceImpl;
import uz.pdp.service.admin.service.ClothCRUD;
import uz.pdp.service.admin.service.PayTypeCRUD;
import uz.pdp.service.admin.service.StoreItemsRUD;
import uz.pdp.service.customer.CustomerServiceImpl;
import uz.pdp.service.interfaces.OrderService;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static uz.pdp.DataBase.*;
import static uz.pdp.library.SessionMessage.*;
import static uz.pdp.library.Util.*;
import static uz.pdp.library.Util.print;

public class OrderServiceImpl implements OrderService {

    static StoreItemsRUD storeItemsRUD = new StoreItemsRUD();
    static ClothCRUD clothCRUD = new ClothCRUD();
    static OrderHistoryImpl orderHistory = new OrderHistoryImpl();
    static AuthServiceImpl authService = new AuthServiceImpl();
    static PayTypeCRUD payTypeCRUD = new PayTypeCRUD();
    static CustomerServiceImpl customerService = new CustomerServiceImpl();

    public void clothList(Customer user) {
        print(BLUE_BOLD, "|---|---------------------|");
        print(CYAN, "| 1 | ADD TO CART \uD83D\uDED2      |");
        print(CYAN, "| 2 | ALL CLOTHES \uD83D\uDC54      |");
        print(CYAN, "| 3 | FILTER PRICE        |");
        print(CYAN, "| 4 | CLOTH IN DISCOUNT   |");
        print(CYAN, "| 5 | FILTER TWO PRICE    |");
        print(CYAN, "| 6 | ADD WISHLIST \uD83D\uDC99     |");
        print(CYAN, "| 0 | BACK                |");
        print(BLUE_BOLD, "|---|---------------------|");

        print(CYAN, "ENTER OPTION");
        int option = inputInt();
        switch (option) {
            case 1:
                addToCart(user);
                break;
            case 2:
                allClothes();
                break;
            case 3:
                filterCloth();
                break;
            case 4:
                clothInDiscount();
                break;
            case 5:
                filterTwoPrice();
                break;
            case 0:
                return;
            default:
                print(RED, "WRONG OPTION");
                break;
        }
        clothList(user);
    }

    public void allClothes() {
        print(GREEN_BOLD, "CLOTH LIST");

        print(BLUE_BOLD, "|---|---------------------|");
        clothList.forEach(cloth -> {
            print(BLUE_BOLD, "NAME : " + cloth.getName() +
                    " , SIZE : " + cloth.getSize() +
                    " , PRICE : " + cloth.getPrice() +
                    ", DISCOUNT : " + (cloth.getPrice() - cloth.getPrice() * cloth.getDiscount() / 100)
            );
        });
        print(BLUE_BOLD, "|---|---------------------|");
    }

    public void filterTwoPrice() {
        print(GREEN_BOLD, "ENTER BEGIN PRICE");
        double begin = inputDouble();
        print(GREEN_BOLD, "ENTER END PRICE");
        double end = inputDouble();
        print(BLUE_BOLD, "|---|---------------------|");

        long count = clothList.stream()
                .filter(cloth -> cloth.getPrice() >= begin && cloth.getPrice() <= end).count();

        if (count > 0) {
            clothList.stream()
                    .filter(cloth -> cloth.getPrice() >= begin && cloth.getPrice() <= end)
                    .forEach(cloth -> print(BLUE_BOLD, "NAME : " + cloth.getName() +
                            " , SIZE : " + cloth.getSize() + " , PRICE : " + cloth.getPrice() +
                            ", DISCOUNT : " + (cloth.getPrice() - cloth.getPrice() * cloth.getDiscount() / 100)));
            print(BLUE_BOLD, "|---|---------------------|");
        } else {
            print(RED_BOLD, NOT_FOUND);
        }
    }

    public void clothInDiscount() {
        print(BLUE_BOLD, "|---|---------------------|");
        print(GREEN_BOLD, "CLOTH IN DISCOUNT");
        List<Cloth> clothDiscountList = clothList.stream().filter(cloth -> cloth.getDiscount() > 0).collect(Collectors.toList());
        clothDiscountList.forEach(cloth -> print(BLUE_BOLD, "NAME : " + cloth.getName() +
                " , SIZE : " + cloth.getSize() +
                ", PRICE : " + (cloth.getPrice() - cloth.getPrice() * cloth.getDiscount() / 100)));
        print(BLUE_BOLD, "|---|---------------------|");
    }

    public void filterCloth() {

        print(GREEN_BOLD, "ENTER PRICE");
        double price = inputDouble();
        print(BLUE_BOLD, "|---|---------------------|");

        long count = clothList.stream()
                .filter(cloth -> (cloth.getPrice() - cloth.getPrice() * cloth.getDiscount() / 100) <= price).count();

        if (count > 0) {
            clothList.stream()
                    .filter(cloth -> (cloth.getPrice() - cloth.getPrice() * cloth.getDiscount() / 100) <= price)
                    .forEach(cloth -> print(BLUE_BOLD, "NAME : " + cloth.getName() +
                            " , SIZE : " + cloth.getSize() +
                            " , PRICE : " + cloth.getPrice() +
                            ", DISCOUNT : " + (cloth.getPrice() - cloth.getPrice() * cloth.getDiscount() / 100)));
            print(BLUE_BOLD, "|---|---------------------|");
        } else {
            print(RED_BOLD, NOT_FOUND);
        }
    }

    @Override
    public void addToCart(Customer user) {
        Cloth selectedCloth = clothCRUD.findById();
        if (selectedCloth != null) {
            boolean flag = false;
            print(CYAN_BOLD,"Enter quantity");
            int quantity = inputInt();

            if (user != null) {
                Customer customer = (Customer) user;
                OrderItem orderItem3 = customer.getMyCart().stream().filter(mycart -> mycart.getCloth().getName().equalsIgnoreCase(selectedCloth.getName())).findFirst().orElse(null);
                if (orderItem3 != null) {
                    orderItem3.setQuantity(orderItem3.getQuantity() + quantity);
                    print(CYAN, SUCCESS);
                    authService.writeJson();
                } else {
                    OrderItem orderItem = new OrderItem(selectedCloth, quantity);
                    customer.setCart(orderItem);
                    print(CYAN, SUCCESS);
                    authService.writeJson();
                }
            } else {
                OrderItem orderItem = new OrderItem(selectedCloth, quantity);
                tempOrderItems.add(orderItem);
                print(CYAN, SUCCESS);
            }

        }

    }

    @Override
    public void checkOut(Customer user) {
        print(BLUE_BOLD, "|---|---------------------");
        print(CYAN, "| 1 | CHECK OUT           |");
        print(CYAN, "| 2 | ADD TO CART \uD83D\uDED2 (" + RED + tempOrderItems.stream().count() + RESET + CYAN + ")  |" + RESET);
        print(CYAN, "| 0 | BACK                |");
        print(BLUE_BOLD, "|---|---------------------");
        int opt = inputInt();
        switch (opt) {
            case 1:
                if (user != null) buyItems(user);
                print(BLUE_BOLD, "|---|---------------------|");
                print(CYAN, "| 1 | LOGIN               |");
                print(CYAN, "| 2 | REGISTER            |");
                print(BLUE_BOLD, "|---|---------------------");
                int option = inputInt();
                switch (option) {
                    case 1:
                        authService.login();
                        break;
                    case 2:
                        authService.register();
                        break;
                }
                break;
            case 2:
                addToCart(user);
                break;
            case 0:
                return;
        }
    }

    @Override
    public void cart(Customer user) {
        Customer customer = (Customer) user;
        if (customer != null) {
            authService.writeJson();
            if (!customer.getMyCart().isEmpty()) {
               extracted(customer);
            }
        } else {
            tempOrderItems.stream().forEach(orderItem -> print(BLUE, "NAME : " + orderItem.getCloth().getName() + ", QUANTITY : " + orderItem.getQuantity() + ", PRICE : " + orderItem.getCloth().getPrice()));
        }

    }


    @Override
    public void buyItems(Customer user) {
        Customer customer = (Customer) user;
        tempOrderItems.stream().forEach(orderItem -> customer.setCart(orderItem));
        tempOrderItems.clear();
        if (!customer.getMyCart().isEmpty()) {
            extracted(customer);
        }
        print(BLUE_BOLD, "|---|--------" +
                "-------------|");
        print(CYAN, "| 1 | BUY ALL             |");
        print(CYAN, "| 2 | REMOVE QUANTITY     |");
        print(CYAN, "| 3 | REMOVE ITEM         |");
        print(CYAN, "| 0 | BACK                |");
        print(BLUE_BOLD, "|---|---------------------|");
        int option = inputInt();
        switch (option) {
            case 1:
                if (!customer.getMyCart().isEmpty()) {
                    PayType payType = payTypeCRUD.findById();
                    if (payType != null) {
                        int total = customer.getMyCart().stream().mapToInt(price -> (int) (price.getCloth().getPrice() * price.getQuantity())).sum();
                        double commissionFeeSum = total + total * payType.getCommissionFee() / 100;
                        if (customer.getBalance() >= commissionFeeSum) {

                            for (OrderItem orderItem : customer.getMyCart()) {
                                for (StoreItem store : storeItemList) {
                                    if (store.getCloth().getName().equalsIgnoreCase(orderItem.getCloth().getName()) && store.getCloth().getColor().equals(orderItem.getCloth().getColor()) && store.getCloth().getSize().equals(orderItem.getCloth().getSize())) {
                                        store.setQuantity(store.getQuantity() - orderItem.getQuantity());
                                        break;
                                    }
                                }
                            }


                            OrderHistory orderHistor = new OrderHistory(customer, customer.getMyCart(), total, commissionFeeSum, LocalDateTime.now(), payType);
                            orderHistoryList.add(orderHistor);
                            orderHistory.writeJson();
                            storeItemsRUD.writeJson();
                            print(RED, "DO YOU WANT CHECK YES=>1, NO=>2");
                            int optionCheck = inputInt();
                            switch (optionCheck) {
                                case 1:
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

                                        RemoveBorder(table);
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
                                        RemoveBorder(table);


                                        document.add(table);

                                        document.close();
                                        Desktop d = Desktop.getDesktop();
                                        d.open(new File("src/main/resources/files/check.pdf"));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case 0:
                                    break;
                            }
                            customer.clear();
                            authService.writeJson();
                            print(CYAN, SUCCESS);

                        } else {
                            print(RED, CHECK_BALANCE);
                        }
                    }
                } else {
                    print(RED, "YOUR CART IS EMPTY");
                }
                break;
            case 2:
                if (!customer.getMyCart().isEmpty()) {
                    cart(user);
                    print(CYAN, "ENTER CLOTH NAME");
                    String clothName = Util.inputStr();
                    OrderItem orderItem1 = customer.getMyCart().stream().filter(mycart -> mycart.getCloth().getName().equalsIgnoreCase(clothName)).findFirst().orElse(null);
                    if (orderItem1 != null) {
                        print(CYAN, "ENTER QUANTITY");
                        int quantity = inputInt();
                        if (quantity <= orderItem1.getQuantity()) {
                            orderItem1.setQuantity(orderItem1.getQuantity() - quantity);
                            orderHistory.writeJson();
                            authService.writeJson();
                            storeItemsRUD.writeJson();
                            print(CYAN, SUCCESS);
                        } else {
                            print(RED, INSUFFICENT_AMOUNT);
                        }

                    } else {
                        print(RED, NOT_FOUND);
                    }
                } else {
                    print(RED, "YOUR CART IS EMPTY");
                }
                break;
            case 3:
                if (!customer.getMyCart().isEmpty()) {
                    cart(user);
                    print(CYAN, "ENTER CLOTH NAME");
                    String name = Util.inputStr();
                    OrderItem orderItem = customer.getMyCart().stream().filter(mycart -> mycart.getCloth().getName().equalsIgnoreCase(name)).findFirst().orElse(null);
                    if (orderItem != null) {
                        customer.getMyCart().remove(orderItem);
                        orderHistory.writeJson();
                        authService.writeJson();
                        storeItemsRUD.writeJson();
                        print(CYAN, SUCCESS);
                    } else {
                        print(RED, NOT_FOUND);
                    }
                } else {
                    print(RED, "YOUR CART IS EMPTY");
                }
                break;
            case 0:
                return;
            default:
                print(RED, WRONG_OPTION);

        }
        buyItems(user);
    }

    private void extracted(Customer customer) {
        print(BLUE_BOLD, "|---|----------------------------------");
        print(CYAN,      "|   | MY CART \uD83D\uDED2 (" + RED + customer.getMyCart().stream().count() + RESET + CYAN + ")       " + RESET);
        print(BLUE_BOLD, "|---|----------------------------------");
        customer.getMyCart().stream().forEach(orderItem -> print(CYAN_BOLD, "|---| NAME : " + orderItem.getCloth().getName() + ", QUANTITY : " + orderItem.getQuantity() + ", PRICE : " + orderItem.getCloth().getPrice()));
        print(BLUE_BOLD, "|---|----------------------------------");
    }

    @Override
    public void orderMenu(Customer user) {

    }

    private static void RemoveBorder(Table table) {
        for (IElement iElement : table.getChildren()) {
            ((Cell) iElement).setBorder(Border.NO_BORDER);
        }
    }
}
