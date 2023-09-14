package org.opendatamesh.platform.core.dpds.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface ResourceContentChecker {
    public JsonNode verifyAll(String rootEntityContent);
	public JsonNode verifyDescriptorContent(JsonNode rootEntity);
	public JsonNode verifyInfoContent(ObjectNode infoNode);
	public JsonNode verifyInterfaceComponentsContent(ObjectNode interfaceComponentsNode);
	public JsonNode verifyApplicationComponentsContent(ArrayNode appComponentNodes);
	public JsonNode verifyInfrastructuralComponentsContent(ArrayNode infraComponents);
	public JsonNode verifyLifecycleContent(ObjectNode lifecycleNode);
}
