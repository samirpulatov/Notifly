package org.notifly.commands;

import com.vdurmont.emoji.EmojiParser;
import org.notifly.dto.UserSession;
import org.notifly.services.ReminderService;
import org.notifly.services.TelegramMessageSender;
import org.notifly.services.UserStateService;
import org.telegram.telegrambots.meta.api.objects.Update;


public class AddCommand implements CommandHandler{


    private final UserStateService userStateService;
    private final TelegramMessageSender telegramMessageSender;



    public AddCommand(TelegramMessageSender telegramMessageSender,ReminderService reminderService){
        this.userStateService = new UserStateService();
        this.telegramMessageSender = telegramMessageSender;
    }

    @Override
    public boolean canHandle(String command) {
        return command.equals("/add");
    }

    @Override
    public void response(Update update) {
        Long chatId = update.getMessage().getChatId();
        userStateService.updateUserStatus(chatId, UserSession.Status.AWAITING_DATE);
        String message = EmojiParser.parseToUnicode("Отлично! Теперь введите дату и время для напоминания в следующем формате:\n" +
                "день/месяц/год, время начала-время окончания (например: 25/11/2025, 15:00-16:00) ⏰\n");
        this.execute(update,message);
    }

    @Override
    public void execute(Update update, String message) {
        this.telegramMessageSender.sendMessage(update,message);
    }


}
