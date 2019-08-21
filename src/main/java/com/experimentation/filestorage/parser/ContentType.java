package com.experimentation.filestorage.parser;

public enum ContentType {

    JSON(TypeConstants.JSON_PARSER),
    CSV(TypeConstants.CSV_PARSER),
    XML(TypeConstants.XML_PARSER);

    private final String parserName;

    ContentType(String parserName) {
        this.parserName = parserName;
    }

    @Override
    public String toString() {
        return this.parserName;
    }
}
