package uk.co.mynameismikegreen.pelicanremote;

import java.time.Clock;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import lombok.Setter;

@Setter
public class PelicanUrlBuilder {

    private static final String ACTIVATION_ENDPOINT = "/actions/activate";
    private static final String DEACTIVATION_ENDPOINT = "/actions/deactivate";
    private static final String STATUS_ENDPOINT = "/status";
    private static final String RESCAN_ENDPOINT = "/actions/rescan";
    public static final String TIMEOUT_PARAM = "?timeout_seconds=";

    public enum Endpoint{
        ACTIVATE,
        ACTIVATE_UNTIL_MIDNIGHT,
        DEACTIVATE,
        STATUS,
        RESCAN
    }

    private String serverProtocol;
    private String serverAddress;
    private String serverPort;
    private String automaticDeactivationTimeoutSeconds;
    private Clock clock;

    public PelicanUrlBuilder(String serverProtocol, String serverAddress, String serverPort){
        this.serverProtocol = serverProtocol;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.automaticDeactivationTimeoutSeconds = "21600";
        this.clock = Clock.systemDefaultZone();
    }

    public String build(Endpoint endpoint){
        switch (endpoint) {

            case ACTIVATE:
                return buildBaseUrl(ACTIVATION_ENDPOINT)
                        + TIMEOUT_PARAM + this.automaticDeactivationTimeoutSeconds;

            case ACTIVATE_UNTIL_MIDNIGHT:
                return buildBaseUrl(ACTIVATION_ENDPOINT)
                        + TIMEOUT_PARAM + secondsUntilMidnight();

            case DEACTIVATE:
                return buildBaseUrl(DEACTIVATION_ENDPOINT);

            case STATUS:
                return buildBaseUrl(STATUS_ENDPOINT);

            case RESCAN:
                return buildBaseUrl(RESCAN_ENDPOINT)
                        + TIMEOUT_PARAM + this.automaticDeactivationTimeoutSeconds;

            default:
                return null;
        }
    }

    private String buildBaseUrl(String endpoint){
        return this.serverProtocol + "://" + this.serverAddress + ":" + this.serverPort + endpoint;
    }

    private int secondsUntilMidnight(){
        return (int) LocalTime.now(this.clock).until(LocalTime.of(23, 59, 59), ChronoUnit.SECONDS);
    }

}
