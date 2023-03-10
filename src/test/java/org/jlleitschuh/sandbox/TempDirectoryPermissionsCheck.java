package org.jlleitschuh.sandbox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;
import java.util.EnumSet;

public class TempDirectoryPermissionsCheck {

    @AfterEach
    void afterEach() {
        System.out.flush();
        System.err.flush();
    }

    @Test
    void checkTempFileDefaultPermissions() throws IOException {
        File temp = File.createTempFile("random", "file");
        System.out.println(temp.getCanonicalPath());
        // This creates with permissions -> -rw-r--r--
        System.out.println("File Temp File: " + temp.getName());
        runLS(temp.getParentFile(), temp);
    }

    @Test
    void checkTempCreateDirTempDefaultPermissions() throws IOException {
        Path temp = Files.createTempDirectory("random-directory");
        // This creates with permissions -> drwx------
        System.out.println("Files Temp Dir: " + temp.getFileName());
        runLS(temp.toFile().getParentFile(), temp.toFile());
        Path child = temp.resolve("jdk-child.txt");
        child.toFile().createNewFile();
        runLS(temp.toFile(), child.toFile());
    }

    @Test
    void checkTempCreateTempFile() throws IOException {
        Path tempFile = Files.createTempFile("random-file", ".txt");
        // This creates with permissions -> -rw-------
        System.out.println("Files Temp Dir: " + tempFile.getFileName());
        runLS(tempFile.toFile().getParentFile(), tempFile.toFile());
    }

    @Test
    void checkTempNewFile() throws IOException {
        File tmpFile = new File(System.getProperty("java.io.tmpdir"));
        File theFile = new File(tmpFile, "/test_subdir");
        theFile.mkdir(); // This creates with permissions -> drwxr-xr-x
        runLS(tmpFile, theFile);
    }

    @Test
    void checkGuavaTempCreateTempDefaultPermissions() throws IOException {
        File guavaTempDir = com.google.common.io.Files.createTempDir();
        // This creates with permissions -> drwxr-xr-x
        System.out.println("Guava Temp Dir: " + guavaTempDir.getName());
        runLS(guavaTempDir.getParentFile(), guavaTempDir);
        File child = new File(guavaTempDir, "guava-child.txt");
        child.createNewFile();
        runLS(guavaTempDir, child);
    }

    @Test
    void testJunitTempDir(@TempDir Path temp) throws IOException {
        System.out.println("Files Temp Dir: " + temp.getFileName());
        runLS(temp.toFile().getParentFile(), temp.toFile());
        Path child = temp.resolve("jdk-child.txt");
        child.toFile().createNewFile();
        runLS(temp.toFile(), child.toFile());
    }

    @Test
    void checkTempFilesExplicitPermissions() throws IOException {
        Path temp = Files.createTempDirectory("random-directory", PosixFilePermissions.asFileAttribute(EnumSet.allOf(PosixFilePermission.class)));
        // This creates with permissions -> drwxr-xr-x
        System.out.println("Files Temp Dir: " + temp.getFileName());
        runLS(temp.toFile().getParentFile(), temp.toFile());
        Path child = temp.resolve("jdk-child.txt");
        child.toFile().createNewFile();
        runLS(temp.toFile(), child.toFile());
    }

    @Test
    void checkTempFilesCreateOutputStream() throws IOException {
        File temp = File.createTempFile("random", "file");
        try (FileWriter writer = new FileWriter(temp)) {
            writer.write("Contents\n");
        }
        try(PrintWriter s = new PrintWriter(Files.newOutputStream(temp.toPath()))) {
            s.println("Another line\n");
        }
        // This creates with permissions -> -rw-r--r--
        System.out.println("File Temp File: " + temp.getName());
        runLS(temp.getParentFile(), temp);
        System.out.println("File contents:");
        Files.readAllLines(temp.toPath()).forEach(line -> System.out.println('\t' + line));
    }

    @Test
    void checkIfNewOutputStreamChangesFilePermissions() throws IOException {
        File myFile = new File("test-file.txt");
        try(PrintWriter s = new PrintWriter(Files.newOutputStream(myFile.toPath()))) {
            s.println("Another line\n");
        }
    }

    @Test
    void checkFileCreateDirectoryPermissions() throws IOException {
        File tempDirChild = new File(System.getProperty("java.io.tmpdir"), "/child-create-directory");
        Files.createDirectory(tempDirChild.toPath()); // Creates with permissions 'drwxr-xr-x'
        runLS(tempDirChild.getParentFile(), tempDirChild);
    }

    @Test
    void checkFileCreateDirectoriesPermissions() throws IOException {
        File tempDirChild = new File(System.getProperty("java.io.tmpdir"), "/child-create-directories/child");
        Files.createDirectories(tempDirChild.toPath());
        runLS(tempDirChild.getParentFile(), tempDirChild);
    }

    @Test
    void checkFileCreateFilePermissions() throws IOException {
        File tempDirChildFile = new File(System.getProperty("java.io.tmpdir"), "/child-create-file.txt");
        Files.createFile(tempDirChildFile.toPath());
        runLS(tempDirChildFile.getParentFile(), tempDirChildFile); // Creates with permissions '-rw-r--r--'
    }

    @Test
    void checkFilesWriteCreateFilePermissions() throws IOException {
        File tempDirChildVuln = new File(System.getProperty("java.io.tmpdir"), "/child-files-write.txt");
        Files.write(tempDirChildVuln.toPath(),
            Arrays.asList("secret"),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE
        ); // Creates file with permissions '-rw-r--r--'
        runLS(tempDirChildVuln.getParentFile(), tempDirChildVuln);
    }

    private static void runLS(File file, File lookingFor) {
        ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.command("ls", "-l", file.getAbsolutePath());

        try {
            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("total")) {
                    output.append(line + "\n");
                }
                if (line.contains(lookingFor.getName())) {
                    output.append(line + "\n");
                }
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
                System.out.println(output);
            } else {
                //abnormal...
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
