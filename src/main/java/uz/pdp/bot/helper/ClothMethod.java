package uz.pdp.bot.helper;




import uz.pdp.bot.database.JsonConfig;
import uz.pdp.model.abtract.User;
import uz.pdp.model.order.OrderItem;
import uz.pdp.model.products.Cloth;
import uz.pdp.model.user.Customer;

import static uz.pdp.DataBase.userFile;
import static uz.pdp.DataBase.userList;
import static uz.pdp.bot.service.AmazonBot.countQty;
import static uz.pdp.library.SessionMessage.SUCCESS;
import static uz.pdp.library.Util.CYAN;
import static uz.pdp.library.Util.print;

public class ClothMethod {


    public StringBuilder getCloth(Cloth cloth) {
        StringBuilder data = new StringBuilder();
        if (cloth.getDiscount() > 0) {
            data.append("<b>Name : </b>" + cloth.getName() + "\n<b>Old price :</b>" + cloth.getPrice() + "\n" + "<b>New Price : </b>" + (cloth.getPrice() - (cloth.getPrice() * cloth.getDiscount() / 100)));
        } else {
            data.append("<b>Name : </b>" + cloth.getName() + "\n<b>Price :</b>" + cloth.getPrice());
        }

        return data;
    }

    public User addToCard(User currentUser) {

        JsonConfig<User> jsonConfig = new JsonConfig<>();
        Customer userFind = null;

        for (User user : userList) {
            if (user.getUserId() != null && user.getUserId().longValue() == currentUser.getUserId().longValue()) {
                Customer customer = (Customer) user;
                Cloth cloth = customer.getOrderItem().getCloth();
                OrderItem orderItem3 = customer.getMyCart().stream().filter(mycart -> mycart.getCloth().getName().equalsIgnoreCase(cloth.getName())).findFirst().orElse(null);
                if (orderItem3 != null) {
                    orderItem3.setQuantity(orderItem3.getQuantity() + countQty);
                    print(CYAN, SUCCESS);

                } else {
                    OrderItem orderItem = new OrderItem(cloth, countQty);
                    customer.setOrderItem(null);
                    customer.getMyCart().add(orderItem);
                    print(CYAN, SUCCESS);
                }
                userFind = customer;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;
    }

    public User clearCard(User currentUser) {

        JsonConfig<User> jsonConfig = new JsonConfig<>();
        Customer userFind = null;

        for (User user : userList) {
            if (user.getUserId() != null && user.getUserId().longValue() == currentUser.getUserId().longValue()) {
                Customer customer = (Customer) user;
                customer.getMyCart().clear();
                userFind = customer;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;
    }

    public StringBuilder getOrderItem(User currentUser, String clothId) {
        StringBuilder data = new StringBuilder();
        Customer customer = (Customer) currentUser;
        for (OrderItem orderItem : customer.getMyCart()) {
            String id = "" + orderItem.getId();
            if (id.equalsIgnoreCase(clothId)) {
                data.append("<b>Name : </b>" + orderItem.getCloth().getName() + "\n<b>Price :</b>" + orderItem.getQuantity());
                break;
            }
        }

        return data;
    }
}
