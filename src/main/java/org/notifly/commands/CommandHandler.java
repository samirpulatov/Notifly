package org.notifly.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {
    boolean canHandle(String command); // check if bot can handle such command
    String handle(Update update); // returns an answer
}
