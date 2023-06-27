package org.opendatamesh.platform.core.dpds.parser;

import lombok.Data;

@Data
public class ParseContext {
    ParseLocation location;
    ParseOptions options;
    ParseResult result;

    public ParseContext(ParseLocation location) {
        this(location, new ParseOptions(), new ParseResult());
    }

    public ParseContext(ParseLocation location, ParseOptions options) {
        this(location, options, new ParseResult());
    }

    public ParseContext(ParseLocation location, ParseOptions options, ParseResult result) {
        this.location = location;
        this.options = options;
        this.result = result;
    }
}
