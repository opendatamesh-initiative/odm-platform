package org.opendatamesh.platform.core.dpds.parser;

import java.net.URI;
import java.net.URISyntaxException;

public class UriUtils {

    public static String getBasePath(URI uri) {
        String path = uri.getPath();
        return path.substring(0, path.lastIndexOf('/') + 1);
    }

    public static String getResourceName(URI uri) {
        String path = uri.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public static URI getBaseUri(URI resourceUri) {
        URI baseUri = null;
        resourceUri = resourceUri.normalize();
        String basePath = getBasePath(resourceUri);
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
            throw new RuntimeException("An unexpected exception occured while creating base uri from uri [" + resourceUri.toString() + "]", e);
        }
        return baseUri;
    }

    public static URI getBaseUri(URI baseUri, URI resourceUri) {
        URI absoluteBaseUri = null;
        URI normalizedUri = resourceUri.normalize();
        String path = normalizedUri.getPath();
        String basePath = path.substring(0, path.lastIndexOf('/') + 1);
        try {
            absoluteBaseUri = new URI(
                    baseUri.getScheme(),
                    baseUri.getUserInfo(),
                    baseUri.getHost(),
                    baseUri.getPort(),
                    basePath,
                    baseUri.getQuery(),
                    baseUri.getFragment());
        } catch (URISyntaxException e) {
            throw new RuntimeException(
                    "An unexpected exception occured while creating base uri from uri [" + resourceUri.toString() + "]",
                    e);
        }
        return absoluteBaseUri;
    }
}
