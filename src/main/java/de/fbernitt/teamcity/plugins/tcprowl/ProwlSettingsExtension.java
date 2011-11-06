package de.fbernitt.teamcity.plugins.tcprowl;

import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.SimplePageExtension;

import javax.servlet.http.HttpServletRequest;

/**
 * Page extension to provide prowl test ui elements.
 * <p/>
 * This extension is only visible on prowl notifier page.
 */
public class ProwlSettingsExtension extends SimplePageExtension {

    static final String NOTIFICATOR_TYPE = "notificatorType";
    static final String PLUGIN_NAME = "tcprowl";
    static final String TCPROWL_SETTINGS_JSP = "tcprowlSettings.jsp";

    public ProwlSettingsExtension(PagePlaces pagePlaces) {
        super(pagePlaces);
        setIncludeUrl(TCPROWL_SETTINGS_JSP);
        setPlaceId(PlaceId.NOTIFIER_SETTINGS_FRAGMENT);
        setPluginName(PLUGIN_NAME);
        register();
    }

    @Override
    public boolean isAvailable(HttpServletRequest request) {
        String notificatorType = request.getParameter(NOTIFICATOR_TYPE);
        return PLUGIN_NAME.equals(notificatorType);
    }
}
