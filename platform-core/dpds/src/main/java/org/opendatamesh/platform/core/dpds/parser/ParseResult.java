package org.opendatamesh.platform.core.dpds.parser;

import lombok.Data;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;

@Data
public class ParseResult {
    DataProductVersionDPDS descriptorDocument;
}
