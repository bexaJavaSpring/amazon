package uz.pdp.service.admin.service;

import com.google.gson.Gson;
import uz.pdp.library.CrudRepository;
import uz.pdp.model.products.Color;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static uz.pdp.DataBase.colorList;
import static uz.pdp.library.SessionMessage.*;
import static uz.pdp.library.Util.*;

public class ColorCRUD implements CrudRepository<Color> {
    @Override
    public void create() {
        print(GREEN, "Enter color name");
        String colorName = inputStr();
        Color color = filter(colorName);
        if (color != null) {
            print(RED, ERROR);
        } else {
            Color colorNew = new Color(colorName);
            colorList.add(colorNew);
            print(CYAN, CREATED);
            writeJson();
        }
    }

    @Override
    public void read() {
        colorList.forEach(color -> print(CYAN, "NAME : " + color.getName().toUpperCase()));
    }

    @Override
    public void update() {
        try {
            Color color = findById();
            print(WHITE, color.getName());
            print(GREEN, "Enter color name");
            String name = inputStr();
            Color check = filter(name);
            if (check != null) {
                print(RED, ALREADY_EXIST);
            } else {
                color.setName(name);
                print(CYAN, UPDATED);
                writeJson();
            }
        } catch (NullPointerException e) {
            print(CYAN, NOT_FOUND);
        }

    }

    @Override
    public void delete() {
        try {
            if (colorList.remove(findById())) {
                print(RED, DELETED);
                writeJson();
            } else print(RED_BACKGROUND, ERROR);
        } catch (NullPointerException e) {
            print(CYAN, NOT_FOUND);
        }
    }

    @Override
    public Color findById() {
        read();
        print(CYAN, "Enter color name");
        String colorName = inputStr();
        return filter(colorName);
    }

    @Override
    public Color filter(String colorName) {
        return colorList.stream().filter(colorType -> colorType.getName().equalsIgnoreCase(colorName)).findFirst().orElse(null);
    }

    @Override
    public void writeJson() {
        try (Writer writer = new FileWriter("src/main/resources/cloth/color.json")) {
            Gson gson = new Gson();
            String s = gson.toJson(colorList);
            writer.write(s);
            print(BLUE, WRITED);
        } catch (IOException e) {
            print(RED, NOT_FOUND);
        }
    }

    @Override
    public void crudMenu() {
        print(GREEN + "1=>ADD COLOR. 2=>EDIT COLOR. 3=>DELETE COLOR. 4=>LIST COLOR. 0=>BACK");
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
