package org.opendatamesh.platform.core.dpds.parser;

import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;

import lombok.Data;

@Data
public class ParseResult {
    DataProductVersionDPDS descriptorDocument;
}
