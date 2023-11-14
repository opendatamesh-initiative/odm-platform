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
                    "Error removing directory [" + directory.getAbsolutePath() + "]",
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
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileToRead));
            String fileContent = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            reader.close();
            return fileContent;
        } catch (IOException e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Error reading file [" +  fileToRead.getAbsolutePath() + "] content",
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
                    "Error writing content to file [" + fileToWrite.getAbsolutePath() + "]",
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
                            + "to [" + destinationDirectory.getAbsolutePath() + "]",
                    e.getCause()
            );
        }
    }

    public static void renameFile(File fileToRename, String pathName) {
        fileToRename.renameTo(new File(pathName));
    }

    public static Boolean existsAsFileInDirectory(File directoryToCheck, String fileNameToCheckInDirectory) {
        String directoryToCheckAbsoulutePath = directoryToCheck.getAbsolutePath();
        Path fileInDirPath = Paths.get(directoryToCheckAbsoulutePath, fileNameToCheckInDirectory);
        if (exists(fileInDirPath) && Files.isRegularFile(fileInDirPath))
            return true;
        else
            return false;
    }

    public static Boolean existsAsDirectoryInDirectory(File directoryToCheck, String directoryNameToCheckInDirectory) {
        String directoryToCheckAbsoulutePath = directoryToCheck.getAbsolutePath();
        Path dirInDirPath = Paths.get(directoryToCheckAbsoulutePath, directoryNameToCheckInDirectory);
        if (exists(dirInDirPath) && Files.isDirectory(dirInDirPath))
            return true;
        else
            return false;
    }

    private static Boolean exists(Path filePathToCheck) {
        return Files.exists(filePathToCheck);
    }

}
