package org.notifly.commands;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import org.notifly.dto.UserStatus;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;

@AllArgsConstructor
public class AddCommand implements CommandHandler{
    private final TelegramClient telegramClient;
    private final Map<Long, UserStatus> users;

    @Override
    public boolean canHandle(String command) {
        return command.equals("/add");
    }

    @Override
    public String handle(Update update) {
        long chatId = update.getMessage().getChatId();
        users.get(chatId).setStatus(UserStatus.Status.AWAITING_DATE);


        return EmojiParser.parseToUnicode("Отлично, теперь введите дату для напоминания в следующем формате: "+"25-10-2025");
    }

}
