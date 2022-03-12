package uz.pdp.service;


import uz.pdp.library.Util;
import uz.pdp.service.admin.service.StoreItemsRUD;
import uz.pdp.service.order.OrderServiceImpl;

import static uz.pdp.DataBase.tempOrderItems;
import static uz.pdp.library.Util.*;

public class Amazon {
    static StoreItemsRUD storeItemsRUD = new StoreItemsRUD();
    static AuthServiceImpl authService = new AuthServiceImpl();
    static OrderServiceImpl orderService = new OrderServiceImpl();

    public void amazonApp() {

        print(CYAN, "|---|---------------------|");
        print(CYAN, "| # |---------------------|");
        print(CYAN, "| 1 | CLOTHE LIST \uD83D\uDC54      |");
        print(CYAN, "| 2 | MY CART \uD83D\uDED2 (" + RED + tempOrderItems.stream().count() + RESET + CYAN + ")      |" + RESET);
        print(CYAN, "| 3 | LOGIN               |");
        print(CYAN, "| 4 | REGISTER            |");
        print(CYAN, "| 0 | EXIT                |");
        print(CYAN, "|---|---------------------|");
        print(GREEN, "ENTER OPTION");
        int option = Util.inputInt();
        switch (option) {
            case 1:
                orderService.clothList(null);
                break;
            case 2:
                tempOrderItems
                        .forEach(orderItem -> print(BLUE, "NAME : " + orderItem.getCloth().getName() + ", QUANTITY : " + orderItem.getQuantity() +
                                ", PRICE : " + orderItem.getCloth().getPrice()));
                orderService.checkOut(null);
                break;
            case 3:
                authService.login();
                break;
            case 4:
                authService.register();
                break;
            case 0:
                return;
        }
        amazonApp();
    }

}
