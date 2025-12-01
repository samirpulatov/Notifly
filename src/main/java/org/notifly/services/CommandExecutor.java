package org.notifly.services;

import org.jetbrains.annotations.NotNull;
import org.notifly.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;


public class CommandExecutor {
    private final static Logger logger = LoggerFactory.getLogger(CommandExecutor.class);

    static List<CommandHandler> commandHandlers = new ArrayList<>();


    private final ReminderService reminderService;

    public CommandExecutor(TelegramClient telegramClient) {
        TelegramMessageSender telegramMessageSender = new TelegramMessageSender(telegramClient);
        this.reminderService = new ReminderService(telegramClient);

        commandHandlers.add(new StartCommand(telegramMessageSender));
        commandHandlers.add(new AddCommand(telegramMessageSender, reminderService));
        commandHandlers.add(new ExportCalendar(telegramMessageSender));
        commandHandlers.add(new ListCommand(commandHandlers, telegramMessageSender));
    }


    public void execute(String message_text, @NotNull Update update){
        logger.info("Executing command: " + message_text);
        Long chatId = update.getMessage().getChatId();
        // Loop through all command handlers and find one that can handle this command
        if(message_text.startsWith("/")) {
            for(CommandHandler commandHandler : commandHandlers) {
                if(commandHandler.canHandle(message_text)) {
                    commandHandler.response(update);
                    break;
                }
            }
        } else {
            logger.info("Parsing message: " + message_text);
            processMessageForReminder(chatId,update);
        }
    }

    public void processMessageForReminder(Long chatId, Update update){
        this.reminderService.createNewReminder(chatId, update);
    }
}
