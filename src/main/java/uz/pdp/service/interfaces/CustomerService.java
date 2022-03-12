package uz.pdp.service.interfaces;

import uz.pdp.model.abtract.User;

public interface CustomerService {
    void customerMenu(User user);

    void fillBalance(User user);
}
