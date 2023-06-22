package org.opendatamesh.platform.core.dpds.resolvers;

import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;

public interface PropertiesResolver {
    public void resolve() throws UnresolvableReferenceException, ParseException;
}
