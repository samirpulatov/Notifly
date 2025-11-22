package org.notifly.services;

import org.jetbrains.annotations.NotNull;
import org.notifly.commands.*;
import org.notifly.database.ReminderDAO;
import org.notifly.dto.UserStatus;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ofPattern;

public class CommandExecutor {

    static NotiflyLogger notiflyLogger = new NotiflyLogger();
    static Map<Long, UserStatus> userStatus = new HashMap<>();
    static List<CommandHandler> commandHandlers = new ArrayList<>();
    private final ReminderDAO reminderDAO;
    private final ExportCalendarService exportCalendarService;
    private final TelegramClient telegramClient;
    public CommandExecutor(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
        this.reminderDAO = new ReminderDAO();
        this.exportCalendarService = new ExportCalendarService();
    }


    static {

        commandHandlers.add(new StartCommand());
        commandHandlers.add(new AddCommand(userStatus));
        commandHandlers.add(new ExportCalendar());
        commandHandlers.add(new ListCommand(commandHandlers));


    }

    public void execute(String message_text, @NotNull Update update){
        notiflyLogger.log(update,message_text);
        UserStatus status = userStatus.get(update.getMessage().getChatId());
        if(status == null){
            status = new UserStatus();
            status.setStatus(UserStatus.Status.NONE);
            userStatus.put(update.getMessage().getChatId(), status);
        }

        if(status.getStatus() == UserStatus.Status.AWAITING_DATE){
            String dateText = update.getMessage().getText();
                System.out.println(status.getStatus());

                try {
                    // Try to parse date entered by user
//                    LocalDate date = LocalDate.parse(dateText, ofPattern("dd-MM-yyyy"));
//                    LocalTime time = LocalTime.parse(dateText, ofPattern("HH:mm:ss"));
//                    LocalDateTime dateTime = LocalDateTime.of(date, time);
                    LocalDateTime dateTime  = LocalDateTime.parse(dateText, ofPattern("dd-MM-yyyy, HH:mm"));
                    status.setStatus(UserStatus.Status.AWAITING_DESCRIPTION);
                    message_text = "Дата сохранена: " + dateTime+"✅\nТеперь введите описание. Например: день рождения друга или '-' если описание не нужно.";
                    status.setSavedDate(dateTime);
                } catch (DateTimeException e) {
                    // Invalid date format
                    message_text = "Неверный формат! Введите дату в формате dd-MM-yyyy, HH:mm";
                }
        }

        else if (status.getStatus() == UserStatus.Status.AWAITING_DESCRIPTION) {
                String descriptionText = update.getMessage().getText();

                if(!descriptionText.equals("-")) {
                    status.setOptionalDescription(descriptionText);
                    message_text = "Описание добавлено: " + descriptionText;
                } else {
                    message_text = "Описание пропущено.";
                }

                status.setOptionalDescription(descriptionText);

                var chatId = update.getMessage().getChatId();
                var date = status.getSavedDate();
                var description = status.getOptionalDescription();
                var first_name = update.getMessage().getFrom().getFirstName();
                var last_name = update.getMessage().getFrom().getLastName();
                var username =  update.getMessage().getFrom().getUserName();

                reminderDAO.saveReminder(chatId, date, description,first_name,last_name,username);

                status.setStatus(UserStatus.Status.NONE);

        }

        else  {
                // Loop through all command handlers and find one that can handle this command
                for(CommandHandler commandHandler : commandHandlers) {
                    if(commandHandler.canHandle(message_text)) {
                        message_text = commandHandler.handle(update);
                        System.out.println(message_text);
                        break;
                    }
                }
        }

        if ((!message_text.equals("Календарь сгенерирован и отправлен ✅"))) {
            sendMessage(update, message_text);
        } else {
            sendCalendar(update);
        }

    }

    private void sendCalendar(Update update) {
        String icsText = exportCalendarService.createIcsCalender(update.getMessage().getChatId());
        Path filePath = Paths.get("week.ics");
        try {
            Files.writeString(filePath, icsText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            SendDocument sendDocument = SendDocument.builder()
                    .chatId(update.getMessage().getChatId())
                    .document(new InputFile(filePath.toFile()))
                    .caption("Ваш календарь 📅")
                    .build();

            telegramClient.execute(sendDocument);
        } catch (Exception e) {

            sendMessage(update,"Ошибка при отправке файла");
        }

    }

    public void sendMessage(Update update, String message_text){
        // Build the outgoing message for Telegram/
        SendMessage message = SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text(message_text)
                .build();

        try {
            // Send response back to user
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
