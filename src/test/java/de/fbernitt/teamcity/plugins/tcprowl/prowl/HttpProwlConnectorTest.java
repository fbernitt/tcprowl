package de.fbernitt.teamcity.plugins.tcprowl.prowl;

import de.fbernitt.teamcity.plugins.tcprowl.ProwlNotification;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.HashMap;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests the HttpProwlConnector.
 */
public class HttpProwlConnectorTest {

    private static final String TEST_API_KEY = "foobarApiKey";
    private static final String ADD_POST_URL = "http://api.prowlapp.com/publicapi/add";

    private final HttpClient mockHttpClient = mock(HttpClient.class);

    @Test
    public void thatMessageIsSent () throws Exception {
       createConnector().sendNotification(createNotification("foo", "bar"));

        verify(this.mockHttpClient).execute(isExpectedPost("foo", "bar"));
    }

    @Test(expected = RuntimeException.class)
    public void thatErrorIsHandled () throws Exception {
        Mockito.when(this.mockHttpClient.execute(Matchers.<HttpUriRequest>any())).thenThrow(new IOException());

        createConnector().sendNotification(createNotification("foo", "bar"));
    }

    private HttpProwlConnector createConnector() {
        return new HttpProwlConnector() {
            @Override
            HttpClient createHttpClient() {
                return HttpProwlConnectorTest.this.mockHttpClient;
            }
        };
    }

    private HttpPost isExpectedPost(String expectedTitle, String expectedMessage) {
        HashMap<String, String> expected = new HashMap<String, String>();
        expected.put("description", expectedMessage);
        expected.put("apikey", TEST_API_KEY);
        expected.put("event", expectedTitle);
        expected.put("application", "TeamCity");
        return argThat(new IsExpectedPost(ADD_POST_URL, expected));
    }

    private ProwlNotification createNotification (String title, String message) {
        return new ProwlNotification(TEST_API_KEY, title, message);
    }

}
