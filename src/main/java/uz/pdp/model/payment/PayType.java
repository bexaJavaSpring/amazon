package uz.pdp.model.payment;


import lombok.*;
import uz.pdp.model.abtract.AbsEntity;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString

public class PayType extends AbsEntity {
    private String name;
    private double commissionFee;
}
