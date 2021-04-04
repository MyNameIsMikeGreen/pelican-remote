package uk.co.mynameismikegreen.pelicanremote.utils;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class PelicanUtils {

    public static MockWebServer setUpMockWebServer(String serverResponse) throws IOException {
        // TODO: Switch exclusively to wiremock
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(serverResponse));
        mockWebServer.start();
        return mockWebServer;
    }

}