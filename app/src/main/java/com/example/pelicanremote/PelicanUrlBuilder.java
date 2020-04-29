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
    private int serverPort;

    public PelicanUrlBuilder(String serverProtocol, String serverAddress, int serverPort){
        this.serverProtocol = serverProtocol;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public String build(Endpoint endpoint){
        switch (endpoint) {

            case ACTIVATE:
                return buildUrl(ACTIVATION_ENDPOINT);

            case DEACTIVE:
                return buildUrl(DEACTIVATION_ENDPOINT);

            case STATUS:
                return buildUrl(STATUS_ENDPOINT);

            default:
                return null;
        }
    }

    private String buildUrl(String endpoint){
        return this.serverProtocol + "://" + this.serverAddress + ":" + this.serverPort + endpoint;
    }

}
