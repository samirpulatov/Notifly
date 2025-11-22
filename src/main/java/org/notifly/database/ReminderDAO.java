package org.notifly.database;


import org.notifly.dto.Reminder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReminderDAO {

    public void saveUser(long chatId,String username,String first_name,String last_name) {
        String sql = "INSERT OR IGNORE INTO users(chat_id,username,first_name,last_name) VALUES(?,?,?,?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1,chatId);
            statement.setString(2,username);
            statement.setString(3,first_name);
            statement.setString(4,last_name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getUser(Long chatId){
        String sql = "SELECT chat_id FROM users WHERE chat_id  = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1,chatId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveReminder(long chatId, LocalDateTime date, String description, String first_name, String last_name, String username) {
        String sql = "INSERT INTO reminders(chat_id,date,description,first_name,last_name,username) VALUES(?,?,?,?,?,?)";
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1,chatId);
            statement.setString(2,date.toString());
            statement.setString(3,description);
            statement.setString(4,first_name);
            statement.setString(5,last_name);
            statement.setString(6,username);
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
                        LocalDateTime.parse(resultSet.getString("date")),
                        resultSet.getString("description")
                ));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reminders;
    }

    public List<Reminder> getAllReminders() {
        String sql = "SELECT chat_id,date, description FROM reminders";
        List<Reminder> reminders = new ArrayList<>();
        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                reminders.add(new Reminder(
                        resultSet.getLong("chat_id"),
                        LocalDateTime.parse(resultSet.getString("date")),
                        resultSet.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reminders;
    }

    public List<Reminder> getUserReminders(Long chatId) {
        String sql = "SELECT chat_id,date, description FROM reminders WHERE chat_id = ?";
        List<Reminder> user_reminders = new ArrayList<>();
        try(Connection connection = DatabaseManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1,chatId);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                user_reminders.add(new Reminder(
                        resultSet.getLong("chat_id"),
                        LocalDateTime.parse(resultSet.getString("date")),
                        resultSet.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user_reminders;
    }

    public List<Reminder> getRemindersBetween(Long chatId,LocalDate start,LocalDate end) {
        String sql = "SELECT * FROM reminders WHERE chat_id = ? AND date >= ? AND date <= ?";
        List<Reminder> reminders = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, chatId);
            statement.setString(2, start.toString());
            statement.setString(3, end.toString());

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                reminders.add(new Reminder(
                        rs.getLong("chat_id"),
                        LocalDateTime.parse(rs.getString("date")),
                        rs.getString("description")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reminders;
    }
}
