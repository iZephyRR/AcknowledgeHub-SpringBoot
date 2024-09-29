package com.echo.acknowledgehub.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomTargetGroupDTO {

    private String title;
    private List<CustomTargetGroupEntityDTO> customTargetGroupEntities;

}
