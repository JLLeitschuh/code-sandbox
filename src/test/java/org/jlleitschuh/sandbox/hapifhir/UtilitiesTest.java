package org.jlleitschuh.sandbox.hapifhir;


import org.junit.jupiter.api.Test;

import java.io.IOException;

public class UtilitiesTest {

    @Test
    void bypassUtilitiesPath() throws IOException {
        System.out.println(Utilities.path("/base", "/child/../test"));
        System.out.println(Utilities.path("/base", "[user]"));
    }
}
