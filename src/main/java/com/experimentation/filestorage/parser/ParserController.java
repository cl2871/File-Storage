package com.experimentation.filestorage.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ParserController {

    @Autowired
    private ParserService parserService;

    @GetMapping("/jsonParser/{content}")
    public String jsonParser(@PathVariable("content") String content) {
        return parserService.doParse(content, ContentType.JSON);
    }

    @GetMapping("/xmlParser/{content}")
    public String xmlParser(@PathVariable("content") String content) {
        return parserService.doParse(content, ContentType.XML);
    }
}
