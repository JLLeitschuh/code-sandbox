package org.jlleitschuh.sandbox;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;

public class TempFileCreationTest {
    File workingDirectory = new File(".");

    @Test
    void vulnerableFileCreateTempFilesNewBufferedWriter() throws IOException {
        File tempDirChild = new File(workingDirectory, "/child-buffered-writer.txt");
        Files.newBufferedWriter(tempDirChild.toPath()).close();
    }

    @Test
    void vulnerableFileCreateTempFilesNewOutputStream() throws IOException {
        File tempDirChild = new File(workingDirectory, "/child-output-stream.txt");
        Files.newOutputStream(tempDirChild.toPath()).close();
    }

    @Test
    void safeFileCreateTempFilesCreateFile() throws IOException {
        File tempDirChild = new File(workingDirectory, "/child-create-file-safe.txt");
        Files.createFile(
            tempDirChild.toPath(),
            PosixFilePermissions.asFileAttribute(EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE))
        );
    }

    @Test
    void vulnerableFileCreateDirectory() throws IOException {
        File tempDirChild = new File(workingDirectory, "/child-create-directory");
        Files.createDirectory(tempDirChild.toPath());
    }

    @Test
    void vulnerableFileCreateDirectories() throws IOException {
        File tempDirChild = new File(workingDirectory, "/child-create-directories/child");
        Files.createDirectories(tempDirChild.toPath());
    }
}
