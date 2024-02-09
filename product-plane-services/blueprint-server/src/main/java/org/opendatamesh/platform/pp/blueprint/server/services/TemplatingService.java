package org.opendatamesh.platform.pp.blueprint.server.services;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.opendatamesh.platform.pp.blueprint.api.resources.ConfigResource;
import org.opendatamesh.platform.pp.blueprint.server.utils.CustomFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class TemplatingService {

    @Autowired
    @Qualifier("apacheVelocityEngine")
    private VelocityEngine velocityEngine;

    @Value("${git.templates.variable.delimiter.start}")
    private String velocityVariableDelimiterStart;

    @Value("${git.templates.variable.delimiter.stop}")
    private String velocityVariableDelimiterStop;

    public void templating(File workingDirectory, ConfigResource configResource) {
        VelocityContext velocityContext = createVelocityContext(configResource);
        processDirectory(workingDirectory, velocityContext);
    }

    private VelocityContext createVelocityContext(ConfigResource configResource) {
        Map<String, String> parameters = configResource.getConfig();
        VelocityContext velocityContext = new VelocityContext();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            velocityContext.put(entry.getKey(), entry.getValue());
        }
        return velocityContext;
    }

    private void processDirectory(File workingDirectory, VelocityContext velocityContext) {

        File[] files = workingDirectory.listFiles();

        if(
                files != null
                && !workingDirectory.getName().contains(".git")
        ) {
            for (File file : files) {
                if (file.isFile()) {
                    // Template file name
                    file = templateName(file, velocityContext);
                    // Template file
                    templateContent(file, velocityContext);
                } else if(file.isDirectory()) {
                    // Template dir name
                    file = templateName(file, velocityContext);
                    // Recursive call
                    processDirectory(file, velocityContext);
                }
            }
        }

    }

    private File templateName(File file, VelocityContext velocityContext) {

        String originalName = file.getName();

        String templatedName = applyVelocityToString(originalName, velocityContext);

        File renamedFile = CustomFileUtils.renameFile(file, templatedName);

        return renamedFile;

    }

    private void templateContent(File file, VelocityContext velocityContext) {

        // Read old content
        String oldContent = CustomFileUtils.readFileAsString(file);

        // Create new content and replace old one
        String newContent = applyVelocityToString(oldContent, velocityContext);
        CustomFileUtils.writeFileAsString(file, newContent);

    }

    private String applyVelocityToString(String inputString, VelocityContext velocityContext) {
        StringWriter stringWriter = new StringWriter();

        velocityEngine.evaluate(velocityContext, stringWriter, "templating", preProcessString(inputString));
        return postProcessString(stringWriter.toString());
    }

    private String preProcessString(String inputString) {
        if(velocityVariableDelimiterStart != null && velocityVariableDelimiterStart != "${"
                && velocityVariableDelimiterStop != null && velocityVariableDelimiterStop != "}") {

            // Replace $ because in this scenario is not an Apache Velocity expression
            inputString = inputString.replace("$", "<dollarChar>");

            // Escape special characters in custom delimiter
            String escapedDelimiterStart = Pattern.quote(velocityVariableDelimiterStart);
            String escapedDelimiterEnd = Pattern.quote(velocityVariableDelimiterStop);

            // Construct the regex pattern dynamically
            String regexPattern = escapedDelimiterStart + "(.*?)" + escapedDelimiterEnd;

            // Replace the matched pattern
            inputString = inputString.replaceAll(regexPattern, "\\${$1}");

        }
        return inputString;
    }

    private String postProcessString(String inputString) {
        if(velocityVariableDelimiterStart != null && velocityVariableDelimiterStart != "${"
                && velocityVariableDelimiterStop != null && velocityVariableDelimiterStop != "}") {
            inputString = inputString.replace("<dollarChar>", "$");
        }
        return inputString;
    }

}
