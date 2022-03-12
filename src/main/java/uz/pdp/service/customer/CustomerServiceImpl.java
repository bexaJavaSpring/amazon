package uz.pdp.service.customer;

import uz.pdp.library.SessionMessage;
import uz.pdp.library.Util;
import uz.pdp.model.abtract.User;
import uz.pdp.model.user.Customer;
import uz.pdp.service.Amazon;
import uz.pdp.service.AuthServiceImpl;
import uz.pdp.service.interfaces.CustomerService;
import uz.pdp.service.order.OrderHistoryImpl;
import uz.pdp.service.order.OrderServiceImpl;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;

import static uz.pdp.DataBase.tempOrderItems;
import static uz.pdp.library.SessionMessage.INSUFFICENT_AMOUNT;
import static uz.pdp.library.SessionMessage.SUCCESS;
import static uz.pdp.library.Util.*;

public class CustomerServiceImpl implements SessionMessage,CustomerService {

    static OrderHistoryImpl orderHistoryService = new OrderHistoryImpl();
    static OrderServiceImpl orderService = new OrderServiceImpl();
    static Amazon amazon = new Amazon();
    static AuthServiceImpl authService = new AuthServiceImpl();


    public void customerMenu(Customer customer) {

        if(!tempOrderItems.isEmpty()){
            tempOrderItems.stream().forEach(orderItem -> customer.setCart(orderItem));
            tempOrderItems.clear();
            authService.writeJson();
        }
        while (true) {
            print(CYAN, "|---|----------------------|");
            print(CYAN, "|---| WELCOME " + customer.getFullName().toUpperCase() + " !");
            print(CYAN, "|---|----------------------|");
            print(CYAN, "| # |----------------------|");
            print(CYAN, "| 1 | CLOTH LIST \uD83D\uDC54        |");
            print(CYAN, "| 2 | MY CART \uD83D\uDED2 (" + RED + customer.getMyCart().stream().count() + RESET + CYAN + ")       |" + RESET);
            print(CYAN, "| 3 | CHECKOUT \uD83D\uDCB3          |");
            print(CYAN, "| 4 | WISHLIST \uD83D\uDC99          |");
            print(CYAN, "| 5 | ORDER HISTORY ACTION |");
            print(CYAN, "| 6 | FILL BALANCE \uD83D\uDCB6      |");
            print(CYAN, "| 0 | LOGOUT               |");
            print(CYAN, "|---|----------------------|");
            print(GREEN, "ENTER OPTION");
            int optionCustomer = new Scanner(System.in).nextInt();
            switch (optionCustomer) {
                case 1:
                    orderService.clothList(customer);
                    break;
                case 2:
                    orderService.cart(customer);
                    break;
                case 3:
                    orderService.buyItems(customer);
                    break;
                case 4:

                    break;
                case 5:
                    orderHistoryService.orderHistoryMenu(customer);
                    break;
                case 6:
                    fillBalance(customer);
                    break;
                case 0:
                    return;
            }

        }
    }

    @Override
    public void customerMenu(User user) {

    }

    @Override
    public void fillBalance(User user) {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        print(RED, "Balance : " + nf.format(user.getBalance()));
        print(CYAN, "ENTER AMOUNT \uD83D\uDCB6 ");
        double amount = Util.inputDouble();
        if (amount > 0) {
            user.setBalance(user.getBalance() + amount);
            print(GREEN, SUCCESS);
            authService.writeJson();
        } else {
            print(RED, INSUFFICENT_AMOUNT);
        }
    }
}
