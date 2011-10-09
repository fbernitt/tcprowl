package de.fbernitt.teamcity.plugins.tcprowl;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.mute.MuteInfo;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.vcs.VcsRoot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: folker
 * Date: 09.10.11
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
public class ProwlNotificator implements Notificator {

    private static final String TYPE = "tcprowl";
    private static final String TYPE_NAME = "Prowl Notifier";
    private static final String PROWL_API_KEY = "tcprowl.gApiKey";


    private final List<UserPropertyInfo> userPropertyInfos = new ArrayList<UserPropertyInfo>();

    public ProwlNotificator(NotificatorRegistry notificatorRegistry) {
        Loggers.SERVER.info("Registering " + TYPE_NAME);
        this.userPropertyInfos.add(new UserPropertyInfo(PROWL_API_KEY, "Prowl API Key"));
        notificatorRegistry.register(this, this.userPropertyInfos);
    }

    public void notifyBuildStarted(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyBuildSuccessful(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyBuildFailed(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyBuildFailedToStart(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyLabelingFailed(Build build, VcsRoot vcsRoot, Throwable throwable, Set<SUser> sUsers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyBuildFailing(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyBuildProbablyHanging(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyResponsibleChanged(SBuildType sBuildType, Set<SUser> sUsers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyResponsibleAssigned(SBuildType sBuildType, Set<SUser> sUsers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyResponsibleChanged(TestNameResponsibilityEntry testNameResponsibilityEntry, TestNameResponsibilityEntry testNameResponsibilityEntry1, SProject sProject, Set<SUser> sUsers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyResponsibleAssigned(TestNameResponsibilityEntry testNameResponsibilityEntry, TestNameResponsibilityEntry testNameResponsibilityEntry1, SProject sProject, Set<SUser> sUsers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyResponsibleChanged(Collection<TestName> testNames, ResponsibilityEntry responsibilityEntry, SProject sProject, Set<SUser> sUsers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyResponsibleAssigned(Collection<TestName> testNames, ResponsibilityEntry responsibilityEntry, SProject sProject, Set<SUser> sUsers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyTestsMuted(Collection<STest> sTests, MuteInfo muteInfo, Set<SUser> sUsers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyTestsUnmuted(Collection<STest> sTests, MuteInfo muteInfo, Set<SUser> sUsers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getNotificatorType() {
        return TYPE;
    }

    public String getDisplayName() {
        return TYPE_NAME;
    }

    public void register() {

    }
}
