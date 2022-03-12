package uz.pdp.model.abtract;

import lombok.*;
import uz.pdp.model.products.Cloth;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)

public abstract class AbsClothQuantity extends AbsEntity {

    private Cloth cloth;
    private Integer quantity;

}
