package uz.pdp.model.products;

import lombok.*;
import uz.pdp.model.abtract.AbsEntity;
import uz.pdp.model.products.enums.Size;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString

public class Cloth extends AbsEntity {
    private String name;
    private Color color;
    private Size size;
    private double price;
    private double discount;

}
