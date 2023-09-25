package org.opendatamesh.platform.core.dpds.processors;

import org.opendatamesh.platform.core.dpds.exceptions.DeserializationException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;

public interface PropertiesProcessor {
    public void process() throws UnresolvableReferenceException, DeserializationException;
}
