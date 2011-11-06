package de.fbernitt.teamcity.plugins.tcprowl.prowl.impl;

import de.fbernitt.teamcity.plugins.tcprowl.prowl.api.ProwlException;
import de.fbernitt.teamcity.plugins.tcprowl.prowl.api.ProwlResult;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Tests the ProwlResponseParser.
 */
public class ProwlResponseParserTest {

    private final TestResponseBuilder testResponseBuilder = new TestResponseBuilder();

    @Test(expected = ProwlException.class)
    public void thatNonSuccessStatusLeadsToException() {
        ProtocolVersion protocolVersion = TestResponseBuilder.PROTOCOL_VERSION;
        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(protocolVersion, HttpStatus.SC_BAD_REQUEST, null));
        new ProwlResponseParser().parse(response);
    }

    @Test
    public void thatSuccessResponseIsParsed() throws IOException {
        BasicHttpResponse response = this.testResponseBuilder.buildSuccessResponse(42);

        ProwlResult result = new ProwlResponseParser().parse(response);

        assertNotNull(result);
        assertEquals(42, result.getRemaining());
    }

    @Test
    public void thatErrorResponseRaisesException() throws IOException {
        final String errorMsg = "A test error occurred!";
        BasicHttpResponse response = this.testResponseBuilder.buildErrorResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, errorMsg);
        try {
            new ProwlResponseParser().parse(response);
            fail("ProwlException expected!");
        } catch (ProwlException e) {
            assertEquals("Error during prowl call. Code: " + HttpStatus.SC_INTERNAL_SERVER_ERROR + ", Message: " + errorMsg, e.getMessage());
        }
    }

}
