package org.jlleitschuh.sandbox;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;

public class TempFileCreationTest {
    File workingDirectory = new File("./temp-testing");

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
    void vulnerableFileCreateTempFile() throws IOException {
        File tempDirChild = new File(workingDirectory, "/child-create-file-vulnerable.txt");
        Files.createFile(
            tempDirChild.toPath()
        );
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
        Files.createDirectory(tempDirChild.toPath(), PosixFilePermissions.asFileAttribute(
            EnumSet.of(
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.OTHERS_READ,
                PosixFilePermission.OTHERS_WRITE
            )
        ));
        Files.createDirectory(tempDirChild.toPath());
    }

    @Test
    void vulnerableFileCreateDirectories() throws IOException {
        File tempDirChild = new File(workingDirectory, "/child-create-directories/child");
        Files.createDirectories(tempDirChild.toPath());
        Files.createDirectories(tempDirChild.toPath());
    }

    @Test
    void vulnerableFileCreate() throws IOException {
        File tempDirChild = new File(workingDirectory, "/child-create-file-writer.txt");
        try (FileWriter writer = new FileWriter(tempDirChild)) {
            writer.write("Contents too\n");
        }
    }

    @Test
    void explicitlySettingFilePermissions() throws IOException {
        File tempFile = File.createTempFile("temp-file-permissions", ".txt");
        if (!tempFile.setReadable(false, false)) {
            throw new IOException("Could not set readable permissions on " + tempFile.getAbsolutePath());
        }
        if (!tempFile.setReadable(true, true)) {
            throw new IOException("Could not set readable permissions on " + tempFile.getAbsolutePath());
        }
    }

    void vulnerableFileCreateTempFileMkdirTainted() throws IOException {
        File tempDirChild = new File(System.getProperty("java.io.tmpdir"), "/child");
        if (tempDirChild.toPath().getFileSystem().supportedFileAttributeViews().contains("posix")) {
            // Explicit permissions setting is only required on unix-like systems because the temporary directory is shared between all users.
            // This is not needed on Windows, since windows gives each their own temp directory
            final EnumSet<PosixFilePermission> posixFilePermissions =
                EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE);
            if (!tempDirChild.exists()) {
                Files.createDirectory(
                    tempDirChild.toPath(),
                    PosixFilePermissions.asFileAttribute(posixFilePermissions)
                );
            } else {
                Files.setPosixFilePermissions(
                    tempDirChild.toPath(),
                    posixFilePermissions
                );
            }
        } else if (!tempDirChild.exists()) {
            // On Windows, we still need to create the directory, when it doesn't already exist.
            Files.createDirectory(tempDirChild.toPath());
        }
    }
}
