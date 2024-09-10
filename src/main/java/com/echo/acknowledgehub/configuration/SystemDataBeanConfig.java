package com.echo.acknowledgehub.configuration;

import com.echo.acknowledgehub.bean.SystemDataBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;
import java.util.logging.Logger;

@Component
@AllArgsConstructor
public class SystemDataBeanConfig {

    private static final Logger LOGGER = Logger.getLogger(SystemDataBeanConfig.class.getName());
    private final Properties PROPERTIES= new Properties();
    private final ObjectMapper OBJECT_MAPPER;
    private final SystemDataBean SYSTEM_DATA_BEAN;

    @PostConstruct
    private void postConstruct() throws IOException {
        InputStream input = new FileInputStream("src/main/resources/system-data.properties");
        PROPERTIES.load(input);
        String json = PROPERTIES.getProperty("storage");
        OBJECT_MAPPER.readerForUpdating(SYSTEM_DATA_BEAN).readValue(json);
        LOGGER.info("PostConstruct passed.");
        System.out.println("PostConstruct passed.");
    }

    @PreDestroy
    private void preDestroy() throws IOException{
        OutputStream output = new FileOutputStream("src/main/resources/system-data.properties");
        PROPERTIES.setProperty("storage", String.valueOf(OBJECT_MAPPER.writeValueAsString(SYSTEM_DATA_BEAN)));
        PROPERTIES.store(output, null);
        LOGGER.info("PreDestroy passed.");
        System.out.println("PreDestroy passed.");
    }
}
