package uz.pdp.bot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pdp.bot.config.ButtonService;
import uz.pdp.model.abtract.User;
import uz.pdp.model.order.OrderItem;
import uz.pdp.model.payment.PayType;
import uz.pdp.model.products.Cloth;
import uz.pdp.model.user.Customer;

import java.util.ArrayList;
import java.util.List;

import static uz.pdp.DataBase.clothList;
import static uz.pdp.DataBase.payTypeList;
import static uz.pdp.bot.service.AmazonBot.countQty;
import static uz.pdp.bot.service.AmazonBot.helperMethod;



public class CustomerButton implements ButtonService {

    @Override
    public ReplyKeyboard getreplyKeyboard(User user) {
        Customer customer = (Customer) user;
        //INLINE
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> buttonList = new ArrayList<>();
        inlineButtons.add(buttonList);
        inlineKeyboardMarkup.setKeyboard(inlineButtons);


        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();
        keyboardMarkup.setKeyboard(rowList);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        switch (user.getCurrentRound()) {
            case 0: {
                KeyboardRow row1 = new KeyboardRow();
                KeyboardButton shareButton = new KeyboardButton();
                shareButton.setText("Share contact");
                shareButton.setRequestContact(true);
                row1.add(shareButton);
                rowList.add(row1);
            }
            break;
            case 1: {
                KeyboardRow row1 = new KeyboardRow();
                row1.add("\uD83D\uDC55 Cloth List");
                row1.add("\uD83D\uDED2 My Cart (" + customer.getMyCart().size() + ")");
                KeyboardRow row2 = new KeyboardRow();
                row2.add("\uD83D\uDCB3 Chekout");
                row2.add("\uD83D\uDCD1 Order History");
                KeyboardRow row3 = new KeyboardRow();
                row3.add("\uD83D\uDCB5 Fill Balance");
                rowList.add(row1);
                rowList.add(row2);
                rowList.add(row3);
            }
            break;
            case 2: {
                List<InlineKeyboardButton> inlineRow = new ArrayList<>();
                List<InlineKeyboardButton> inlinePrev = new ArrayList<>();
                for (int i = 0; i < clothList.size(); i++) {
                    Cloth value = clothList.get(i);

                    InlineKeyboardButton buttonN = new InlineKeyboardButton();
                    buttonN.setText(value.getName());
                    buttonN.setCallbackData("" + value.getId());
                    inlineRow.add(buttonN);

                    if (i % 2 == 0) {
                        inlineButtons.add(inlineRow);
                    } else {
                        inlineRow = new ArrayList<>();
                    }
                }
                InlineKeyboardButton buttonH = new InlineKeyboardButton();
                buttonH.setText("⏏️Menu");
                buttonH.setCallbackData("Menu");
                inlinePrev.add(buttonH);

                inlineButtons.add(inlinePrev);
                return inlineKeyboardMarkup;
            }
            case 3: {
                List<InlineKeyboardButton> inlineRow = new ArrayList<>();
                List<InlineKeyboardButton> inlineCart = new ArrayList<>();
                List<InlineKeyboardButton> inlinePrev = new ArrayList<>();

                InlineKeyboardButton buttonE = new InlineKeyboardButton();
                buttonE.setText("-");
                buttonE.setCallbackData("-");

                InlineKeyboardButton buttonS = new InlineKeyboardButton();
                buttonS.setText("" + countQty);
                buttonS.setCallbackData("" + countQty);

                InlineKeyboardButton buttonN = new InlineKeyboardButton();
                buttonN.setText("+");
                buttonN.setCallbackData("+");
                inlineRow.add(buttonE);
                inlineRow.add(buttonS);
                inlineRow.add(buttonN);

                InlineKeyboardButton buttonW = new InlineKeyboardButton();
                buttonW.setText("\uD83D\uDED2 Add To Cart");
                buttonW.setCallbackData("Add To Cart");
                inlineCart.add(buttonW);


                InlineKeyboardButton buttonH = new InlineKeyboardButton();
                buttonH.setText("⏏️Menu");
                buttonH.setCallbackData("Menu");
                inlinePrev.add(buttonH);
                inlineButtons.add(inlineRow);
                inlineButtons.add(inlineCart);
                inlineButtons.add(inlinePrev);
                return inlineKeyboardMarkup;
            }
            case 4: {
                List<InlineKeyboardButton> inlineRow = new ArrayList<>();
                List<InlineKeyboardButton> inlinePrev = new ArrayList<>();
                for (int i = 0; i < payTypeList.size(); i++) {
                    PayType value = payTypeList.get(i);

                    InlineKeyboardButton buttonN = new InlineKeyboardButton();
                    buttonN.setText(value.getName());
                    buttonN.setCallbackData("" + value.getId());
                    inlineRow.add(buttonN);

                    if (i % 2 == 0) {
                        inlineButtons.add(inlineRow);
                    } else {
                        inlineRow = new ArrayList<>();
                    }
                }
                InlineKeyboardButton buttonH = new InlineKeyboardButton();
                buttonH.setText("⏏️Menu");
                buttonH.setCallbackData("Menu");
                inlinePrev.add(buttonH);

                inlineButtons.add(inlinePrev);
                return inlineKeyboardMarkup;
            }
            case 5: {
                KeyboardRow row1 = new KeyboardRow();
                row1.add("\uD83D\uDED2 Buy All");
                row1.add("⛔️Remove Item");
                row1.add("⏏️Menu");
                rowList.add(row1);
            }
            break;
            case 6: {
                List<InlineKeyboardButton> inlinePrev = new ArrayList<>();
                InlineKeyboardButton buttonH = new InlineKeyboardButton();


                buttonH.setText("Cancel");
                buttonH.setCallbackData("Cancel");
                inlinePrev.add(buttonH);

                inlineButtons.add(inlinePrev);
                return inlineKeyboardMarkup;
            }
            case 7: {
                Customer customer1 = (Customer) user;
                List<InlineKeyboardButton> inlineRow = new ArrayList<>();
                List<InlineKeyboardButton> inlinePrev = new ArrayList<>();
                for (int i = 0; i < customer1.getMyCart().size(); i++) {
                    OrderItem value = customer1.getMyCart().get(i);

                    InlineKeyboardButton buttonN = new InlineKeyboardButton();
                    buttonN.setText(value.getCloth().getName());
                    buttonN.setCallbackData("" + value.getId());
                    inlineRow.add(buttonN);

                    if (i % 2 == 0) {
                        inlineButtons.add(inlineRow);
                    } else {
                        inlineRow = new ArrayList<>();
                    }
                }
                InlineKeyboardButton buttonH = new InlineKeyboardButton();
                buttonH.setText("⏏️Menu");
                buttonH.setCallbackData("Menu");
                inlinePrev.add(buttonH);

                inlineButtons.add(inlinePrev);
                return inlineKeyboardMarkup;
            }
            case 10: {
                KeyboardRow row1 = new KeyboardRow();
                row1.add("Resend Code");
                rowList.add(row1);
            }
            break;
            default:
                KeyboardRow row4 = new KeyboardRow();
                row4.add("◀️ Back");
                row4.add("⏏️ Menu");
                rowList.add(row4);
                break;
        }

        return keyboardMarkup;
    }

    public ReplyKeyboard getreplyKeyboard(User user, String data) {
        Customer customer = (Customer) user;
        //INLINE
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> buttonList = new ArrayList<>();
        inlineButtons.add(buttonList);
        inlineKeyboardMarkup.setKeyboard(inlineButtons);
        OrderItem selectedOrder = helperMethod.selectOrder(user, data);


        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();
        keyboardMarkup.setKeyboard(rowList);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        switch (user.getCurrentRound()) {
            case 8: {
                List<InlineKeyboardButton> inlineRow = new ArrayList<>();
                List<InlineKeyboardButton> inlineCart = new ArrayList<>();
                List<InlineKeyboardButton> inlinePrev = new ArrayList<>();

                InlineKeyboardButton buttonE = new InlineKeyboardButton();
                buttonE.setText("-");
                buttonE.setCallbackData("-");

                InlineKeyboardButton buttonS = new InlineKeyboardButton();
                buttonS.setText("" + selectedOrder.getQuantity());
                buttonS.setCallbackData("" + selectedOrder.getQuantity());


                inlineRow.add(buttonE);
                inlineRow.add(buttonS);

                InlineKeyboardButton buttonW = new InlineKeyboardButton();
                buttonW.setText("⛔️ Remove");
                buttonW.setCallbackData("Remove");
                inlineCart.add(buttonW);


                InlineKeyboardButton buttonH = new InlineKeyboardButton();
                buttonH.setText("⏏️Menu");
                buttonH.setCallbackData("Menu");
                inlinePrev.add(buttonH);
                inlineButtons.add(inlineRow);
                inlineButtons.add(inlineCart);
                inlineButtons.add(inlinePrev);
                return inlineKeyboardMarkup;
            }
            default:
                KeyboardRow row4 = new KeyboardRow();
                row4.add("◀️ Back");
                row4.add("⏏️ Menu");
                rowList.add(row4);
                break;
        }

        return keyboardMarkup;

    }
}
