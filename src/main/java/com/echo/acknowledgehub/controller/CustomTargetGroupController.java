package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.dto.CustomTargetGroupDTO;
import com.echo.acknowledgehub.dto.CustomTargetGroupEntityDTO;
import com.echo.acknowledgehub.entity.CustomTargetGroup;
import com.echo.acknowledgehub.exception_handler.EmailSenderException;
import com.echo.acknowledgehub.service.CustomTargetGroupService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RestController
@RequestMapping("${app.api.base-url}/custom-target")
@AllArgsConstructor
public class CustomTargetGroupController {
    private static final Logger LOGGER = Logger.getLogger(CustomTargetGroupController.class.getName());
    private final CustomTargetGroupService CUSTOM_TARGET_GROUP_SERVICE;

    @PostMapping
    private CustomTargetGroup save(@RequestBody CustomTargetGroupDTO customTargetGroup){
        return CUSTOM_TARGET_GROUP_SERVICE.save(customTargetGroup).join();
       // throw new EmailSenderException("Test");
    }

    @GetMapping
    private List<CustomTargetGroup> findAll(){
        return CUSTOM_TARGET_GROUP_SERVICE.findAll().join();
    }

    @GetMapping("/{id}")
    private Optional<CustomTargetGroup> findById(@PathVariable("id") Long id){
        return CUSTOM_TARGET_GROUP_SERVICE.findById(id).join();
    }

    @DeleteMapping("/{id}")
    private void deleteById(@PathVariable("id") Long id){
         CUSTOM_TARGET_GROUP_SERVICE.deleteById(id);
    }
}
