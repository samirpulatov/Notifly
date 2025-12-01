package org.notifly.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

public class CreateWeekSchedule implements CommandHandler{

    @Override
    public boolean canHandle(String command) {
        return command.equals("/create_week_schedule");
    }

    @Override
    public void response(Update update) {
    }

    @Override
    public void execute(Update update, String message) {

    }
}
