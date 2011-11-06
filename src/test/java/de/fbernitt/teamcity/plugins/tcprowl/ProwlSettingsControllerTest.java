package de.fbernitt.teamcity.plugins.tcprowl;

import de.fbernitt.teamcity.plugins.tcprowl.prowl.api.ProwlConnector;
import de.fbernitt.teamcity.plugins.tcprowl.prowl.api.ProwlException;
import jetbrains.buildServer.controllers.ActionMessages;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.isA;

/**
 * Tests the ProwlSettingsController.
 */
public class ProwlSettingsControllerTest {

    public static final String ERROR_KEY = "tcprowlProblem";
    public static final String ATTRIBUTE_ID = "id";
    private final SBuildServer buildServer = Mockito.mock(SBuildServer.class);
    private final WebControllerManager manager = Mockito.mock(WebControllerManager.class);
    private final ProwlConnector prowlConnector = Mockito.mock(ProwlConnector.class);
    private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    private final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    private final HttpSession session = Mockito.mock(HttpSession.class);
    private final ActionMessages actionMessages = new ActionMessages();

    @Before
    public void initRequest() {
        Mockito.when(this.request.getSession()).thenReturn(this.session);
        Mockito.when(this.request.getAttribute("actionMessages")).thenReturn(this.actionMessages);
    }

    @Test
    public void thatConstructorRegistersController() {
        ProwlSettingsController controller = new ProwlSettingsController(this.buildServer, this.manager, this.prowlConnector);

        Mockito.verify(this.manager).registerController("/tcprowlSettings.html", controller);
    }

    @Test
    public void thatConsistentRequestIsHandled() throws Exception {
        //given
        Mockito.when(this.request.getParameter("prowlApiKey")).thenReturn("apiKey");
        Mockito.when(this.request.getParameter("prowlTestMessage")).thenReturn("test message");
        ProwlNotification expected = new ProwlNotification("apiKey", "tcprowl config test", "test message");
        Element element = new Element("test");
        ProwlSettingsController controller = new ProwlSettingsController(this.buildServer, this.manager, this.prowlConnector);

        // when
        controller.processAjaxRequest(this.request, element);

        //then
        Mockito.verify(this.prowlConnector).sendNotification(expected);
        assertEquals("Message sent!", this.actionMessages.getMessage("tcprowlMessage"));
    }

    @Test
    public void thatErrorIsAddedToJDom() throws IOException {
        //given
        final String exceptionMessage = "test exception";
        Mockito.when(this.request.getParameter("prowlApiKey")).thenReturn("apiKey");
        Mockito.when(this.request.getParameter("prowlTestMessage")).thenReturn("test message");
        ProwlNotification expected = new ProwlNotification("apiKey", "tcprowl config test", "test message");
        Element element = new Element("test");
        ProwlSettingsController controller = new ProwlSettingsController(this.buildServer, this.manager, this.prowlConnector);

        Mockito.when(this.prowlConnector.sendNotification(isA(ProwlNotification.class))).thenThrow(new ProwlException(exceptionMessage));

        // when
        controller.processAjaxRequest(request, element);

        //then
        Element errors = element.getChild("errors");
        assertNotNull(errors);
        assertEquals(1, errors.getChildren("error").size());
        Element error = errors.getChild("error");
        assertEquals(ERROR_KEY, error.getAttributeValue(ATTRIBUTE_ID));
        assertEquals(exceptionMessage, error.getText());

        assertEquals(exceptionMessage, this.actionMessages.getMessage("tcprowlMessage"));
    }

}
