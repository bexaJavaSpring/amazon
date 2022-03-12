package uz.pdp.bot.service;



import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.bot.config.BotConfig;
import uz.pdp.bot.database.JsonConfig;
import uz.pdp.bot.helper.ClothMethod;
import uz.pdp.bot.helper.DocGenerator;
import uz.pdp.bot.helper.HelperMethod;
import uz.pdp.model.abtract.User;
import uz.pdp.model.order.OrderHistory;
import uz.pdp.model.order.OrderItem;
import uz.pdp.model.payment.PayType;
import uz.pdp.model.products.Cloth;
import uz.pdp.model.user.Customer;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static uz.pdp.DataBase.*;

public class AmazonBot extends TelegramLongPollingBot implements BotConfig {

    static HelperMethod helperMethod = new HelperMethod();
    static CustomerButton userButtons = new CustomerButton();
    static ClothMethod clothMethod = new ClothMethod();
    static DocGenerator docGenerator = new DocGenerator();
    public static int countQty = 1;

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasMessage()) {


            User currentUser = helperMethod.findUser(update);
            Long chatId = update.getMessage().getChatId();

            Message message = update.getMessage();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());

            if (update.hasPreCheckoutQuery()) {
                PreCheckoutQuery preCheckoutQuery = new PreCheckoutQuery();
                preCheckoutQuery.setCurrency("UZS");
                preCheckoutQuery.setInvoicePayload("65");
                preCheckoutQuery.setTotalAmount(1081499);

            } else {

                hasMessageMethod(currentUser, chatId, message, sendMessage);
            }


            sendMessage.setChatId(chatId.toString());

            try {

                execute(sendMessage);

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {
            callBackMsg(update);
        }


    }

    private void hasMessageMethod(User currentUser, Long chatId, Message message, SendMessage sendMessage) {
        switch (currentUser.getCurrentRound()) {
            case 0:
                if (message.getText().equalsIgnoreCase("/start")) {
                    sendMessage.setText("WELCOME ONLINE AMAZON SHOP BOT");
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRound(currentUser, 0, 1);
                }
                break;
            case 1:
                if (message.hasContact()) {
                    currentUser = helperMethod.changeUserData(currentUser, message.getContact());
                    verifyCode(currentUser, sendMessage);
                } else if (currentUser.isVerified() || currentUser.getVerifyCode().equalsIgnoreCase(message.getText())) {
                    sendMessage.setText("Menu");
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRound(currentUser, 1, 2);
                    currentUser = helperMethod.changeRoundVerify(currentUser);
                } else {
                    sendMessage.setText("Confirmation Failed");
                    currentUser = helperMethod.changeRound(currentUser, 1, 10);
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                }
                break;
            case 2: {
                Customer customer = (Customer) currentUser;
                if (message.getText().equalsIgnoreCase("\uD83D\uDC55 Cloth List")) {
                    StringBuilder clothes = helperMethod.clothList();
                    sendMessage.setText("Choose Clothes\n" + clothes);
                    sendMessage.setParseMode(ParseMode.HTML);
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRound(currentUser, 2, 3);

                } else if (message.getText().equalsIgnoreCase("\uD83D\uDED2 My Cart (" + customer.getMyCart().size() + ")")) {

                    StringBuilder clothLists = new StringBuilder();
                    double total = 0;
                    if (!customer.getMyCart().isEmpty()) {
                        for (OrderItem orderItem : customer.getMyCart()) {
                            clothLists.append("" + "<b>Name     : </b>" + orderItem.getCloth().getName() + "\n" + "<b>Quantity : </b>" + orderItem.getQuantity() + "\n");
                            total += orderItem.getQuantity() * orderItem.getCloth().getPrice();
                        }

                    }
                    sendMessage.setText("My cart \n" + clothLists + "\n" + "<b>Total : " + total + "</b>");
                    sendMessage.setParseMode(ParseMode.HTML);
                    currentUser = helperMethod.changeRound(currentUser, 2, 1);
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRound(currentUser, 2, 2);
                } else if (message.getText().equalsIgnoreCase("\uD83D\uDCB3 Chekout")) {
                    sendMessage.setText("Choose option");
                    SendInvoice sendInvoice = new SendInvoice();
                    sendInvoice.setChatId(chatId.toString());
                    sendInvoice.setTitle("CLICK PAYMENT");
                    sendInvoice.setDescription("CLICK");
                    sendInvoice.setPayload("65");
                    List<LabeledPrice> labeledPrices = new ArrayList<>();
                    LabeledPrice labeledPrice = new LabeledPrice();
                    labeledPrice.setLabel("UZS");
                    labeledPrice.setAmount(1081499);
                    labeledPrices.add(labeledPrice);
                    sendInvoice.setProviderToken(PAYMENT_PROVIDER);
                    sendInvoice.setCurrency("UZS");
                    sendInvoice.setMaxTipAmount(500000);
                    sendInvoice.setPrices(labeledPrices);
                    sendInvoice.setStartParameter("Pay");
                    //  sendInvoice.setProviderData("Provider Data");
                    sendInvoice.setPhotoUrl("https://web-telegram.net/telegramm/uploads/posts/2017-05/1496256123_photo_2017-05-31_23-40-00.jpg");
                    sendInvoice.setPhotoHeight(80);
                    sendInvoice.setPhotoWidth(80);
                    sendInvoice.setNeedName(true);
                    sendInvoice.setNeedPhoneNumber(true);
                    sendInvoice.setNeedShippingAddress(true);
                    sendInvoice.setNeedEmail(true);
                    sendInvoice.setSendPhoneNumberToProvider(true);
                    sendInvoice.setDisableNotification(false);

                    try {
                        execute(sendInvoice);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    currentUser = helperMethod.changeRound(currentUser, 2, 5);
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                } else if (message.getText().equalsIgnoreCase("\uD83D\uDCD1 Order History")) {
                    sendMessage.setText("Excel");
                    SendDocument sendDocument = new SendDocument();
                    sendDocument.setChatId(chatId.toString());
                    File file = docGenerator.historyXls((Customer) currentUser);
                    InputFile inputFile = new InputFile(file);
                    sendDocument.setDocument(inputFile);
                    sendDocument.setCaption("History");
                    currentUser = helperMethod.changeRound(currentUser, 2, 1);
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRound(currentUser, 1, 2);
                    try {
                        execute(sendDocument);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else if (message.getText().equalsIgnoreCase("\uD83D\uDCB5 Fill Balance")) {
                    sendMessage.setText("Under Construction , Currently is not available");
                    currentUser = helperMethod.changeRound(currentUser, 2, 1);
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRound(currentUser, 1, 2);
                }
            }
            break;
            case 5: {
                Customer customer = (Customer) currentUser;
                if (message.getText().equalsIgnoreCase("\uD83D\uDED2 Buy All")) {
                    StringBuilder clothLists = new StringBuilder();
                    double total = 0;
                    if (!customer.getMyCart().isEmpty()) {
                        for (OrderItem orderItem : customer.getMyCart()) {
                            clothLists.append("" + "<b>Name     : </b>" + orderItem.getCloth().getName() + "\n" + "<b>Quantity : </b>" + orderItem.getQuantity() + "\n");
                            total += orderItem.getQuantity() * orderItem.getCloth().getPrice();
                        }

                    }
                    sendMessage.setText("My cart \n" + clothLists + "\n" + "<b>Total : " + total + "</b>" + "\n\n" + "<b>Choose payment Method</b>\n");
                    sendMessage.setParseMode(ParseMode.HTML);
                    currentUser = helperMethod.changeRound(currentUser, 2, 4);
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                } else if (message.getText().equalsIgnoreCase("⛔️Remove Item")) {
                    StringBuilder clothes = helperMethod.myCart(customer);
                    sendMessage.setText("Select Cloth\n" + clothes);
                    sendMessage.setParseMode(ParseMode.HTML);
                    currentUser = helperMethod.changeRound(currentUser, 5, 7);
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                } else if (message.getText().equalsIgnoreCase("⏏️Menu")) {
                    sendMessage.setText("Menu");
                    currentUser = helperMethod.changeRound(currentUser, 5, 1);
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRound(currentUser, 1, 2);
                }
            }
            break;
            case 7: {
                if (message.getText().equalsIgnoreCase("Menu")) {
                    sendMessage.setText("Menu");
                    currentUser = helperMethod.changeRound(currentUser, 7, 1);
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRound(currentUser, 1, 2);
                }
            }
            break;
            case 10: {
                if (message.getText().equalsIgnoreCase("Resend Code")) {
                    verifyCode(currentUser, sendMessage);
                    currentUser = helperMethod.changeRound(currentUser, 10, 1);
                }
            }
            break;
            default:
                sendMessage.setText("SERVER ERROR");
        }
    }

    private void callBackMsg(Update update) {
        User currentUser = helperMethod.findUserCallBack(update);
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();

        SendMessage sendMessage = new SendMessage();
        EditMessageText new_message = new EditMessageText();

        sendMessage.setChatId(chatId.toString());
        new_message.setChatId(chatId.toString());

        String data = callbackQuery.getData();
        boolean isCloth = false;

        switch (currentUser.getCurrentRound()) {
            case 7: {
                if (data.equalsIgnoreCase("Menu")) {
                    sendMessage.setText("Menu");
                    currentUser = helperMethod.changeRound(currentUser, 7, 1);
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRound(currentUser, 1, 2);
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    return;
                } else {
                    StringBuilder myCart = clothMethod.getOrderItem(currentUser, data);
                    currentUser = helperMethod.changeRound(currentUser, 7, 8);
                    new_message.setMessageId(callbackQuery.getMessage().getMessageId());
                    new_message.setText("" + myCart);
                    new_message.setParseMode(ParseMode.HTML);
                    new_message.setReplyMarkup((InlineKeyboardMarkup) userButtons.getreplyKeyboard(currentUser, data));
                    try {
                        execute(new_message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case 8: {
                if (data.equalsIgnoreCase("Menu")) {
                    sendMessage.setText("Menu");
                    currentUser = helperMethod.changeRound(currentUser, 7, 1);
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRound(currentUser, 1, 2);
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    return;
                } else if (data.equalsIgnoreCase("Remove")) {

                } else {

                }
                break;
            }
        }


        if (data.equalsIgnoreCase("Cancel")) {
            currentUser = helperMethod.setPress(currentUser, true);
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());
            deleteMessage.setChatId(chatId.toString());
            try {
                execute(deleteMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            System.out.println("CANCEL");
//            StringBuilder clothes = helperMethod.clothList();
//            sendMessage.setText("Canceled \n" + clothes);
//            sendMessage.setParseMode(ParseMode.HTML);
            currentUser = helperMethod.changeRound(currentUser, 3, 2);
//            sendMessage.setReplyMarkup((InlineKeyboardMarkup) userButtons.getreplyKeyboard(currentUser));
//            try {
//                execute(sendMessage);
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
            currentUser = helperMethod.setPress(currentUser, false);
            return;

        } else if (data.equalsIgnoreCase("Add To Cart")) {
            currentUser = helperMethod.setPress(currentUser, false);
            User finalCurrentUser = currentUser;
            AtomicBoolean last = new AtomicBoolean(false);
            Thread thread = new Thread(() -> {
                for (int i = 5; i > 0 && !finalCurrentUser.isCancelPressed(); i--) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setText("\n\nAdded To Cart  <b>" + (i) + "</b> second");
                    editMessageText.setChatId(chatId.toString());
                    editMessageText.setParseMode(ParseMode.HTML);
                    editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
                    helperMethod.changeRound(finalCurrentUser, 3, 6);
                    editMessageText.setReplyMarkup((InlineKeyboardMarkup) userButtons.getreplyKeyboard(finalCurrentUser));
                    if (i == 1) {
                        last.set(true);
                    }
                    try {
                        execute(editMessageText);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                if (last.get()) {
                    DeleteMessage deleteMessage = new DeleteMessage();
                    deleteMessage.setChatId(chatId.toString());
                    deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());
                    try {
                        execute(deleteMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                if (!finalCurrentUser.isCancelPressed()) {
//                    StringBuilder clothes = helperMethod.clothList();
//                    new_message.setText("Added \n" + clothes);
//                    new_message.setParseMode(ParseMode.HTML);
//                    new_message.setMessageId(callbackQuery.getMessage().getMessageId());
                    // helperMethod.changeRound(finalCurrentUser, 3, 3);
                    clothMethod.addToCard(finalCurrentUser);
                    // new_message.setReplyMarkup((InlineKeyboardMarkup) userButtons.getreplyKeyboard(finalCurrentUser));

//                    try {
//                        execute(new_message);
//                    } catch (TelegramApiException e) {
//                        e.printStackTrace();
//                    }
                }
            });
            thread.start();

        }

        Customer customer = (Customer) currentUser;
        if (!data.equalsIgnoreCase("Menu") && currentUser.getCurrentRound() == 4) {

            if (customer.getMyCart().size() > 0) {
                for (PayType payType : payTypeList) {
                    String id = "" + payType.getId();
                    if (id.equalsIgnoreCase(data)) {
                        SendDocument sendDocument = new SendDocument();

                        double sum = customer.getMyCart().stream().mapToDouble(orderItem -> orderItem.getCloth().getPrice() * orderItem.getQuantity()).sum();
                        OrderHistory orderHistory = new OrderHistory(customer, customer.getMyCart(), sum, (sum + sum * payType.getCommissionFee() / 100), LocalDateTime.now(), payType);
                        orderHistoryList.add(orderHistory);
                        JsonConfig<OrderHistory> orderHistoryJsonConfig = new JsonConfig<>();
                        orderHistoryJsonConfig.writeJson("src/main/resources/order/orderhistory.json", orderHistoryList);
                        File file = docGenerator.checkPdf(customer, payType);
                        InputFile inputFile = new InputFile(file);
                        sendDocument.setDocument(inputFile);
                        sendDocument.setCaption("Check");
                        sendDocument.setChatId(chatId.toString());
                        currentUser = helperMethod.changeRound(currentUser, 4, 1);
                        sendMessage.setText("Menu");
                        currentUser = clothMethod.clearCard(currentUser);
                        sendDocument.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                        currentUser = helperMethod.changeRound(currentUser, 1, 2);
                        try {
                            execute(sendDocument);
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            } else {
                sendMessage.setText("Empty Cart");
                currentUser = helperMethod.changeRound(currentUser, 4, 1);
                sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                currentUser = helperMethod.changeRound(currentUser, 1, 2);
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

        } else if (customer.getCurrentRound() < 7 && !data.equalsIgnoreCase("Add To Cart")) {

            StringBuilder clothData = new StringBuilder();

            for (Cloth cloth : clothList) {
                String id = "" + cloth.getId();
                if (id.equalsIgnoreCase(data)) {
                    clothData.append(clothMethod.getCloth(cloth));
                    currentUser = helperMethod.changeRound(currentUser, 2, 3);
                    currentUser = helperMethod.setTempCart(currentUser, cloth);
                    isCloth = true;
                    break;
                }
            }

            sendMessage.setText("" + clothData);
            if (clothData.length() > 0) sendMessage.setParseMode(ParseMode.HTML);
            sendMessage.setReplyMarkup((InlineKeyboardMarkup) userButtons.getreplyKeyboard(currentUser));

            if (data.equalsIgnoreCase("+")) {
                countQty += 1;
                StringBuilder clothes = new StringBuilder();
                clothes.append(clothMethod.getCloth(customer.getOrderItem().getCloth()));
                new_message.setText("" + clothes);
                new_message.setParseMode(ParseMode.HTML);
                new_message.setMessageId(callbackQuery.getMessage().getMessageId());
                new_message.setReplyMarkup((InlineKeyboardMarkup) userButtons.getreplyKeyboard(currentUser));
                isCloth = true;
            } else if (data.equalsIgnoreCase("-")) {
                if (countQty > 1) {
                    countQty -= 1;
                    StringBuilder clothes = new StringBuilder();
                    clothes.append(clothMethod.getCloth(customer.getOrderItem().getCloth()));
                    new_message.setText("" + clothes);
                    new_message.setParseMode(ParseMode.HTML);
                    new_message.setMessageId(callbackQuery.getMessage().getMessageId());
                    new_message.setReplyMarkup((InlineKeyboardMarkup) userButtons.getreplyKeyboard(currentUser));
                    isCloth = true;
                } else {
                    StringBuilder clothes = new StringBuilder();
                    clothes.append(clothMethod.getCloth(customer.getOrderItem().getCloth()));
                    new_message.setText("No changes\n" + clothes);
                    new_message.setParseMode(ParseMode.HTML);
                    new_message.setMessageId(callbackQuery.getMessage().getMessageId());
                    new_message.setReplyMarkup((InlineKeyboardMarkup) userButtons.getreplyKeyboard(currentUser));
                    isCloth = true;
                }
            }

            if (!isCloth && data.equalsIgnoreCase("Menu")) {
                currentUser = helperMethod.changeRound(currentUser, 3, 1);
                sendMessage.setText("Menu");
                sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                currentUser = helperMethod.changeRound(currentUser, 1, 2);
            }


            try {
                if (isCloth && new_message.getText() != null) {
                    execute(new_message);
                } else if (sendMessage.getText() != null) {
                    execute(sendMessage);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void verifyCode(User currentUser, SendMessage sendMessage) {
        int code = (int) (Math.random() * (9999 - 1000 + 1) + 1000);
        currentUser = helperMethod.addCode(currentUser, code);
        sendMessage.setText("Verification code <b>" + code + "</b>\nEnter confirmation code");
        sendMessage.setParseMode(ParseMode.HTML);
    }
}
