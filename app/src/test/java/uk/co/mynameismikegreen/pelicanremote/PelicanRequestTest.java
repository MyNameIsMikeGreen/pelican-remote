package uk.co.mynameismikegreen.pelicanremote;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class PelicanRequestTest {


    private final PelicanRequest pelicanRequest = new PelicanRequest();

    @Test
    public void returnsResponseBodyAsString() throws IOException {
        // Given: a server returning fixed data
        String serverResponse = "My server response";
        MockWebServer mockWebServer = setUpMockWebServer(serverResponse);

        // When: the class performs its background task
        String result = pelicanRequest.doInBackground(mockWebServer.url("/status").toString());

        // Then: the server response is returned
        Assert.assertEquals(serverResponse, result);

        // Cleanup
        mockWebServer.shutdown();
    }

    private MockWebServer setUpMockWebServer(String serverResponse) throws IOException {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(serverResponse));
        mockWebServer.start();
        return mockWebServer;
    }

}