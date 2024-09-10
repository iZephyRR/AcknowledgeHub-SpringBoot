package com.echo.acknowledgehub.bean;

import com.echo.acknowledgehub.dto.UserDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class TempDataBean {
    private volatile boolean tempStorage=true;

    //Add your temp data field here.(Primitive or non-primitive. !Don't use entity directly.)
    // The data will lose when server stop.
}
