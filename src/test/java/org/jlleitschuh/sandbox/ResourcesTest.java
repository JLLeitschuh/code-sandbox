package org.jlleitschuh.sandbox;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResourcesTest {
    @Test
    void resourcesGet() throws IOException {
        URL resource = ResourcesTest.class.getClassLoader().getResource("https://google.com");
        assertNotNull(resource);
    }
}
