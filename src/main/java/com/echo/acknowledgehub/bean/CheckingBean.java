package com.echo.acknowledgehub.bean;

import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.constant.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class CheckingBean {
    private EmployeeStatus status;
    private EmployeeRole role;
    private String name;
    private Long id;

    public void refresh() {
        this.status = null;
        this.role = null;
        this.name = null;
        this.id = null;
    }
}
