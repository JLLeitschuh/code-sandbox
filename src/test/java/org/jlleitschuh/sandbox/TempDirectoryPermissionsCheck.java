package org.jlleitschuh.sandbox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class TempDirectoryPermissionsCheck {

    @AfterEach
    void afterEach() {
        System.out.flush();
        System.err.flush();
    }

    @Test
    void checkTempFileDefaultPermissions() throws IOException {
        File temp = File.createTempFile("random", "file");
        // Issue this creates with permissions -> -rw-r--r--
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
