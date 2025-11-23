package org.notifly.services;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import org.notifly.database.ReminderDAO;
import org.notifly.dto.Reminder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;


public class ExportCalendarService {
    private final ReminderDAO reminderDAO;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public ExportCalendarService() {
        this.reminderDAO = new ReminderDAO();
    }

    public Calendar createIcsCalender(Long chatId){
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);

        // get all reminders
        var reminders = reminderDAO.getUserReminders(chatId);
        System.out.println(reminders.size());


        Calendar calendar = new Calendar();
        calendar.add(new ProdId("-//NotiFly// 1.0//EN"));
        calendar.add(ImmutableVersion.VERSION_2_0);
        calendar.add(ImmutableCalScale.GREGORIAN);


        for (Reminder reminder : reminders) {
            VEvent vEvent = new VEvent(reminder.getDate(), reminder.getDescription());
            vEvent.add(new Uid(UUID.randomUUID().toString()));
            calendar.add(vEvent);
        }

        return  calendar;
    }

}
