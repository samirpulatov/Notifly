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
        Long chatId = update.getMessage().getChat().getId();
        String firstName = update.getMessage().getChat().getFirstName();
        String message;
        if(chatId == 1760003189L) {

            message ="Здравствуйте, Анастасия! 👋\n" +
                    "Добро пожаловать в Notifly — Ваш личный помощник по организации времени.\n" +
                    "Я помогу Вам фиксировать события, создавать напоминания и формировать аккуратное расписание\n"+
                    "Для Вас доступна эксклюзивная функция, о которой Вы скоро узнаете \uD83D\uDE09.";
            return message;
        }

        message = "Привет, "+ firstName+" \uD83D\uDD90.\n" +
               "Я — Notifly \uD83E\uDD16." +
                "Помогаю вам сохранять фокус на важном: фиксирую события, создаю напоминания и формирую аккуратное расписание.\n" +
                "Чтобы узнать больше, введите /list.";
        return message;

    }
}
