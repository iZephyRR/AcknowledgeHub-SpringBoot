package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.constant.ContentType;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.entity.TelegramGroup;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

@Service
@AllArgsConstructor
public class TelegramService extends TelegramLongPollingBot {

    private static final Logger LOGGER = Logger.getLogger(TelegramService.class.getName());
    private final String BOT_USERNAME;
    private final String BOT_TOKEN;
    private final EmployeeService EMPLOYEE_SERVICE;
    private final TelegramGroupService TELEGRAM_GROUP_SERVICE;


    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Async
    @Override
    public void onUpdateReceived(Update updateInfo) {
        if (updateInfo.hasCallbackQuery()) {
            CallbackQuery callbackQuery = updateInfo.getCallbackQuery();
            String callbackData = callbackQuery.getData();
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedNow = now.format(formatter); // to save firebase
            if (callbackData.startsWith("seen_confirmed:")) {
                String[] dataParts = callbackData.split(":");
                long announcementId = Long.parseLong(dataParts[1]); // to save firebase
                User user = callbackQuery.getFrom();
                String username = user.getUserName();
                Long chatId = user.getId();
                Long employeeId = EMPLOYEE_SERVICE.getEmployeeIdByTelegramUsername(username); // to save firebase
                LOGGER.info("User " + username + " userId " + employeeId + " clicked for announcement " + announcementId + " at " + formattedNow);
                updateCaption(callbackQuery, chatId);
            }
        } else if (updateInfo.hasMessage()) {
            try {
                registerTelegram(updateInfo);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else if (updateInfo.hasPollAnswer()) {
            handlePollAnswer(updateInfo.getPollAnswer());
        }
    }

    public void updateCaption(CallbackQuery callbackQuery, Long chatId){
        EditMessageCaption editMessage = new EditMessageCaption();
        editMessage.setChatId(callbackQuery.getMessage().getChatId().toString());
        editMessage.setMessageId(callbackQuery.getMessage().getMessageId());
        String newCaption = callbackQuery.getMessage().getCaption() + "\n\nThanks";
        editMessage.setCaption(newCaption);
        editMessage.setReplyMarkup(null);
        try {
            execute(editMessage);
            sendMessageAfterNotice(chatId);
        } catch (TelegramApiException e) {
            LOGGER.info("Error editing message: " + e.getMessage());
        }
    }

    private void sendMessageAfterNotice(long chatId) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Thank you for your noted");
        execute(message);
    }

    @Async
    private CompletableFuture<Void> registerTelegram(Update updateInfo) throws TelegramApiException {
        if (updateInfo.hasMessage()) {
            Message message = updateInfo.getMessage();
            // System.out.println(message.getReplyToMessage().getText());
            Long chatId = message.getChatId();
            if (message.getChat().isGroupChat() || message.getChat().isSuperGroupChat()) {
                String groupTitle = message.getChat().getTitle();
                TelegramGroup telegramGroup = TELEGRAM_GROUP_SERVICE.findByGroupName(groupTitle);
                if (telegramGroup.getGroupName().equals(groupTitle) && telegramGroup.getGroupChatId() == null) {
                    int updateResult = TELEGRAM_GROUP_SERVICE.updateGroupChatId(chatId, groupTitle);
                    sendMessage(chatId, "Hello Everyone.");
                }
            } else if (message.getChat().isUserChat()) {
                String username = message.getChat().getUserName();
                Employee telegramUser = EMPLOYEE_SERVICE.findByTelegramUsername(username);
                if (telegramUser.getUsername().equals(username) && telegramUser.getTelegramUserId() == null) {
                    int updateResult = EMPLOYEE_SERVICE.updateTelegramUserId(chatId, username);
                    LOGGER.info("Update result : " + updateResult);
                    sendMessage(chatId, "Hello");
                }
            }
        }
        return CompletableFuture.completedFuture(null);
    }

//    public Long getChatIdByUserId(Long userId) {
//        return EMPLOYEE_SERVICE.getChatIdByUserId(userId);
//    }

    private Long getChatIdByUsername(String username) {
        return EMPLOYEE_SERVICE.getChatIdByTelegramUsername(username);
    }

    @Async
    public void sendToTelegram(List<Long> chatIdsList, MultipartFile file ,String contentType, Long announcementId, String filePathOrUrl, String title, String creator) {
        LOGGER.info("in send to telegram");
        LOGGER.info("Content Type: " + contentType);
        contentType = contentType.trim();
        if (contentType.startsWith(ContentType.AUDIO.getValues()[0])) {
            LOGGER.info("send audio");
            sendAudioInBatches(chatIdsList, announcementId, filePathOrUrl, title, creator);
        } else if (contentType.startsWith(ContentType.VIDEO.getValues()[0])) {
            LOGGER.info("send video");
            sendVideoInBatches(chatIdsList, announcementId, filePathOrUrl, title, creator);
        } else if (contentType.startsWith(ContentType.IMAGE.getValues()[0])) {
            LOGGER.info("send image");
            sendImageInBatches(chatIdsList, announcementId, filePathOrUrl, title, creator);
        } else if (contentType.equals(ContentType.PDF.getValues()[0]) ||
                contentType.equals(ContentType.EXCEL.getValues()[0]) ||
                contentType.equals(ContentType.EXCEL.getValues()[1])) {
            LOGGER.info("send pdf or excel ");
            sendReportsInBatches(chatIdsList, announcementId, filePathOrUrl, title, creator );
        } else if (contentType.equals(ContentType.ZIP.getValues()[0])) {
            LOGGER.info("send zip ");
            sendZipInBatches(chatIdsList, announcementId,file,title, creator);
        } else {
            LOGGER.info("Unknown content type: " + contentType);
        }
    }

    // handle Poll Answer
    private void handlePollAnswer(PollAnswer pollAnswer) {
        Long userId = pollAnswer.getUser().getId();
        String pollId = pollAnswer.getPollId();
        List<Integer> optionIds = pollAnswer.getOptionIds();
        String answer = pollAnswer.getOptionIds().toString();

        System.out.println("User ID: " + userId);
        System.out.println("Poll ID: " + pollId);
        System.out.println("Chosen Option IDs: " + optionIds.toString());
        System.out.println("Answer : " + answer);
    }

    // send Poll if u want to send to more than one user, call sendPollInBatches
    private void sendPoll(Long groupChatId, String question, String... options) throws TelegramApiException {
        SendPoll poll = SendPoll.builder()
                .chatId(groupChatId)
                .question(question)
                .options(Arrays.asList(options))
                .isAnonymous(false).build();
        execute(poll);
    }

    private void sendPollInBatches(List<Long> groupChatIds, String question, String... options) {
        int batchSize = 30;
        int delay = 1; // 1-second delay between batches
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        for (int i = 0; i < groupChatIds.size(); i += batchSize) {
            List<Long> batch = groupChatIds.subList(i, Math.min(i + batchSize, groupChatIds.size()));
            executor.schedule(() -> {
                for (Long groupChatId : batch) {
                    try {
                        sendPoll(groupChatId, question, options);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, delay * (i / batchSize), TimeUnit.SECONDS);
        }
        executor.shutdown();
    }

    // send message if u want to send to more than one user, call ....InBatches
    public void sendMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        execute(message);
    }

    private void sendMessagesInBatches(List<Long> chatIds, String message) {
        int batchSize = 30;
        int delay = 1; // 1-second delay between batches
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        for (int i = 0; i < chatIds.size(); i += batchSize) {
            List<Long> batch = chatIds.subList(i, Math.min(i + batchSize, chatIds.size()));
            executor.schedule(() -> {
                for (Long chatId : batch) {
                    try {
                        sendMessage(chatId, message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, delay * (i / batchSize), TimeUnit.SECONDS);
        }
        executor.shutdown();
    }

    // send report pdf or excel if u want to send to more than one user, call ....InBatches
    private void sendReports(Long chatId,Long announcementId, String filePathOrUrl, String title, String creator) throws TelegramApiException {
        SendDocument sendDocumentRequest = new SendDocument();
        sendDocumentRequest.setChatId(chatId);
        InputFile file = new InputFile(filePathOrUrl);
        sendDocumentRequest.setDocument(file);
        sendDocumentRequest.setCaption("Title : " + title + "\nSend By : " + creator);
        InlineKeyboardMarkup markupInline = getInlineKeyboardMarkup(announcementId);
        sendDocumentRequest.setReplyMarkup(markupInline);
        LOGGER.info("Before sending to : "+ chatId);
        execute(sendDocumentRequest);
        LOGGER.info("After sending to : "+ chatId);
    }

    private void sendReportsInBatches(List<Long> chatIds,Long announcementId, String filePath, String title, String creator) {
        int batchSize = 30;
        int delay = 1; // 1-second delay between batches
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        for (int i = 0; i < chatIds.size(); i += batchSize) {
            List<Long> batch = chatIds.subList(i, Math.min(i + batchSize, chatIds.size()));
            executor.schedule(() -> {
                for (Long chatId : batch) {
                    try {
                        sendReports(chatId,announcementId, filePath, title, creator);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, delay * (i / batchSize), TimeUnit.SECONDS);
        }
        executor.shutdown();
    }

    //send audio if u want to send to more than one user, call ....InBatches
    private void sendAudio(Long chatId,Long announcementId, String fileUrl, String title, String senderName) throws TelegramApiException {
        SendAudio sendAudio = new SendAudio();
        sendAudio.setChatId(chatId.toString());
        sendAudio.setAudio(new InputFile(fileUrl));
        sendAudio.setCaption("Title : " + title + "\nSent by: " + senderName);
        InlineKeyboardMarkup markupInline = getInlineKeyboardMarkup(announcementId);
        sendAudio.setReplyMarkup(markupInline);
        LOGGER.info("Before sending to : "+chatId);
        execute(sendAudio);
    }

    private void sendAudioInBatches(List<Long> chatIds,Long announcementId, String fileUrl, String title, String senderName) {
        int batchSize = 30;
        int delay = 1; // 1-second delay between batches
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        for (int i = 0; i < chatIds.size(); i += batchSize) {
            List<Long> batch = chatIds.subList(i, Math.min(i + batchSize, chatIds.size()));
            executor.schedule(() -> {
                for (Long chatId : batch) {
                    try {
                        sendAudio(chatId,announcementId, fileUrl, title, senderName);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, delay * (i / batchSize), TimeUnit.SECONDS);
        }
        executor.shutdown();
    }

    // send video if u want to send to more than one user, call ....InBatches
    private void sendVideo(Long chatId,Long announcementId, String fileUrl, String title, String senderName) throws TelegramApiException {
        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(chatId.toString());
        sendVideo.setVideo(new InputFile(fileUrl));
        sendVideo.setCaption("Title : " + title + "\nSent by: " + senderName);
        InlineKeyboardMarkup markupInline = getInlineKeyboardMarkup(announcementId);
        sendVideo.setReplyMarkup(markupInline);
        LOGGER.info("Before sending to : "+chatId);
        execute(sendVideo);
    }

    private void sendVideoInBatches(List<Long> chatIds,Long announcementId, String fileUrl, String filename, String senderName) {
        int batchSize = 30;
        int delay = 1; // 1-second delay between batches
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        for (int i = 0; i < chatIds.size(); i += batchSize) {
            List<Long> batch = chatIds.subList(i, Math.min(i + batchSize, chatIds.size()));
            executor.schedule(() -> {
                for (Long chatId : batch) {
                    try {
                        sendVideo(chatId,announcementId, fileUrl, filename, senderName);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, delay * (i / batchSize), TimeUnit.SECONDS);
        }
        executor.shutdown();
    }

    // send image if u want to send to more than one user, call ....InBatches
    private void sendImage(Long chatId,Long announcementId, String fileUrl, String title, String senderName) throws TelegramApiException {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setPhoto(new InputFile(fileUrl));
        sendPhoto.setCaption("Title : " + title + "\nSent by: " + senderName);
        InlineKeyboardMarkup markupInline = getInlineKeyboardMarkup(announcementId);
        sendPhoto.setReplyMarkup(markupInline);
        LOGGER.info("Before sending to :"+ chatId);
        execute(sendPhoto);
    }

    private void sendImageInBatches(List<Long> chatIds,Long announcementId, String fileUrl, String title, String senderName) {
        int batchSize = 30;
        int delay = 1; // 1-second delay between batches
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        for (int i = 0; i < chatIds.size(); i += batchSize) {
            List<Long> batch = chatIds.subList(i, Math.min(i + batchSize, chatIds.size()));
            executor.schedule(() -> {
                for (Long chatId : batch) {
                    try {
                        sendImage(chatId,announcementId, fileUrl, title, senderName);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, delay * (i / batchSize), TimeUnit.SECONDS);
        }
        executor.shutdown();
    }

    // send zip if u want to send to more than one user, call ....InBatches
    private void sendZip(Long chatId,Long announcementId, MultipartFile file, String title, String senderName) throws TelegramApiException, IOException {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setPhoto(new InputFile(file.getResource().getInputStream(), file.getOriginalFilename()));
        sendPhoto.setCaption("Title : " + title + "\nSent by: " + senderName);
        InlineKeyboardMarkup markupInline = getInlineKeyboardMarkup(announcementId);
        sendPhoto.setReplyMarkup(markupInline);
        LOGGER.info("Before sending to :"+ chatId);
        execute(sendPhoto);
    }

    public void sendZipInBatches(List<Long> chatIds, Long announcementId, MultipartFile file, String title, String senderName) {
        int batchSize = 30;
        int delay = 1; // 1-second delay between batches
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        for (int i = 0; i < chatIds.size(); i += batchSize) {
            List<Long> batch = chatIds.subList(i, Math.min(i + batchSize, chatIds.size()));
            executor.schedule(() -> {
                for (Long chatId : batch) {
                    try {
                        sendZip(chatId,announcementId, file, title, senderName);
                    } catch (TelegramApiException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, delay * (i / batchSize), TimeUnit.SECONDS);
        }
        executor.shutdown();
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(Long announcementId) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Click For Your Confirmation");
        button.setCallbackData("seen_confirmed:"+ announcementId);
        rowInline.add(button);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

}
