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

        File[] files = workingDirectory.listFiles();

        if(
                files != null
                && !workingDirectory.getName().contains(".git")
        ) {
            for (File file : files) {
                System.out.println("---------------------------------------------------------------------------------");
                System.out.println("FILE: ");
                System.out.println(file);
                System.out.println("---------------------------------------------------------------------------------");
                if (file.isFile()) {
                    // Template file
                    templateContent(file, velocityContext);
                    // Template file name
                    templateName(file, velocityContext);
                } else if(file.isDirectory()) {
                    // Recursive call
                    processDirectory(file, velocityContext);
                    // Template dir name
                    templateName(file, velocityContext);
                }
            }
        }

    }

    private void templateName(File file, VelocityContext velocityContext) {

        String originalName = file.getName();

        StringWriter stringWriter = new StringWriter();
        velocityEngine.evaluate(velocityContext, stringWriter, "templating", originalName);
        String templatedName = stringWriter.toString();

        file.renameTo(new File(file.getParentFile(), templatedName));

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
