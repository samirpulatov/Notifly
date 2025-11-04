package org.notifly.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class Reminder {
    private long chatId;
    private LocalDate date;
    private String description;
}
