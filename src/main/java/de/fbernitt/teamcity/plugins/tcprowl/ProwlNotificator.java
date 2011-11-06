package de.fbernitt.teamcity.plugins.tcprowl;

import com.intellij.openapi.diagnostic.Logger;
import de.fbernitt.teamcity.plugins.tcprowl.prowl.api.ProwlConnector;
import de.fbernitt.teamcity.plugins.tcprowl.prowl.api.ProwlException;
import jetbrains.buildServer.Build;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.mute.MuteInfo;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.users.NotificatorPropertyKey;
import jetbrains.buildServer.users.PropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.vcs.VcsRoot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Registers into TeamCity as a Notificator for prowl.
 */
public class ProwlNotificator implements Notificator {
    static final String TYPE = "tcprowl";
    static final String TYPE_NAME = "Prowl Notifier";
    static final String PROPERTY_APIKEY = "apiKey";
    static final PropertyKey PROPERTY_APIKEY_KEY = new NotificatorPropertyKey(TYPE, PROPERTY_APIKEY);
    static final String PROWL_APIKEY_DESCRIPTION = "Prowl API Key";

    private final ProwlConnector prowlConnector;

    public ProwlNotificator(NotificatorRegistry notificatorRegistry, ProwlConnector prowlConnector) {
        this.prowlConnector = prowlConnector;

        logger().info("Registering " + TYPE_NAME);
        List<UserPropertyInfo> userPropertyInfos = new ArrayList<UserPropertyInfo>();
        userPropertyInfos.add(new UserPropertyInfo(PROPERTY_APIKEY, PROWL_APIKEY_DESCRIPTION));
        notificatorRegistry.register(this, userPropertyInfos);
    }

    public void notifyBuildStarted(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        notifyUsers(sRunningBuild, sUsers);
    }

    public void notifyBuildSuccessful(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        notifyUsers(sRunningBuild, sUsers);
    }

    public void notifyBuildFailed(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        notifyUsers(sRunningBuild, sUsers);
    }

    public void notifyBuildFailedToStart(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        notifyUsers(sRunningBuild, sUsers);
    }

    public void notifyLabelingFailed(Build build, VcsRoot vcsRoot, Throwable throwable, Set<SUser> sUsers) {
        // not yet implemented
    }

    public void notifyBuildFailing(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        notifyUsers(sRunningBuild, sUsers);
    }

    public void notifyBuildProbablyHanging(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        notifyUsers(sRunningBuild, sUsers);
    }

    public void notifyResponsibleChanged(SBuildType sBuildType, Set<SUser> sUsers) {
        // not yet implemented
    }

    public void notifyResponsibleAssigned(SBuildType sBuildType, Set<SUser> sUsers) {
        // not yet implemented
    }

    public void notifyResponsibleChanged(TestNameResponsibilityEntry testNameResponsibilityEntry, TestNameResponsibilityEntry testNameResponsibilityEntry1, SProject sProject, Set<SUser> sUsers) {
        // not yet implemented
    }

    public void notifyResponsibleAssigned(TestNameResponsibilityEntry testNameResponsibilityEntry, TestNameResponsibilityEntry testNameResponsibilityEntry1, SProject sProject, Set<SUser> sUsers) {
        // not yet implemented
    }

    public void notifyResponsibleChanged(Collection<TestName> testNames, ResponsibilityEntry responsibilityEntry, SProject sProject, Set<SUser> sUsers) {
        // not yet implemented
    }

    public void notifyResponsibleAssigned(Collection<TestName> testNames, ResponsibilityEntry responsibilityEntry, SProject sProject, Set<SUser> sUsers) {
        // not yet implemented
    }

    public void notifyTestsMuted(Collection<STest> sTests, MuteInfo muteInfo, Set<SUser> sUsers) {
        // not yet implemented
    }

    public void notifyTestsUnmuted(Collection<STest> sTests, MuteInfo muteInfo, Set<SUser> sUsers) {
        // not yet implemented
    }

    public String getNotificatorType() {
        return TYPE;
    }

    public String getDisplayName() {
        return TYPE_NAME;
    }

    public void register() {

    }

    private void notifyUsers(SRunningBuild build, Set<SUser> sUsers) {
        logger().info("Prowl: Notify users about " + build.getFullName());
        for (SUser user : sUsers) {
            notifyUser(build, user);
        }
    }

    private void notifyUser(SRunningBuild build, SUser user) {
        logger().info("Prowl: Notify " + user.getName() + " about " + build.getFullName());

        String apiKey = user.getPropertyValue(PROPERTY_APIKEY_KEY);
        if (isApiKeyAvailable(apiKey)) {
            logger().info("Skipped notification of " + user.getName() + " because no API key is defined!");
            return; // skip notification
        }

        ProwlNotification notification = constructNotification(build, apiKey);
        sendNotification(notification);
    }

    private void sendNotification(ProwlNotification notification) {
        try {
            this.prowlConnector.sendNotification(notification);
        } catch (ProwlException e) {
            logger().error("Failed to send message to prowl: " + e.getMessage(), e);
        }
    }

    private ProwlNotification constructNotification(SRunningBuild build, String apiKey) {
        String title = build.getStatusDescriptor().getStatus().getText() + ": " + build.getBuildTypeName() + "#" + build.getBuildNumber();
        String message = "Build " + build.getStatusDescriptor().getStatus().getText() + ": " + build.getFullName() + " on agent " + build.getAgentName();

        return new ProwlNotification(apiKey, title, message);
    }

    private boolean isApiKeyAvailable(String apiKey) {
        return apiKey == null || apiKey.trim().length() == 0;
    }

    Logger logger() {
        return Loggers.SERVER;
    }
}
