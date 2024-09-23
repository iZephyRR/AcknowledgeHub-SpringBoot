package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.entity.Target;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class SaveTargetsForSchedule {
    private List<Target> targets;
    private String filename;
    private String filePath;
}
