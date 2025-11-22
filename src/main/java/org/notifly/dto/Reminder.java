package org.notifly.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class Reminder {
    private long chatId;
    private LocalDateTime date;
    private String description;
}
