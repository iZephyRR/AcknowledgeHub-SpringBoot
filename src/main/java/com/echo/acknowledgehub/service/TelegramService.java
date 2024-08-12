package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.entity.TelegramGroup;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
public class TelegramService extends TelegramLongPollingBot {

    private static final Logger LOGGER = Logger.getLogger(TelegramService.class.getName());
    private final String botUsername;
    private final String botToken;
    private final EmployeeService employeeService;
    private final TelegramGroupService telegramGroupService;

    public TelegramService(String botUsername, String botToken,
                           EmployeeService employeeService,
                           TelegramGroupService telegramGroupService) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.employeeService = employeeService;
        this.telegramGroupService = telegramGroupService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

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
            Long chatId = message.getChatId();
            Chat chat = message.getChat();

            if (chat.isGroupChat() || chat.isSuperGroupChat()) {
                handleGroupChat(chatId, chat.getTitle());
            } else if (chat.isUserChat()) {
                handleUserChat(chatId, chat.getUserName());
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    private void handleGroupChat(Long chatId, String groupTitle) throws TelegramApiException {
        Optional<TelegramGroup> optionalTelegramGroup = telegramGroupService.findByGroupName(groupTitle);
        TelegramGroup telegramGroup = new TelegramGroup();
        if (optionalTelegramGroup.isPresent()){
            telegramGroup = optionalTelegramGroup.get();
        }
        System.out.println("telegramGroup.getGroup_chatId()" + telegramGroup.getGroupChatId() +"group name"+ telegramGroup.getGroupName());
        if (telegramGroup.getGroupName() != null && telegramGroup.getGroupChatId() == null) {
            int updateResult = telegramGroupService.updateGroupChatId(chatId, groupTitle);
            LOGGER.info("insert group id : "+ updateResult);
            sendMessage(chatId,"Hello Everyone");
        }
    }

    private void handleUserChat(Long chatId, String username) throws TelegramApiException {
        Employee telegramUser = employeeService.findByTelegramUsername(username);
        if (telegramUser != null && telegramUser.getUsername() != null && telegramUser.getTelegramUserId() == null) {
            int updateResult = employeeService.updateTelegramUserId(chatId, username);
            sendMessage(chatId, "Hello A hla lay");
        }
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
