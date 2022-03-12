package uz.pdp.model.user;

import lombok.Data;
import lombok.ToString;
import uz.pdp.model.abtract.User;
import uz.pdp.model.enums.Role;
import uz.pdp.model.order.OrderItem;
import uz.pdp.model.products.Cloth;

import java.util.ArrayList;
import java.util.List;


@Data
@ToString

public class Customer extends User { ;
    private boolean isActive = true;
    private List<OrderItem> myCart = new ArrayList<>();
    private OrderItem orderItem;

    private List<OrderItem> wishList = new ArrayList<>();

    public Customer(String fullName, String username, String password, double balance,int lastRound,int currentRound,Long userId,String verifyCode,boolean isVerified) {
        super(fullName, username, password, balance, Role.CUSTOMER,lastRound,currentRound,userId,verifyCode,isVerified);
    }



    public Customer() {

    }


    public void setCart(OrderItem myCart) {
        this.myCart.add(myCart);
    }

    public void clear() {
        this.myCart.clear();
    }

    public Customer(String fullName, String username, String password, double balance,int lastRound,int currentRound,Long userId,String verifyCode,boolean isVerified, boolean isActive, List<OrderItem> myCart) {
        super(fullName, username, password, balance, Role.CUSTOMER,lastRound,currentRound,userId,verifyCode,isVerified);
        this.isActive = isActive;
        this.myCart = myCart;
    }

    public Customer(boolean isActive, List<OrderItem> myCart) {
        this.isActive = isActive;
        this.myCart = myCart;
    }

    public Customer(String fullName, String username, String password, double balance,int lastRound,int currentRound,Long userId,String verifyCode,boolean isVerified, boolean isActive) {
        super(fullName, username, password, balance, Role.CUSTOMER,lastRound,currentRound,userId,verifyCode,isVerified);
        this.isActive = isActive;
    }

    public Customer(boolean isActive) {
        this.isActive = isActive;
    }
}
