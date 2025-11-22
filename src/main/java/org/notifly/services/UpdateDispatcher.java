package org.notifly.services;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.notifly.commands.AddCommand;
import org.notifly.commands.CommandHandler;
import org.notifly.commands.StartCommand;
import org.notifly.dto.UserStatus;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;


public class UpdateDispatcher {
    private final UserService userService;
    private final CommandExecutor commandExecutor;


    public UpdateDispatcher(TelegramClient telegramClient) {
        this.userService = new UserService();
        this.commandExecutor = new CommandExecutor(telegramClient);
    }


    public void handle(Update update) {
        // Check if the update has a message with text
        if (update.hasMessage() && update.getMessage().hasText()) {
            String first_name = update.getMessage().getFrom().getFirstName();
            String last_name = update.getMessage().getFrom().getLastName();
            String username = update.getMessage().getFrom().getUserName();
            Long chatId = update.getMessage().getChatId();
            if (userService.userExists(chatId)){
                commandExecutor.execute(update.getMessage().getText(),update);
        } else {
            userService.saveUser(chatId,username,first_name,last_name);
            }
        }
    }

}
