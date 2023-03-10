package oracle.nosql.driver.ops.serde;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import oracle.nosql.driver.AuthorizationProvider;
import oracle.nosql.driver.NoSQLHandle;
import oracle.nosql.driver.NoSQLHandleConfig;
import oracle.nosql.driver.NoSQLHandleFactory;
import oracle.nosql.driver.httpclient.HttpClient;
import oracle.nosql.driver.httpclient.HttpClientHandler;
import oracle.nosql.driver.ops.GetRequest;
import oracle.nosql.driver.ops.GetResult;
import oracle.nosql.driver.ops.Request;
import oracle.nosql.driver.util.HttpRequestUtil;
import oracle.nosql.driver.values.MapValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class NoSqlDriverTest {

    private static class MockAuthorizationProvider implements AuthorizationProvider {
        private final String authorizationString;

        private MockAuthorizationProvider(final String authorizationString) {
            this.authorizationString = authorizationString;
        }

        @Override
        public String getAuthorizationString(Request request) {
            return authorizationString;
        }

        @Override
        public void close() {

        }
    }

    @Test
    void testNoSqlCall() throws IOException {
        String endpoint = "localhost:8986";
        NoSQLHandleConfig config = new NoSQLHandleConfig(endpoint);

        config.setRequestTimeout(5000);
        config.setAuthorizationProvider(new MockAuthorizationProvider(
            "test-auth-string\n\rContent-Type: application/json\n\rTransfer-Encoding: chunked\n\r\n\r" + "{ \"key\": \"malicious value\"}" + "\n\r\n\r"
        ));
        NoSQLHandle handle = NoSQLHandleFactory.createNoSQLHandle(config);
        System.out.println("Acquired handle at endpoint " + endpoint);
        try {
            MapValue key = new MapValue().put("key", "value");
            GetRequest getRequest = new GetRequest().setKey(key).setTableName("test");
            GetResult result = handle.get(getRequest);
            System.out.println("Result: " + result);
        } finally {
            handle.close();
        }
    }

    @Test
    void testClientUsage() {
        HttpClient httpClient = new HttpClient("127.0.0.1", 8986, 0, 0, 0, null, "Test Client", null);
        DefaultHttpHeaders headers = new DefaultHttpHeaders(false);
        headers.add("Accept", "application/json");
        headers.add("Injected-Header", "User supplied value\n\rAnother-Header: Another-Value");
        HttpRequestUtil.doGetRequest(httpClient, "http://127.0.0.1:8986/test", headers, 50, null);

        System.getProperties().getProperty("user.dir");
    }
}
