package org.notifly.services;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.FileOutputStream;

public class TelegramMessageSender {
    private final TelegramClient telegramClient;
    private final ExportCalendarService  exportCalendarService;
    private final static Logger logger = LoggerFactory.getLogger(TelegramMessageSender.class);

    public TelegramMessageSender(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
        this.exportCalendarService = new ExportCalendarService();
    }


    public void sendMessage(Update update, String message_text){
        // Build the outgoing message for Telegram/
        SendMessage message = SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text(message_text)
                .build();

        try {
            logger.info("Sending a message {}",message_text);
            // Send response back to user
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            logger.error("Sending a message failed ",e);
        }
    }


    public void sendCalendar(Update update) {
        Long chatId = update.getMessage().getChatId();
        Calendar calendar = exportCalendarService.createIcsCalender(chatId);
        String filePath = "mycalendar.ics";

        try {

            logger.info("Creating a document from a generated .ics file");
            FileOutputStream fout =  new FileOutputStream(filePath);
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(calendar, fout);
            SendDocument sendDocument = SendDocument.builder()
                    .chatId(chatId)
                    .document(new InputFile(new File(filePath)))
                    .caption("Ваш календарь 📅")
                    .build();

            telegramClient.execute(sendDocument);
        } catch (Exception e) {
            logger.error("Failed to generate a .ics file");
            this.sendMessage(update,"Ошибка при отправке файла");
        }

        this.sendMessage(update,"Файл с вашим календарем 📅 успешно сгенерирован и отправлен!\n\n" +
                "Чтобы добавить его в приложение Календарь:\n" +
                "1. Сохраните файл на устройстве.\n" +
                "2. Откройте приложение «Файлы» или «Мои документы».\n" +
                "3. Найдите файл `mycalendar.ics`.\n" +
                "4. Удерживайте файл одним пальцем, затем перетащите его в приложение Календарь.\n" +
                "5. В календаре нажмите «Добавить» или «Сохранить».\n\n" +
                "После этого все ваши события появятся в календаре 📅✅");
    }
}
