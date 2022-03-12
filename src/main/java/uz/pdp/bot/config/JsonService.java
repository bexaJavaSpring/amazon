package uz.pdp.bot.config;



import java.util.List;

public interface JsonService<T> {

    void writeJson(String file, List<T> list);
}
