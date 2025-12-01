package org.notifly.commands;

import org.notifly.services.ExportCalendarService;
import org.notifly.services.TelegramMessageSender;
import org.telegram.telegrambots.meta.api.objects.Update;


public class ExportCalendar implements CommandHandler{
    private final TelegramMessageSender telegramMessageSender;

    public ExportCalendar(TelegramMessageSender telegramMessageSender) {
        this.telegramMessageSender = telegramMessageSender;
    }

    @Override
    public boolean canHandle(String command) {
        return "/export_calendar".equals(command);
    }

    @Override
    public void response(Update update) {
        String message = "Генерация файла...";
        this.execute(update, message);
    }

    @Override
    public void execute(Update update, String message) {
        telegramMessageSender.sendCalendar(update);
    }
}
