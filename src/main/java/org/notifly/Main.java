package org.notifly;

import org.notifly.config.ConfigLoader;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        final String botToken = ConfigLoader.getToken();
        try{
        TelegramBotsLongPollingApplication botsAplicacion = new TelegramBotsLongPollingApplication();
        botsAplicacion.registerBot(botToken, new NotiflyBot());
        Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
}
}