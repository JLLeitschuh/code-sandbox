package org.jlleitschuh.sandbox.hapifhir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Utilities {
    private static final String C_TEMP_DIR = "c:\\temp";

    public static boolean noString(String v) {
        return v == null || v.equals("");
    }

    public static String path(String... args) throws IOException {
        StringBuilder s = new StringBuilder();
        boolean d = false;
        boolean first = true;
        for (String arg : args) {
            if (first && arg == null)
                continue;
            first = false;
            if (!d)
                d = !noString(arg);
            else if (!s.toString().endsWith(File.separator))
                s.append(File.separator);
            String a = arg;
            if (s.length() == 0) {
                if ("[tmp]".equals(a)) {
                    if (hasCTempDir()) {
                        a = C_TEMP_DIR;
//                    } else if (ToolGlobalSettings.hasTempPath()) {
//                        a = ToolGlobalSettings.getTempPath();
                    } else {
                        a = System.getProperty("java.io.tmpdir");
                    }
                } else if ("[user]".equals(a)) {
                    a = System.getProperty("user.home");
                } else if (a.startsWith("[") && a.endsWith("]")) {
                    String ev = System.getenv(a.replace("[", "").replace("]", ""));
                    if (ev != null) {
                        a = ev;
                    } else {
                        a = "null";
                    }
                }
            }
            a = a.replace("\\", File.separator);
            a = a.replace("/", File.separator);
            if (s.length() > 0 && a.startsWith(File.separator))
                a = a.substring(File.separator.length());

            while (a.startsWith(".." + File.separator)) {
                if (s.length() == 0) {
                    s = new StringBuilder(Paths.get(".").toAbsolutePath().normalize().toString());
                } else {
                    String p = s.toString().substring(0, s.length() - 1);
                    if (!p.contains(File.separator)) {
                        s = new StringBuilder();
                    } else {
                        s = new StringBuilder(p.substring(0, p.lastIndexOf(File.separator)) + File.separator);
                    }
                }
                a = a.substring(3);
            }
            if ("..".equals(a)) {
                int i = s.substring(0, s.length() - 1).lastIndexOf(File.separator);
                s = new StringBuilder(s.substring(0, i + 1));
            } else
                s.append(a);
        }
        return s.toString();
    }

    private static boolean hasCTempDir() {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            return false;
        }
        File tmp = new File(C_TEMP_DIR);
        return tmp.exists() && tmp.isDirectory() && tmp.canWrite();
    }
}
