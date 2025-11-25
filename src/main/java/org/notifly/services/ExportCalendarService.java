package org.notifly.services;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import org.notifly.database.ReminderDAO;
import org.notifly.dto.Reminder;

import java.time.*;
import java.util.UUID;


public class ExportCalendarService {
    private final ReminderDAO reminderDAO;


    public ExportCalendarService() {
        this.reminderDAO = new ReminderDAO();
    }

    public Calendar createIcsCalender(Long chatId){

        // get all reminders
        var reminders = reminderDAO.getUserReminders(chatId);
        System.out.println(reminders.size());


        Calendar calendar = new Calendar();
        calendar.add(new ProdId("-//NotiFly// 1.0//EN"));
        calendar.add(ImmutableVersion.VERSION_2_0);
        calendar.add(ImmutableCalScale.GREGORIAN);


        for (Reminder reminder : reminders) {
            LocalDate date = reminder.getDate();
            LocalTime startTime = reminder.getStartTime();
            LocalTime endTime = reminder.getEndTime();

            LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
            LocalDateTime endDateTime  = LocalDateTime.of(date, endTime);

            ZoneId zoneId = ZoneId.of("Europe/Berlin");
            ZonedDateTime zonedStart = startDateTime.atZone(zoneId);
            ZonedDateTime zonedEnd = endDateTime.atZone(zoneId);
            VEvent vEvent = new VEvent(zonedStart,zonedEnd,reminder.getDescription());
            vEvent.add(new Uid(UUID.randomUUID().toString()));
            calendar.add(vEvent);
        }

        return  calendar;
    }

}
