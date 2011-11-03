package de.fbernitt.teamcity.plugins.tcprowl.prowl.api;

import de.fbernitt.teamcity.plugins.tcprowl.ProwlNotification;

/**
 * Provides an interface to the prowl server.
 * <p>
 * The connector encapsulates the entire communication with the prowl service.
 */
public interface ProwlConnector {

    /**
     * Sends the notification to prowl service.
     *
     * @param notification The notification
     * @return The prowl result
     * @throws ProwlException
     */
    ProwlResult sendNotification(ProwlNotification notification);
}
