package uz.pdp.model.order;

import lombok.*;
import uz.pdp.model.abtract.AbsEntity;
import uz.pdp.model.payment.PayType;
import uz.pdp.model.user.Customer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor

@Setter
@Getter
@ToString

public class OrderHistory extends AbsEntity {

    private Customer customer;
    private List<OrderItem> items = new ArrayList<>();
    private double price;
    private double commissionFeeSum;
    private LocalDateTime date = LocalDateTime.now();
    private PayType payType;
    private String deliveryLocation;

    public OrderHistory(Customer customer, List<OrderItem> items, double price, double commissionFeeSum, LocalDateTime date, PayType payType) {
        this.customer = customer;
        this.items.addAll(items);
        this.price = price;
        this.commissionFeeSum = commissionFeeSum;
        this.date = date;
        this.payType = payType;
    }
}
