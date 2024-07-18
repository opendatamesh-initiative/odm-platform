package org.opendatamesh.platform.core.commons.utils;

import org.apache.commons.io.FileUtils;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public abstract class CustomFileUtils {

    private CustomFileUtils() {
    }

    //============== WRITE =================================================================0

    public static String readFileAsString(File fileToRead) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileToRead));) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Error reading file [" + fileToRead.getAbsolutePath() + "] content - " + e.getMessage(),
                    e.getCause()
            );
        }
    }

    public static void writeFileAsString(File fileToWrite, String content) {
        try (PrintWriter writer = new PrintWriter(fileToWrite);) {
            writer.println(content);
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
        if (fileToRename.renameTo(renamedFile)) {
            return renamedFile;
        } else {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Error renaming file [ + " + fileToRename.getAbsolutePath() + "]"
            );
        }
    }

    // === EXISTS ======================================================================================================

    public static boolean existsAsFileInDirectory(File directoryToCheck, String fileNameToCheckInDirectory) {
        String directoryAbsolutePath = directoryToCheck.getAbsolutePath();
        Path fileInDirPath = Paths.get(directoryAbsolutePath, fileNameToCheckInDirectory);
        return Files.exists(fileInDirPath) && Files.isRegularFile(fileInDirPath);
    }

    public static boolean existsAsDirectoryInDirectory(File directoryToCheck, String directoryNameToCheckInDirectory) {
        String directoryAbsolutePath = directoryToCheck.getAbsolutePath();
        Path dirInDirPath = Paths.get(directoryAbsolutePath, directoryNameToCheckInDirectory);
        return Files.exists(dirInDirPath) && Files.isDirectory(dirInDirPath);
    }


    // === CLEANUP =========================================================================================================
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
        //TODO Refactor: fix this --> used in gitService implementation but removes all repositories instead of the one cloned !!!
        for (File file : dirToClean.listFiles()) {
            if (!file.getName().equals(subdirectoryToKeep)) {
                if (file.isDirectory()) {
                    removeDirectory(file);
                } else {
                    file.delete();
                }
            }
        }

    }

    // === COPY =========================================================================================================
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
}
