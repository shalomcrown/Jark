package com.kirayim.jark;

import com.sun.net.httpserver.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.InetSocketAddress;
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
    static Executor executor = null;

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

    public Jark staticFiles = this;

    public boolean virtualThreads = true;

    int poolSize = 1;

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

            if (tryFileName.exists() == false && fileName.startsWith("/")) {
                tryFileName = new File(fileName.substring(1));
            }

            if (tryFileName.exists()) {
                return new FileInputStream(tryFileName);
            }
        }

        InputStream resourceStream = ClassLoader.getSystemResourceAsStream(fileName);

        if (resourceStream == null) {
            if (fileName.startsWith("/")) {
                resourceStream = ClassLoader.getSystemResourceAsStream(fileName.substring(1));
            }

            if (resourceStream == null) {
                // Try with both slash types.
                fileName = fileName.replace("/", "\\");
                resourceStream = ClassLoader.getSystemResourceAsStream(fileName);
                if (resourceStream == null) {
                    fileName = fileName.replace("\\", "/");
                    resourceStream = ClassLoader.getSystemResourceAsStream(fileName);
                }
            }
        }

        return resourceStream;
    }

    // ===========================================================================

    public void handleStaticContent(JarkStaticContent target, HttpExchange exchange, Request request, Response response) throws Exception {
        if (target.getTarget() instanceof String stringTarget) {

            String path = request.getPath();

            if (path.startsWith(target.getPath())) {
                path = path.substring(target.getPath().length());

            } else if (target.getPath().startsWith("/")) {
                String targetNoSlash = target.getPath().substring(1);
                if (path.startsWith(targetNoSlash)) {
                    path = path.substring((targetNoSlash.length()));
                }
            }

            if (stringTarget.endsWith("/")) {
                if (path.startsWith("/")) {
                    stringTarget = stringTarget + path.substring(1);
                } else {
                    stringTarget = stringTarget + path;
                }
            } else {
                if (path.startsWith("/")) {
                    stringTarget = stringTarget + path;
                } else {
                    stringTarget = stringTarget + "/" + path;
                }
            }

            try (var in = loadResourceAsStream(stringTarget)) {
                if (in != null) {
                    byte[] inputData = in.readAllBytes(); // Sorry - need to read it all in so we know how long it is
                    exchange.sendResponseHeaders(200, inputData.length);
                    var out = exchange.getResponseBody();
                    out.write(inputData);
                    out.close();
                    exchange.close();
                }
            }
        }
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

    public Jark start() throws Exception {
        if (ssl != null) {
            server = HttpsServer.create(new InetSocketAddress(ipAddress, port), -1);
            ((HttpsServer)server).setHttpsConfigurator(new HttpsConfigurator(ssl));
        } else {
            server = HttpServer.create(new InetSocketAddress(ipAddress, port), -1);
        }

        if (basePath.endsWith("/") == false) {
            basePath = basePath + "/";
        }

        executor = virtualThreads ? Executors.newVirtualThreadPerTaskExecutor() : Executors.newCachedThreadPool();

        server.setExecutor(executor);
        context = server.createContext(basePath, this);
        server.start();
        return this;
    }

    // ===========================================================================

    public Jark init() throws Exception {
        return start();
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
        String pathWithleadingSlash = "/" + request.path();
        String pathWithTrainingSlash = request.path() + "/";

        return routes.stream()
                .filter(p -> p.getPath() == null
                        || request.path().startsWith(p.getPath())
                        || pathWithleadingSlash.startsWith(p.getPath())
                        || (p.hasPathParameters &&  p.pathPattern.matcher(request.path).find())
                )
                .filter(p -> p.getHttpMethod() == null || p.getHttpMethod() == request.method)
                .filter(p -> (request.acceptTypes == null
                        || request.acceptTypes.isEmpty()
                        || request.acceptTypes.stream().anyMatch(q -> p != null
                            && (p.getAcceptType() == null  || p.getAcceptType().equals(q)))))
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
                switch (route.getTarget()) {
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
        try {
            HttpExchangeRequest request = new HttpExchangeRequest(exchange, basePath);
            Response response = new HttpExchangeResponse(exchange);

            if (executeFilters(request, response, beforeFilters) == false) {
                return;
            }

            List<Object> results = new ArrayList<>();
            List<byte[]> binaryResults = new ArrayList<>();

            List<JarkRoute> filteredRoutes = filterRoutes(request, routes);

            if (filteredRoutes == null | filteredRoutes.isEmpty()) {
                String message = "Path not found";

                exchange.sendResponseHeaders(404, message.getBytes().length);
                exchange.getResponseBody().write(message.getBytes());
                exchange.close();
                return;
            }

            for (var route : filteredRoutes) {
                request.setRoute(route);

                try {
                    switch (route) {
                        case JarkStaticContent f -> handleStaticContent(f, exchange, request, response);
                        case JarkRoute j -> {
                            switch (j.getTarget()) {
                                case null -> {
                                }
                                case JarkFilter f -> f.handle(request, response);
                                case Route f -> {
                                    results.add(f.handle(request, response));
                                    if (response.body() != null) {
                                        binaryResults.add(response.body());
                                    }
                                }
                                default -> throw new Exception("No target for route");
                            }
                        }
                    }

                } catch (Exception e) {
                    handleException(request.exchange, e);
                    return;
                }
            }
            ;

            if (executeFilters(request, response, afterFilters) == false) {
                return;
            }

            if (exchange.getResponseCode() == -1) {
                int byteCount = 0;
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                String stringData;

                if (results.isEmpty() == false) {
                    for (Object result : results) {
                        switch (result) {
                            case null -> {
                            }
                            case String s -> bytes.write(s.getBytes());
                            default -> bytes.write(result.toString().getBytes());
                        }
                    }
                }

                if (binaryResults.isEmpty() == false) {
                    for (byte[] result : binaryResults) {
                        if (result != null) {
                            bytes.write(result);
                        }
                    }
                }

                byteCount += bytes.size();

                exchange.sendResponseHeaders(response.getStatus(), byteCount);
                exchange.getResponseBody().write(bytes.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public Jark port(int port) {
        this.port = port;
        return this;
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

    public Jark ipAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
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

    public Jark ssl(SSLContext ssl) {
        this.ssl = ssl;
        return this;
    }

    /**
     * Set a base path inside yor JAR for serving static content.
     * <br/>This serves the static content using a base URL
     * <br/>In the words of SJ: A file /public/css/style.css is made available as http://{host}:{port}/css/style.css
     * @param location
     */
    public Jark location(String location) {
        routes.add(new JarkStaticContent(HttpMethod.GET, "/", null, location));
        return this;
    }

    public Jark location(String location, String urlPath) {
        routes.add(new JarkStaticContent(HttpMethod.GET, urlPath, null, location));
        return this;
    }

    public Jark externalLocation(String location) {
        routes.add(new JarkStaticContent(HttpMethod.GET, "/", null, location));
        return this;
    }

    public Jark externalLocation(String location, String urlPath) {
        routes.add(new JarkStaticContent(HttpMethod.GET, urlPath, null, location));
        return this;
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

    /**
     * Set thread pool size. This is ignored by Jark when it uses
     * a virtual thread executor.
     * @param poolSize
     */
    public Jark threadPool(int poolSize) {
        this.poolSize = poolSize;
        return this;
    }

    /**
     * Set thread pool size. This is ignored by Jark when it uses
     * a virtual thread executor.
     * @param poolSize
     */
    public Jark poolSize(int poolSize) {
        this.poolSize = poolSize;
        return this;
    }

    public int getPoolSize() {
        return poolSize;
    }

    /**
     * Set thread pool size. This is ignored by Jark when it uses
     * a virtual thread executor.
     * @param poolSize
     */
    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public boolean isVirtualThreads() {
        return virtualThreads;
    }

    public void setVirtualThreads(boolean virtualThreads) {
        this.virtualThreads = virtualThreads;
    }

    public Jark virtualThreads(boolean virtualThreads) {
        this.virtualThreads = virtualThreads;
        return this;
    }
}
