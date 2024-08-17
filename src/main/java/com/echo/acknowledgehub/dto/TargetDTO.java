package com.echo.acknowledgehub.dto;

import lombok.Data;

@Data
public class TargetDTO {
    private Long id;
    private String receiverType;
    private Long sendTo;
}
