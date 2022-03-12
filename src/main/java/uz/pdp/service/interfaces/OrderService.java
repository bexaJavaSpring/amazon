package uz.pdp.service.interfaces;

import uz.pdp.model.abtract.User;
import uz.pdp.model.user.Customer;

public interface OrderService {

    void addToCart(Customer user);

    void checkOut(Customer user);

    void cart(Customer user);

    void buyItems(Customer user);

    void orderMenu(Customer user);

}
