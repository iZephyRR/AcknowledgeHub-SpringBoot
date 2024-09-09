package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.ReceiverType;
import lombok.Data;

@Data
public class TargetDTO {
    private Long id;
    private ReceiverType receiverType;
    private Long sendTo;
}
