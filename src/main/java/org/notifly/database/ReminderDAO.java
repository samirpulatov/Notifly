package org.notifly.database;


import org.notifly.dto.Reminder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReminderDAO {

    public void saveUser(long chatId) {
        String sql = "INSERT OR IGNORE INTO users(chat_id) VALUES(?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1,chatId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveReminder(long chatId, LocalDate date,String description) {
        String sql = "INSERT INTO reminders(chat_id,date,description) VALUES(?,?,?)";
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1,chatId);
            statement.setString(2,date.toString());
            statement.setString(3,description);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Reminder> getReminders(LocalDate today,LocalDate tomorrow) {
        String sql = "SELECT chat_id,date, description FROM reminders WHERE date = ? OR date = ?";
        List<Reminder> reminders = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1,today.toString());
            statement.setString(2,tomorrow.toString());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                reminders.add(new Reminder(
                        resultSet.getLong("chat_id"),
                        LocalDate.parse(resultSet.getString("date")),
                        resultSet.getString("description")
                ));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reminders;
    }
}
