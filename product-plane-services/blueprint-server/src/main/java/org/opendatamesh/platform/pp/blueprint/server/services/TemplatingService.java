package org.opendatamesh.platform.pp.blueprint.server.services;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.opendatamesh.platform.pp.blueprint.api.resources.ConfigResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

        if(
                workingDirectory.listFiles() != null
                && !workingDirectory.getName().equals(".git")
        ) {
            for (File file : workingDirectory.listFiles()) {
                if(file.isDirectory()) {
                    // Template dir name
                    templateName(file, velocityContext);
                    // Recursive call
                    processDirectory(file, velocityContext);
                } else if (file.isFile()) {
                    // Template file name
                    templateName(file, velocityContext);
                    // Template file
                    templateContent(file, velocityContext);
                }
            }
        }

    }

    private void templateName(File file, VelocityContext velocityContext) {

        String originalName = file.getName();

        StringWriter stringWriter = new StringWriter();
        velocityEngine.evaluate(velocityContext, stringWriter, "templating", originalName);
        String templatedName = stringWriter.toString();

        File newFile = new File(file.getParentFile(), templatedName);
        if(!file.equals(newFile)) {
            file.renameTo(newFile);
        }
    }

    private void templateContent(File file, VelocityContext velocityContext) {

        String inputFileName = file.getAbsolutePath();
        String outputFileName = inputFileName + ".tmp";

        Template template = velocityEngine.getTemplate(inputFileName);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
            template.merge(velocityContext, writer);
            Files.move(
                    new File(outputFileName).toPath(),
                    new File(inputFileName).toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
