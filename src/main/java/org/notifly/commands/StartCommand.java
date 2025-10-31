package org.notifly.commands;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartCommand implements CommandHandler{
    @Override
    public boolean canHandle(String command) {
        return "/start".equals(command);
    }

    @Override
    public String handle(Update update) {
        String firstName = update.getMessage().getChat().getFirstName();
        return "Привет, " + firstName + "! 👋\n" +
                "Я ваш личный помощник в Telegram. " +
                "Я буду напоминать вам о важных датах, чтобы Вы ничего не забыли.\uD83D\uDE07"+
                "\nНапишите /list, чтобы просмотреть список всех моих команд.";

    }
}
