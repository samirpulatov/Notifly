package org.notifly.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MotivationScheduler {
    private final TelegramClient telegramClient;
    private final static Logger logger = LoggerFactory.getLogger(MotivationScheduler.class);

    public  MotivationScheduler(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    long delay = computeInitialDelay(); // 8 am
    long period = TimeUnit.DAYS.toMillis(1);

    public void start(){
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                sendDailyMotivation();
                logger.info("DailyMotivation scheduled");
            } catch (TelegramApiException e) {
                logger.error("Failed to schedule dailyMotivation", e);
            }
        }, delay, period, TimeUnit.MILLISECONDS);

    }

    private void sendDailyMotivation() throws TelegramApiException {
        Long nastyaId = 893239756L;

        String greeting = GreetingGenerator.getGreetingForToday();
        String randomMessage = greeting+MotivationGenerator.getMessageForToday();

        // Build the outgoing message for Telegram/
        SendMessage message = SendMessage
                .builder()
                .chatId(nastyaId)
                .text(randomMessage)
                .build();

        try {
            // Send response back to user
            telegramClient.execute(message);
            logger.info("DailyMotivation sending to telegram {}",message);
        } catch (TelegramApiException e) {
            logger.error("Failed to send dailyMotivation to telegram", e);
        }
    }



    private long computeInitialDelay() {
        ZoneId zoneId = ZoneId.of("Europe/Berlin");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime nextRun = now.withHour(8).withMinute(0).withSecond(0).withNano(0);


        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }

        return Duration.between(now,nextRun).toMillis();

    }

}
