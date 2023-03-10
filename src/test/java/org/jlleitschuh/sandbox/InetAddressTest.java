package org.jlleitschuh.sandbox;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressTest {
    @Test
    void inetAddressTest() throws UnknownHostException {
        InetAddress localhost = InetAddress.getLocalHost();
        System.out.println(localhost);
        System.out.println(localhost.getCanonicalHostName());

        InetAddress test = InetAddress.getByName("`ls>bug`-6.jlleitschuh.org.jlleitschuh.org");
        System.out.println(test);
        System.out.println(test.getCanonicalHostName());

        InetAddress testDocker = InetAddress.getByName("`ls>bug`.bar.baz");
        System.out.println(testDocker);
        System.out.println(testDocker.getCanonicalHostName());

        InetAddress google = InetAddress.getByName("google.com");
        System.out.println(google);
        System.out.println(google.getCanonicalHostName());

        InetAddress kubernetes = InetAddress.getByName("kubernetes.docker.internal");
        System.out.println(kubernetes);
        System.out.println(kubernetes.getCanonicalHostName());
        // docker run -h foo.bar.baz -i -t ubuntu bash
    }
}
