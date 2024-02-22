package com.kirayim.jark;

import com.sun.net.httpserver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

    List<JarkRoute> beforeFilters = new ArrayList<>();

    List<JarkRoute> afterFilters = new ArrayList<>();

    // ===========================================================================

    public static Jark ignite() {
        return new Jark();
    }

    public Jark() {
    }

    // ===========================================================================

    public void start() throws Exception {
        if (ssl != null) {
            server = HttpsServer.create(new InetSocketAddress(ipAddress, port), -1);
            ((HttpsServer)server).setHttpsConfigurator(new HttpsConfigurator(ssl));
        } else {
            server = HttpServer.create(new InetSocketAddress(ipAddress, port), -1);
        }

        if (basePath.endsWith("/") == false) {
            basePath = basePath + "/";
        }

        server.setExecutor(executor);
        server.createContext(basePath, this);
        server.start();
    }

    // ===========================================================================

    public void init() throws Exception {
        start();
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

    String checkPath(String path) {
        if (path == null) {
            return null;
        }

        if (path.startsWith("/")) {
            return path.substring(1);
        }

        return path;
    }

    // ===========================================================================

    public Jark get(String path, String accept, Route handler) {
        routes.add(new JarkRoute(HttpMethod.GET, checkPath(path), accept, handler));
        return this;
    }

    public Jark post(String path, String accept, Route handler) {
        routes.add(new JarkRoute(HttpMethod.POST, checkPath(path), accept, handler));
        return this;
    }

    public Jark put(String path, String accept, Route handler) {
        routes.add(new JarkRoute(HttpMethod.PUT, checkPath(path), accept, handler));
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

    public Jark before(HttpMethod method, String path, String accept, Filter handler) {
        beforeFilters.add(new JarkRoute(method, checkPath(path), accept, handler));
        return this;
    }

    public Jark before(HttpMethod method, String path, Filter handler) {
        return before(method, path, null, handler);
    }

    public Jark before(String path, Filter handler) {
        return before(null, path, null, handler);
    }

    public Jark before(Filter handler) {
        return before(null, null, null, handler);
    }

    public Jark after(HttpMethod method, String path, String accept, Filter handler) {
        afterFilters.add(new JarkRoute(method, checkPath(path), accept, handler));
        return this;
    }

    public Jark after(HttpMethod method, String path, Filter handler) {
        return after(method, path, null, handler);
    }

    public Jark after(String path, Filter handler) {
        return after(null, path, null, handler);
    }

    public Jark after(Filter handler) {
        return after(null, null, null, handler);
    }

    // ===========================================================================

    List<JarkRoute> filterRoutes(Request request, List<JarkRoute> routes) {

        var stream = routes.stream()
                .filter(p -> p.path() == null || p.path().startsWith(request.path))
                .filter(p -> p.httpMethod() == null || p.httpMethod() == request.method);

        if (request.acceptTypes != null && request.acceptTypes.isEmpty() == false) {
            stream.filter(p -> request.acceptTypes.stream().anyMatch(q -> p.acceptType().equals(q)));
        }

        return stream.collect(Collectors.toList());
    }

    // ===========================================================================

    void handleException(HttpExchange exchange, Exception e) throws IOException{
        if (exchange.getResponseCode() == -1) {
            String message = "Internal error: " + e.getMessage();

            exchange.getResponseBody().write(message.getBytes());
            exchange.sendResponseHeaders(500, message.getBytes().length);
        }

        exchange.close();
    }

    // ===========================================================================

    boolean executeFilters(Request request, Response response, List<JarkRoute> filters) throws IOException {
        for (var route: filterRoutes(request, filters))  {
            try {
                switch (route.target()) {
                    case null -> { }
                    case Filter f -> f.handle(request, response);
                    default -> throw new Exception("No target for filter");
                }
            } catch (Exception e) {
                handleException(request.exchange, e);
                return false;
            }
        };

        return true;
    }

    // ==============================================================================

    public void handle(HttpExchange exchange) throws IOException {

        Request request = new Request(exchange, basePath);
        Response response = new Response(exchange);

        if (executeFilters(request, response, beforeFilters) == false) {
            return;
        }

        List<Object> results = new ArrayList<>();

        for (var route: filterRoutes(request, routes))  {
            try {
                switch (route.target()) {
                    case null -> { }
                    case Filter f -> f.handle(request, response);
                    case Route f -> results.add(f.handle(request, response));
                    default -> throw new Exception("No target for route");
                }
            } catch (Exception e) {
                handleException(request.exchange, e);
                return;
            }
        };

        if (executeFilters(request, response, afterFilters) == false) {
            return;
        }

        if (exchange.getResponseCode() == -1) {
            int byteCount = 0;

            String string = results.stream()
                    .map(p -> p.toString())
                    .reduce("", (p, q) -> p + q);

            byte[] bytes =  string.getBytes();
            byteCount += bytes.length;

            exchange.sendResponseHeaders(response.getStatus(), byteCount);
            exchange.getResponseBody().write(bytes);
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
