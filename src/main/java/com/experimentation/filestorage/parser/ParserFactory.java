package com.experimentation.filestorage.parser;

public interface ParserFactory {
    public Parser getParser(ContentType contentType);
}
