package de.fbernitt.teamcity.plugins.tcprowl;

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
     */
    void sendNotification(ProwlNotification notification);
}
