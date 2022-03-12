package uz.pdp;

import uz.pdp.model.products.Cloth;
import uz.pdp.service.Amazon;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uz.pdp.DataBase.clothList;
import static uz.pdp.library.Util.*;


public class Main {

    static DataBase dataBase = new DataBase();
    static Amazon amazon = new Amazon();
    static uz.pdp.bot.Main main = new uz.pdp.bot.Main();

    public static void main(String[] args) throws IOException {
        dataBase.migration();
        main.botStart();
        amazon.amazonApp();

    }
}
