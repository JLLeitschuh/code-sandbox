package org.jlleitschuh.sandbox;

import org.junit.jupiter.api.Test;

public class JvmAdditionalOptionsCleanerTest {
    @Test
    void jvmArgumentsEscapeTest() {
        System.out.println(JvmAdditionalOptionsCleaner.clean("-XX:OnOutOfMemoryError=\"\npotato"));
    }
}
