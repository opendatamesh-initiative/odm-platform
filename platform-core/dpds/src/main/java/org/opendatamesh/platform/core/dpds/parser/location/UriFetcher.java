package org.opendatamesh.platform.core.dpds.parser.location;

import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.opendatamesh.platform.core.dpds.exceptions.FetchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class UriFetcher implements DescriptorLocation.Fetcher {

    /**
     * Encoding of the resources to fetch.
     */
    private String encoding;

    private List<AuthorizationValue> authorizationValues;

    private static final String ACCEPT_HEADER_VALUE = "application/json, application/yaml, */*";
    private static final String USER_AGENT_HEADER_VALUE = "Apache-HttpClient/ODMP";

    private static final String TRUST_ALL = String.format("%s.trustAll", UriFetcher.class.getName());
    private static final ConnectionConfigurator CONNECTION_CONFIGURATOR = createConnectionConfigurator();

    private static final Logger logger = LoggerFactory.getLogger(UriFetcher.class);

    /**
     * 
     * @param baseUri can be null, in that case only absolute uri(s) can be fatched
     */
    public UriFetcher() {
        this(null);
    }

    public UriFetcher(String encoding) {
        this(null, null);
    }

    public UriFetcher(String encoding, List<AuthorizationValue> authorizationValues) {

     
        if (encoding == null || encoding.trim().isEmpty()) {
            encoding = StandardCharsets.UTF_8.displayName();
        } else if (Charset.isSupported(encoding)) {
            this.encoding = encoding;
        } else {
            throw new RuntimeException("Encoding [" + encoding + "] not supported");
        }

        if (authorizationValues != null) {
            this.authorizationValues = authorizationValues;
        } else {
            this.authorizationValues = new ArrayList<AuthorizationValue>();
        }

    }

    public String fetch(URI baseUri, URI resourceUri) throws FetchException {
        Objects.requireNonNull(baseUri);
        Objects.requireNonNull(resourceUri);
        if(resourceUri.isAbsolute()) {
            logger.warn("The uri [" + resourceUri + "] to be fetched is alredy absolute. No need to resolve it against base uri [" + baseUri + "]");
        } else {
            if(!baseUri.isAbsolute()) {
                throw new FetchException("Base uri in not absolute [" + baseUri + "]. To fetch a relative uri an absolute base uri must be provided", resourceUri); 
            }
        }
        URI absoluteURI = baseUri.resolve(resourceUri);
        return fetch(absoluteURI.normalize());
    }

    @Override
    public String fetch(URI resourceUri) throws FetchException {

        String content = "";

        Objects.requireNonNull(resourceUri);
        if(!resourceUri.isAbsolute()) {
            throw new FetchException("Impossible to fetch relative uri", resourceUri); 
        }

        String schema = resourceUri.getScheme().toLowerCase();
        if (schema.startsWith("http")) {
            content = fetchFromRemote(resourceUri);
        } else if (schema.toLowerCase().startsWith("jar")) {
            content = fetchFromJar(resourceUri);
        } else if (schema.toLowerCase().startsWith("file")) {
            content = fetchFromFile(resourceUri);
        } else {
            throw new FetchException("Impossible to fetch from [" + schema + "]", resourceUri);
        }

        return content;
    }

    private String fetchFromRemote(URI resourceUri) throws FetchException {
        String content = "";

        URI uriToFetch = resourceUri;
        try {
            URLConnection conn;
            do {
                List<AuthorizationValue> query = new ArrayList<>();
                List<AuthorizationValue> header = new ArrayList<>();
                if (!authorizationValues.isEmpty()) {
                    for (AuthorizationValue authorizationValue : authorizationValues) {
                        if (authorizationValue.getUrlMatcher().test(uriToFetch)) {
                            if ("query".equals(authorizationValue.getType())) {
                                query.add(authorizationValue);
                            } else if ("header".equals(authorizationValue.getType())) {
                                header.add(authorizationValue);
                            }
                        }
                    }
                }

                uriToFetch = setQueryParameters(uriToFetch, query);
                conn = uriToFetch.toURL().openConnection();
                conn = CONNECTION_CONFIGURATOR.process(conn);
                conn = setRequestProperties(conn, header);
                conn.connect();
                uriToFetch = getRedirectionUri(conn);

            } while (uriToFetch != null);

            content = readResponse(conn);
            
        } catch (javax.net.ssl.SSLProtocolException e) {
            logger.warn("there is a problem with the target SSL certificate");
            logger.warn("**** you may want to run with -Djsse.enableSNIExtension=false\n\n");
            throw new FetchException(
                    "Impossible to fetch uri [" + uriToFetch + "]. There is a problem with the target SSL certificate",
                    uriToFetch, e);
        } catch (Exception e) {
            throw new FetchException("Impossible to fetch uri [" + uriToFetch + "]", uriToFetch, e);
        } 

        return content;
    }

    private URI setQueryParameters(URI uri, List<AuthorizationValue> query)
            throws URISyntaxException, UnsupportedEncodingException {

        URI uriToFetch = null;
        if (!query.isEmpty()) {
            final StringBuilder newQuery = new StringBuilder(
                    uri.getQuery() == null ? "" : uri.getQuery());
            for (AuthorizationValue item : query) {
                if (newQuery.length() > 0) {
                    newQuery.append("&");
                }
                newQuery.append(URLEncoder.encode(item.getKeyName(), StandardCharsets.UTF_8.name())).append("=")
                        .append(URLEncoder.encode(item.getValue(), StandardCharsets.UTF_8.name()));
            }
            uriToFetch = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(),
                    newQuery.toString(),
                    uri.getFragment());
        } else {
            uriToFetch = uri;
        }

        return uriToFetch;
    }

    private URLConnection setRequestProperties(URLConnection conn, List<AuthorizationValue> header) {
        for (AuthorizationValue item : header) {
            conn.setRequestProperty(item.getKeyName(), item.getValue());
        }
        conn.setRequestProperty("Accept", ACCEPT_HEADER_VALUE);
        conn.setRequestProperty("User-Agent", USER_AGENT_HEADER_VALUE);

        return conn;
    }

    private String readResponse(URLConnection conn) throws IOException {
        String content = null;

        InputStream inputStream = null;
        BufferedReader reader = null;

        try {
            inputStream = conn.getInputStream();
            StringBuilder contents = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8.name()));
            for (int i = 0; i != -1; i = reader.read()) {
                char c = (char) i;
                if (!Character.isISOControl(c)) {
                    contents.append((char) i);
                }
                if (c == '\n') {
                    contents.append('\n');
                }
            }
            content = contents.toString();
        } finally {
            
            if (inputStream != null) {
                inputStream.close();
            }
            if (reader != null) {
                reader.close();
            }
           
        }

        return content;
    }

    private URI getRedirectionUri(URLConnection conn) throws IOException, URISyntaxException {
        URI redirectionUri = null;

        int responseCode = ((HttpURLConnection) conn).getResponseCode();
        if ((301 == responseCode) || (302 == responseCode)
                || (307 == responseCode) || (308 == responseCode)) {

            String url = ((HttpURLConnection) conn).getHeaderField("Location");
            if (url != null) {
                redirectionUri = new URI(cleanUrl(url));
            } else {
                throw new IOException(
                        "Response code is equal to [" + responseCode + "] but redirection location is not available");
            }
        }
        return redirectionUri;
    }

    private String cleanUrl(String url) {
        String result = null;
        try {
            result = url.replaceAll("\\{", "%7B").replaceAll("\\}", "%7D").replaceAll(" ", "%20");
        } catch (Exception e) {
            throw new RuntimeException("Impossible to clean url [" + url + "]", e);
        }
        return result;
    }

    private String fetchFromJar(URI resourceUri) throws FetchException {
        String content = "";

        try (InputStream in = resourceUri.toURL().openStream()) {
            content = IOUtils.toString(in, encoding);
        } catch (IOException e) {
            throw new FetchException("Impossible to fetch uri [" + resourceUri + "]", resourceUri, e);
        }
        return content;
    }

    private String fetchFromFile(URI resourceUri) throws FetchException {
        String content = "";

        try {
            final Path path = Paths.get(resourceUri);
            if (Files.exists(path)) {
                content = FileUtils.readFileToString(path.toFile(), encoding);
            } else {
                content = loadFileFromClasspath(resourceUri);
            }
        } catch (IOException e) {
            throw new FetchException("Impossible to fetch uri [" + resourceUri + "]", resourceUri, e);
        }

        return content;
    }

    private String loadFileFromClasspath(URI resourceUri) throws FetchException {

        String content = "";

        String location = resourceUri.toString();
        String file = FilenameUtils.separatorsToUnix(location);

        InputStream inputStream = UriFetcher.class.getResourceAsStream(file);

        if (inputStream == null) {
            inputStream = UriFetcher.class.getClassLoader().getResourceAsStream(file);
        }

        if (inputStream == null) {
            inputStream = ClassLoader.getSystemResourceAsStream(file);
        }

        if (inputStream != null) {
            try {
                content = IOUtils.toString(inputStream, Charset.forName(encoding));
            } catch (IOException e) {
                throw new RuntimeException("Could not read " + file + " from the classpath", e);
            }
        } else {
            throw new FetchException("Impossible to fetch file [" + file + "]", resourceUri);
        }

        return content;
    }

    private static ConnectionConfigurator createConnectionConfigurator() {
        ConnectionConfigurator configurator = null;

        if (Boolean.parseBoolean(System.getProperty(TRUST_ALL))) {
            configurator = createTrustAllConnectionConfigurator();
        } else {
            configurator = new ConnectionConfigurator() {

                @Override
                public URLConnection process(URLConnection connection) {
                    // Do nothing
                    return connection;
                }
            };
        }

        return configurator;
    }

    private static ConnectionConfigurator createTrustAllConnectionConfigurator() {
        ConnectionConfigurator configurator = null;
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {

                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sf = sc.getSocketFactory();

            // Create all-trusting host name verifier
            final HostnameVerifier trustAllNames = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            configurator = new ConnectionConfigurator() {

                @Override
                public URLConnection process(URLConnection connection) {
                    if (connection instanceof HttpsURLConnection) {
                        final HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                        httpsConnection.setSSLSocketFactory(sf);
                        httpsConnection.setHostnameVerifier(trustAllNames);
                    }
                    return connection;
                }
            };
        } catch (NoSuchAlgorithmException e) {
            logger.error("Not Supported", e);
        } catch (KeyManagementException e) {
            logger.error("Not Supported", e);
        }

        return configurator;
    }

    private interface ConnectionConfigurator {
        URLConnection process(URLConnection connection);
    }

}
