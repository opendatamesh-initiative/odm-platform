package org.opendatamesh.platform.pp.devops.server.utils;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.devops.server.resources.context.ActivityContext;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class VariableTemplateUtilsTest {

    @Test
    void testReplaceVariables_WithNullTemplate_ReturnsNull() {
        Map<String, ActivityContext> context = new HashMap<>();
        String result = VariableTemplateUtils.replaceVariables(null, context);
        assertThat(result).isNull();
    }

    @Test
    void testReplaceVariables_WithNullContext_ReturnsOriginalTemplate() {
        String template = "Hello ${test.value}";
        String result = VariableTemplateUtils.replaceVariables(template, null);
        assertThat(result).isEqualTo(template);
    }

    @Test
    void testReplaceVariables_WithEmptyTemplate_ReturnsEmptyString() {
        Map<String, ActivityContext> context = new HashMap<>();
        String result = VariableTemplateUtils.replaceVariables("", context);
        assertThat(result).isEqualTo("");
    }

    @Test
    void testReplaceVariables_WithNoVariables_ReturnsOriginalTemplate() {
        Map<String, ActivityContext> context = new HashMap<>();
        String template = "This is a plain string without variables";
        String result = VariableTemplateUtils.replaceVariables(template, context);
        assertThat(result).isEqualTo(template);
    }

    @Test
    void testReplaceVariables_WithSimpleVariable_ReplacesCorrectly() {
        Map<String, ActivityContext> context = new HashMap<>();
        ActivityContext activityContext = new ActivityContext();
        Map<String, Object> results = new HashMap<>();
        results.put("value", "testValue");
        activityContext.setResults(results);
        context.put("test", activityContext);

        String template = "Hello ${test.results.value}";
        String result = VariableTemplateUtils.replaceVariables(template, context);
        assertThat(result).isEqualTo("Hello testValue");
    }

    @Test
    void testReplaceVariables_WithNestedVariable_ReplacesCorrectly() {
        Map<String, ActivityContext> context = new HashMap<>();
        ActivityContext activityContext = new ActivityContext();
        Map<String, Object> results = new HashMap<>();
        
        Map<String, Object> vmMap = new HashMap<>();
        vmMap.put("ip", "192.168.1.1");
        vmMap.put("port", "8080");
        
        results.put("vm", vmMap);
        activityContext.setResults(results);
        context.put("activityName", activityContext);

        String template = "Server: ${activityName.results.vm.ip}:${activityName.results.vm.port}";
        String result = VariableTemplateUtils.replaceVariables(template, context);
        assertThat(result).isEqualTo("Server: 192.168.1.1:8080");
    }

    @Test
    void testReplaceVariables_WithMultipleVariables_ReplacesAllCorrectly() {
        Map<String, ActivityContext> context = new HashMap<>();
        
        // First activity
        ActivityContext activity1 = new ActivityContext();
        Map<String, Object> results1 = new HashMap<>();
        results1.put("name", "database");
        results1.put("port", "5432");
        activity1.setResults(results1);
        context.put("db", activity1);
        
        // Second activity
        ActivityContext activity2 = new ActivityContext();
        Map<String, Object> results2 = new HashMap<>();
        results2.put("name", "webapp");
        results2.put("port", "8080");
        activity2.setResults(results2);
        context.put("app", activity2);

        String template = "Database: ${db.results.name}:${db.results.port}, App: ${app.results.name}:${app.results.port}";
        String result = VariableTemplateUtils.replaceVariables(template, context);
        assertThat(result).isEqualTo("Database: database:5432, App: webapp:8080");
    }

    @Test
    void testReplaceVariables_WithNonExistentActivity_LeavesVariableUnchanged() {
        Map<String, ActivityContext> context = new HashMap<>();
        String template = "Hello ${nonexistent.results.value}";
        String result = VariableTemplateUtils.replaceVariables(template, context);
        assertThat(result).isEqualTo(template);
    }

    @Test
    void testReplaceVariables_WithActivityWithNullResults_LeavesVariableUnchanged() {
        Map<String, ActivityContext> context = new HashMap<>();
        ActivityContext activityContext = new ActivityContext();
        activityContext.setResults(null);
        context.put("test", activityContext);

        String template = "Hello ${test.results.value}";
        String result = VariableTemplateUtils.replaceVariables(template, context);
        assertThat(result).isEqualTo(template);
    }

    @Test
    void testReplaceVariables_WithNonExistentNestedPath_LeavesVariableUnchanged() {
        Map<String, ActivityContext> context = new HashMap<>();
        ActivityContext activityContext = new ActivityContext();
        Map<String, Object> results = new HashMap<>();
        results.put("value", "testValue");
        activityContext.setResults(results);
        context.put("test", activityContext);

        String template = "Hello ${test.results.nonexistent.value}";
        String result = VariableTemplateUtils.replaceVariables(template, context);
        assertThat(result).isEqualTo(template);
    }

    @Test
    void testReplaceVariables_WithNonMapValueInPath_LeavesVariableUnchanged() {
        Map<String, ActivityContext> context = new HashMap<>();
        ActivityContext activityContext = new ActivityContext();
        Map<String, Object> results = new HashMap<>();
        results.put("value", "testValue"); // This is a String, not a Map
        activityContext.setResults(results);
        context.put("test", activityContext);

        String template = "Hello ${test.results.value.nested}";
        String result = VariableTemplateUtils.replaceVariables(template, context);
        assertThat(result).isEqualTo(template);
    }

    @Test
    void testReplaceVariables_WithNullValueInPath_LeavesVariableUnchanged() {
        Map<String, ActivityContext> context = new HashMap<>();
        ActivityContext activityContext = new ActivityContext();
        Map<String, Object> results = new HashMap<>();
        results.put("value", null);
        activityContext.setResults(results);
        context.put("test", activityContext);

        String template = "Hello ${test.results.value}";
        String result = VariableTemplateUtils.replaceVariables(template, context);
        assertThat(result).isEqualTo(template);
    }

    @Test
    void testReplaceVariables_WithEmptyActivityName_LeavesVariableUnchanged() {
        Map<String, ActivityContext> context = new HashMap<>();
        String template = "Hello ${.results.value}";
        String result = VariableTemplateUtils.replaceVariables(template, context);
        assertThat(result).isEqualTo(template);
    }

    @Test
    void testReplaceVariables_WithComplexNestedStructure_ReplacesCorrectly() {
        Map<String, ActivityContext> context = new HashMap<>();
        ActivityContext activityContext = new ActivityContext();
        Map<String, Object> results = new HashMap<>();
        
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> database = new HashMap<>();
        database.put("host", "localhost");
        database.put("port", 5432);
        database.put("name", "mydb");
        config.put("database", database);
        
        Map<String, Object> server = new HashMap<>();
        server.put("host", "0.0.0.0");
        server.put("port", 8080);
        config.put("server", server);
        
        results.put("config", config);
        activityContext.setResults(results);
        context.put("setup", activityContext);

        String template = "DB: ${setup.results.config.database.host}:${setup.results.config.database.port}/${setup.results.config.database.name}, Server: ${setup.results.config.server.host}:${setup.results.config.server.port}";
        String result = VariableTemplateUtils.replaceVariables(template, context);
        assertThat(result).isEqualTo("DB: localhost:5432/mydb, Server: 0.0.0.0:8080");
    }

    @Test
    void testReplaceVariables_WithNonStringValues_ConvertsToString() {
        Map<String, ActivityContext> context = new HashMap<>();
        ActivityContext activityContext = new ActivityContext();
        Map<String, Object> results = new HashMap<>();
        results.put("number", 42);
        results.put("boolean", true);
        results.put("decimal", 3.14);
        activityContext.setResults(results);
        context.put("test", activityContext);

        String template = "Number: ${test.results.number}, Boolean: ${test.results.boolean}, Decimal: ${test.results.decimal}";
        String result = VariableTemplateUtils.replaceVariables(template, context);
        assertThat(result).isEqualTo("Number: 42, Boolean: true, Decimal: 3.14");
    }

    @Test
    void testReplaceVariables_WithMalformedVariable_LeavesUnchanged() {
        Map<String, ActivityContext> context = new HashMap<>();
        String template = "Hello ${test.results.value and ${another.malformed";
        String result = VariableTemplateUtils.replaceVariables(template, context);
        assertThat(result).isEqualTo(template);
    }

    @Test
    void testReplaceVariables_WithEmptyVariable_LeavesUnchanged() {
        Map<String, ActivityContext> context = new HashMap<>();
        String template = "Hello ${}";
        String result = VariableTemplateUtils.replaceVariables(template, context);
        assertThat(result).isEqualTo(template);
    }

    @Test
    void testReplaceVariables_WithSameVariableMultipleTimes_ReplacesAll() {
        Map<String, ActivityContext> context = new HashMap<>();
        ActivityContext activityContext = new ActivityContext();
        Map<String, Object> results = new HashMap<>();
        results.put("value", "repeated");
        activityContext.setResults(results);
        context.put("test", activityContext);

        String template = "${test.results.value} ${test.results.value} ${test.results.value}";
        String result = VariableTemplateUtils.replaceVariables(template, context);
        assertThat(result).isEqualTo("repeated repeated repeated");
    }
}
