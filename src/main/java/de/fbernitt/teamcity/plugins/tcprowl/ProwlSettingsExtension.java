package de.fbernitt.teamcity.plugins.tcprowl;

import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.SimplePageExtension;

/**
 * Created by IntelliJ IDEA.
 * User: folker
 * Date: 09.10.11
 * Time: 14:07
 * To change this template use File | Settings | File Templates.
 */
public class ProwlSettingsExtension extends SimplePageExtension {
    public ProwlSettingsExtension(PagePlaces pagePlaces) {
        super(pagePlaces);
        setIncludeUrl("tcprowlSettings.jsp");
        setPlaceId(PlaceId.NOTIFIER_SETTINGS_FRAGMENT);
        setPluginName("tcprowl");
        register();
    }
}
