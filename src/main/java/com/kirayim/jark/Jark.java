package com.kirayim.jark;

import com.sun.net.httpserver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Hello world!
 *
 */
public class Jark implements Closeable, HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(Jark.class);
    protected static final String DEFAULT_ACCEPT_TYPE = "*/*";
    protected boolean initialized = false;
    protected int port = 4567;
    protected String ipAddress = "0.0.0.0";

    protected String basePath = "/";

    protected SSLContext ssl = null;

    HttpServer server;
    static Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    List<JarkRoute> routes = new ArrayList<>();


    // ===========================================================================

    public static Jark ignite() {
        return new Jark();
    }

    // ===========================================================================

    public void start() throws Exception {
        if (ssl != null) {
            server = HttpsServer.create(new InetSocketAddress(ipAddress, port), -1);
            ((HttpsServer)server).setHttpsConfigurator(new HttpsConfigurator(ssl));
        } else {
            server = HttpServer.create(new InetSocketAddress(ipAddress, port), -1);
        }

        server.setExecutor(executor);
        server.createContext(basePath, this);
    }

    // ===========================================================================

    public void close() {
        try {
            server.stop(1);
        } catch (Exception e) {
            logger.error("Couldn't stop Jark server", e);
        }
    }

    // ===========================================================================

    public Jark get(String path, String accept, Route handler) {
        routes.add(new JarkRoute(HttpMethod.GET, path, accept, handler));
        return this;
    }

    public Jark post(String path, String accept, Route handler) {
        routes.add(new JarkRoute(HttpMethod.POST, path, accept, handler));
        return this;
    }

    public Jark put(String path, String accept, Route handler) {
        routes.add(new JarkRoute(HttpMethod.PUT, path, accept, handler));
        return this;
    }

    public Jark get(String path, Route handler) {
        return get(path, null, handler);
    }

    public Jark post(String path, Route handler) {
        return post(path, null, handler);
    }

    public Jark put(String path, Route handler) {
        return put(path, null, handler);
    }

    // ==============================================================================

    public void handle(HttpExchange exchange) throws IOException {
//        exchange.getRequestMethod()

//        exchange.getResponseHeaders().set(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);

        try {
//            List<ByteBuffer> requestBufs = AiHttpClient.readBuffers(exchange.getRequestBody());
//
//            List<ByteBuffer> responseBufs = responder.respond(requestBufs);
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//            AiHttpClient.writeBuffers(responseBufs, out);
//
//            exchange.sendResponseHeaders(200, out.size());
//            exchange.getResponseBody().write(out.toByteArray());

        } catch (Exception e) {
            exchange.sendResponseHeaders(400, 0);
            logger.error("Jark error", e);
        }

        exchange.close();
    }

    // ===========================================================================


    // ===========================================================================

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public SSLContext getSsl() {
        return ssl;
    }

    public void setSsl(SSLContext ssl) {
        this.ssl = ssl;
    }

    public static Executor getExecutor() {
        return executor;
    }

    public static void setExecutor(Executor executor) {
        Jark.executor = executor;
    }
}
