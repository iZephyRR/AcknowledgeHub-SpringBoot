package com.echo.acknowledgehub.constant;

public enum ContentType {
    AUDIO("audio/"),
    VIDEO("video/"),
    PDF("application/pdf"),
    EXCEL("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    IMAGE("image/");

    private final String[] values;

    ContentType(String... values) {
        this.values = values;
    }

    public String[] getValues() {
        return values;
    }

    public String getFirstValue() {
        return values[0];  // Return the first MIME type
    }

    @Override
    public String toString() {
        return this.name();  // Default to the enum name
    }
}

