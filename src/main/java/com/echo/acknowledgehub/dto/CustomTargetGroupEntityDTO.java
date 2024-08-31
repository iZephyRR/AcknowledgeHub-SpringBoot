package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.ReceiverType;
import lombok.Data;

@Data
public class CustomTargetGroupEntityDTO {
    private Long sendTo;
    private ReceiverType receiverType;
}
