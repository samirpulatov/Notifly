package org.notifly.services;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import org.notifly.database.ReminderDAO;
import org.notifly.dto.Reminder;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.GregorianCalendar;
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
//            String date = reminder.getDate().toString(); // 2025-11-25
//            String year = date.substring(0,4);
//            String month = date.substring(5,7);
//            String day = date.substring(8,10);
//
//            String startTime = reminder.getStartTime().toString();
//            System.out.println(startTime); // 16:00
//            String startHour = startTime.substring(0,2);
//            String startMinute = startTime.substring(3,5);
//            String endTime = reminder.getEndTime().toString();
//            String endHour = endTime.substring(0,2);
//            String endMinute = endTime.substring(3,5);
//            System.out.println(endTime);
//            System.out.println(date);
//
//            java.util.Calendar startDate = new GregorianCalendar();
//            startDate.set(java.util.Calendar.MONTH,Integer.parseInt(month));
//            startDate.set(java.util.Calendar.DAY_OF_MONTH,Integer.parseInt(day));
//            startDate.set(java.util.Calendar.YEAR,Integer.parseInt(year));
//            startDate.set(java.util.Calendar.HOUR_OF_DAY,Integer.parseInt(startHour));
//            startDate.set(java.util.Calendar.MINUTE,Integer.parseInt(startMinute));
//            startDate.set(java.util.Calendar.SECOND,0);
//
//            java.util.Calendar endDate = new GregorianCalendar();
//            endDate.set(java.util.Calendar.MONTH,Integer.parseInt(month));
//            endDate.set(java.util.Calendar.DAY_OF_MONTH,Integer.parseInt(day));
//            endDate.set(java.util.Calendar.YEAR,Integer.parseInt(year));
//            endDate.set(java.util.Calendar.HOUR_OF_DAY,Integer.parseInt(endHour));
//            endDate.set(java.util.Calendar.MINUTE,Integer.parseInt(endMinute));
//            endDate.set(java.util.Calendar.SECOND,0);
//
//            DateTime start = new DateTime(startDate.getTime());
//            DateTime end = new DateTime(endDate.getTime());
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
