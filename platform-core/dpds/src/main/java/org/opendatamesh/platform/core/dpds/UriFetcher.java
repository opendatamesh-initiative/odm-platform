package org.opendatamesh.platform.core.dpds;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.opendatamesh.platform.core.dpds.exceptions.FetchException;

public class UriFetcher implements DataProductVersionSource.Fetcher {

    URI baseUri;
    
    /**
     * 
     * @param baseUri can be null, in that case only absolute uri(s) can be fatched
     */
    public UriFetcher(URI baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public String fetch(URI resourceUri) throws FetchException {
        String content = "";

        if(!baseUri.isAbsolute() && baseUri == null) {
            throw new RuntimeException("Impossible to fetch relative uri [" + resourceUri + "]");
        }

        URI absoluteURI = baseUri.resolve(resourceUri);
        
        BufferedReader in;
        try {
            in = new BufferedReader(
                    new InputStreamReader(absoluteURI.toURL().openStream()));
            String line;
            while ((line = in.readLine()) != null)
                content +=  line + "\n";
            in.close();
        } catch(Exception e) {
            throw new FetchException("Impossible to fetch uri [" + resourceUri + "]", resourceUri, e);
        }

        return content;
    }
    
}
