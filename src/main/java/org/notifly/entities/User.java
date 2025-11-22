package org.notifly.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.notifly.dto.UserStatus;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String firstName;
    private String lastName;
    private Long chatId;
    private UserStatus userStatus;
}
