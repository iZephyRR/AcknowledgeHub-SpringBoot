package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.entity.TelegramGroup;
import com.echo.acknowledgehub.repository.TelegramGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class TelegramGroupService {

    private static final Logger LOGGER = Logger.getLogger(TelegramGroupService.class.getName());
    public final TelegramGroupRepository telegramGroupRepository;


    public Optional<TelegramGroup> findByGroupName(String groupName) {
        LOGGER.info("Group title : "+ groupName);
        return telegramGroupRepository.findByGroupName(groupName);
    }

    @Transactional
    public Long getGroupChatId(String groupName) {
        return telegramGroupRepository.getGroupChatId(groupName);
    }

    @Transactional
    public int updateGroupChatId(Long chatId , String groupName) {
        LOGGER.info("in update group chat : " + chatId);
        return telegramGroupRepository.updateGroupChatId(chatId, groupName);
    }

}
