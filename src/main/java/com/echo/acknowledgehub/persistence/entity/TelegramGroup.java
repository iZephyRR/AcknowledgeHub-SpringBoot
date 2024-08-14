package com.echo.acknowledgehub.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "telegram_group")
public class TelegramGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT")
    private Long id;
    @Column(name = "group_name", unique = true, columnDefinition = "VARCHAR(50)")
    private String groupName;
    @Column(name = "group_chatId", unique = true, columnDefinition = "BIGINT")
    private Long groupChatId;
}
