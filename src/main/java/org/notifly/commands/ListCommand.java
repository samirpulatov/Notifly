package org.notifly.commands;

import org.notifly.services.TelegramMessageSender;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class ListCommand implements CommandHandler{
    private final List<CommandHandler> allCommands;
    private final TelegramMessageSender telegramMessageSender;
    public ListCommand(List<CommandHandler> allCommands, TelegramMessageSender telegramMessageSender) {
        this.allCommands = allCommands;
        this.telegramMessageSender = telegramMessageSender;

    }

    @Override
    public boolean canHandle(String command){
        return "/list".equals(command);
    }

    @Override
    public void response(Update update){
        Long chatId = update.getMessage().getChat().getId();
        StringBuilder sb = new StringBuilder();
        sb.append("📋 *Список команд Notifly*\n\n");

        for (CommandHandler command : allCommands) {
            if (command instanceof StartCommand) {
                sb.append("🟢 /start")
                        .append(" — Запустить бота \n");
            } else if (command instanceof ListCommand) {
                sb.append("📄 /list")
                        .append(" — Показать список всех команд \n");
            } else if (command instanceof AddCommand) {
                sb.append("➕ /add")
                        .append(" — Добавить напоминание \n");
            } else if (command instanceof ExportCalendar) {
                sb.append("📅 /export_calendar")
                        .append(" — Сгенерировать календарь \n");
            }
        }

        sb.append("\n💡 Используйте команды, чтобы легко управлять своими напоминаниями!");
        this.execute(update,sb.toString());

    }

    @Override
    public void execute(Update update, String message) {
        telegramMessageSender.sendMessage(update,message);
    }
}
