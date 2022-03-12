package uz.pdp.model.order;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.model.abtract.AbsClothQuantity;
import uz.pdp.model.products.Cloth;


@Data

public class OrderItem extends AbsClothQuantity {
    public OrderItem(Cloth selectedCloth, int i) {
        super(selectedCloth, i);
    }
}
