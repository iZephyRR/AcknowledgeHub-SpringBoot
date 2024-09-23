//package com.echo.acknowledgehub.configuration;
//
//import com.echo.acknowledgehub.service.*;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.telegram.telegrambots.meta.TelegramBotsApi;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
//
//@Configuration
//@EnableAsync(proxyTargetClass = true)
//public class TelegramBotConfig {
//    @Value("${telegram.bot.username}")
//    private String botUsername;
//
//    @Value("${telegram.bot.token}")
//    private String botToken;
//
//    @Bean
//    public TelegramBotsApi telegramBotsApi(TelegramService telegramService) throws TelegramApiException {
//        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
//        telegramBotsApi.registerBot(telegramService);
//        return telegramBotsApi;
//    }
//
//    @Bean
//    public TelegramService telegramService(EmployeeService employeeService, TelegramGroupService telegramGroupService, FirebaseNotificationService firebaseNotificationService, AnnouncementService announcementService) {
//        return new TelegramService(botUsername, botToken, employeeService, telegramGroupService,firebaseNotificationService, announcementService);
//    }
//}