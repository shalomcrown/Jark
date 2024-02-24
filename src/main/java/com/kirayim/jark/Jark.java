package com.kirayim.jark;

import com.sun.net.httpserver.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.SecureRandom;
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

    HttpContext context;


    String keystoreFile;
    String keystorePassword;
    String certAlias;
    String truststoreFile;
    String truststorePassword;
    boolean needsClientCert;

    // ===========================================================================

    public static Jark ignite() {
        return new Jark();
    }

    public Jark() {
    }

    // ===========================================================================

    public InputStream loadResourceAsStream(String fileName) throws Exception {

        if (fileName.startsWith("file:")) {
            File tryFileName = new File(fileName.substring(5));

            if (tryFileName.exists()) {
                return new FileInputStream(tryFileName);
            }
        } else {
            File tryFileName = new File(fileName);

            if (tryFileName.exists()) {
                return new FileInputStream(tryFileName);
            }
        }

        InputStream resourceStream = ClassLoader.getSystemResourceAsStream(fileName);

        if (resourceStream == null) {
            // Try with both slash types.
            fileName = fileName.replace("/", "\\");
            resourceStream = ClassLoader.getSystemResourceAsStream(fileName);
            if (resourceStream == null) {
                fileName = fileName.replace("\\", "/");
                resourceStream = ClassLoader.getSystemResourceAsStream(fileName);
                if (resourceStream == null) {
                    // It really doesn't exist.
                    throw new FileNotFoundException("No file found at: " + fileName);
                }
            }
        }

        return resourceStream;
    }

    // ===========================================================================

    public void setupSSl() throws Exception {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

        try (InputStream tStore = loadResourceAsStream(truststoreFile)) {
            trustStore.load(tStore, truststorePassword == null ? new char[0]  : keystorePassword.toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);


        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        try (InputStream tStore = loadResourceAsStream(keystoreFile)) {
            keyStore.load(tStore, keystorePassword == null ? new char[0] : keystorePassword.toCharArray());
        }

        kmf.init(keyStore, keystorePassword == null ? new char[0] :  keystorePassword.toCharArray());

        synchronized (this) {
            ssl = SSLContext.getInstance("TLS");
            ssl.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
        }
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
        context = server.createContext(basePath, this);
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

            synchronized (this) {
                this.notifyAll();
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    public void stop() {
        close();
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

    public Jark before(HttpMethod method, String path, String accept, JarkFilter handler) {
        beforeFilters.add(new JarkRoute(method, checkPath(path), accept, handler));
        return this;
    }

    public Jark before(HttpMethod method, String path, JarkFilter handler) {
        return before(method, path, null, handler);
    }

    public Jark before(String path, JarkFilter handler) {
        return before(null, path, null, handler);
    }

    public Jark before(JarkFilter handler) {
        return before(null, null, null, handler);
    }

    public Jark after(HttpMethod method, String path, String accept, JarkFilter handler) {
        afterFilters.add(new JarkRoute(method, checkPath(path), accept, handler));
        return this;
    }

    public Jark after(HttpMethod method, String path, JarkFilter handler) {
        return after(method, path, null, handler);
    }

    public Jark after(String path, JarkFilter handler) {
        return after(null, path, null, handler);
    }

    public Jark after(JarkFilter handler) {
        return after(null, null, null, handler);
    }

    // ===========================================================================

    List<JarkRoute> filterRoutes(HttpExchangeRequest request, List<JarkRoute> routes) {

        return routes.stream()
                .filter(p -> p.path() == null || request.path().startsWith(p.path()))
                .filter(p -> p.httpMethod() == null || p.httpMethod() == request.method)
                .filter(p -> (request.acceptTypes == null || request.acceptTypes.isEmpty() || request.acceptTypes.stream().anyMatch(q -> p != null && p.acceptType().equals(q))))
                .collect(Collectors.toList());
    }

    // ===========================================================================

    void handleException(HttpExchange exchange, Exception e) throws IOException{
        if (exchange.getResponseCode() == -1) {
            String message;
            int code = 500;

            if (e instanceof  HTTPStatusException statusException) {
                code = statusException.statusCode;
                message = statusException.getMessage();

            } else {
                message = "Internal error: " + e.getMessage();
            }

            exchange.sendResponseHeaders(code, message.getBytes().length);
            exchange.getResponseBody().write(message.getBytes());
        }

        exchange.close();
    }

    // ===========================================================================

    boolean executeFilters(HttpExchangeRequest request, Response response, List<JarkRoute> filters) throws IOException {
        for (var route: filterRoutes(request, filters))  {
            try {
                switch (route.target()) {
                    case null -> { }
                    case JarkFilter f -> f.handle(request, response);
                    default -> throw new Exception("No target for filter");
                }
            } catch (Exception e) {
                handleException(request.exchange, e);
                return false;
            }
        }

        return true;
    }

    // ==============================================================================

    public void handle(HttpExchange exchange) throws IOException {

        HttpExchangeRequest request = new HttpExchangeRequest(exchange, basePath);
        Response response = new HttpExchangeResponse(exchange);

        if (executeFilters(request, response, beforeFilters) == false) {
            return;
        }

        List<Object> results = new ArrayList<>();

        List<JarkRoute> filteredRoutes = filterRoutes(request, routes);

        if (filteredRoutes == null | filteredRoutes.isEmpty()) {
            String message = "Path not found";

            exchange.sendResponseHeaders(404, message.getBytes().length);
            exchange.getResponseBody().write(message.getBytes());
            exchange.close();
            return;
        }

        for (var route: filteredRoutes)  {
            try {
                switch (route.target()) {
                    case null -> { }
                    case JarkFilter f -> f.handle(request, response);
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

    /**
     * Suspend thread until server is stopped
     * @throws InterruptedException
     */
    public void join() throws InterruptedException {
        synchronized (this) {
            this.wait();
        }
    }

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

    public void port(int port) {
        this.port = port;
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

    public void ssl(SSLContext ssl) {
        this.ssl = ssl;
    }

    /**
     * Set the connection to be secure, using the specified keystore and
     * truststore. This has to be called before any route mapping is done. You
     * have to supply a keystore file, truststore file is optional (keystore
     * will be reused). By default, client certificates are not checked.
     * This method is only relevant when using embedded Jetty servers. It should
     * not be used if you are using Servlets, where you will need to secure the
     * connection in the servlet container
     *
     * @param keystoreFile       The keystore file location as string
     * @param keystorePassword   the password for the keystore
     * @param truststoreFile     the truststore file location as string, leave null to reuse
     *                           keystore
     * @param truststorePassword the trust store password
     * @return the object with connection set to be secure
     */
    public synchronized Jark secure(String keystoreFile,
                                       String keystorePassword,
                                       String truststoreFile,
                                       String truststorePassword) throws Exception {
        return secure(keystoreFile, keystorePassword, null, truststoreFile, truststorePassword, false);
    }

    /**
     * Set the connection to be secure, using the specified keystore and
     * truststore. This has to be called before any route mapping is done. You
     * have to supply a keystore file, truststore file is optional (keystore
     * will be reused). By default, client certificates are not checked.
     * This method is only relevant when using embedded Jetty servers. It should
     * not be used if you are using Servlets, where you will need to secure the
     * connection in the servlet container
     *
     * @param keystoreFile       The keystore file location as string
     * @param keystorePassword   the password for the keystore
     * @param certAlias          the default certificate Alias
     * @param truststoreFile     the truststore file location as string, leave null to reuse
     *                           keystore
     * @param truststorePassword the trust store password
     * @return the object with connection set to be secure
     */
    public synchronized Jark secure(String keystoreFile,
                                       String keystorePassword,
                                       String certAlias,
                                       String truststoreFile,
                                       String truststorePassword) throws Exception  {
        return secure(keystoreFile, keystorePassword, certAlias, truststoreFile, truststorePassword, false);
    }

    /**
     * Set the connection to be secure, using the specified keystore and
     * truststore. This has to be called before any route mapping is done. You
     * have to supply a keystore file, truststore file is optional (keystore
     * will be reused).
     * This method is only relevant when using embedded Jetty servers. It should
     * not be used if you are using Servlets, where you will need to secure the
     * connection in the servlet container
     *
     * @param keystoreFile       The keystore file location as string
     * @param keystorePassword   the password for the keystore
     * @param truststoreFile     the truststore file location as string, leave null to reuse
     *                           keystore
     * @param needsClientCert    Whether to require client certificate to be supplied in
     *                           request
     * @param truststorePassword the trust store password
     * @return the object with connection set to be secure
     */
    public synchronized Jark secure(String keystoreFile,
                                       String keystorePassword,
                                       String truststoreFile,
                                       String truststorePassword,
                                       boolean needsClientCert) throws Exception {
        return secure(keystoreFile, keystorePassword, null, truststoreFile, truststorePassword, needsClientCert);
    }

    /**
     * Set the connection to be secure, using the specified keystore and
     * truststore. This has to be called before any route mapping is done. You
     * have to supply a keystore file, truststore file is optional (keystore
     * will be reused).
     * This method is only relevant when using embedded Jetty servers. It should
     * not be used if you are using Servlets, where you will need to secure the
     * connection in the servlet container
     *
     * @param keystoreFile       The keystore file location as string
     * @param keystorePassword   the password for the keystore
     * @param certAlias          the default certificate Alias
     * @param truststoreFile     the truststore file location as string, leave null to reuse
     *                           keystore
     * @param needsClientCert    Whether to require client certificate to be supplied in
     *                           request
     * @param truststorePassword the trust store password
     * @return the object with connection set to be secure
     */
    public synchronized Jark secure(String keystoreFile,
                                       String keystorePassword,
                                       String certAlias,
                                       String truststoreFile,
                                       String truststorePassword,
                                       boolean needsClientCert) throws Exception {
        if (server != null) {
            throw new IllegalStateException("Server already running");
        }

        if (keystoreFile == null) {
            throw new IllegalArgumentException(
                    "Must provide a keystore file to run secured");
        }

        this.keystoreFile = keystoreFile;
        this.keystorePassword  = keystorePassword;
        this.certAlias = certAlias;
        this.truststoreFile  = truststoreFile;
        this.truststorePassword  = truststorePassword;
        this.needsClientCert  =  needsClientCert;

        setupSSl();

        return this;
    }

    public static Executor getExecutor() {
        return executor;
    }

    public static void setExecutor(Executor executor) {
        Jark.executor = executor;
    }
}
