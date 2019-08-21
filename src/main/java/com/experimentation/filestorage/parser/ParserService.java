package com.experimentation.filestorage.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParserService {

    Logger logger = LoggerFactory.getLogger(ParserService.class);

    @Autowired
    private ParserFactory parserFactory;

    public String doParse(String parseString, ContentType contentType) {
        Parser parser = parserFactory.getParser(contentType);
        logger.info("ParserService.doParse " + parser);
        return parser.parse(parseString);
    }
}
