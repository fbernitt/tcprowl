package de.fbernitt.teamcity.plugins.tcprowl;

import jetbrains.buildServer.web.openapi.PagePlace;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the ProwlSettingsExtension.
 */
public class ProwlSettingsExtensionTest {

    private final PagePlaces pagePlaces = Mockito.mock(PagePlaces.class);
    private final PagePlace pagePlace = Mockito.mock(PagePlace.class);
    private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    @Test
    public void thatExtensionIsRegisteredOnConstruction () {
        //given
        initPagePlacesMock();

        //when
        ProwlSettingsExtension extension = new ProwlSettingsExtension(this.pagePlaces);

        //then
        PositionConstraint positionConstraint = null;
        Mockito.verify(this.pagePlace).addExtension(extension, positionConstraint);
    }

    private void initPagePlacesMock() {
        Mockito.when(this.pagePlaces.getPlaceById(PlaceId.NOTIFIER_SETTINGS_FRAGMENT)).thenReturn(this.pagePlace);
    }

    @Test
    public void thatExtensionNotAvailableWithMissingParam () {
        //given
        initPagePlacesMock();
        ProwlSettingsExtension extension = new ProwlSettingsExtension(this.pagePlaces);

        //when
        assertFalse(extension.isAvailable(this.request));
    }

    @Test
    public void thatExtensionNotAvailableForWrongParamValue () {
        //given
        initPagePlacesMock();
        ProwlSettingsExtension extension = new ProwlSettingsExtension(this.pagePlaces);
        Mockito.when(this.request.getParameter(ProwlSettingsExtension.NOTIFICATOR_TYPE)).thenReturn("foobar");

        //when
        assertFalse(extension.isAvailable(this.request));
    }

    @Test
    public void thatExtensionAvailableForCorrectParam () {
        //given
        initPagePlacesMock();
        ProwlSettingsExtension extension = new ProwlSettingsExtension(this.pagePlaces);
        Mockito.when(this.request.getParameter(ProwlSettingsExtension.NOTIFICATOR_TYPE)).thenReturn(ProwlSettingsExtension.PLUGIN_NAME);

        //when
        assertTrue(extension.isAvailable(this.request));
    }

}
