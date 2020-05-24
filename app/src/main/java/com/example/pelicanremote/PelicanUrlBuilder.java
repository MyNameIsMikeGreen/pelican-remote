package com.example.pelicanremote;

public class PelicanUrlBuilder {

    private static final String ACTIVATION_ENDPOINT = "/actions/activate";
    private static final String DEACTIVATION_ENDPOINT = "/actions/deactivate";
    private static final String STATUS_ENDPOINT = "/status";

    public enum Endpoint{
        ACTIVATE,
        DEACTIVE,
        STATUS
    }

    private String serverProtocol;
    private String serverAddress;
    private String serverPort;
    private String automaticDeactivationTimeoutSeconds;

    public PelicanUrlBuilder(String serverProtocol, String serverAddress, String serverPort){
        this.serverProtocol = serverProtocol;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.automaticDeactivationTimeoutSeconds = "21600";
    }

    public String build(Endpoint endpoint){
        switch (endpoint) {

            case ACTIVATE:
                return buildBaseUrl(ACTIVATION_ENDPOINT)
                        + "?timeout_seconds=" + this.automaticDeactivationTimeoutSeconds;

            case DEACTIVE:
                return buildBaseUrl(DEACTIVATION_ENDPOINT);

            case STATUS:
                return buildBaseUrl(STATUS_ENDPOINT);

            default:
                return null;
        }
    }

    public void setServerProtocol(String serverProtocol) {
        this.serverProtocol = serverProtocol;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public void setAutomaticDeactivationTimeoutHours(String automaticDeactivationTimeoutSeconds) {
        this.automaticDeactivationTimeoutSeconds = automaticDeactivationTimeoutSeconds;
    }

    private String buildBaseUrl(String endpoint){
        return this.serverProtocol + "://" + this.serverAddress + ":" + this.serverPort + endpoint;
    }

}
