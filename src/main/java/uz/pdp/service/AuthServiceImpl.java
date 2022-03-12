package uz.pdp.service;

import com.google.gson.Gson;
import uz.pdp.library.AuthRepository;
import uz.pdp.library.Util;
import uz.pdp.model.abtract.User;
import uz.pdp.model.user.Customer;
import uz.pdp.service.admin.AdminServiceImpl;
import uz.pdp.service.customer.CustomerServiceImpl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static uz.pdp.DataBase.userList;
import static uz.pdp.library.SessionMessage.*;
import static uz.pdp.library.Util.*;

public class AuthServiceImpl implements AuthRepository {
    static AdminServiceImpl adminService = new AdminServiceImpl();
    static CustomerServiceImpl customerService = new CustomerServiceImpl();
    static Amazon amazon = new Amazon();

    @Override
    public void login() {

        print(CYAN, "Enter username");
        String username = Util.inputStr();

        print(CYAN, "Enter password");
        String password = Util.inputStr();

        User user = userList.stream()
                .filter(user1 -> user1.getUsername().equals(username) && user1.getPassword().equals(password))
                .findFirst()
                .orElse(null);
        if (user != null) {
            switch (user.getRole()) {
                case SUPER_ADMIN:
                    adminService.adminMenu(user);
                    amazon.amazonApp();
                    break;
                case CUSTOMER:
                    Customer customer = (Customer) user;
                    customerService.customerMenu(customer);
                    amazon.amazonApp();
                    break;
            }
        } else {
            print(RED, INVALID_USERNAME_OR_PASSWORD);
        }

    }

    @Override
    public void register() {

        print(CYAN, "Enter fullName");
        String fullName = Util.inputStr();
        print(CYAN, "Enter username");
        String username = Util.inputStr();
        User user = userList
                .stream()
                .filter(user1 -> user1.getUsername().equalsIgnoreCase(username))
                .findFirst().orElse(null);
        if (user != null) {
            print(RED, ALREADY_EXIST);
            return;
        }
        print(CYAN, "Enter password");
        String password = Util.inputStr();
        print(CYAN, "Enter confirmPassword");
        String confirmPassword = Util.inputStr();
        if (password.equals(confirmPassword)) {
            Customer customer = new Customer();
            customer.setFullName(fullName);
            customer.setPassword(password);
            customer.setBalance(0);

            userList.add(customer);
            writeJson();
            print(CYAN, CREATED);
        } else {
            print(RED, ERROR);
        }

    }


    public void writeJson() {
        try (Writer writer = new FileWriter("src/main/resources/user/user.json")) {
            Gson gson = new Gson();
            String s = gson.toJson(userList);
            writer.write(s);
            print(BLUE, WRITED);
        } catch (IOException e) {
            print(RED, NOT_FOUND);
        }
    }

    @Override
    public void AuthenticatedUsers() {
        userList.forEach(user -> print(GREEN, "NAME : " + user.getFullName() + ", ROLE : " + user.getRole()));
    }
}
