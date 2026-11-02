package org.opendatamesh.platform.pp.registry.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.internals.LifecycleTaskInfoDPDS;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistryClientTest {

    private static final String RESOURCE_DPV_NORMALIZED = "src/test/resources/dpd-multiple-tasks-normalized.json";
    private static final String RESOURCE_DPV_CANONICAL = "src/test/resources/dpd-multiple-tasks-canonical.json";

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testParseNormalizedLifecycleInfo() throws IOException {
        DataProductVersionDPDS dpv = RegistryClient.parseNormalizedDataProductVersion(read(RESOURCE_DPV_NORMALIZED));

        assertThat(dpv.getInternalComponents().getLifecycleInfo().getStageNames())
                .containsExactlyInAnyOrder("test", "prod", "stage-notemplate", "stage-noconf",
                        "stage-noservice", "stage-empty", "stage-wrong-executor");
        assertThat(describeTasks(dpv, "prod")).containsExactly(
                "prod|prod:task:1|1|azure-devops|{\"organization\":\"andreagioia\",\"project\":\"opendatamesh\",\"pipelineId\":\"3\",\"branch\":\"main\"}",
                "prod|prod:task:2|2|azure-devops|{\"organization\":\"andreagioia\",\"project\":\"opendatamesh\",\"pipelineId\":\"3\",\"branch\":\"main\"}");
    }

    @Test
    public void testParseCanonicalLifecycleInfo() throws IOException {
        DataProductVersionDPDS normalized = RegistryClient.parseNormalizedDataProductVersion(read(RESOURCE_DPV_NORMALIZED));
        DataProductVersionDPDS canonical = RegistryClient.parseNormalizedDataProductVersion(read(RESOURCE_DPV_CANONICAL));

        assertThat(canonical.getInternalComponents().getLifecycleInfo().getStageNames())
                .isEqualTo(normalized.getInternalComponents().getLifecycleInfo().getStageNames());
        for (String stage : normalized.getInternalComponents().getLifecycleInfo().getStageNames()) {
            assertThat(describeTasks(canonical, stage)).isEqualTo(describeTasks(normalized, stage));
        }
    }

    @Test
    public void testParseCanonicalLifecycleInfoKeepsRawContent() throws IOException {
        String descriptorContent = read(RESOURCE_DPV_CANONICAL);

        DataProductVersionDPDS dpv = RegistryClient.parseNormalizedDataProductVersion(descriptorContent);

        assertThat(dpv.getRawContent()).isEqualTo(descriptorContent);
        LifecycleTaskInfoDPDS task = dpv.getInternalComponents().getLifecycleInfo().getTasksInfo("test").get(0);
        assertThat(mapper.readTree(task.getRawContent()))
                .isEqualTo(mapper.readTree(descriptorContent).at("/internalComponents/lifecycleInfo/test/0"));
    }

    private List<String> describeTasks(DataProductVersionDPDS dpv, String stage) throws IOException {
        List<String> tasks = new ArrayList<>();
        for (LifecycleTaskInfoDPDS task : dpv.getInternalComponents().getLifecycleInfo().getTasksInfo(stage)) {
            JsonNode templateDefinition = mapper.readTree(task.getRawContent()).at("/template/definition");
            tasks.add(String.join("|",
                    task.getStageName(),
                    task.getName(),
                    String.valueOf(task.getOrder()),
                    task.getService() != null ? task.getService().getHref() : "null",
                    templateDefinition.isMissingNode() ? "null" : templateDefinition.toString()));
        }
        return tasks;
    }

    private String read(String path) throws IOException {
        return Files.readString(Path.of(path), StandardCharsets.UTF_8);
    }
}
