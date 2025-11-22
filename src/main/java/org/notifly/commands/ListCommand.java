package org.notifly.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class ListCommand implements CommandHandler{
    private final List<CommandHandler> allCommands;

    public ListCommand(List<CommandHandler> allCommands) {
        this.allCommands = allCommands;
    }

    @Override
    public boolean canHandle(String command){
        return "/list".equals(command);
    }

    @Override
    public String handle(Update update){
        StringBuilder sb = new StringBuilder();
        sb.append("Список доступных команд:\n");
        for (CommandHandler command : allCommands){
            if(command instanceof StartCommand){
                sb.append("/start - запустить бота\n");
            }
            else if (command instanceof ListCommand){
                sb.append("/list - показать список всех команд\n");
            }
            else if(command instanceof AddCommand){
                sb.append("/add - добавить напоминание\n");
            }
            else if(command instanceof ExportCalendar){
                sb.append("/export_calendar - cгенерировать календарь\n");
            }
        }
        return sb.toString();
    }
}
