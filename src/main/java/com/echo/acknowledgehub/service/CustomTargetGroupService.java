package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.dto.CustomTargetGroupDTO;
import com.echo.acknowledgehub.entity.CustomTargetGroup;
import com.echo.acknowledgehub.entity.CustomTargetGroupEntity;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.repository.CustomTargetGroupEntityRepository;
import com.echo.acknowledgehub.repository.CustomTargetGroupRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class CustomTargetGroupService {
    private static final Logger LOGGER = Logger.getLogger(CustomTargetGroupService.class.getName());
    private final CustomTargetGroupRepository CUSTOM_TARGET_GROUP_REPOSITORY;
    private final CustomTargetGroupEntityRepository CUSTOM_TARGET_GROUP_ENTITY_REPOSITORY;
    private final ModelMapper MAPPER;
    private final CheckingBean CHECKING_BEAN;

    @Async
    public CompletableFuture<CustomTargetGroup> save(CustomTargetGroupDTO customTargetGroupDTO){
        LOGGER.info("Service data : "+customTargetGroupDTO);
        List<CustomTargetGroupEntity> entities = customTargetGroupDTO.getEntities().stream()
                .map(data -> MAPPER.map(data, CustomTargetGroupEntity.class))
                .toList();
        CustomTargetGroup customTargetGroup=new CustomTargetGroup();
        Employee employee=new Employee();
        employee.setId(CHECKING_BEAN.getId());
        customTargetGroup.setEmployee(employee);
        customTargetGroup.setTitle(customTargetGroupDTO.getTitle());
        final CustomTargetGroup responseCustomTargetGroup = CUSTOM_TARGET_GROUP_REPOSITORY.save(customTargetGroup);
        entities.forEach(e->{
            e.setCustomTargetGroup(responseCustomTargetGroup);
        });
        CUSTOM_TARGET_GROUP_ENTITY_REPOSITORY.saveAll(entities);
        return CompletableFuture.completedFuture(responseCustomTargetGroup);
    }

    @Async
    public CompletableFuture<List<CustomTargetGroup>> findAll(){
        return CompletableFuture.completedFuture(CUSTOM_TARGET_GROUP_REPOSITORY.findAll());
    }
    @Async
    public CompletableFuture<Optional<CustomTargetGroup>> findById(Long id){
        return CompletableFuture.completedFuture(CUSTOM_TARGET_GROUP_REPOSITORY.findById(id));
    }

    @Async
    public void deleteById(Long id){
        CUSTOM_TARGET_GROUP_REPOSITORY.deleteById(id);
    }
}
