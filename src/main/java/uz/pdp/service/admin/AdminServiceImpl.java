package uz.pdp.service.admin;

import uz.pdp.model.abtract.User;
import uz.pdp.service.AuthServiceImpl;
import uz.pdp.service.admin.service.ClothCRUD;
import uz.pdp.service.admin.service.ColorCRUD;
import uz.pdp.service.admin.service.PayTypeCRUD;
import uz.pdp.service.admin.service.StoreItemsRUD;
import uz.pdp.service.interfaces.AdminService;
import uz.pdp.service.order.OrderHistoryImpl;

import static uz.pdp.library.Util.*;

public class AdminServiceImpl implements AdminService {
    static PayTypeCRUD payTypeCRUD = new PayTypeCRUD();
    static ClothCRUD clothCRUD = new ClothCRUD();
    static ColorCRUD colorCRUD = new ColorCRUD();
    static StoreItemsRUD storeItemsRUD = new StoreItemsRUD();
    static AuthServiceImpl authService = new AuthServiceImpl();
    static OrderHistoryImpl orderHistoryService = new OrderHistoryImpl();

    @Override
    public void adminMenu(User user) {
        print(CYAN, "|---|----------------------|");
        print(CYAN, "|---| WELCOME " + user.getFullName().toUpperCase() + " !");
        print(CYAN, "|---|----------------------|");
        print(CYAN, "|---|----------------------|");
        print(CYAN, "| # |----------------------|");
        print(CYAN, "| 1 | USER ACTION          |");
        print(CYAN, "| 2 | CLOTH CRUD           |");
        print(CYAN, "| 3 | PAY-TYPE CRUD        |");
        print(CYAN, "| 4 | ORDER HISTORY ACTION |");
        print(CYAN, "| 5 | STORE ITEMS          |");
        print(CYAN, "| 6 | COLOR CRUD           |");
        print(CYAN, "| 7 | SEE BALANCE          |");
        print(CYAN, "| 0 | LOGOUT               |");
        print(CYAN, "|---|----------------------|");
        int optionAdmin = inputInt();
        switch (optionAdmin) {
            case 1:
                authService.AuthenticatedUsers();
                break;
            case 2:
                clothCRUD.crudMenu();
                break;
            case 3:
                payTypeCRUD.crudMenu();
                break;
            case 4:
                orderHistoryService.orderHistoryMenu(user);
                break;
            case 5:
                storeItemsRUD.crudMenu();
                break;
            case 6:
                colorCRUD.crudMenu();
                break;
            case 7:
                print(BLUE, "Balance : " + user.getBalance());
                break;
            case 0:
                return;
        }
        adminMenu(user);
    }
}
