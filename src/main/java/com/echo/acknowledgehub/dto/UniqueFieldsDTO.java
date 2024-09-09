package com.echo.acknowledgehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UniqueFieldsDTO {
    private List<String> emails;
    private List<String> nrcs;
    private List<String> staffIds;
    private List<String> telegramUsernames;
}
