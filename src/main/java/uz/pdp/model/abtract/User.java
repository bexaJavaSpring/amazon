package uz.pdp.model.abtract;

import lombok.*;
import uz.pdp.model.enums.Role;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString


public abstract class User extends AbsEntity {

    private String fullName;
    private String username;
    private String botUserName;
    private String password;
    private double balance;
    private Role role = Role.CUSTOMER;
    private int lastRound;
    private int currentRound;
    private Long userId;
    private String verifyCode;
    private boolean isVerified;
    private boolean cancelPressed;

    public User(String fullName, String username, String password, double balance, Role role, int lastRound, int currentRound, Long userId, String verifyCode, boolean isVerified) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.role = role;
        this.lastRound = lastRound;
        this.currentRound = currentRound;
        this.userId = userId;
        this.verifyCode = verifyCode;
        this.isVerified = isVerified;
    }
}
