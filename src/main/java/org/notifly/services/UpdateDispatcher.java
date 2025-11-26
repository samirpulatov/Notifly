package org.notifly.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;


public class UpdateDispatcher {
    private final UserService userService;
    private final CommandExecutor commandExecutor;
    private final static Logger logger = LoggerFactory.getLogger(UpdateDispatcher.class);

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
                logger.info("User {} requested execution of {} command",first_name, update.getMessage().getText());
                commandExecutor.execute(update.getMessage().getText(),update);
        } else {
            userService.saveUser(chatId,username,first_name,last_name);
            logger.info("User {} successfully registered", first_name);
            }
        }
    }

}
