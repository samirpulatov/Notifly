package org.notifly.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class UserSession {
    public enum Status{
        NONE,
        AWAITING_DATE,
        AWAITING_DESCRIPTION
    }

    private Status status = Status.NONE;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String optionalDescription;
    private String tempName;

}
