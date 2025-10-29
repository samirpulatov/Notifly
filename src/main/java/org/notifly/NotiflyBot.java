package org.notifly;

import org.notifly.commands.CommandHandler;
import org.notifly.commands.StartCommand;
import org.notifly.config.ConfigLoader;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotiflyBot implements LongPollingSingleThreadUpdateConsumer {
    private final String token = ConfigLoader.getToken();
    private final List<CommandHandler> commandHandlers = List.of(new StartCommand());
    private final TelegramClient telegramClient = new OkHttpTelegramClient(token);
    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if(update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            System.out.println(update.getMessage().getText());

            for(CommandHandler commandHandler : commandHandlers) {
                if(commandHandler.canHandle(message_text)) {
                    message_text = commandHandler.handle(update);
                    System.out.println(message_text);
                    break;
                }
            }


            SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text(message_text)
                    .build();
            log(update, message_text);
            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }
    }

    private void log(Update update,String bot_answer){
        String first_name = update.getMessage().getChat().getFirstName();
        String last_name = update.getMessage().getChat().getLastName();
        long user_id = update.getMessage().getChat().getId();
        String message_text = update.getMessage().getText();
        String response = bot_answer;
        long chat_id = update.getMessage().getChatId();

        System.out.println("\n----------------------------");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Message from "+first_name+" "+last_name+". (id = "+user_id+")\nText - "+message_text);
        System.out.println("Bot answer: \nText - "+ bot_answer);
        System.out.println("---------------------------");

    }
}
