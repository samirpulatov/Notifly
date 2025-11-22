package org.notifly.services;

import org.notifly.database.ReminderDAO;


public class UserService {
    private final ReminderDAO reminderDAO;

    public UserService() {
        this.reminderDAO = new ReminderDAO();
    }

    public boolean userExists(Long chatId){
        if(reminderDAO.getUser(chatId)) {
            return true;
        }
        else {
            return false;
        }
    }

    public void saveUser(Long chatId,String username,String first_name,String last_name){
        reminderDAO.saveUser(chatId,username,first_name,last_name);
    }
}
