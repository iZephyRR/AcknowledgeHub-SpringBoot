package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.constant.ReceiverType;
import com.echo.acknowledgehub.dto.StringResponseDTO;
import com.echo.acknowledgehub.entity.CustomTargetGroupEntity;
import com.echo.acknowledgehub.service.CustomTargetGroupEntityService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api.base-url}/custom-group-entity")
@AllArgsConstructor
public class CustomTargetGroupEntityController {
    private final CustomTargetGroupEntityService  CUSTOM_TARGET_GROUP_ENTITY_SERVICE;

    @GetMapping
    private StringResponseDTO findTargetNameByTypeAndId(@RequestParam ReceiverType receiverType, @RequestParam Long receiverId){
        return new StringResponseDTO(CUSTOM_TARGET_GROUP_ENTITY_SERVICE.findTargetNameByTypeAndId(receiverType, receiverId));
    }

}
