package org.notifly.services;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.notifly.Main;
import org.notifly.database.ReminderDAO;
import org.notifly.dto.Reminder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class ReminderScheduler {
    private final ReminderDAO reminderDAO;
    private final TelegramClient telegramClient;
    private final static Logger logger = LoggerFactory.getLogger(ReminderScheduler.class);



    @SneakyThrows
    public void start() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        logger.info("Starting reminder scheduler");
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                checkReminders();  // здесь TelegramApiException будет обработан
                logger.info("Reminders are checked");
            } catch (TelegramApiException e) {
                logger.error("Reminders of a user could not be checked",e);
            }
        }, 0, 1, TimeUnit.DAYS);


    }

    @SneakyThrows
    private void checkReminders() throws TelegramApiException {
        LocalDate today = LocalDate.now();
        LocalDate tommorrow = today.plusDays(1);

        List<Reminder> reminders = reminderDAO.getReminders(today, tommorrow);
        for (Reminder reminder : reminders) {
            String message = reminder.getDate().equals(tommorrow)
                    ? "\uD83D\uDCC5 Напоминанию Вам о следующем событии на завтра: " +reminder.getDescription()
                    : "Напоминанию Вам о следующем событии на сегодня: " + reminder.getDescription();

            telegramClient.execute(
                    SendMessage.builder()
                            .chatId(reminder.getChatId())
                            .text(message)
                            .build()
            );
        }
    }
}
