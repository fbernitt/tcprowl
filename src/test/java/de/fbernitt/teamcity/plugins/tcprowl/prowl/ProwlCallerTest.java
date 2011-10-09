package de.fbernitt.teamcity.plugins.tcprowl.prowl;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.util.EntityUtils;
import org.hamcrest.Description;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.io.IOException;
import java.util.List;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

/**
 * Tests the ProwlCaller.
 *
 * @author Folker Bernitt
 */
public class ProwlCallerTest {

    private static String TEST_API_KEY = "foobarApiKey";
    private class IsExpectedPost extends ArgumentMatcher<HttpPost> {

        @Override
        public boolean matches(Object argument) {
            try {
                HttpPost post = (HttpPost)argument;
                UrlEncodedFormEntity entity = (UrlEncodedFormEntity)post.getEntity();
                List<NameValuePair> params = URLEncodedUtils.parse(entity);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            super.describeTo(description);
        }
    }


    private final HttpClient mockHttpClient = mock(HttpClient.class);


    @Test
    public void thatMessageIsSent () throws Exception {
       new ProwlCaller(TEST_API_KEY, mockHttpClient).sendMessage("foobar");

        verify(this.mockHttpClient).execute(isExpectedPost());
    }

    private HttpPost isExpectedPost() {
        return argThat(new IsExpectedPost());
    }
}
