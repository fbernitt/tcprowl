package de.fbernitt.teamcity.plugins.tcprowl.prowl;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Encapsulates the actual communication with prowl server.
 */
public class ProwlCaller {
    private final static String PROWL_SERVER_URL = "http://api.prowlapp.com/publicapi/add";

    private final String apiKey;
    private final HttpClient httpClient;

    public ProwlCaller(String apiKey) {
        this(apiKey, new DefaultHttpClient());
    }

    public ProwlCaller (String apiKey, HttpClient httpClient) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
    }

    public void sendMessage (String message) throws RuntimeException {
        try {
            HttpPost post = new HttpPost(PROWL_SERVER_URL);
            post.setEntity(constructPostParams(message));
            this.httpClient.execute(post);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private UrlEncodedFormEntity constructPostParams (String message) throws UnsupportedEncodingException {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("apikey", apiKey));
        params.add(new BasicNameValuePair("application", "TeamCity"));
        params.add(new BasicNameValuePair("event", "tcprowl config test"));
        params.add(new BasicNameValuePair("description", message));
        return new UrlEncodedFormEntity(params);
    }
}
