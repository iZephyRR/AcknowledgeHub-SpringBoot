//package com.echo.acknowledgehub.repository;
//
//
//import com.echo.acknowledgehub.entity.TelegramGroup;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface TelegramGroupRepository extends JpaRepository<TelegramGroup, Long> {
//
//    Optional<TelegramGroup> findByGroupName(String groupName);
//
//    @Query("SELECT tg.groupChatId FROM TelegramGroup tg WHERE tg.groupName = :groupName")
//    Long getGroupChatId(@Param("groupName") String groupName);
//
//    @Modifying
//    @Query("UPDATE TelegramGroup tg SET tg.groupChatId = :chatId WHERE tg.groupName = :groupName")
//    int updateGroupChatId(@Param("chatId") Long telegramGroupChatId, @Param("groupName") String telegramGroupName);
//
//
//}
