package org.notifly.services;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotiflyLogger {
    /**
     * Logs incoming and outgoing messages to console
     */
    public void log(Update update, String bot_answer){
        String first_name = update.getMessage().getChat().getFirstName();
        String last_name = update.getMessage().getChat().getLastName();
        long user_id = update.getMessage().getChat().getId();
        String message_text = update.getMessage().getText();

        System.out.println("\n----------------------------");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Message from "+first_name+" "+last_name+". (id = "+user_id+")\nText - "+message_text);
        System.out.println("Bot answer: \nText - "+ bot_answer);
        System.out.println("---------------------------");
    }
}
