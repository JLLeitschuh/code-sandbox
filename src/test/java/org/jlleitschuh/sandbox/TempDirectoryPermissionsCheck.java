package org.jlleitschuh.sandbox;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class TempDirectoryPermissionsCheck {
    @Test
    public void checkTempDefaultPermissions() throws IOException {
        File temp = File.createTempFile("random", "file");
        runLS(temp.getParentFile());
    }

    @Test
    public void checkTempCreateTempDefaultPermissions() throws IOException {
        Path temp = Files.createTempDirectory("random");
        System.out.println("Files Temp Dir: " + temp.getFileName());
        runLS(temp.toFile());
    }

    @Test
    public void guavaTempCreateTempDefaultPermissions() {
        File guavaTempDir = com.google.common.io.Files.createTempDir();
        System.out.println("Guava Temp Dir: " + guavaTempDir.getName());
        runLS(guavaTempDir);
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
                System.exit(0);
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
