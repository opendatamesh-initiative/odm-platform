package org.opendatamesh.platform.pp.devops.server.utils;

import org.opendatamesh.platform.pp.devops.server.resources.context.ActivityContext;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for replacing template variables in strings with values from activity contexts.
 * 
 * The templating is done using a pattern like ${activityName.results.vm.ip}. 
 * This template is then replaced with the value found in the activity context.
 */
public class VariableTemplateUtils {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    /**
     * Replaces template variables in the given string with values from the activity context.
     * 
     * @param template the string containing template variables
     * @param context the map of activity contexts keyed by activity name
     * @return the string with variables replaced, or the original string if template or context is null
     */
    public static String replaceVariables(String template, Map<String, ActivityContext> context) {
        if (template == null || context == null) {
            return template;
        }

        String result = template;
        Matcher matcher = VARIABLE_PATTERN.matcher(result);

        while (matcher.find()) {
            String placeholder = matcher.group(0);  // Full match e.g. ${activityName.results.vm.ip}
            String path = matcher.group(1);         // Capture group e.g. activityName.results.vm.ip
            
            String[] parts = path.split("\\.");
            if (parts.length >= 1) {
                String activityName = parts[0];
                ActivityContext activityContext = context.get(activityName);
                
                if (activityContext != null && activityContext.getResults() != null) {
                    Map<String, Object> results = activityContext.getResults();
                    Object value = results;
                    
                    // Navigate nested maps according to path
                    for (int i = 2; i < parts.length && value != null; i++) {
                        if (value instanceof Map) {
                            value = ((Map<?, ?>) value).get(parts[i]);
                        } else {
                            value = null;
                        }
                    }

                    if (value != null) {
                        result = result.replace(placeholder, value.toString());
                    }
                }
            }
        }

        return result;
    }
}
