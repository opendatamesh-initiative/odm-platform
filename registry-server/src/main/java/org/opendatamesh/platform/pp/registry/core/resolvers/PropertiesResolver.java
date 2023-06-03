package org.opendatamesh.platform.pp.registry.core.resolvers;

import org.opendatamesh.platform.pp.registry.core.exceptions.ParseException;
import org.opendatamesh.platform.pp.registry.core.exceptions.UnresolvableReferenceException;

public interface PropertiesResolver {
    public void resolve() throws UnresolvableReferenceException, ParseException;
}
