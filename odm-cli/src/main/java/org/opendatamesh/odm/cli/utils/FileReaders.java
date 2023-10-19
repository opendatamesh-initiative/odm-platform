package org.opendatamesh.odm.cli.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileReaders {

    public static String readFileFromPath(String localPath) throws IOException {

        Path resolvedPath = Paths.get(localPath).toAbsolutePath();

        if (!Files.exists(resolvedPath) || !Files.isRegularFile(resolvedPath)) {
            throw new IOException("File \"" + resolvedPath + "\" not found");
        }

        try {
            String fileContent = Files.readString(resolvedPath, StandardCharsets.UTF_8);
            System.out.println("Reading file: " + resolvedPath);
            return fileContent;
        } catch (IOException e) {
            throw new IOException("Error reading the file: " + e.getMessage());
        }
    }

}
