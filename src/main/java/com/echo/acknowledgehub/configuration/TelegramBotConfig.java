//package com.echo.acknowledgehub.configuration;
//
//import com.echo.acknowledgehub.service.EmployeeService;
//import com.echo.acknowledgehub.service.TelegramGroupService;
//import com.echo.acknowledgehub.service.TelegramService;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.telegram.telegrambots.meta.TelegramBotsApi;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
//
//@Configuration
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
//    public TelegramService telegramService(EmployeeService employeeService, TelegramGroupService telegramGroupService) {
//        return new TelegramService(botUsername, botToken, employeeService, telegramGroupService);
//    }
//}
