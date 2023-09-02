package org.opendatamesh.platform.core.dpds.parser;

import lombok.Data;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;

import com.fasterxml.jackson.databind.ObjectMapper;

@Data
public class ParseContext {
    DescriptorLocation location;
    ParseOptions options;
    ParseResult result;
    ObjectMapper mapper;

    public ParseContext(DescriptorLocation location) {
        this(location, new ParseOptions(), new ParseResult());
    }

    public ParseContext(DescriptorLocation location, ParseOptions options) {
        this(location, options, new ParseResult());
    }

    public ParseContext(DescriptorLocation location, ParseOptions options, ParseResult result) {
        this.location = location;
        this.options = options;
        this.result = result;
    }
}
