package com.experimentation.filestorage.parser;

import org.springframework.stereotype.Component;

@Component(TypeConstants.XML_PARSER)
public class XMLParser implements Parser{

    @Override
    public String parse(String str) {
        return "XMLParser.parse :: " + str;
    }
}
