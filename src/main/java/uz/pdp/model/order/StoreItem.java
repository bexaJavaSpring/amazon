package uz.pdp.model.order;

import lombok.EqualsAndHashCode;
import uz.pdp.model.abtract.AbsClothQuantity;
import uz.pdp.model.products.Cloth;

@EqualsAndHashCode(callSuper = true)
public class StoreItem extends AbsClothQuantity {

    public StoreItem(Cloth clothNew, int quantity) {
        super(clothNew, quantity);
    }
}
