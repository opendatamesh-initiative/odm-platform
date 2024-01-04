package org.opendatamesh.platform.pp.blueprint.server.utils;

import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;

import java.io.*;
import java.util.stream.Collectors;

public final class CustomFileUtils extends org.opendatamesh.platform.core.commons.utils.CustomFileUtils {

    public static String readFileAsString(File fileToRead) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileToRead));
            String fileContent = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            reader.close();
            return fileContent;
        } catch (IOException e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Error reading file [" +  fileToRead.getAbsolutePath() + "] content - " + e.getMessage(),
                    e.getCause()
            );
        }
    }

    public static void writeFileAsString(File fileToWrite, String content) {

        StringBuilder newContent = new StringBuilder();
        newContent.append(content);
        try {
            PrintWriter writer = new PrintWriter(fileToWrite);
            writer.println(newContent);
            writer.close();
        } catch (Exception e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Error writing content to file [" + fileToWrite.getAbsolutePath() + "]  - " + e.getMessage(),
                    e.getCause()
            );
        }
    }

    public static File renameFile(File fileToRename, String fileName) {
        File renamedFile = new File(fileToRename.getParentFile(), fileName);
        fileToRename.renameTo(renamedFile);
        return renamedFile;
    }

}
