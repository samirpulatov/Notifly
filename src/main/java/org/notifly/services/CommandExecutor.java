package org.notifly.services;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import org.jetbrains.annotations.NotNull;
import org.notifly.commands.*;
import org.notifly.database.ReminderDAO;
import org.notifly.dto.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.FileOutputStream;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ofPattern;

public class CommandExecutor {
    private final static Logger logger = LoggerFactory.getLogger(CommandExecutor.class);
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
            String dateText = update.getMessage().getText().trim();
                try {
                    String datePart = dateText.substring(0,dateText.indexOf(",")).trim();
                    String startTimePart = dateText.substring(dateText.indexOf(",")+1,dateText.indexOf("-")).trim();
                    String endTimePart = dateText.substring(dateText.indexOf("-")+1).trim();
                    logger.info("Parsing given date from a user");
                    LocalDate date  = LocalDate.parse(datePart, ofPattern("dd/MM/yyyy"));
                    status.setDate(date);
                    LocalTime startTime = LocalTime.parse(startTimePart, ofPattern("HH:mm"));
                    status.setStartTime(startTime);
                    LocalTime endTime = LocalTime.parse(endTimePart, ofPattern("HH:mm"));
                    status.setEndTime(endTime);
                    status.setStatus(UserStatus.Status.AWAITING_DESCRIPTION);
                    message_text = "Дата сохранена: " + date+", "+startTime+"-"+endTime+"✅\nТеперь введите описание. Например: день рождения друга или '-' если описание не нужно.";
                } catch (Exception e) {
                    // Invalid date format
                    message_text = "Неверный формат! Введите дату в формате dd/MM/yyyy, HH:mm-HH:mm";
                    logger.error("Parsing of a given date failed",e);
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
                var date = status.getDate();
                var startTime = status.getStartTime();
                var endTime = status.getEndTime();
                var description = status.getOptionalDescription();
                var first_name = update.getMessage().getFrom().getFirstName();
                var last_name = update.getMessage().getFrom().getLastName();
                var username =  update.getMessage().getFrom().getUserName();

                logger.info("New reminder from {} user saved",first_name);
                reminderDAO.saveReminder(chatId, date,startTime,endTime, description,first_name,last_name,username);

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
            logger.info("Sending a message: {}",message_text);
            sendMessage(update, message_text);
        } else {
            logger.info("Sending a generated .ics file {}",message_text);
            sendCalendar(update);
        }

    }

    private void sendCalendar(Update update) {
        Long chatId = update.getMessage().getChatId();
        Calendar calendar = exportCalendarService.createIcsCalender(chatId);
        String filePath = "mycalendar.ics";

        try {

            logger.info("Creating a document from a generated .ics file");
            FileOutputStream fout =  new FileOutputStream(filePath);
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(calendar, fout);
            SendDocument sendDocument = SendDocument.builder()
                    .chatId(chatId)
                    .document(new InputFile(new File(filePath)))
                    .caption("Ваш календарь 📅")
                    .build();

            telegramClient.execute(sendDocument);
        } catch (Exception e) {
            logger.error("Failed to generate a .ics file");
            sendMessage(update,"Ошибка при отправке файла");
        }

        sendMessage(update,"Файл с вашим календарем 📅 успешно сгенерирован и отправлен!\n\n" +
                "Чтобы добавить его в приложение Календарь:\n" +
                "1. Сохраните файл на устройстве.\n" +
                "2. Откройте приложение «Файлы» или «Мои документы».\n" +
                "3. Найдите файл `mycalendar.ics`.\n" +
                "4. Удерживайте файл одним пальцем, затем перетащите его в приложение Календарь.\n" +
                "5. В календаре нажмите «Добавить» или «Сохранить».\n\n" +
                "После этого все ваши события появятся в календаре 📅✅");

    }

    public void sendMessage(Update update, String message_text){
        // Build the outgoing message for Telegram/
        SendMessage message = SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text(message_text)
                .build();

        try {
            logger.info("Sending a message {}",message_text);
            // Send response back to user
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            logger.error("Sending a message failed ",e);
        }
    }
}
