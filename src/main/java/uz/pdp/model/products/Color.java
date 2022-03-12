package uz.pdp.model.products;

import lombok.*;
import uz.pdp.model.abtract.AbsEntity;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Color extends AbsEntity {
    private String name;
}
