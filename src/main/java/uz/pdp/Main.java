package uz.pdp;

import uz.pdp.service.Amazon;

import java.io.IOException;


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
