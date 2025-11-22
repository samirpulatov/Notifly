package org.notifly;

import org.notifly.commands.*;
import org.notifly.config.ConfigLoader;
import org.notifly.database.ReminderDAO;
import org.notifly.services.ReminderScheduler;
import org.notifly.services.UpdateDispatcher;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;


import java.util.*;

public class NotiflyBot implements LongPollingSingleThreadUpdateConsumer {

    private final UpdateDispatcher updateDispatcher;


    public NotiflyBot() {
        // Load token from YAML configuration
        // Telegram bot token loaded from config file
        String token = ConfigLoader.getToken();
        // Telegram client used to send messages to users
        TelegramClient telegramClient = new OkHttpTelegramClient(token);
        this.updateDispatcher = new UpdateDispatcher(telegramClient);
        ReminderDAO reminderDAO = new ReminderDAO();

        ReminderScheduler reminderScheduler = new ReminderScheduler(reminderDAO, telegramClient);
        reminderScheduler.start();

    }

    @Override
    public void consume(Update update) {
        updateDispatcher.handle(update);

    }
}
