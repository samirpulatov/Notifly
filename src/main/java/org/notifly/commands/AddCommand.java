package org.notifly.commands;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import org.notifly.dto.UserStatus;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;

@AllArgsConstructor
public class AddCommand implements CommandHandler{

    private final Map<Long, UserStatus> users;

    @Override
    public boolean canHandle(String command) {
        return command.equals("/add");
    }

    @Override
    public String handle(Update update) {
        long chatId = update.getMessage().getChatId();
        users.get(chatId).setStatus(UserStatus.Status.AWAITING_DATE);


        return EmojiParser.parseToUnicode("Отлично! Теперь введите дату и время для напоминания в формате: " +
                "день-месяц-год, часы:минуты (например: 25-10-2025, 14:30) ⏰");
    }

}
