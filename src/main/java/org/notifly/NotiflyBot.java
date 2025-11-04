package org.notifly;

import org.notifly.commands.AddCommand;
import org.notifly.commands.CommandHandler;
import org.notifly.commands.ListCommand;
import org.notifly.commands.StartCommand;
import org.notifly.config.ConfigLoader;
import org.notifly.database.ReminderDAO;
import org.notifly.dto.UserStatus;
import org.notifly.services.ReminderScheduler;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NotiflyBot implements LongPollingSingleThreadUpdateConsumer {
    // Telegram bot token loaded from config file
    private final String token;

    // Telegram client used to send messages to users
    private final TelegramClient telegramClient;

    // Stores the state (status) of each user (by chat ID)
    private final Map<Long, UserStatus> users;

    // List of available command handlers (/start, /add, /list, etc.)
    private List<CommandHandler> commandHandlers = new ArrayList<>();

    private ReminderDAO reminderDAO;
    private ReminderScheduler reminderScheduler;


    public NotiflyBot() {
        // Load token from YAML configuration
        this.token = ConfigLoader.getToken();
        this.telegramClient = new OkHttpTelegramClient(token);


        // Initialize user map
        users = new HashMap<>();


        // Initialize and register all command handlers
        ListCommand listCommand = new ListCommand(commandHandlers);
        this.commandHandlers.add(new StartCommand());
        this.commandHandlers.add(new AddCommand(this.telegramClient, users));
        this.commandHandlers.add(listCommand);

        this.reminderDAO = new ReminderDAO();
        this.reminderScheduler = new ReminderScheduler(reminderDAO,telegramClient);

        this.reminderScheduler.start();


    }

    @Override
    public void consume(Update update) {
        // Check if the update has a message with text
        if(update.hasMessage() && update.getMessage().hasText()) {

            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();

            // Get user state or create a new one if user is new
            UserStatus userStatus = users.get(chat_id);
            if(userStatus == null) {
                userStatus = new UserStatus();
                userStatus.setStatus(UserStatus.Status.NONE);
                users.put(chat_id, userStatus);
                reminderDAO.saveUser(chat_id);
            }

            System.out.println(update.getMessage().getText());

            // If user is waiting for a date input
            if(userStatus.getStatus() == UserStatus.Status.AWAITING_DATE) {
                String dateText = update.getMessage().getText();
                System.out.println(userStatus.getStatus());

                try {
                    // Try to parse date entered by user
                    LocalDate date = LocalDate.parse(dateText, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    userStatus.setStatus(UserStatus.Status.AWAITING_DESCRIPTION);
                    message_text = "Дата сохранена: " + date+"✅\nТеперь введите описание. Например: день рождения друга или '-' если описание не нужно.";
                    userStatus.setSavedDate(date);
                } catch (DateTimeException e) {
                    // Invalid date format
                    message_text = "Неверный формат! Введите дату в формате dd-MM-yyyy";
                }
            }
            else if (userStatus.getStatus() == UserStatus.Status.AWAITING_DESCRIPTION) {
                String descriptionText = update.getMessage().getText();

                if(!descriptionText.equals("-")) {
                    userStatus.setOptionalDescription(descriptionText);
                    message_text = "Описание добавлено: " + descriptionText;
                } else {
                    message_text = "Описание пропущено.";
                }

                userStatus.setOptionalDescription(descriptionText);

                reminderDAO.saveReminder(chat_id,userStatus.getSavedDate(),userStatus.getOptionalDescription());

                userStatus.setStatus(UserStatus.Status.NONE);

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

            // Build the outgoing message for Telegram
            SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text(message_text)
                    .build();

            // Log message to console (for debugging)
            log(update, message_text);

            try {
                // Send response back to user
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Logs incoming and outgoing messages to console
     */
    private void log(Update update,String bot_answer){
        String first_name = update.getMessage().getChat().getFirstName();
        String last_name = update.getMessage().getChat().getLastName();
        long user_id = update.getMessage().getChat().getId();
        String message_text = update.getMessage().getText();

        System.out.println("\n----------------------------");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Message from "+first_name+" "+last_name+". (id = "+user_id+")\nText - "+message_text);
        System.out.println("Bot answer: \nText - "+ bot_answer);
        System.out.println("---------------------------");
    }
}
