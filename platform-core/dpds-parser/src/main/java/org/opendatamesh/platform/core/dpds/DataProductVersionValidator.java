package org.opendatamesh.platform.core.dpds;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.ValidationMessage;
import org.opendatamesh.platform.core.dpds.exceptions.DeserializationException;

import java.net.URI;
import java.util.Comparator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DataProductVersionValidator {

    ObjectMapper mapper;

    private String validationSchemaBaseUrl;

    private String validationSchemaMinSupportedVersion;

    private String validationSchemaMaxSupportedVersion;

    private static final String SCHEMA_URI = "%s/v%s/schema.json";

    private static final String DEFAULT_SCHEMA_URI = "classpath:schemas/v%s/schema.json";

    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+\\.\\d+\\.\\d+).*");

    private Matcher matcher;

    public DataProductVersionValidator(
            String validationSchemaBaseUrl,
            String validationSchemaMinSupportedVersion,
            String validationSchemaMaxSupportedVersion
    ) {
        this.mapper = ObjectMapperFactory.JSON_MAPPER;
        this.validationSchemaBaseUrl = validationSchemaBaseUrl;
        this.validationSchemaMinSupportedVersion = validationSchemaMinSupportedVersion;
        this.validationSchemaMaxSupportedVersion = validationSchemaMaxSupportedVersion;
    }

    public Set<ValidationMessage> validateSchema(JsonNode jsonNode, String version) {

        // Check if version is supported
        if(!isSupported(version))
            version = "1.0.0";

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(VersionFlag.V202012);
        JsonSchema dpdsSchema;
        try {
            dpdsSchema = factory.getSchema(new URI(String.format(SCHEMA_URI, validationSchemaBaseUrl, version)));
        } catch (Exception e) {
            // Back-up: trying to obtain the schema locally
            try {
                dpdsSchema = factory.getSchema(new URI(String.format(DEFAULT_SCHEMA_URI, version)));
            } catch (Exception ex) {
                throw new RuntimeException(
                        "JSON DPDS Schema [" + version + "] not found at path [" +
                                String.format(SCHEMA_URI, validationSchemaBaseUrl, version) + "] " +
                                "and neither at local path [" + String.format(DEFAULT_SCHEMA_URI, version)+ "]. " +
                                "Impossible to validate DPDS."
                );
            }
        }
        Set<ValidationMessage> validationMessages = dpdsSchema.validate(jsonNode);
        return validationMessages;
    }

    public Set<ValidationMessage> validateSchema(String rawContent, String version) throws DeserializationException {
        JsonNode jsonNode;
        try {
            jsonNode = mapper.readTree(rawContent);
        } catch (Throwable t) {
            throw new DeserializationException("Descriptor document is not a valid JSON document", t);
        } 
        return validateSchema(jsonNode, version);
    }

    private Boolean isSupported(String version) {
        if(version == null)
            return false;
        matcher = VERSION_PATTERN.matcher(version);
        if(matcher.matches()) {
            String cleanedVersion = matcher.group(1);
            if(validationSchemaMinSupportedVersion == null && validationSchemaMaxSupportedVersion == null)
                 return false;
            if(validationSchemaMinSupportedVersion == null && validationSchemaMaxSupportedVersion != null) {
                if(compareVersions(cleanedVersion, validationSchemaMaxSupportedVersion) <= 0)
                    return true;
                else
                    return false;
            }
            if(validationSchemaMinSupportedVersion != null && validationSchemaMaxSupportedVersion == null) {
                if(compareVersions(cleanedVersion, validationSchemaMinSupportedVersion) >= 0)
                    return true;
                else
                    return false;
            }
            if(
                    compareVersions(cleanedVersion, validationSchemaMinSupportedVersion) >= 0 &&
                    compareVersions(cleanedVersion, validationSchemaMaxSupportedVersion) <= 0
            )
                return true;
            else
                return false;
        }
        return false;
    }

    private static int compareVersions(String version1, String version2) {
        Comparator<String> vComp = Comparator.comparingInt((String s) -> Integer.parseInt(s.split("\\.")[0]))
                .thenComparingInt(s -> Integer.parseInt(s.split("\\.")[1]))
                .thenComparingInt(s -> Integer.parseInt(s.split("\\.")[2]));

        return vComp.compare(version1, version2);
    }

}
