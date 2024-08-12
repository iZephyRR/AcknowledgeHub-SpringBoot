package com.echo.acknowledgehub.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BaseURL {
    @Value("${app.api.base-url}")
    private String baseUrl;

    public String toString(){
        return this.baseUrl;
    }
}
