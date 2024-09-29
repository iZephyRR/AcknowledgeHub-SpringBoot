package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.constant.ReceiverType;
import com.echo.acknowledgehub.entity.CustomTargetGroup;
import com.echo.acknowledgehub.entity.CustomTargetGroupEntity;
import com.echo.acknowledgehub.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.Check;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomTargetGroupEntityService {

    private final CustomTargetGroupEntityRepository CUSTOM_TARGET_GROUP_ENTITY_REPOSITORY;
    private final CustomTargetGroupRepository CUSTOM_TARGET_GROUP_REPOSITORY;
    private final DepartmentRepository DEPARTMENT_REPOSITORY;
    private final CompanyRepository COMPANY_REPOSITORY;
    private final EmployeeRepository EMPLOYEE_REPOSITORY;

    public List<CustomTargetGroupEntity> getAllGroupEntity (Long groupId) {
        CustomTargetGroup customTargetGroup = CUSTOM_TARGET_GROUP_REPOSITORY.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Not Found "));
        return CUSTOM_TARGET_GROUP_ENTITY_REPOSITORY.findByCustomTargetGroup(customTargetGroup);
    }

    public String findTargetNameByTypeAndId(ReceiverType receiverType, Long receiverId) {
        switch (receiverType){
            case DEPARTMENT -> {
                return DEPARTMENT_REPOSITORY.findDepartmentNameById(receiverId);
            }
            case COMPANY -> {
                return COMPANY_REPOSITORY.findCompanyNameById(receiverId);
            }
            case EMPLOYEE -> {
                return EMPLOYEE_REPOSITORY.findNameById(receiverId);
            }
            default -> {
                return null;
            }
        }
    }

}
