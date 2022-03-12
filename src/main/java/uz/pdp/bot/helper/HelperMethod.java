package uz.pdp.bot.helper;


import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pdp.bot.database.JsonConfig;
import uz.pdp.model.abtract.User;
import uz.pdp.model.order.OrderItem;
import uz.pdp.model.products.Cloth;
import uz.pdp.model.user.Customer;

import static uz.pdp.DataBase.*;

public class HelperMethod {

    public User findUser(Update update) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        for (User user : userList)
            if (user.getUserId() != null && user.getUserId().longValue() == update.getMessage().getFrom().getId().longValue())
                return user;

        Customer newUser = new Customer();
        newUser.setUserId(update.getMessage().getFrom().getId());
        newUser.setLastRound(0);
        newUser.setCurrentRound(0);

        userList.add(newUser);
        jsonConfig.writeJson(userFile, userList);
        return newUser;

    }

    public User findUserCallBack(Update update) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        for (User user : userList)
            if (user.getUserId() != null && user.getUserId().longValue() == update.getCallbackQuery().getFrom().getId().longValue())
                return user;

        Customer newUser = new Customer();
        newUser.setUserId(update.getCallbackQuery().getFrom().getId());
        newUser.setLastRound(0);
        newUser.setCurrentRound(0);

        userList.add(newUser);

        jsonConfig.writeJson(userFile, userList);
        return newUser;
    }

    public User changeRound(User currentUser, int lastRound, int currentRound) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getUserId() != null && user.getUserId().longValue() == currentUser.getUserId().longValue()) {
                user.setLastRound(lastRound);
                user.setCurrentRound(currentRound);
                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;
    }

    public User changeUserData(User currentUser, Contact contact) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getUserId() != null && user.getUserId().longValue() == currentUser.getUserId().longValue()) {
                String fullName = contact.getFirstName() + " " + contact.getLastName();
                user.setFullName(fullName);
                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;
    }


    public StringBuilder clothList() {
        StringBuilder clothes = new StringBuilder();
        for (Cloth cloth : clothList) {
            if (cloth.getDiscount() > 0) {
                clothes.append("-------------------------------" + "\n<b>Name : </b>" + cloth.getName() + "\n<b>Old price :</b>" + cloth.getPrice() + "\n" + "<b>New Price : </b>" + (cloth.getPrice() - (cloth.getPrice() * cloth.getDiscount() / 100)) + "\n-------------------------------\n");

            } else {
                clothes.append("-------------------------------" + "\n<b>Name  : </b>" + cloth.getName() + "\n<b>Price : </b>" + cloth.getPrice() + "\n" + "-------------------------------\n");
            }

        }
        return clothes;
    }


    public Customer setTempCart(User currentUser, Cloth cloth) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        Customer userFind = null;

        for (User user : userList) {
            if (user.getUserId() != null && user.getUserId().longValue() == currentUser.getUserId().longValue()) {
                Customer customer = (Customer) user;
                OrderItem orderItem = new OrderItem(cloth, 1);
                customer.setOrderItem(orderItem);
                userFind = customer;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;
    }

    public User addCode(User currentUser, int code) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getUserId() != null && user.getUserId().longValue() == currentUser.getUserId().longValue()) {
                user.setVerifyCode("" + code);
                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;
    }

    public User changeRoundVerify(User currentUser) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getUserId() != null && user.getUserId().longValue() == currentUser.getUserId().longValue()) {
                user.setVerified(true);
                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;
    }

    public User setPress(User currentUser, boolean b) {

        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getUserId() != null && user.getUserId().longValue() == currentUser.getUserId().longValue()) {
                user.setCancelPressed(b);
                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;
    }

    public StringBuilder myCart(Customer customer) {
        StringBuilder stringBuilder = new StringBuilder();
        for (OrderItem orderItem : customer.getMyCart()) {
            stringBuilder.append("-----------\n<b>Name : </b>" + orderItem.getCloth().getName() + "\n" + "<b>Quantity : </b>" + orderItem.getQuantity() + "\n-----------");
        }
        return stringBuilder;
    }

    public OrderItem selectOrder(User user, String data) {
        Customer customer = (Customer) user;
        OrderItem orderItem1 = null;
        for (OrderItem orderItem : customer.getMyCart()) {
            String id = "" + orderItem.getId();
            if (id.equalsIgnoreCase(data)) {
                orderItem1 = orderItem;
                break;
            }
        }
        return orderItem1;
    }
}
