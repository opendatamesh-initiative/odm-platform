package org.opendatamesh.platform.pp.blueprint.server.utils;

import org.apache.commons.io.FileUtils;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public final class CustomFileUtils {

    public static void removeDirectory(File directory) {
        try {
            FileUtils.deleteDirectory(directory);
        } catch (IOException e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Error removing directory [" + directory.getAbsolutePath() + "] - " + e.getMessage(),
                    e.getCause()
            );
        }
    }

    public static void cleanDirectoryExceptOneDir(File dirToClean, String subdirectoryToKeep) {
        for (File file : dirToClean.listFiles()) {
            if(!file.getName().equals(subdirectoryToKeep)) {
                if(file.isDirectory()) {
                    removeDirectory(file);
                } else {
                    file.delete();
                }
            }
        }

    }

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

    public static void copyDirectory(File sourceDirectory, File destinationDirectory) {
        try {
            FileUtils.copyDirectory(sourceDirectory, destinationDirectory);
        } catch (IOException e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Error copying directory [" + sourceDirectory.getAbsolutePath() + "] "
                            + "to [" + destinationDirectory.getAbsolutePath() + "]  - " + e.getMessage(),
                    e.getCause()
            );
        }
    }

    public static File renameFile(File fileToRename, String fileName) {
        File renamedFile = new File(fileToRename.getParentFile(), fileName);
        fileToRename.renameTo(renamedFile);
        return renamedFile;
    }

    public static Boolean existsAsFileInDirectory(File directoryToCheck, String fileNameToCheckInDirectory) {
        Path fileInDirPath = getFileInDirPath(directoryToCheck, fileNameToCheckInDirectory);
        if (exists(fileInDirPath) && Files.isRegularFile(fileInDirPath))
            return true;
        else
            return false;
    }

    public static Boolean existsAsDirectoryInDirectory(File directoryToCheck, String directoryNameToCheckInDirectory) {
        Path dirInDirPath = getFileInDirPath(directoryToCheck, directoryNameToCheckInDirectory);
        if (exists(dirInDirPath) && Files.isDirectory(dirInDirPath))
            return true;
        else
            return false;
    }

    private static Path getFileInDirPath(File directory, String fileInDirectoryName) {
        String directoryAbsolutePath = directory.getAbsolutePath();
        return Paths.get(directoryAbsolutePath, fileInDirectoryName);
    }

    private static Boolean exists(Path filePathToCheck) {
        return Files.exists(filePathToCheck);
    }

}
