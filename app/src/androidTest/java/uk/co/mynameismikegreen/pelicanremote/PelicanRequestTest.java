package uk.co.mynameismikegreen.pelicanremote;

import android.os.AsyncTask;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

import uk.co.mynameismikegreen.pelicanremote.utils.MockPelicanServerTest;


@RunWith(AndroidJUnit4.class)
public class PelicanRequestTest extends MockPelicanServerTest {

    @Test
    public void publicMethodReturnsAnAsyncTaskWrappingTheCoreBackgroundTask() throws ExecutionException, InterruptedException {
        // Given: a server returning fixed data
        String serverResponse = "My server response";
        String endpoint = "/status";
        setPelicanStub(endpoint, serverResponse);

        // When: the request is executed
        AsyncTask<String, String, String> result = PelicanRequest.execute(
                "http://" + getMockPelicanServerHost() + ":" + getMockPelicanServerPort() + endpoint
        );

        // Then: the result of the async task is the server response body
        Assert.assertEquals(serverResponse, result.get());
    }
}
