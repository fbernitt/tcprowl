package de.fbernitt.teamcity.plugins.tcprowl.prowl;

import de.fbernitt.teamcity.plugins.tcprowl.ProwlConnector;
import de.fbernitt.teamcity.plugins.tcprowl.ProwlNotification;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * HttpClient implementation of ProwlConnector.
 */
public class HttpProwlConnector implements ProwlConnector {

    /**
     * Encapsulates the actual communication with prowl server using HttpClient.
     */
    private static class HttpClientProwlCaller {
        static final String PROWL_SERVER_URL = "http://api.prowlapp.com/publicapi/add";
        static final String APPLICATION_NAME = "TeamCity";

        private final HttpClient httpClient;
        private final String apiKey;


        public HttpClientProwlCaller(String apiKey, HttpClient httpClient) {
            this.apiKey = apiKey;
            this.httpClient = httpClient;
        }

        public void sendMessage(String eventTitle, String message) throws RuntimeException {
            try {
                HttpPost post = new HttpPost(PROWL_SERVER_URL);
                post.setEntity(constructPostParams(eventTitle, message));
                this.httpClient.execute(post);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private UrlEncodedFormEntity constructPostParams(String eventTitle, String message) throws UnsupportedEncodingException {
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("apikey", this.apiKey));
            params.add(new BasicNameValuePair("application", APPLICATION_NAME));
            params.add(new BasicNameValuePair("event", eventTitle));
            params.add(new BasicNameValuePair("description", message));
            return new UrlEncodedFormEntity(params);
        }
    }

    public void sendNotification(ProwlNotification notification) {
         new HttpClientProwlCaller(notification.getApiKey(), createHttpClient()).sendMessage(notification.getTitle(), notification.getMessage());
    }

    HttpClient createHttpClient () {
        return new DefaultHttpClient();
    }
}
