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
        jark.get("/test/longer", (p, q) -> ":longer");
        jark.put("/putter", (p, q) -> {System.out.println(p.body());  return "Reply: " + p.body();}); // Use body twice.
        jark.start();

        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/test"))
                .build();

        var client = HttpClient.newHttpClient();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Simple Get should return \"test\"", "test", response.body());

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/frod"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Request to bad path should return not found", 404, response.statusCode());

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/test/longer"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Get matching two paths should return \"test:longer\"", "test:longer", response.body());


        var publisher = HttpRequest.BodyPublishers.ofString("request buddy");
        request = HttpRequest.newBuilder()
                .PUT(publisher)
                .uri(URI.create("http://localhost:4567/putter"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Put should return \"Reply: request buddy\"", "Reply: request buddy", response.body());
    }
}
