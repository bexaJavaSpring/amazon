package uz.pdp.bot;



import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.pdp.bot.service.AmazonBot;

public class Main {

    public void botStart() {

        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(new AmazonBot());
            System.out.println("Bot started");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
