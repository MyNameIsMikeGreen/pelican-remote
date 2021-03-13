package uk.co.mynameismikegreen.pelicanremote;

import android.os.AsyncTask;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutionException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;


@RunWith(AndroidJUnit4.class)
public class PelicanRequestTest {

    @Test
    public void publicMethodReturnsAnAsyncTaskWrappingTheCoreBackgroundTask() throws IOException, ExecutionException, InterruptedException {
        // Given: a server returning fixed data
        String serverResponse = "My server response";
        MockWebServer mockWebServer = setUpMockWebServer(serverResponse);

        // When: the request is executed
        AsyncTask<String, String, String> result = PelicanRequest.execute(mockWebServer.url("/status").toString());

        // Then: the result of the async task is the server response body
        Assert.assertEquals(serverResponse, result.get());

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
