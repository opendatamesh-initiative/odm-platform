package org.opendatamesh.platform.core.dpds.parser.location;

import java.net.URI;
import java.net.URISyntaxException;

public class UriUtils {

    public static String getNormalizedPath(URI uri) {
        return uri.normalize().getPath();
    }

    public static String getResourcePath(URI uri) {
        String path = getNormalizedPath(uri);
        return path.substring(0, path.lastIndexOf('/') + 1);
    }

    public static String getResourceName(URI uri) {
        String path = getNormalizedPath(uri);
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public static URI getResourcePathUri(URI resourceUri) {
        URI baseUri = null;
        resourceUri = resourceUri.normalize();
        String basePath = getResourcePath(resourceUri);
        try {
            baseUri = new URI(
                    resourceUri.getScheme(),
                    resourceUri.getUserInfo(),
                    resourceUri.getHost(),
                    resourceUri.getPort(),
                    basePath,
                    resourceUri.getQuery(),
                    resourceUri.getFragment());
        } catch (URISyntaxException e) {
            throw new RuntimeException(
                    "An unexpected exception occured while creating base uri from uri [" + resourceUri.toString() + "]",
                    e);
        }
        return baseUri;
    }

    public static URI getResourceAbsolutePathUri(URI baseUri, URI resourceUri) throws URISyntaxException {
        URI resAbsoluteUri = getResourceAbsoluteUri(baseUri, resourceUri); 
        return getResourcePathUri(resAbsoluteUri);
    }

    public static URI getResourceAbsoluteUri(URI baseUri, URI resourceUri) throws URISyntaxException {
        URI absoluteResourceUri = null;

        if(resourceUri.isAbsolute()) {
            absoluteResourceUri = resourceUri;
        } else if(baseUri != null && baseUri.isAbsolute()){
           absoluteResourceUri  = baseUri.resolve(resourceUri);
        } else {
            new URISyntaxException(resourceUri.toString(), "refUri is relative and baseUri is not defined or relative");
        }

        return absoluteResourceUri.normalize();
    }

    public static URI getResourceAbsolutePathUri(URI resourceUri) throws URISyntaxException {
        return getResourceAbsolutePathUri(null, resourceUri);
    }

    
}
