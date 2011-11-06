package de.fbernitt.teamcity.plugins.tcprowl;

import com.intellij.openapi.diagnostic.Logger;
import de.fbernitt.teamcity.plugins.tcprowl.prowl.api.ProwlConnector;
import de.fbernitt.teamcity.plugins.tcprowl.prowl.api.ProwlException;
import jetbrains.buildServer.StatusDescriptor;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.UserPropertyInfo;
import jetbrains.buildServer.users.SUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;

/**
 * Tests the ProwlNotificator.
 */
public class ProwlNotificatorTest {

    private final NotificatorRegistry notificatorRegistry = Mockito.mock(NotificatorRegistry.class);
    private final ProwlConnector prowlConnector = Mockito.mock(ProwlConnector.class);
    private final SRunningBuild runningBuild = Mockito.mock(SRunningBuild.class);
    private final SUser user = Mockito.mock(SUser.class);
    private final Set<SUser> userSet = new HashSet<SUser>();
    private final ProwlNotificator notificator = createProwlNotificatorWithMockedLogger();


    @Before
    public void setUp() {
        this.userSet.add(this.user);
    }

    @Test
    public void thatConstructorRegistersNotificatorWithApiKeyProperty() {
        // given
        ArrayList<UserPropertyInfo> infos = new ArrayList<UserPropertyInfo>();
        infos.add(new UserPropertyInfo(ProwlNotificator.PROPERTY_APIKEY, ProwlNotificator.PROWL_APIKEY_DESCRIPTION));

        //then
        Mockito.verify(this.notificatorRegistry).register(Matchers.same(notificator), Matchers.eq(infos));
    }

    @Test
    public void thatCorrectConstantsAreReturned() {
        //when/then
        assertEquals(ProwlNotificator.TYPE, this.notificator.getNotificatorType());
        assertEquals(ProwlNotificator.TYPE_NAME, this.notificator.getDisplayName());
    }

    @Test
    public void thatNotificationIsConstructedProperly() {
        //given
        setupBuildMock(Status.NORMAL);
        setupMockUser("testapikey");
        ProwlNotification expected = new ProwlNotification("testapikey", "SUCCESS: BuildTypeName#42", "Build SUCCESS: FullBuildName on agent AgentName");

        //when
        this.notificator.notifyBuildSuccessful(this.runningBuild, this.userSet);

        //then
        Mockito.verify(this.prowlConnector).sendNotification(expected);
    }

    @Test
    public void thatTwoUsersAreNotificatedForProperlyNotification() {
        //given
        setupBuildMock(Status.NORMAL);
        setupMockUser("testapikey");
        SUser secondUser = Mockito.mock(SUser.class);
        Mockito.when(secondUser.getPropertyValue(ProwlNotificator.PROPERTY_APIKEY_KEY)).thenReturn("secondApiKey");
        this.userSet.add(secondUser);

        ProwlNotification firstExpected = new ProwlNotification("testapikey", "SUCCESS: BuildTypeName#42", "Build SUCCESS: FullBuildName on agent AgentName");
        ProwlNotification secondExpected = new ProwlNotification("secondApiKey", "SUCCESS: BuildTypeName#42", "Build SUCCESS: FullBuildName on agent AgentName");

        //when
        this.notificator.notifyBuildSuccessful(this.runningBuild, this.userSet);

        //then
        Mockito.verify(this.prowlConnector).sendNotification(firstExpected);
        Mockito.verify(this.prowlConnector).sendNotification(secondExpected);
    }

    @Test
    public void thatNoNotificationIsSentForEmptyApiKey() {
        //given
        setupBuildMock(Status.NORMAL);
        setupMockUser(null);

        //when
        this.notificator.notifyBuildSuccessful(this.runningBuild, this.userSet);

        //then
        Mockito.verifyZeroInteractions(this.prowlConnector);
    }

    @Test
    public void thatOnlyExpectedNotificationsArePropagated() {
        //given
        setupBuildMock(Status.NORMAL);
        setupMockUser("testapikey");

        //when
        this.notificator.notifyBuildStarted(this.runningBuild, this.userSet);
        this.notificator.notifyBuildSuccessful(this.runningBuild, this.userSet);
        this.notificator.notifyBuildFailed(this.runningBuild, this.userSet);
        this.notificator.notifyBuildFailedToStart(this.runningBuild, this.userSet);
        this.notificator.notifyBuildFailing(this.runningBuild, this.userSet);
        this.notificator.notifyBuildProbablyHanging(this.runningBuild, this.userSet);

        //then
        Mockito.verify(this.prowlConnector, Mockito.times(6)).sendNotification(isA(ProwlNotification.class));
    }

    @Test
    public void thatNoUnsupportedNotificationsArePropagated() {
        //given
        setupBuildMock(Status.NORMAL);
        setupMockUser("testapikey");

        //when
        this.notificator.notifyLabelingFailed(this.runningBuild, null, null, this.userSet);
        this.notificator.notifyResponsibleChanged(null, this.userSet);
        this.notificator.notifyResponsibleAssigned(null, this.userSet);
        this.notificator.notifyResponsibleChanged((TestNameResponsibilityEntry) null, null, null, this.userSet);
        this.notificator.notifyResponsibleAssigned((TestNameResponsibilityEntry) null, null, null, this.userSet);
        this.notificator.notifyResponsibleChanged(null, (ResponsibilityEntry) null, null, this.userSet);
        this.notificator.notifyResponsibleAssigned(null, (ResponsibilityEntry) null, null, this.userSet);
        this.notificator.notifyTestsMuted(null, null, this.userSet);
        this.notificator.notifyTestsUnmuted(null, null, this.userSet);

        //then
        Mockito.verifyZeroInteractions(this.prowlConnector);
    }

    @Test
    public void thatProwlExceptionIsNotPropageted() {
        //given
        setupBuildMock(Status.NORMAL);
        setupMockUser("testapikey");

        Mockito.when(this.prowlConnector.sendNotification(isA(ProwlNotification.class))).thenThrow(new ProwlException("A test exception"));

        //when
        this.notificator.notifyBuildSuccessful(this.runningBuild, this.userSet);

        //then expected no exception
    }

    private void setupMockUser(String apiKey) {
        Mockito.when(this.user.getPropertyValue(ProwlNotificator.PROPERTY_APIKEY_KEY)).thenReturn(apiKey);
    }

    private void setupBuildMock(Status status) {
        Mockito.when(this.runningBuild.getBuildTypeName()).thenReturn("BuildTypeName");
        Mockito.when(this.runningBuild.getBuildNumber()).thenReturn("42");
        Mockito.when(this.runningBuild.getFullName()).thenReturn("FullBuildName");
        Mockito.when(this.runningBuild.getAgentName()).thenReturn("AgentName");
        Mockito.when(this.runningBuild.getStatusDescriptor()).thenReturn(new StatusDescriptor(status, "Status Text"));
    }

    private ProwlNotificator createProwlNotificatorWithMockedLogger() {
        return new ProwlNotificator(this.notificatorRegistry, this.prowlConnector) {
            @Override
            Logger logger() {
                return Mockito.mock(Logger.class);
            }
        };
    }
}
