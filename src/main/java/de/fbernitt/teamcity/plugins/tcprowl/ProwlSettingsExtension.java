package de.fbernitt.teamcity.plugins.tcprowl;

import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.SimplePageExtension;

import javax.servlet.http.HttpServletRequest;

/**
 * Page extension to provide prowl test ui elements.
 * <p>
 * This extension is only visible on prowl notifier page.
 */
public class ProwlSettingsExtension extends SimplePageExtension {
    public ProwlSettingsExtension(PagePlaces pagePlaces) {
        super(pagePlaces);
        setIncludeUrl("tcprowlSettings.jsp");
        setPlaceId(PlaceId.NOTIFIER_SETTINGS_FRAGMENT);
        setPluginName("tcprowl");
        register();
    }

    @Override
    public boolean isAvailable(HttpServletRequest request) {
        String notificatorType = request.getParameter("notificatorType");
        return "tcprowl".equals(notificatorType);
    }
}
