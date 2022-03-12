package uz.pdp.service.admin.service;

import com.google.gson.Gson;
import uz.pdp.library.CrudRepository;
import uz.pdp.model.payment.PayType;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static uz.pdp.DataBase.payTypeList;
import static uz.pdp.library.SessionMessage.*;
import static uz.pdp.library.Util.*;

public class PayTypeCRUD implements CrudRepository<PayType> {

    @Override
    public void create() {
        print(GREEN, "Enter pay-type name");
        String name = inputStr();
        print(GREEN, "Enter pay-type commission Fee");
        double fee = inputDouble();

        PayType find = filter(name);

        if (find != null) {
            print(RED_BACKGROUND, ALREADY_EXIST);
        } else {
            PayType payType = new PayType(name, fee);
            payTypeList.add(payType);
            writeJson();
            print(CYAN, CREATED);
        }


    }

    @Override
    public void read() {
        print(GREEN_BOLD, "PAYMENT LIST");

        print(BLUE_BOLD, "|---|---------------------|");
        payTypeList
                .forEach(s -> print(GREEN_BOLD, "|---| NAME : " + s.getName().toUpperCase() + ", FEE : " + s.getCommissionFee()));
        print(BLUE_BOLD, "|---|---------------------|");
    }

    @Override
    public void update() {
        try {
            PayType payType = findById();
            print(GREEN, "Enter fee");
            double fee = inputDouble();
            payType.setCommissionFee(fee);
            print(GREEN, UPDATED);
            writeJson();
        } catch (NullPointerException e) {
            print(CYAN, NOT_FOUND);
        }

    }

    @Override
    public void delete() {
        try {
            if (payTypeList.remove(findById())) {
                print(RED, DELETED);
                writeJson();
            } else print(RED_BACKGROUND, ERROR);
        } catch (NullPointerException e) {
            print(CYAN, NOT_FOUND);
        }
    }

    @Override
    public PayType findById() {
        read();
        print(CYAN, "Enter pay-type name");
        String payName = inputStr();
        return filter(payName);
    }

    @Override
    public PayType filter(String payName) {
        return payTypeList.stream().filter(payType -> payType.getName().equalsIgnoreCase(payName)).findFirst().orElse(null);
    }

    @Override
    public void writeJson() {
        try (Writer writer = new FileWriter("src/main/resources/payment.json")) {
            Gson gson = new Gson();
            String s = gson.toJson(payTypeList);
            writer.write(s);
            print(BLUE, WRITED);
        } catch (IOException e) {
            print(RED, NOT_FOUND);
        }
    }


    @Override
    public void crudMenu() {
        print(GREEN + "1=>ADD PAY-TYPE. 2=>EDIT PAY-TYPE. 3=>DELETE PAY-TYPE. 4=>LIST PAY-TYPE. 0=>BACK");
        int option = inputInt();
        switch (option) {
            case 1:
                create();
                break;
            case 2:
                update();
                break;
            case 3:
                delete();
                break;
            case 4:
                read();
                break;
            case 0:
                return;
        }
        crudMenu();
    }

}
