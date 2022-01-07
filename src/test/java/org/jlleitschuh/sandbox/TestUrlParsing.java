package org.jlleitschuh.sandbox;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestUrlParsing {

    @Test
    void createUrl() throws MalformedURLException {
        ImmutableMap<String, String> params = ImmutableMap.of("file", ":baz@127.0.0.1/");
        System.out.println(createUrl(params, "https://builds.gradle.org"));
    }

    private static URL createUrl(Map<String, String> params, String serverUrl) throws MalformedURLException {
        URL url = new URL(serverUrl);
        StringBuilder sb = new StringBuilder();
        sb.append(url.getProtocol()).append("://").append(url.getHost());
        if (url.getPort() != -1) {
            sb.append(':').append(url.getPort());
        }

        sb.append((String) params.get("file"));
        sb.append("&modId=").append((String) params.get("modId"));
        sb.append("&personal=").append((String) params.get("personal"));
        sb.append("&inline=").append("true");
        return new URL(sb.toString());
    }

    @Test
    void characterEncodings() {
        StringBuilder builder = new StringBuilder();
        builder.append('\u002e');
        String str = builder.toString();
        System.out.println(str);
        System.out.println(str.charAt(0) == '.');
        System.out.println('\u002e');
        System.out.println(str.charAt(0) == '\u002e');
        System.out.println("Payloads:");
        List<String> payloads = Arrays.asList("%c0%2e", "%e0%40%ae", "%c0ae");
        payloads.forEach(encoded -> {
            System.out.println(encoded + ":");
            String output = URLDecoder.decode(encoded);
            System.out.println(output);
            System.out.println(output.length());
            System.out.println(new File(output + output).getAbsoluteFile());
        });
    }

    @Test
    void urlParsing() throws MalformedURLException, URISyntaxException {
        URL url = new URL("http://localhost:8888\\\\@yahoo.com/");
        System.out.println(url.getHost());
    }
}
