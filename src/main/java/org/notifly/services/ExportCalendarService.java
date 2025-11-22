package org.notifly.services;

import org.notifly.database.ReminderDAO;
import org.notifly.dto.Reminder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;


public class ExportCalendarService {
    private final ReminderDAO reminderDAO;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public ExportCalendarService() {
        this.reminderDAO = new ReminderDAO();
    }

    public String createIcsCalender(Long chatId){
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);

        // get all reminders
        var reminders = reminderDAO.getUserReminders(chatId);
        System.out.println(reminders.size());

        StringBuilder ics = new StringBuilder();
        ics.append("BEGIN:VCALENDAR\n");
        ics.append("VERSION:2.0\n");
        ics.append("CALSCALE:GREGORIAN\n");
        ics.append("METHOD:PUBLISH\n");
        ics.append("PRODID:-//NotiflyBot//EN\n");
        ics.append("BEGIN:VTIMEZONE\n");
        ics.append("TZID:Europe/Berlin\n");

        // summer time:
        ics.append("BEGIN:DAYLIGHT\n");
        ics.append("TZOFFSETFROM:+0100\n");
        ics.append("TZOFFSETTO:+0200\n");
        ics.append("TZNAME:CEST\n");
        ics.append("DTSTART:19700329T020000\n");
        ics.append("RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=3\n");
        ics.append("END:DAYLIGHT\n");

        // winter time
        ics.append("BEGIN:STANDARD\n");
        ics.append("TZOFFSETFROM:+0200\n");
        ics.append("TZOFFSETTO:+0100\n");
        ics.append("TZNAME:CEST\n");
        ics.append("DTSTART:19701025T030000\n");
        ics.append("RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=10");
        ics.append("END:STANDARD\n");

        ics.append("END:VTIMEZONE\n");

        for (Reminder r : reminders) {

            LocalDateTime dateTime = r.getDate();

            DateTimeFormatter icsFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

            String dtstart = dateTime.format(icsFormatter);
            String dtend = dateTime.plusHours(1).format(icsFormatter); // by default 1 hour for event duration

            String dtstamp = LocalDateTime.now().format(icsFormatter);

            String summary = "-".equals(r.getDescription()) ? "Напоминание" : r.getDescription();

            ics.append("BEGIN:VEVENT\n");
            ics.append("UID:" + r.getChatId() + "-" + r.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "@notifly\n");// Уникальный UID
            ics.append("DTSTAMP:TZID=Europe/Berlin:").append(dtstamp).append("Z\n");
            ics.append("DTSTART:TZID=Europe/Berlin:").append(dtstart).append("Z\n");
            ics.append("DTEND:TZID=Europe/Berlin:").append(dtend).append("Z\n");
            ics.append("SUMMARY:").append(summary).append("\n");
            ics.append("DESCRIPTION:").append(summary).append("\n");
            ics.append("END:VEVENT\n");
        }

        ics.append("END:VCALENDAR\n");

        return ics.toString();
    }

}
