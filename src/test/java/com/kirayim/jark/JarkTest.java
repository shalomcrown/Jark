/*
 * Copyright notice
 * This code is not covered by any copyright
 *
 * In no event shall the author(s) be liable for any special, direct, indirect, consequential,
 *  or incidental damages or any damages whatsoever, whether in an action of contract,
 *  negligence or other tort, arising out of or in connection with the use of the code or the
 *  contents of the code
 *
 *  All information in the code is provided "as is" with no guarantee of completeness, accuracy,
 *   timeliness or of the results obtained from the use of this code, and without warranty of any
 *   kind, express or implied, including, but not limited to warranties of performance,
 *   merchantability and fitness for a particular purpose.
 *
 *  The author(s) will not be liable to You or anyone else for any decision made or action
 *  taken in reliance on the information given by the code or for any consequential, special
 *  or similar damages, even if advised of the possibility of such damages.
 *
 *
 */

package com.kirayim.jark;

import org.junit.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class JarkTest {

    public SSLContext getSelfSignedAcceptingContext() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("SSL"); // OR TLS
        sslContext.init(null, new TrustManager[]{new X509ExtendedTrustManager() {
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
            }
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
            }
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
            }
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
            }
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }}, new SecureRandom());

        return sslContext;
    }

    // ===========================================================================

    @Test
    public void basicTest() throws Exception{
        final long nowMillis = System.currentTimeMillis();

        try (Jark jark = Jark.ignite()) {
            jark.get("/test", (p, q) -> "test");
            jark.get("/test/longer", (p, q) -> ":longer");
            jark.put("/putter", (p, q) -> {
                System.out.println(p.body());
                return "Reply: " + p.body();
            }); // Use body twice.
            jark.get("/whats_the_time", "text/plain", (p, q) -> new Date().toString());
            jark.get("/whats_the_time", "application/json", (p, q) -> String.format("{\"time\":\"%1$TFT%1$TT %1$TZ\"}", new Date()));

            jark.get("/whats_the_time", "application/octet-stream", (p, q) -> {
                try (var out = new ByteArrayOutputStream();
                        var write = new ObjectOutputStream(out)) {
                    write.writeLong(nowMillis);
                    write.flush();
                    q.body(out.toByteArray());
                    q.type("application/octet-stream");
                }
                return null;

            });
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


            request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://localhost:4567/whats_the_time"))
                    .header("Accept", "text/plain")
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertFalse("Body should not be JSON", response.body().contains("{"));

            request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://localhost:4567/whats_the_time"))
                    .header("Accept", "application/json")
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertTrue("Body should be JSON:" + response.body(), response.body().contains("{"));

            request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://localhost:4567/whats_the_time"))
                    .header("Accept", "application/octet-stream")
                    .build();

            var in = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            assertEquals("Expect correct content type", in.headers().firstValue("Content-type").get(), "application/octet-stream");
            var obj = new ObjectInputStream(in.body());
            long time = obj.readLong();
            assertEquals("Body should be binary time", time, nowMillis);
        }
    }

    // ===========================================================================

    @Test
    public void sslTest() throws Exception {

        try (Jark jark = Jark.ignite()) {
            jark.get("/test", (p, q) -> "test");

            URL url = this.getClass().getClassLoader().getResource("jark.p12");

            jark.secure(url.toString(), "jarkpass", url.toString(), "jarkpass");
            jark.start();


            var request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("https://localhost:4567/test"))
                    .build();

            var client = HttpClient
                    .newBuilder()
                    .sslContext(getSelfSignedAcceptingContext())
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("Simple Get should return \"test\"", "test", response.body());
        }
    }


    // ===========================================================================
    @Test
    public void pathParamTest() throws Exception {
        try (Jark jark = Jark.ignite()) {
            jark.get("/test/:whoami/hello", (p, q) -> "Hello " + p.params("whoami"));
            jark.start();

            var request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("http://localhost:4567/test/" + URLEncoder.encode("Fred and Mike", StandardCharsets.UTF_8) + "/hello"))
                    .build();

            var client = HttpClient.newHttpClient();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("Should return result with names", "Hello Fred and Mike", response.body());
        }
    }


    // ===========================================================================

    @Test
    public void portTest() throws Exception {
        try (Jark jark = Jark.ignite()) {
            jark.port(10345)
                .get("/test", (p, q) -> "test")
                .start();

            var request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://localhost:10345/test"))
                    .build();

            var client = HttpClient
                    .newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("Simple Get should return \"test\"", "test", response.body());
        }
    }

    // ===========================================================================

    @Test
    public void staticContentTestResource() throws Exception {
        try (Jark jark = Jark.ignite()) {
            jark.location("/");
            jark.location("/", "/path");
            jark.start();

            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://localhost:4567/TestDataResource.html"))
                    .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertTrue("Simple Get should return \"test\"", response.body().contains("Yes - there is some text"));

            request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://localhost:4567/path/TestDataResource.html"))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertTrue("Simple Get should return \"test\"", response.body().contains("Data Yes - there is some text"));
        }
    }

    // ===========================================================================

    @Test
    public void staticContentTestFileSystem() throws Exception {
        try (Jark jark = Jark.ignite()) {
            jark.location("/tmp");
            jark.location("/tmp", "/path");
            jark.start();

            try (InputStream in = ClassLoader.getSystemResourceAsStream("TestDataResource.html");
                                OutputStream out = new FileOutputStream("/tmp/TestDataFile.html");) {

                in.transferTo(out);
            }

            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://localhost:4567/TestDataFile.html"))
                    .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertTrue("Simple Get should return \"test\"", response.body().contains("Yes - there is some text"));

            request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://localhost:4567/path/TestDataFile.html"))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertTrue("Simple Get should return \"test\"", response.body().contains("Yes - there is some text"));
        }
    }
}
