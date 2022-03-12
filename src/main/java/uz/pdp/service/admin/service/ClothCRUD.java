package uz.pdp.service.admin.service;

import com.google.gson.Gson;
import uz.pdp.library.CrudRepository;
import uz.pdp.model.order.StoreItem;
import uz.pdp.model.products.Cloth;
import uz.pdp.model.products.Color;
import uz.pdp.model.products.enums.Size;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import static uz.pdp.DataBase.*;
import static uz.pdp.library.SessionMessage.*;
import static uz.pdp.library.Util.*;

public class ClothCRUD implements CrudRepository<Cloth> {
    static ColorCRUD colorCRUD = new ColorCRUD();
    static StoreItemsRUD storeItemsRUD = new StoreItemsRUD();
    @Override
    public void create() {
        if (!colorList.isEmpty()) {
            print(GREEN, "Enter cloth name");
            String clothName = inputStr();
            Cloth cloth = filter(clothName);
            if (cloth != null) {
                Color color = colorCRUD.findById();
                try {
                    print(WHITE, color.toString());
                    size();
                    print(GREEN, "Enter cloth size");
                    String size = inputStr();
                    try {
                        Size size1 = Size.valueOf(size.toUpperCase());
                        Cloth clothData = filter(cloth, color, size1);
                        if (clothData != null) {
                            StoreItem storeItemData = storeItemList.stream().filter(storeItem -> storeItem.getCloth().getId() == clothData.getId()).findFirst().orElse(null);
                            storeItemData.setQuantity(storeItemData.getQuantity() + 1);
                            print(CYAN, SUCCESS);
                            storeItemsRUD.writeJson();
                        } else {
                            print(GREEN, "Enter cloth price");
                            double price = inputDouble();
                            print(GREEN, "Enter cloth discount");
                            double discount = inputDouble();
                            Cloth clothNew = new Cloth(clothName, color, Size.valueOf(size.toUpperCase()), price, discount);
                            clothList.add(clothNew);
                            StoreItem storeItem = new StoreItem(clothNew, 1);
                            storeItemList.add(storeItem);
                            storeItemsRUD.writeJson();
                            writeJson();
                            print(CYAN, CREATED);
                            return;
                        }
                    } catch (IllegalArgumentException e) {
                        print(RED, NOT_FOUND + " SIZE");
                    }

                } catch (NullPointerException e) {
                    print(RED, NOT_FOUND);
                }
            } else {
                Color color = colorCRUD.findById();
                try {
                    print(WHITE, color.toString());
                    size();
                    print(GREEN, "Enter cloth size");
                    String size = inputStr();
                    try {
                        Size.valueOf(size.toUpperCase());
                        print(GREEN, "Enter cloth price");
                        double price = inputDouble();
                        print(GREEN, "Enter cloth discount");
                        double discount = inputDouble();
                        Cloth clothNew = new Cloth(clothName, color, Size.valueOf(size.toUpperCase()), price, discount);
                        clothList.add(clothNew);
                        StoreItem storeItem = new StoreItem(clothNew, 1);
                        storeItemList.add(storeItem);
                        storeItemsRUD.writeJson();
                        writeJson();
                        print(CYAN, CREATED);
                        return;
                    } catch (IllegalArgumentException e) {
                        print(RED, NOT_FOUND);
                    }

                } catch (NullPointerException e) {
                    print(RED, NOT_FOUND);
                }
            }
        } else {
            print(RED, "NO COLOR YET");
        }
    }


    @Override
    public void read() {
        print(GREEN_BOLD, "CLOTH LIST");

        print(BLUE_BOLD, "|---|---------------------|");
        clothList.forEach(cloth -> {
            print(BLUE_BOLD, "NAME : " + cloth.getName() +
                    " , SIZE : " + cloth.getSize() +
                    " , PRICE : " + cloth.getPrice() +
                    ", DISCOUNT : " + (cloth.getPrice() - cloth.getPrice() * cloth.getDiscount() / 100)
            );
        });
        print(BLUE_BOLD, "|---|---------------------|");
    }

    @Override
    public void update() {
        // TODO: 12/6/21
    }

    @Override
    public void delete() {
        try {
            if (clothList.remove(findById())) {
                print(RED, DELETED);
                writeJson();
            } else print(RED_BACKGROUND, ERROR);
        } catch (NullPointerException e) {
            print(CYAN, NOT_FOUND);
        }
    }

    @Override
    public Cloth findById() {
        read();
        print(CYAN, "Enter cloth name");
        String cloth = inputStr();
        return filter(cloth);
    }

    @Override
    public Cloth filter(String cloth) {
        return clothList.stream()
                .filter(clothName -> clothName.getName().equalsIgnoreCase(cloth))
                .findFirst()
                .orElse(null);
    }

    private Cloth filter(Cloth clothName, Color color, Size valueOf) {
        return clothList.stream().filter(cloth -> cloth.getName().equals(clothName.getName()) && cloth.getColor().equals(color) && cloth.getSize().equals(valueOf)).findFirst().orElse(null);
    }

    @Override
    public void writeJson() {
        try (Writer writer = new FileWriter("src/main/resources/cloth/cloth.json")) {
            Gson gson = new Gson();
            String s = gson.toJson(clothList);
            writer.write(s);
            print(BLUE, WRITED);
        } catch (IOException e) {
            print(RED, NOT_FOUND);
        }
    }


    public void size() {
        Arrays.stream(Size.values()).forEach(s -> print(CYAN, String.valueOf(s)));
    }

    @Override
    public void crudMenu() {
        print(GREEN + "1=>ADD CLOTH. 2=>EDIT CLOTH. 3=>DELETE CLOTH. 4=>LIST CLOTH. 0=>BACK");
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
