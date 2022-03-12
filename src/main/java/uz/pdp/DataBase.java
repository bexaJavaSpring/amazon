package uz.pdp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import uz.pdp.model.abtract.User;
import uz.pdp.model.order.OrderHistory;
import uz.pdp.model.order.OrderItem;
import uz.pdp.model.order.StoreItem;
import uz.pdp.model.payment.PayType;
import uz.pdp.model.products.Cloth;
import uz.pdp.model.products.Color;
import uz.pdp.model.user.Admin;
import uz.pdp.model.user.Customer;
import uz.pdp.service.AuthServiceImpl;
import uz.pdp.service.admin.service.ClothCRUD;
import uz.pdp.service.admin.service.ColorCRUD;
import uz.pdp.service.admin.service.PayTypeCRUD;
import uz.pdp.service.admin.service.StoreItemsRUD;
import uz.pdp.service.order.OrderHistoryImpl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static uz.pdp.library.SessionMessage.FAILED;
import static uz.pdp.library.SessionMessage.SUCCESS;
import static uz.pdp.library.Util.RED;
import static uz.pdp.library.Util.print;

public class DataBase {

    public static String userFile = "src/main/resources/user/user.json";

    public static List<User> userList = new ArrayList<>();


    public static List<PayType> payTypeList = new ArrayList<>(Arrays.asList(new PayType("CLICK", 0.5), new PayType("PAYME", 1)));
    public static List<Cloth> clothList = new ArrayList<>();
    public static List<Color> colorList = new ArrayList<>(Arrays.asList(new Color("BLACK"), new Color("WHITE")));
    public static List<StoreItem> storeItemList = new ArrayList<>();
    public static List<OrderItem> tempOrderItems = new ArrayList<>();
    public static List<OrderHistory> orderHistoryList = new ArrayList<>();


    static PayTypeCRUD payTypeCRUD = new PayTypeCRUD();
    static ColorCRUD colorCRUD = new ColorCRUD();
    static ClothCRUD clothCRUD = new ClothCRUD();
    static AuthServiceImpl authService = new AuthServiceImpl();
    static StoreItemsRUD storeItemsRUD = new StoreItemsRUD();
    static OrderHistoryImpl orderHistory = new OrderHistoryImpl();

    public void migration() {

        if (orderHistory() && userData() && paymentData() && colorData() && getClothData() && stackData())
            print(RED, "RUNNING DATABASE.." + SUCCESS);
        else print(RED, "RUNNING DATABASE.." + FAILED);
    }

    private boolean orderHistory() {
        File order = new File("src/main/resources/order/orderhistory.json");
        if (order.length() == 0) {
            orderHistory.writeJson();
            return true;
        } else {
            try (Reader reader = new FileReader(order)) {
                Gson gson = new Gson();
                List<OrderHistory> orderType = gson.fromJson(reader, new TypeToken<List<OrderHistory>>() {
                }.getType());
                orderHistoryList.removeAll(orderHistoryList);
                orderHistoryList.addAll(orderType);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    private boolean userData() {
        File user = new File("src/main/resources/user/user.json");
        if (user.length() == 0) {
            authService.writeJson();
            return true;
        } else {
            try (Reader reader = new FileReader(user)) {
                Gson gson = new Gson();
                List<User> userType = gson.fromJson(reader, new TypeToken<List<Customer>>() {
                }.getType());
                userList.removeAll(userList);
                userList.addAll(userType);
                return true;
            } catch (IOException e) {
                return true;
            }
        }
    }

    private boolean stackData() {
        File stackData = new File("src/main/resources/order/storeItems.json");
        if (stackData.length() == 0) {
            storeItemsRUD.writeJson();
            return true;
        } else {
            try (Reader reader = new FileReader(stackData)) {
                Gson gson = new Gson();
                List<StoreItem> storeItemTypes = gson.fromJson(reader, new TypeToken<List<StoreItem>>() {
                }.getType());
                storeItemList.removeAll(storeItemList);
                storeItemList.addAll(storeItemTypes);
                return true;
            } catch (IOException e) {
                return true;
            }
        }
    }

    private boolean getClothData() {
        File clothData = new File("src/main/resources/cloth/cloth.json");
        if (clothData.length() == 0) {
            clothCRUD.writeJson();
            return true;
        } else {
            try (Reader reader = new FileReader(clothData)) {
                Gson gson = new Gson();
                List<Cloth> clothTypes = gson.fromJson(reader, new TypeToken<List<Cloth>>() {
                }.getType());
                clothList.removeAll(clothList);
                clothList.addAll(clothTypes);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    private boolean colorData() {
        File color = new File("src/main/resources/cloth/color.json");
        if (color.length() == 0) {
            colorCRUD.writeJson();
            return true;
        } else {
            try (Reader reader = new FileReader(color)) {
                Gson gson = new Gson();
                List<Color> colorTypes = gson.fromJson(reader, new TypeToken<List<Color>>() {
                }.getType());
                colorList.removeAll(colorList);
                colorList.addAll(colorTypes);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    private boolean paymentData() {
        File payment = new File("src/main/resources/payment.json");
        if (payment.length() == 0) {
            payTypeCRUD.writeJson();
            return true;
        } else {
            try (Reader reader = new FileReader(payment)) {
                Gson gson = new Gson();
                List<PayType> payTypes = gson.fromJson(reader, new TypeToken<List<PayType>>() {
                }.getType());
                payTypeList.removeAll(payTypeList);
                payTypeList.addAll(payTypes);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }


}
