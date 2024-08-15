package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.entity.Employee;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
public class TelegramService extends TelegramLongPollingBot {

    private static final Logger LOGGER = Logger.getLogger(TelegramService.class.getName());
    private final String botUsername;
    private final String botToken;
    private final EmployeeService employeeService;

    public TelegramService(String botUsername, String botToken, EmployeeService employeeService) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.employeeService = employeeService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
@Async
    @Override
    public void onUpdateReceived(Update updateInfo) {
        try {
            registerTelegram(updateInfo);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    public CompletableFuture<Void> registerTelegram(Update updateInfo) throws TelegramApiException {
        if (updateInfo.hasMessage()) {
            Message message = updateInfo.getMessage();
           // System.out.println(message.getReplyToMessage().getText());
            Long chatId = message.getChatId();

            if (message.getChat().isGroupChat() || message.getChat().isSuperGroupChat()) {
                String groupTitle = message.getChat().getTitle();

            } else if (message.getChat().isUserChat()) {
                String username = message.getChat().getUserName();
                Employee telegramUser = employeeService.findByTelegramUsername(username);
                if (telegramUser != null && telegramUser.getUsername() != null) {
                    if (telegramUser.getTelegramUserId() == null) {
                        int updateResult = employeeService.updateTelegramUserId(chatId,username);
                        LOGGER.info("Update result : "+ updateResult);
                        sendMessage(chatId,"hello ");
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    public Long getChatIdByUsername(String username) {
        return employeeService.getChatIdByUsername(username);
    }

    public void sendMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        execute(message);
    }

    public void sendDocument(Long chatId, String filePath, String description, String creator) throws  TelegramApiException {
        SendDocument sendDocumentRequest = new SendDocument();
        sendDocumentRequest.setChatId(chatId);
        InputFile pdfFile = new InputFile(new File(filePath));
        sendDocumentRequest.setDocument(pdfFile);
        sendDocumentRequest.setCaption(description + "\n" + creator);
        execute(sendDocumentRequest);
    }
}
