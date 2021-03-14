package uk.co.mynameismikegreen.pelicanremote;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import junitparams.JUnitParamsRunner;

import static org.junit.Assert.assertEquals;

import junitparams.Parameters;
import uk.co.mynameismikegreen.pelicanremote.PelicanUrlBuilder.Endpoint;


@RunWith(JUnitParamsRunner.class)
public class PelicanUrlBuilderTest {

    private final static String PROTOCOL = "http";
    private final static String ADDRESS = "192.168.0.1";
    private final static String PORT = "8888";
    private static final PelicanUrlBuilder URL_BUILDER = new PelicanUrlBuilder(PROTOCOL, ADDRESS, PORT);

    @BeforeClass
    public static void setUp() {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2021, 3, 14, 22, 30, 30, 0, zoneId);
        URL_BUILDER.setClock(Clock.fixed(zonedDateTime.toInstant(), zoneId));
    }

    @Test
    @Parameters({
            "ACTIVATE, http://192.168.0.1:8888/actions/activate?timeout_seconds=21600",
            "ACTIVATE_UNTIL_MIDNIGHT, http://192.168.0.1:8888/actions/activate?timeout_seconds=5369",
            "DEACTIVATE, http://192.168.0.1:8888/actions/deactivate",
            "RESCAN, http://192.168.0.1:8888/actions/rescan?timeout_seconds=21600",
            "STATUS, http://192.168.0.1:8888/status",
    })
    public void buildsEndpointsCorrectly(Endpoint endpoint, String expectedUrl) {
        String result = URL_BUILDER.build(endpoint);
        assertEquals(expectedUrl, result);
    }
}