package com.echo.acknowledgehub.bean;

import com.echo.acknowledgehub.entity.Company;
import com.echo.acknowledgehub.entity.Employee;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class SystemDataBean {
    private volatile boolean checkPasswordEveryTime;
    private String defaultPassword;
    private int sessionExpireTime;

    //Add your system data field(Important data) here. Only primitive type allow here.
    // When remove a data field, you need to remove in system-data.properties file.
}
