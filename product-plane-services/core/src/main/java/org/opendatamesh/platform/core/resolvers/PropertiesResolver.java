package org.opendatamesh.platform.core.resolvers;

import org.opendatamesh.platform.core.exceptions.ParseException;
import org.opendatamesh.platform.core.exceptions.UnresolvableReferenceException;

public interface PropertiesResolver {
    public void resolve() throws UnresolvableReferenceException, ParseException;
}
