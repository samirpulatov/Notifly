package org.notifly.commands;

import org.notifly.services.TelegramMessageSender;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartCommand implements CommandHandler{

    private final TelegramMessageSender telegramMessageSender;


    public StartCommand(TelegramMessageSender telegramMessageSender) {
        this.telegramMessageSender = telegramMessageSender;
    }

    @Override
    public boolean canHandle(String command) {
        return "/start".equals(command);
    }

    @Override
    public void response(Update update) {
        Long chatId = update.getMessage().getChat().getId();
        String firstName = update.getMessage().getChat().getFirstName();
        String message;
        message = "Привет, "+ firstName+" \uD83D\uDD90.\n" +
               "Я — Notifly \uD83E\uDD16." +
                "Помогаю вам сохранять фокус на важном: фиксирую события, создаю напоминания и формирую аккуратное расписание.\n" +
                "Чтобы узнать больше, введите /list.";
        this.execute(update,message);
    }

    @Override
    public void execute(Update update, String message) {
        telegramMessageSender.sendMessage(update, message);
    }


}
