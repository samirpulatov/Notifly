package org.notifly.services;

import org.notifly.database.ReminderDAO;
import org.notifly.dto.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDate;
import java.time.LocalTime;

import static java.time.format.DateTimeFormatter.ofPattern;


public class ReminderService {

    private final UserStateService userStateService;
    private final ReminderDAO  reminderDAO;
    private final TelegramMessageSender telegramMessageSender;
    private final static Logger logger = LoggerFactory.getLogger(ReminderService.class);

    public ReminderService(TelegramClient telegramClient){
        this.userStateService = new UserStateService();
        this.reminderDAO = new ReminderDAO();
        this.telegramMessageSender = new TelegramMessageSender(telegramClient);
    }

    public void createNewReminder(Long chatId, Update update)
    {
        String message_text = "";
        UserSession userSession = new UserSession();
        UserSession.Status status = userStateService.getUserStatus(chatId);

        if(status == UserSession.Status.AWAITING_DATE){
            String dateText = update.getMessage().getText().trim();
            try {
                String datePart = dateText.substring(0,dateText.indexOf(",")).trim();
                String startTimePart = dateText.substring(dateText.indexOf(",")+1,dateText.indexOf("-")).trim();
                String endTimePart = dateText.substring(dateText.indexOf("-")+1).trim();
                logger.info("Parsing given date from a user");
                LocalDate date  = LocalDate.parse(datePart, ofPattern("dd/MM/yyyy"));
                userSession.setDate(date);
                LocalTime startTime = LocalTime.parse(startTimePart, ofPattern("HH:mm"));
                userSession.setStartTime(startTime);
                LocalTime endTime = LocalTime.parse(endTimePart, ofPattern("HH:mm"));
                userSession.setEndTime(endTime);
                userSession.setStatus(UserSession.Status.AWAITING_DESCRIPTION);
                message_text = "Дата сохранена: " + date+", "+startTime+"-"+endTime+"✅\nТеперь введите описание. Например: день рождения друга или '-' если описание не нужно.";
            } catch (Exception e) {
                // Invalid date format
                message_text = "Неверный формат! Введите дату в формате dd/MM/yyyy, HH:mm-HH:mm";
                logger.error("Parsing of a given date failed",e);
            }
        }

        else if (status == UserSession.Status.AWAITING_DESCRIPTION) {
            String descriptionText = update.getMessage().getText();

            if(!descriptionText.equals("-")) {
                userSession.setOptionalDescription(descriptionText);
                message_text = "Описание добавлено: " + descriptionText;
            } else {
                message_text = "Описание пропущено.";
            }

            userSession.setOptionalDescription(descriptionText);


            var date = userSession.getDate();
            var startTime = userSession.getStartTime();
            var endTime = userSession.getEndTime();
            var description = userSession.getOptionalDescription();
            var first_name = update.getMessage().getFrom().getFirstName();
            var last_name = update.getMessage().getFrom().getLastName();
            var username =  update.getMessage().getFrom().getUserName();

            logger.info("New reminder from {} user saved",first_name);
            reminderDAO.saveReminder(chatId, date,startTime,endTime, description,first_name,last_name,username);
            userStateService.updateUserStatus(chatId, UserSession.Status.NONE);

        }

        telegramMessageSender.sendMessage(update,message_text);

    }
}
