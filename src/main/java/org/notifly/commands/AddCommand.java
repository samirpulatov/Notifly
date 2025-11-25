package org.notifly.commands;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import org.notifly.dto.UserStatus;
import org.telegram.telegrambots.meta.api.objects.Update;

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


        return EmojiParser.parseToUnicode("Отлично! Теперь введите дату и время для напоминания в следующем формате:\n" +
                "день/месяц/год, время начала-время окончания (например: 25/11/2025, 15:00-16:00) ⏰\n");
    }

}
