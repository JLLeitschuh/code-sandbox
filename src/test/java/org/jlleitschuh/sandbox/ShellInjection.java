package org.jlleitschuh.sandbox;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Scanner;

public class ShellInjection {

    /**
     * a Unix command to get a given user's groups list. If the OS is not WINDOWS, the command will get the user's primary group first and
     * finally get the groups list which includes the primary group. i.e. the user's primary group will be included twice.
     */
    public static String[] getGroupsForUserCommand(final String user) {
        //'groups username' command return is non-consistent across different unixes
        return new String[]{
            "bash", "-c", "id -gn " + user
            + "&& id -Gn " + user
        };
    }

    @Test
    void executeProcessBuilder() throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(getGroupsForUserCommand("jonathanleitschuh && sleep 2"));

        Process process = builder.start();
        inheritIO(process.getInputStream(), System.out);
        inheritIO(process.getErrorStream(), System.err);
        process.waitFor();
    }

    @Test
    void executeRuntime() throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("idea --line 0 \"/Users/jonathanleitschuh/code/personal/code-sandbox/child-output-stream.txt\" | bash -c {echo,c2xlZXAgMzk=}|{base64,-d}|{bash,-i}");
        // print result
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        process.waitFor();
    }

    private static void inheritIO(final InputStream src, final PrintStream dest) {
        new Thread(new Runnable() {
            public void run() {
                Scanner sc = new Scanner(src);
                while (sc.hasNextLine()) {
                    dest.println(sc.nextLine());
                }
            }
        }).start();
    }
}
