package org.notifly.services;

import org.notifly.dto.UserSession;

import java.util.HashMap;
import java.util.Map;

public class UserStateService {
    static Map<Long, UserSession> users_status = new HashMap<>();

    public UserSession.Status getUserStatus(Long chatId) {
        return  users_status.get(chatId).getStatus();
    }
    public void updateUserStatus(Long chatId, UserSession.Status newStatus) {
        users_status.get(chatId).setStatus(newStatus);
    }

    public Map<Long, UserSession> getUsers_status() {
        return users_status;
    }
}
