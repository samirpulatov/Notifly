package org.notifly.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserStatus {
    public enum Status{
        NONE,
        AWAITING_DATE,
        AWAITING_DESCRIPTION
    }

    private Status status = Status.NONE;
    private LocalDate savedDate;
    private String optionalDescription;
    private String tempName;

}
