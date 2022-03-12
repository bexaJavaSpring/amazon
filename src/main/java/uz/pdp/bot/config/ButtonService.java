package uz.pdp.bot.config;


import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import uz.pdp.model.abtract.User;





public interface ButtonService {
    ReplyKeyboard getreplyKeyboard(User customer);
}
