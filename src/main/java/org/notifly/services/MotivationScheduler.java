package org.notifly.services;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MotivationScheduler {
    private final TelegramClient telegramClient;

    public  MotivationScheduler(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    long delay = computeInitialDelay(21); // 19 pm
    long period = TimeUnit.DAYS.toMillis(1);

    public void start(){
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                sendDailyMotivation();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }, delay, period, TimeUnit.MILLISECONDS);

    }

    private void sendDailyMotivation() throws TelegramApiException {
        Long nastyaId = 1760003189L;

        String randoMessage = MotivationGenerator.getMessageForToday();

        // Build the outgoing message for Telegram/
        SendMessage message = SendMessage
                .builder()
                .chatId(nastyaId)
                .text(randoMessage)
                .build();

        try {
            // Send response back to user
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    private long computeInitialDelay(int targetHour) {
        ZoneId zoneId = ZoneId.of("Europe/Berlin");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime nextRun = now.withHour(targetHour).withMinute(30).withSecond(0).withNano(0);


        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }

        return Duration.between(now,nextRun).toMillis();

    }

}
