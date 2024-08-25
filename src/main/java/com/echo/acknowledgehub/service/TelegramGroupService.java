//package com.echo.acknowledgehub.service;
//
//import com.echo.acknowledgehub.entity.TelegramGroup;
//import com.echo.acknowledgehub.repository.TelegramGroupRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.Optional;
//import java.util.logging.Logger;
//
//@Service
//@AllArgsConstructor
//public class TelegramGroupService {
//
//    private static final Logger LOGGER = Logger.getLogger(TelegramGroupService.class.getName());
//    public final TelegramGroupRepository TELEGRAM_GROUP_REPOSITORY;
//
//    public TelegramGroup findByGroupName(String groupName) {
//        Optional<TelegramGroup> optionalTelegramGroup = TELEGRAM_GROUP_REPOSITORY.findByGroupName(groupName);
//        TelegramGroup telegramGroup = new TelegramGroup();
//        if(optionalTelegramGroup.isPresent()) {
//            telegramGroup = optionalTelegramGroup.get();
//        }
//        return telegramGroup;
//    }
//
//    @Transactional
//    public Long getGroupChatId(String groupName) {
//        return TELEGRAM_GROUP_REPOSITORY.getGroupChatId(groupName);
//    }
//
//    @Transactional
//    public int updateGroupChatId(Long chatId , String groupName) {
//        LOGGER.info("in update group chat : " + chatId);
//        return TELEGRAM_GROUP_REPOSITORY.updateGroupChatId(chatId, groupName);
//    }
//
//}