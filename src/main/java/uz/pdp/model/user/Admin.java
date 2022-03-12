package uz.pdp.model.user;

import lombok.*;
import uz.pdp.model.abtract.User;
import uz.pdp.model.enums.Role;

@ToString


public class Admin extends User {
    public Admin(String fullName, String username, String password, double balance,int lastRound,int currentRound,Long userId,String verifyCode,boolean isVerified) {
        super(fullName, username, password, balance, Role.SUPER_ADMIN,lastRound,currentRound,userId,verifyCode,isVerified);
    }
}
