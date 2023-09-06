package org.opendatamesh.platform.core.dpds;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThatThrownBy;



import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.dpds.exceptions.DeserializationException;
import org.opendatamesh.platform.core.dpds.exceptions.FetchException;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.exceptions.ValidationException;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSParser;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.ParseResult;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;
import org.opendatamesh.platform.core.dpds.parser.location.UriUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DPDSParserErrorTests extends DPDSTests {

    @Test
    public void parseDpdCoreWithEmptyDescriptorTest() {

        DescriptorLocation location = new UriLocation("     ");

        DPDSParser parser = new DPDSParser();
        assertThatThrownBy(() -> {
            parser.parse(location, DEFAULT_PARSE_OPTION);
        })
        .isInstanceOf(ParseException.class)
        .hasMessageContaining("Impossible to parse root descriptor document")
        .hasCauseInstanceOf(DeserializationException.class)
        .hasRootCauseInstanceOf(MismatchedInputException.class)
        .hasRootCauseMessage("No content to map due to end-of-input\n" + //
                " at [Source: (StringReader); line: 1, column: 6]")
        .hasFieldOrPropertyWithValue("stage", ParseException.Stage.LOAD_ROOT_DOC);
    }

    @Test
    public void parseDpdCoreWithMalformedDescriptorTest() {

        DescriptorLocation location = new UriLocation("{\n" + //
                "  \"dataProductDescriptor\": \"1.0.0\"\n" + //
                "  \"info\": {\n" + //
                "    \"name\": \"dpdCore\"\n" + //
                "  }"  //
        );

        DPDSParser parser = new DPDSParser();
        assertThatThrownBy(() -> {
            parser.parse(location, DEFAULT_PARSE_OPTION);
        })
        .isInstanceOf(ParseException.class)
        .hasMessageContaining("Impossible to parse root descriptor document")
        .hasCauseInstanceOf(DeserializationException.class)
        .hasRootCauseInstanceOf(JsonParseException.class)
        .hasRootCauseMessage("Unexpected character ('\"' (code 34)): was expecting comma to separate Object entries\n" + //
                " at [Source: (String)\"{\n" + //
                "  \"dataProductDescriptor\": \"1.0.0\"\n" + //
                "  \"info\": {\n" + //
                "    \"name\": \"dpdCore\"\n" + //
                "  }\"; line: 3, column: 4]")
        .hasFieldOrPropertyWithValue("stage", ParseException.Stage.LOAD_ROOT_DOC);
    }

    @Test
    public void parseDpdCoreWithMissingDataProductDescriptorTest() {

        String descriptorContent = null;
        try {
            descriptorContent = DPDSTestResources.DPD_CORE.getContent();
        } catch (Throwable t) {
            fail("Impossible to get descriptor location from path [" + DPDSTestResources.DPD_CORE.path + "]", t);
        }

        try {
            ObjectNode rootEntity = (ObjectNode) ObjectMapperFactory.JSON_MAPPER.readTree(descriptorContent);
            rootEntity.remove("dataProductDescriptor");
            descriptorContent = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(rootEntity);
        } catch (Throwable t) {
            fail("Impossible to parese descriptor read from path [" + DPDSTestResources.DPD_CORE.path + "]", t);
        }
        DescriptorLocation location = new UriLocation(descriptorContent);

        DPDSParser parser = new DPDSParser();
        assertThatThrownBy(() -> {
            parser.parse(location, DEFAULT_PARSE_OPTION);
        })
        .isInstanceOf(ParseException.class)
        .hasMessageContaining("Parsed document is invalid")
        .hasRootCauseInstanceOf(ValidationException.class)
        .hasRootCauseMessage("Descriptor document does not comply with DPDS. The following validation errors has been found during validation [[$.dataProductDescriptor: null found, string expected]]")
        .hasFieldOrPropertyWithValue("stage", ParseException.Stage.VALIDATE);
    }

    @Test
    public void parseDpdCoreWithMissingNameTest() {

        String descriptorContent = null;
        try {
            descriptorContent = DPDSTestResources.DPD_CORE.getContent();
        } catch (Throwable t) {
            fail("Impossible to get descriptor location from path [" + DPDSTestResources.DPD_CORE.path + "]", t);
        }

        try {
            ObjectNode rootEntity = (ObjectNode) ObjectMapperFactory.JSON_MAPPER.readTree(descriptorContent);
            ObjectNode infoNode = (ObjectNode) rootEntity.get("info");
            infoNode.remove("name");
            descriptorContent = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(rootEntity);
        } catch (Throwable t) {
            fail("Impossible to parese descriptor read from path [" + DPDSTestResources.DPD_CORE.path + "]", t);
        }
        DescriptorLocation location = new UriLocation(descriptorContent);

        DPDSParser parser = new DPDSParser();
        assertThatThrownBy(() -> {
            parser.parse(location, DEFAULT_PARSE_OPTION);
        })
        .isInstanceOf(ParseException.class)
        .hasMessageContaining("Impossible to process read only properties")
        .hasRootCauseMessage("Impossible to define fqn of product because the name is empty")
        .hasFieldOrPropertyWithValue("stage", ParseException.Stage.RESOLVE_READ_ONLY_PROPERTIES);
    }

    @Test
    public void parseDpdCoreWithMissingVersionTest() {

        String descriptorContent = null;
        try {
            descriptorContent = DPDSTestResources.DPD_CORE.getContent();
        } catch (Throwable t) {
            fail("Impossible to get descriptor location from path [" + DPDSTestResources.DPD_CORE.path + "]", t);
        }

        try {
            ObjectNode rootEntity = (ObjectNode) ObjectMapperFactory.JSON_MAPPER.readTree(descriptorContent);
            ObjectNode infoNode = (ObjectNode) rootEntity.get("info");
            infoNode.remove("version");
            descriptorContent = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(rootEntity);
        } catch (Throwable t) {
            fail("Impossible to parese descriptor read from path [" + DPDSTestResources.DPD_CORE.path + "]", t);
        }
        DescriptorLocation location = new UriLocation(descriptorContent);

        DPDSParser parser = new DPDSParser();
        assertThatThrownBy(() -> {
            parser.parse(location, DEFAULT_PARSE_OPTION);
        })
        .isInstanceOf(ParseException.class)
        .hasMessageContaining("Impossible to process read only properties")
        //.hasRootCauseMessage("Impossible to define fqn of product because the name is empty")
        .hasFieldOrPropertyWithValue("stage", ParseException.Stage.RESOLVE_READ_ONLY_PROPERTIES);
    }

    @Test
    public void parseDpdCoreWithMissingOwnerTest() {

        String descriptorContent = null;
        try {
            descriptorContent = DPDSTestResources.DPD_CORE.getContent();
        } catch (Throwable t) {
            fail("Impossible to get descriptor location from path [" + DPDSTestResources.DPD_CORE.path + "]", t);
        }

        try {
            ObjectNode rootEntity = (ObjectNode) ObjectMapperFactory.JSON_MAPPER.readTree(descriptorContent);
            ObjectNode infoNode = (ObjectNode) rootEntity.get("info");
            infoNode.remove("owner");
            descriptorContent = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(rootEntity);
        } catch (Throwable t) {
            fail("Impossible to parese descriptor read from path [" + DPDSTestResources.DPD_CORE.path + "]", t);
        }
        DescriptorLocation location = new UriLocation(descriptorContent);

        DPDSParser parser = new DPDSParser();
        assertThatThrownBy(() -> {
            parser.parse(location, DEFAULT_PARSE_OPTION);
        })
        .isInstanceOf(ParseException.class)
        .hasMessageContaining("Parsed document is invalid")
        .hasRootCauseMessage("Descriptor document does not comply with DPDS. The following validation errors has been found during validation [[$.info.owner: is missing but it is required]]")
        .hasFieldOrPropertyWithValue("stage", ParseException.Stage.VALIDATE);
    }


    @Test
    public void parseDpdCoreWithOutputPortMissingNameTest() {

        String descriptorContent = null;
        try {
            descriptorContent = DPDSTestResources.DPD_CORE.getContent();
        } catch (Throwable t) {
            fail("Impossible to get descriptor location from path [" + DPDSTestResources.DPD_CORE.path + "]", t);
        }

        try {
            ObjectNode rootEntity = (ObjectNode) ObjectMapperFactory.JSON_MAPPER.readTree(descriptorContent);
            ObjectNode interfaceComponentsNode = (ObjectNode) rootEntity.get("interfaceComponents");
            ArrayNode outputPorts =(ArrayNode)interfaceComponentsNode.get("outputPorts");
            ObjectNode port = (ObjectNode)outputPorts.get(0);
            port.remove("name");
            descriptorContent = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(rootEntity);
        } catch (Throwable t) {
            fail("Impossible to parese descriptor read from path [" + DPDSTestResources.DPD_CORE.path + "]", t);
        }
        DescriptorLocation location = new UriLocation(descriptorContent);

        DPDSParser parser = new DPDSParser();
        assertThatThrownBy(() -> {
            parser.parse(location, DEFAULT_PARSE_OPTION);
        })
        .isInstanceOf(ParseException.class)
        .hasMessageContaining("Impossible to process read only properties")
        //.hasRootCauseMessage("Impossible to define fqn of component because the name is empty")
        .hasFieldOrPropertyWithValue("stage", ParseException.Stage.RESOLVE_READ_ONLY_PROPERTIES);
    }

    @Test
    public void parseDpdCoreWithOutputPortMissingNameVersion() {

        String descriptorContent = null;
        try {
            descriptorContent = DPDSTestResources.DPD_CORE.getContent();
        } catch (Throwable t) {
            fail("Impossible to get descriptor location from path [" + DPDSTestResources.DPD_CORE.path + "]", t);
        }

        try {
            ObjectNode rootEntity = (ObjectNode) ObjectMapperFactory.JSON_MAPPER.readTree(descriptorContent);
            ObjectNode interfaceComponentsNode = (ObjectNode) rootEntity.get("interfaceComponents");
            ArrayNode outputPorts =(ArrayNode)interfaceComponentsNode.get("outputPorts");
            ObjectNode port = (ObjectNode)outputPorts.get(0);
            port.remove("version");
            descriptorContent = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(rootEntity);
        } catch (Throwable t) {
            fail("Impossible to parese descriptor read from path [" + DPDSTestResources.DPD_CORE.path + "]", t);
        }
        DescriptorLocation location = new UriLocation(descriptorContent);

        DPDSParser parser = new DPDSParser();
        assertThatThrownBy(() -> {
            parser.parse(location, DEFAULT_PARSE_OPTION);
        })
        .isInstanceOf(ParseException.class)
        .hasMessageContaining("Impossible to process read only properties")
        //.hasRootCauseMessage("Impossible to define fqn of component because the version type is empty")
        .hasFieldOrPropertyWithValue("stage", ParseException.Stage.RESOLVE_READ_ONLY_PROPERTIES);
    }

    @Test
    public void parseDpdCorePropsReadonlyWrongTest()  {

        DescriptorLocation location = getContentLocation(DPDSTestResources.DPD_CORE_PROPS_READONLY_WRONG);
       
        ParseOptions options = new ParseOptions();
        options.setServerUrl("http://localhost:80");
        options.setValidateReadOnlyProperties(true);
    
        DPDSParser parser = new DPDSParser();
        assertThatThrownBy(() -> {
            parser.parse(location, options);
        })
        .isInstanceOf(ParseException.class)
        .hasMessageContaining("Impossible to process read only properties")
        .hasRootCauseMessage("Invalid value [dataproductx] for field entityType in infoObject. Expected [dataproduct]")
        .hasFieldOrPropertyWithValue("stage", ParseException.Stage.RESOLVE_READ_ONLY_PROPERTIES);
    }

    @Test
    public void parseDpdCoreTestWithWrongExternalRef()  {

        String descriptorContent = null;
        try {
            descriptorContent = DPDSTestResources.DPD_CORE_WITH_EXTERNAL_REF.getContent();
        } catch (Throwable t) {
            fail("Impossible to get descriptor location from path [" + DPDSTestResources.DPD_CORE_WITH_EXTERNAL_REF.path + "]", t);
        }

        try {
            ObjectNode rootEntity = (ObjectNode) ObjectMapperFactory.JSON_MAPPER.readTree(descriptorContent);
            ArrayNode outputPorts =(ArrayNode)rootEntity.at("/interfaceComponents/outputPorts");
            ObjectNode port = (ObjectNode)outputPorts.get(0);
            port.put("$ref", "wrongUri");
            descriptorContent = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(rootEntity);
        } catch (Throwable t) {
            fail("Impossible to parese descriptor read from path [" + DPDSTestResources.DPD_CORE_WITH_EXTERNAL_REF.path + "]", t);
        }
        UriLocation location = new UriLocation(descriptorContent);
        URI baseUri = null;
        try {
            baseUri = UriUtils.getBaseUri(DPDSTestResources.DPD_CORE_WITH_EXTERNAL_REF.getUri());
        } catch (IOException e) {
            fail("Impossible to calculate base uri for resource [" + DPDSTestResources.DPD_CORE_WITH_EXTERNAL_REF.path + "]");
        }
        location.setRootDocumentBaseUri(baseUri);

        DPDSParser parser = new DPDSParser();
        assertThatThrownBy(() -> {
            parser.parse(location, DEFAULT_PARSE_OPTION);
        })
        .isInstanceOf(ParseException.class)
        .hasMessageContaining("Impossible to resolve external reference of root descriptor document")
        .hasRootCauseInstanceOf(FetchException.class)
        //.hasRootCauseMessage("Impossible to fetch file [file:/home/andrea.gioia/Sviluppi/quantyca/open-data-mesh/github/odm-platform/platform-core/dpds/target/test-classes/dpd-references/dpd-core-external/wrongUri]")
        .hasFieldOrPropertyWithValue("stage", ParseException.Stage.RESOLVE_EXTERNAL_REFERENCES);
    }
}
