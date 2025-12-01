package org.notifly;

import org.notifly.config.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        //final String botToken = ConfigLoader.getToken();
        final String botToken = "8357719046:AAHFlr6fNnjMfJWCKMfVs66gWwSW-UvS420";
        logger.info("Starting Notifly Bot");
        try{
        TelegramBotsLongPollingApplication botsAplicacion = new TelegramBotsLongPollingApplication();
        botsAplicacion.registerBot(botToken, new NotiflyBot());

        logger.info("Notifly Bot successfully registered and running");

        Thread.currentThread().join();
        } catch (Exception e) {
            logger.error("Error while starting Notifly Bot",e);
        }
    }
}