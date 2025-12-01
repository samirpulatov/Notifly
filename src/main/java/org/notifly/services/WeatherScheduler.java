package org.notifly.services;

import org.notifly.dto.WeatherInfo;
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

public class WeatherScheduler {

    private final Logger logger = LoggerFactory.getLogger(WeatherScheduler.class);
    private final WeatherService weatherService;
    private final TelegramClient telegramClient;

    long delay = computeInitialDelay();
    long period = TimeUnit.DAYS.toMillis(1);

    public WeatherScheduler(TelegramClient telegramClient){
        this.weatherService = new WeatherService();
        this.telegramClient = telegramClient;

    }


    public void start() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                sendDailyWeatherAdvice();
            } catch (TelegramApiException e){
                logger.error("Error sending weather advice", e);
            }
        }, delay,period, TimeUnit.MILLISECONDS);
    }

    private void sendDailyWeatherAdvice() throws TelegramApiException {
        Long chatId = 893239756L;
        WeatherInfo weatherInfo = weatherService.getWeather();
        String advice = WeatherAdviceGenerator.generateAdvice(weatherInfo);

        // Build the outgoing message for Telegram/
        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(advice)
                .build();

        try {
            // Send response back to user
            telegramClient.execute(message);
            logger.info("WeatherMessage sending to telegram {}",message);
        } catch (TelegramApiException e) {
            logger.error("Failed to send WeatherMessage to telegram", e);
        }
    }

    private long computeInitialDelay() {
        ZoneId zoneId = ZoneId.of("Europe/Berlin");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime nextRun = now.withHour(8).withMinute(5).withSecond(0).withNano(0);

        if(now.isAfter(nextRun)){
            nextRun = nextRun.plusDays(1);
        }

        return Duration.between(now,nextRun).toMillis();
    }
}
