package org.opendatamesh.platform.core.commons.utils;

import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

public class CustomFileUtils {

    // === GET =========================================================================================================

    private static Path getFileInDirPath(File directory, String fileInDirectoryName) {
        String directoryAbsolutePath = directory.getAbsolutePath();
        return Paths.get(directoryAbsolutePath, fileInDirectoryName);
    }


    // === EXISTS ======================================================================================================

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

    private static Boolean exists(Path filePathToCheck) {
        return Files.exists(filePathToCheck);
    }


    // === BOH =========================================================================================================

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
