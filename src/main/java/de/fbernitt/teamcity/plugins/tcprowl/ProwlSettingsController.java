package de.fbernitt.teamcity.plugins.tcprowl;

import jetbrains.buildServer.controllers.ActionErrors;
import jetbrains.buildServer.controllers.AjaxRequestProcessor;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jdom.Element;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Ajax controller to allow sending of test messages.
 */
public class ProwlSettingsController extends BaseController {

    public ProwlSettingsController(SBuildServer server, WebControllerManager manager) {
        super(server);

        manager.registerController("/tcprowlSettings.html", this);
    }

    @Override
    protected ModelAndView doHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        new AjaxRequestProcessor().processRequest(request, response, new AjaxRequestProcessor.RequestHandler() {
            public void handleRequest(HttpServletRequest request, HttpServletResponse response, Element xmlResponse) {
                try {
                    sendProwlNotification(request);
                } catch (Exception e) {
                    Loggers.SERVER.warn(e);
                    ActionErrors errors = new ActionErrors();
                    errors.addError("tcprowlProblem", createMessageWithNested(e));
                    getOrCreateMessages(request).addMessage("tcprowlMessage", createMessageWithNested(e));
                    errors.serialize(xmlResponse);
                }
            }
        });

        return null;
    }

    private void sendProwlNotification(HttpServletRequest request) throws IOException {
        String apiKey = request.getParameter("prowlApiKey");
        String message = request.getParameter("prowlTestMessage");
        Loggers.SERVER.info("Sending prowl test notification to [" + apiKey + "]");
        doHttpRequestCall(apiKey, message);
        getOrCreateMessages(request).addMessage("tcprowlMessage", "Message sent!");
    }

    private String createMessageWithNested(Exception e) {
        return e.getMessage();
    }

    private void doHttpRequestCall(String apiKey, String message) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://api.prowlapp.com/publicapi/add");

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("apikey", apiKey));
        params.add(new BasicNameValuePair("application", "TeamCity"));
        params.add(new BasicNameValuePair("event", "tcprowl config test"));
        params.add(new BasicNameValuePair("description", message));
        UrlEncodedFormEntity form = new UrlEncodedFormEntity(params);
        post.setEntity(form);

        HttpResponse response = client.execute(post);

        Loggers.SERVER.info("Response status: " + response.getStatusLine());
        Loggers.SERVER.info("Response status: " + EntityUtils.toString(response.getEntity()));
    }
}
