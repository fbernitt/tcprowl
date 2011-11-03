package de.fbernitt.teamcity.plugins.tcprowl;

import de.fbernitt.teamcity.plugins.tcprowl.prowl.api.ProwlConnector;
import jetbrains.buildServer.controllers.ActionErrors;
import jetbrains.buildServer.controllers.AjaxRequestProcessor;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jdom.Element;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Ajax controller to allow sending of test messages.
 */
public class ProwlSettingsController extends BaseController {

    private final ProwlConnector prowlConnector;

    public ProwlSettingsController(SBuildServer server, WebControllerManager manager, ProwlConnector prowlConnector) {
        super(server);
        this.prowlConnector = prowlConnector;

        manager.registerController("/tcprowlSettings.html", this);
    }

    @Override
    protected ModelAndView doHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        new AjaxRequestProcessor().processRequest(request, response, new AjaxRequestProcessor.RequestHandler() {
            public void handleRequest(HttpServletRequest request, HttpServletResponse response, Element xmlResponse) {
                try {
                    sendProwlNotification(request);
                } catch (RuntimeException e) {
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

    private void sendProwlNotification(HttpServletRequest request) {
        String apiKey = request.getParameter("prowlApiKey");
        String message = request.getParameter("prowlTestMessage");
        Loggers.SERVER.info("Sending prowl test notification to [" + apiKey + "]");
        doHttpRequestCall(apiKey, message);
        getOrCreateMessages(request).addMessage("tcprowlMessage", "Message sent!");
    }

    private String createMessageWithNested(Exception e) {
        return e.getMessage();
    }

    private void doHttpRequestCall(String apiKey, String message) {
        ProwlNotification notification = new ProwlNotification(apiKey, "tcprowl config test", message);
        this.prowlConnector.sendNotification(notification);
    }
}
