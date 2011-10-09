package de.fbernitt.teamcity.plugins.tcprowl;

import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.MainConfigProcessor;
import jetbrains.buildServer.serverSide.SBuildServer;

/**
 * Created by IntelliJ IDEA.
 * User: folker
 * Date: 09.10.11
 * Time: 13:16
 * To change this template use File | Settings | File Templates.
 */
public class ProwlMainSettings implements MainConfigProcessor {

    private final SBuildServer server;

    public ProwlMainSettings (SBuildServer server) {
        this.server = server;
    }

    public void readFrom(org.jdom.Element element) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void writeTo(org.jdom.Element element) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void register () {
        Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: Registering");
        this.server.registerExtension(MainConfigProcessor.class, "tcprowl", this);
    }
}
