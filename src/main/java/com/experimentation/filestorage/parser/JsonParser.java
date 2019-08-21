package com.experimentation.filestorage.parser;

import org.springframework.stereotype.Component;

@Component(TypeConstants.JSON_PARSER)
public class JsonParser implements Parser {

    @Override
    public String parse(String str) {
        return "JsonParser.parse::" + str;
    }
}
