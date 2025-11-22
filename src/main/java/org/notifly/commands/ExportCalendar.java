package org.notifly.commands;

import org.notifly.services.ExportCalendarService;
import org.telegram.telegrambots.meta.api.objects.Update;


public class ExportCalendar implements CommandHandler{

    public ExportCalendar() {
        ExportCalendarService exportCalendarService = new ExportCalendarService();
    }

    @Override
    public boolean canHandle(String command) {
        return "/export_calendar".equals(command);
    }

    @Override
    public String handle(Update update) {
        return "Календарь сгенерирован и отправлен ✅";
    }
}
