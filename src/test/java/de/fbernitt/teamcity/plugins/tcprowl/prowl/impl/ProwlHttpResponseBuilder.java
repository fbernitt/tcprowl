package de.fbernitt.teamcity.plugins.tcprowl.prowl.impl;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Utility class to help build prowl http responses.
 */
class ProwlHttpResponseBuilder {
    public static final ProtocolVersion PROTOCOL_VERSION = new ProtocolVersion("HTTP", 1, 1);

    public BasicHttpResponse buildErrorResponse(int statusCode, String errorMsg) throws IOException {
        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(PROTOCOL_VERSION, HttpStatus.SC_INTERNAL_SERVER_ERROR, null));


        Element root = new Element("prowl");
        Element success = new Element("error");
        success.setAttribute("code", String.valueOf(statusCode));
        success.setText(errorMsg);
        root.addContent(success);

        response.setEntity(jdomToEntity(root));
        return response;
    }

    public BasicHttpResponse buildSuccessResponse(int remaining) throws IOException {
        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(PROTOCOL_VERSION, 200, null));

        Element root = new Element("prowl");
        Element success = new Element("success");
        success.setAttribute("code", String.valueOf(200));
        success.setAttribute("remaining", String.valueOf(remaining));
        root.addContent(success);

        response.setEntity(jdomToEntity(root));
        return response;
    }

    private HttpEntity jdomToEntity(Element root) throws IOException {
        return new StringEntity(jdomToString(root));
    }

    private String jdomToString(Element root) throws IOException {
        Document doc = new Document(root);

        XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
        StringWriter writer = new StringWriter();

        outputter.output(doc, writer);

        return writer.getBuffer().toString();
    }
}