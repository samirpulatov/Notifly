package org.notifly.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
@Setter
public class Reminder {
    private long chatId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;
}
