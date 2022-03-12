package uz.pdp.service.interfaces;

import uz.pdp.model.abtract.User;

public interface OrderHistoryService {

    void orderHistory(User user);

    void orderHistoryPayType(User user);

    void convertExcel(User user);

    void writeJson();

    void orderHistoryMenu(User user);

}
