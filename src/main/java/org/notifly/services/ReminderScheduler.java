package org.notifly.services;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.notifly.database.ReminderDAO;
import org.notifly.dto.Reminder;
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



    @SneakyThrows
    public void start() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        System.out.println("Starting reminder scheduler");
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                checkReminders();  // здесь TelegramApiException будет обработан
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.HOURS);


    }

    @SneakyThrows
    private void checkReminders() throws TelegramApiException {
        LocalDate today = LocalDate.now();
        LocalDate tommorrow = today.plusDays(1);

        List<Reminder> reminders = reminderDAO.getReminders(today, tommorrow);
        for (Reminder reminder : reminders) {
            String message = reminder.getDate().equals(tommorrow)
                    ? "\"\uD83D\uDCC5 Напоминанию Вам о следующем событии на завтра: \"" +reminder.getDescription()
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
