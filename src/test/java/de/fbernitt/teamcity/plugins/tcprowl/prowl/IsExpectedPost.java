package de.fbernitt.teamcity.plugins.tcprowl.prowl;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

import java.io.IOException;
import java.util.*;

/**
 * Verifies that a post contains the expected parameters with correct values.
 */
class IsExpectedPost extends ArgumentMatcher<HttpPost> {

    private final String POST_METHOD = "POST";

    private final Map<String, String> expectedParams;
    private final String expectedUri;
    private HttpPost latestPost;

    public IsExpectedPost(String expectedUri, Map<String, String> expectedParams) {
        this.expectedUri = expectedUri;
        this.expectedParams = expectedParams;
    }

    @Override
    public boolean matches(Object argument) {
        this.latestPost = (HttpPost) argument;
        if (!POST_METHOD.equals(this.latestPost.getMethod())) {
            return false;
        }
        if (!this.expectedUri.equals(this.latestPost.getURI().toString())) {
            return false;
        }

        return isExpectedParams(latestParams());
    }

    @Override
    public void describeTo(Description description) {
        if (!POST_METHOD.equals(this.latestPost.getMethod())) {
            description.appendText("Expected [POST] method but was [" + this.latestPost.getMethod() + "]");
        }
        if (!this.expectedUri.equals(this.latestPost.getURI().toString())) {
            description.appendText("Expected uri [" + this.expectedUri + "] method but was [" + this.latestPost.getURI().toString() + "]");
        }
        description.appendText("Invalid post form entity:");
        describeMismatchingKeys(description);
        describeMismatchingParams(description);
    }

    private Map<String, String> latestParams() {
        return parseParameterMap(this.latestPost);
    }

    private Map<String, String> parseParameterMap(HttpPost post) {
        try {
            UrlEncodedFormEntity entity = (UrlEncodedFormEntity) post.getEntity();
            return convertPairsToMap(URLEncodedUtils.parse(entity));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private boolean isExpectedParams(Map<String, String> map) {
        return this.expectedParams.equals(map);
    }

    private Map<String, String> convertPairsToMap(List<NameValuePair> params) {
        HashMap<String, String> map = new HashMap<String, String>();

        for (NameValuePair param : params) {
            map.put(param.getName(), param.getValue());
        }

        return map;
    }

    private void describeMismatchingParams(Description description) {
        Map<String, String> actualParams = latestParams();
        for (String key : this.expectedParams.keySet()) {
            if (actualParams.containsKey(key)) {
                String expectedValue = this.expectedParams.get(key);
                String actualValue = actualParams.get(key);
                if (!expectedValue.equals(actualValue)) {
                    description.appendText("Param " + key + " is expected to be [" + expectedValue + "] but was [" + actualValue + "]!");
                }
            }
        }
    }

    private void describeMismatchingKeys(Description description) {
        Set<String> expectedKeys = this.expectedParams.keySet();
        Set<String> actualKeys = latestParams().keySet();

        Set<String> missingKeys = new HashSet<String>(expectedKeys);
        missingKeys.removeAll(actualKeys);

        Set<String> superfluousKeys = new HashSet<String>(actualKeys);
        superfluousKeys.removeAll(expectedKeys);

        Set<String> mismatchingKeys = new HashSet<String>();
        mismatchingKeys.addAll(missingKeys);
        mismatchingKeys.addAll(superfluousKeys);

        if (!mismatchingKeys.isEmpty()) {
            description.appendText("Mismatching keys " + mismatchingKeys.toString() + "!");
        }
    }
}
