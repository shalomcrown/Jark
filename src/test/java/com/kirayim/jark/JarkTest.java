package com.kirayim.jark;
import org.junit.Test;
import static org.junit.Assert.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Unit test for simple App.
 */
public class JarkTest {

    @Test
    public void testApp() throws Exception{

        Jark jark = Jark.ignite();
        jark.get("/test", (p, q) -> "test");
        jark.start();

        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/test"))
                .build();

        var client = HttpClient.newHttpClient();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("test", response.body());
    }
}
