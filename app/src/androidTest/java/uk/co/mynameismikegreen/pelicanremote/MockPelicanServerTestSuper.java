package uk.co.mynameismikegreen.pelicanremote;

import com.github.tomakehurst.wiremock.WireMockServer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class MockPelicanServerTestSuper {

    public static final WireMockServer mockServer = new WireMockServer(wireMockConfig());

    @BeforeClass
    public static void setUpClass(){
        mockServer.start();
    }

    @After
    public void setUpTest(){
        mockServer.resetAll();
    }

    @AfterClass
    public static void cleanUpMockPelicanServer() {
        mockServer.stop();
    }

    public void setPelicanStub(String endpoint, String serverResponse) {
        mockServer.stubFor(get(urlPathEqualTo(endpoint))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withBody(serverResponse)
                        .withStatus(200)));
    }

    public String getMockPelicanServerHost(){
        return "localhost";
    }

    public int getMockPelicanServerPort(){
        return mockServer.port();
    }

}