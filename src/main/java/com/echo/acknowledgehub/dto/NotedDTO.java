package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.ReceiverType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotedDTO {
    private String receiverName;
    private ReceiverType receiverType;
    private Long receiverId;
    private double notedProgress;
    private List<NotedDTO> childPreviews = new LinkedList<>();

    public NotedDTO (ReceiverType receiverType, Long receiverId){
        this.receiverType=receiverType;
        this.receiverId=receiverId;
    }
}
