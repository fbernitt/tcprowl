package de.fbernitt.teamcity.plugins.tcprowl.prowl.impl;

import de.fbernitt.teamcity.plugins.tcprowl.prowl.api.ProwlException;
import de.fbernitt.teamcity.plugins.tcprowl.prowl.api.ProwlResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.IOException;

/**
 * Parses the prowl HTTP response into a ProwlResult. If an error occurs a ProwlException is raised.
 */
public class ProwlResponseParser {

    public ProwlResult parse(HttpResponse response) {
        try {
            if (isHttpStatusCodeOk(response)) {
                return parseSuccessResponse(responseToJdom(response.getEntity()));
            } else {
                String errorMsg = handleErrorResponse(response);
                throw new ProwlException(errorMsg);
            }
        } catch (IOException e) {
            throw new ProwlException(e);
        } catch (JDOMException e) {
            throw new ProwlException(e);
        }
    }

    private String handleErrorResponse(HttpResponse response) throws IOException, JDOMException {
        if (isNullOrEmptyResponseEntity(response)) {
            throw new ProwlException("No error content. HTTP status line: " + response.getStatusLine());
        }
        return parseErrorResponse(responseToJdom(response.getEntity()));
    }

    private boolean isNullOrEmptyResponseEntity(HttpResponse response) {
        return null == response.getEntity() || 0 == response.getEntity().getContentLength();
    }

    private boolean isHttpStatusCodeOk(HttpResponse response) {
        return HttpStatus.SC_OK == response.getStatusLine().getStatusCode();
    }


    private String parseErrorResponse(Element root) throws DataConversionException {
        Element error = root.getChild("error");
        int errorCode = error.getAttribute("code").getIntValue();
        String errorMsg = error.getText();

        return "Error during prowl call. Code: " + errorCode + ", Message: " + errorMsg;
    }

    private ProwlResult parseSuccessResponse(Element root) throws DataConversionException {
        Element success = root.getChild("success");
        int remaining = success.getAttribute("remaining").getIntValue();
        return new ProwlResult(remaining, null);
    }

    private Element responseToJdom(HttpEntity entity) throws IOException, JDOMException {
        if (0 == entity.getContentLength()) {
            throw new ProwlException("No actual response body!");
        }
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(entity.getContent());
        return doc.getRootElement();
    }
}
