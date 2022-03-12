package uz.pdp.service.admin.service;

import com.google.gson.Gson;
import uz.pdp.library.CrudRepository;
import uz.pdp.library.Util;
import uz.pdp.model.order.StoreItem;
import uz.pdp.model.products.Cloth;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static uz.pdp.DataBase.clothList;
import static uz.pdp.DataBase.storeItemList;
import static uz.pdp.library.SessionMessage.*;
import static uz.pdp.library.Util.*;

public class StoreItemsRUD implements CrudRepository<StoreItem> {

    static ClothCRUD clothCRUD = new ClothCRUD();

    @Override
    public void create() {
    }

    @Override
    public void read() {
        storeItemList.forEach(storeItem -> {
            print(CYAN, "NAME : " + storeItem.getCloth().getName() + ", COLOR : " + storeItem.getCloth().getColor() + ", SIZE : " + storeItem.getCloth().getSize() + ", QUANTITY : " + storeItem.getQuantity());
        });
    }

    @Override
    public void update() {
        StoreItem storeItem = findById();
        if (storeItem != null) {
            print(CYAN, "Add quantity");
            int quantity = Util.inputInt();
            if (quantity > 0) {
                storeItem.setQuantity(storeItem.getQuantity() + quantity);
                print(GREEN, UPDATED);
                writeJson();
            } else {
                print(RED, ERROR);
            }
        }
    }

    @Override
    public void delete() {
        Cloth cloth = clothCRUD.findById();
        if (cloth != null) {
            StoreItem storeItemDel = storeItemList.stream().filter(storeItem -> storeItem.getCloth().getId() == cloth.getId()).findFirst().orElse(null);
            storeItemList.remove(storeItemDel);
            clothList.remove(cloth);
            clothCRUD.writeJson();
            writeJson();
            print(RED, DELETED);
        } else {
            print(RED, NOT_FOUND);
        }
    }

    @Override
    public StoreItem findById() {
        read();
        print(CYAN, "Enter cloth name");
        String clothName = inputStr();
        return filter(clothName);
    }

    @Override
    public StoreItem filter(String object) {
        return storeItemList.stream().filter(storeItem -> storeItem.getCloth().getName().equalsIgnoreCase(object)).findFirst().orElse(null);
    }

    @Override
    public void writeJson() {
        try (Writer writer = new FileWriter("src/main/resources/order/storeItems.json")) {
            Gson gson = new Gson();
            String s = gson.toJson(storeItemList);
            writer.write(s);
            print(BLUE, WRITED);
        } catch (IOException e) {
            print(RED, NOT_FOUND);
        }
    }

    @Override
    public void crudMenu() {
        print(GREEN + "1=>EDIT STORE. 2=>DELETE IN STORE. 3=>STORE LIST. 0=>BACK");
        int option = inputInt();
        switch (option) {
            case 1:
                update();
                break;
            case 2:
                delete();
                break;
            case 3:
                read();
                break;
            case 0:
                return;
        }
        crudMenu();
    }
}
