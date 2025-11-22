package org.notifly.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserStatus {
    public enum Status{
        NONE,
        AWAITING_DATE,
        AWAITING_DESCRIPTION
    }

    private Status status = Status.NONE;
    private LocalDateTime savedDate;
    private String optionalDescription;
    private String tempName;

}
