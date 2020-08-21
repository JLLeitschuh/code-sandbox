package org.jlleitschuh.sandbox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

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
    void checkTempDefaultPermissions() throws IOException {
        File temp = File.createTempFile("random", "file");
        System.out.println("File Temp File: " + temp.getName());
        runLS(temp.getParentFile());
    }

    @Test
    void checkTempCreateTempDefaultPermissions() throws IOException {
        Path temp = Files.createTempDirectory("random-directory");
        System.out.println("Files Temp Dir: " + temp.getFileName());
        runLS(temp.toFile().getParentFile());
    }

    @Test
    void checkGuavaTempCreateTempDefaultPermissions() {
        File guavaTempDir = com.google.common.io.Files.createTempDir();
        System.out.println("Guava Temp Dir: " + guavaTempDir.getName());
        runLS(guavaTempDir.getParentFile());
    }

    private static void runLS(File file) {
        ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.command("ls", "-l", file.getAbsolutePath());

        try {
            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
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
