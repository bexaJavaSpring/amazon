package uz.pdp.bot.database;



import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import uz.pdp.bot.config.JsonService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class JsonConfig<T> implements JsonService<T> {


    @Override
    public void writeJson(String file, List<T> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(file)) {
            String s = gson.toJson(list);
            writer.write(s);
        } catch (IOException e) {
        }
    }
}
