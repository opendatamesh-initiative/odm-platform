package org.opendatamesh.platform.pp.blueprint.server.services;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.opendatamesh.platform.core.commons.utils.CustomFileUtils;
import org.opendatamesh.platform.pp.blueprint.api.resources.ConfigResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;

@Service
public class TemplatingService {

    @Autowired
    private VelocityEngine velocityEngine;

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
        velocityEngine.evaluate(velocityContext, stringWriter, "templating", inputString);
        return stringWriter.toString();
    }

}
