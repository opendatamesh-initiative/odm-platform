package org.opendatamesh.platform.pp.policy.server.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource.EventType;

import static org.assertj.core.api.Assertions.assertThat;

class SpELUtilsTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String V1_DESCRIPTOR_INPUT =
            "{" +
            "\"currentState\": {}," +
            "\"afterState\": {" +
            "\"dataProductVersion\": {" +
            "\"info\": { \"domain\": \"sampleDomain\", \"name\": \"tripExecution\" }" +
            "}" +
            "}" +
            "}";

    private static final String V2_ACTIVITY_INPUT =
            "{" +
            "\"afterState\": {" +
            "\"activity\": { \"uuid\": \"a-1\", \"name\": \"prod\", \"sortOrder\": 2, \"status\": \"PENDING\" }," +
            "\"otherActivities\": [" +
            "{ \"uuid\": \"a-0\", \"name\": \"dev\", \"sortOrder\": 1, \"status\": \"SUCCEEDED\" }" +
            "]" +
            "}" +
            "}";

    private static final String V2_NO_SIBLINGS_INPUT =
            "{" +
            "\"afterState\": {" +
            "\"activity\": { \"uuid\": \"a-1\", \"name\": \"prod\", \"sortOrder\": 1, \"status\": \"PENDING\" }," +
            "\"otherActivities\": []" +
            "}" +
            "}";

    @Test
    void whenV1DescriptorExpressionMatches_thenTrue() {
        assertThat(matches(V1_DESCRIPTOR_INPUT,
                "afterState.dataProductVersion.info.domain == 'sampleDomain' && afterState.dataProductVersion.info.domain != 'sampleDomainTwo'"))
                .isTrue();
    }

    @Test
    void whenV1DescriptorExpressionDoesNotMatch_thenFalse() {
        assertThat(matches(V1_DESCRIPTOR_INPUT,
                "afterState.dataProductVersion.info.domain == 'otherDomain'"))
                .isFalse();
    }

    @Test
    void whenV2ActivityFieldsMatch_thenTrue() {
        assertThat(matches(V2_ACTIVITY_INPUT,
                "afterState.activity.name == 'prod' && afterState.activity.status == 'PENDING'"))
                .isTrue();
    }

    @Test
    void whenV2ActivityStatusDiffers_thenFalse() {
        assertThat(matches(V2_ACTIVITY_INPUT,
                "afterState.activity.name == 'prod' && afterState.activity.status == 'RUNNING'"))
                .isFalse();
    }

    @Test
    void whenSiblingActivityMatchesSelection_thenTrue() {
        assertThat(matches(V2_ACTIVITY_INPUT,
                "afterState.otherActivities.?[name == 'dev' and status == 'SUCCEEDED'].size() > 0"))
                .isTrue();
    }

    @Test
    void whenNoSiblingActivityMatchesSelection_thenFalse() {
        assertThat(matches(V2_NO_SIBLINGS_INPUT,
                "afterState.otherActivities.?[name == 'dev' and status == 'SUCCEEDED'].size() > 0"))
                .isFalse();
    }

    @Test
    void whenKeyIsMissing_thenItReadsAsNull() {
        assertThat(matches(V2_ACTIVITY_INPUT, "afterState.missing == null")).isTrue();
        assertThat(matches(V2_ACTIVITY_INPUT, "afterState['missing'] == null")).isTrue();
    }

    @Test
    void whenBracketKeyNavigation_thenMatches() {
        assertThat(matches(V2_ACTIVITY_INPUT, "afterState['activity']['name'] == 'prod'")).isTrue();
    }

    @Test
    void whenMethodCalledOnStringValue_thenItWorks() {
        assertThat(matches(V2_ACTIVITY_INPUT, "afterState.activity.name.startsWith('pr')")).isTrue();
    }

    @Test
    void whenTypeReferenceUsed_thenNotEvaluatedAndFalse() {
        assertThat(matches(V2_ACTIVITY_INPUT, "T(java.lang.Runtime).getRuntime() != null")).isFalse();
    }

    @Test
    void whenConstructorUsed_thenNotEvaluatedAndFalse() {
        assertThat(matches(V2_ACTIVITY_INPUT, "new java.io.File('/tmp').exists()")).isFalse();
    }

    @Test
    void whenGetClassIsCalled_thenEvaluationErrorFiltersOut() {
        // "|| true" makes the expression true whenever its left side evaluates without error.
        // A rejected getClass() call must therefore make the whole expression fail (false).
        assertThat(matches(V2_ACTIVITY_INPUT,
                "afterState.activity.name.getClass() != null || true")).isFalse();
        assertThat(matches(V2_ACTIVITY_INPUT,
                "afterState.activity.name.getClass().forName('java.lang.Runtime') != null || true")).isFalse();
    }

    @Test
    void whenNoErrorOccurs_thenOrWithTrueIsTrue() {
        // Control for the test above: proves "|| true" alone does not force false.
        assertThat(matches(V2_ACTIVITY_INPUT, "afterState.activity.name.startsWith('pr') || true")).isTrue();
    }

    @Test
    void whenResultIsNotBoolean_thenFalse() {
        assertThat(matches(V2_ACTIVITY_INPUT, "afterState.activity.name")).isFalse();
    }

    @Test
    void whenResultIsNull_thenFalseWithoutNullPointer() {
        assertThat(matches(V2_ACTIVITY_INPUT, "afterState.missing")).isFalse();
    }

    @Test
    void whenNavigatingIntoNullValue_thenEvaluationErrorFiltersOut() {
        assertThat(matches(V2_ACTIVITY_INPUT, "afterState.missing.name == 'x'")).isFalse();
    }

    @Test
    void whenEvaluated_thenSharedJsonMapperIsNotMutated() {
        matches(V1_DESCRIPTOR_INPUT,
                "afterState.dataProductVersion.info.domain == 'sampleDomain'");
        matches(V2_ACTIVITY_INPUT, "afterState.activity.name == 'prod'");

        assertThat(ObjectMapperFactory.JSON_MAPPER.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES))
                .isFalse();
    }

    private static boolean matches(String input, String expression) {
        try {
            JsonNode inputObject = MAPPER.readTree(input);
            return SpELUtils.eventObjectMatchesSpelExpression(inputObject, expression, EventType.ACTIVITY_EXECUTION_RESULT);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
