package com.kirayim.jark;

import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class HttpExchangeRequest implements Request {
    HttpExchange exchange;
    HttpMethod method;
    URI uri;

    String path;

    List<String> acceptTypes;

    String stringBody;

    byte[] rawBody;
    String[] pathItems;

    boolean gotBody = false;
    boolean gotQuery = false;
    boolean gotPath = false;

    JarkRoute route;

    Map<String, String> queryItems = new HashMap<>();

    // ===========================================================================

    public HttpExchangeRequest(HttpExchange exchange, String basePath) {
        this.exchange = exchange;
        this.method = HttpMethod.get(exchange.getRequestMethod());
        this.uri = exchange.getRequestURI();
        this.path = uri.getPath();
        this.acceptTypes = exchange.getRequestHeaders().get("Accept");

        if (path != null && path.startsWith(basePath)) {
            path = path.substring(basePath.length());
        }
    }

    // ===========================================================================

    public synchronized void checkBody() {
        if (gotBody == false) {
            try (var in = exchange.getRequestBody()) {
                rawBody = in.readAllBytes();
                stringBody = new String(rawBody);
            } catch (Exception e) {
                // Ignore - body will be empty
            }
            gotBody = true;
        }
    }

    // ===========================================================================

    public synchronized void checkQuery() {
        if (gotQuery == false) {
            gotQuery = true;

            URI uri = exchange.getRequestURI();
            String query = uri.getQuery();

            if (query.startsWith("?")) {
                query = query.substring(1);
            }

            String[] parts = query.split("&");

            for (String part: parts) {
                if (part.contains("=")) {
                    String[] pair = part.split("=");
                    queryItems.put(pair[0], pair[1]);
                } else {
                    queryItems.put(part, null);
                }
            }
        }
    }

    // ===========================================================================

    public synchronized void checkPath() {
        if (gotPath == false) {
            gotPath = true;

            pathItems = path.split("/");
        }
    }

    // ===========================================================================


    protected JarkRoute getRoute() {
        return route;
    }

    protected void setRoute(JarkRoute route) {
        this.route = route;
    }

    @Override
    public String body() {
        checkBody();
        return stringBody;
    }

    public byte[] getRawBody() {
        checkBody();
        return rawBody;
    }

    public byte[] bodyAsBytes() {
        return getRawBody();
    }

    public int contentLength() {
        checkBody();
        return rawBody == null ? 0 : rawBody.length;
    }

    public Map<String, List<String>> headers() {
        return exchange.getRequestHeaders();
    }

    // ===========================================================================

    public HttpExchange getExchange() {
        return exchange;
    }

    public void setExchange(HttpExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }
    @Override
    public String path() {
        return path;
    }

    @Override
    public List<String> getAcceptTypes() {
        return acceptTypes;
    }

    @Override
    public void setAcceptTypes(List<String> acceptTypes) {
        this.acceptTypes = acceptTypes;
    }

    @Override
    public Object getContext() {
        return exchange;
    }

    @Override
    public Map<String, Object> attributes() {
        return exchange.getHttpContext().getAttributes();
    }

    @Override
    public Object attribute(String key) {
        return exchange.getHttpContext().getAttributes().get(key);
    }

    @Override
    public void attribute(String key, Object value) {
        exchange.getHttpContext().getAttributes().put(key, value);
    }

    @Override
    public List<String> headers(String key) {
        return exchange.getRequestHeaders().get(key);
    }

    @Override
    public String host() {
        return exchange.getRemoteAddress().getHostName();
    }

    @Override
    public String ip() {
        return exchange.getRemoteAddress().getAddress().toString();
    }

    @Override
    public int port() {
        return exchange.getRemoteAddress().getPort();
    }

    @Override
    public Object raw() {
        return exchange;
    }

    @Override
    public String protocol() {
        return exchange.getProtocol();
    }

    @Override
    public String scheme() {
        return null;
    }

    public String requestMethod() {
        return exchange.getRequestMethod();
    }

    public String contentType() {
        return exchange.getRequestHeaders().getFirst("Content-Type");
    }

    public Map<String, String> queryMap() {
        checkQuery();
        return queryItems;
    }

    public Map<String, String> queryMap(String key) {
        checkQuery();
        if (queryMap().containsKey(key)) {
            return Map.of(key, queryItems.get(key));
        }

        return null;
    }

    public String queryParamsValues(String key) {
        checkQuery();
        return queryMap().get(key);
    }

    /** value of foo path parameter
     *
     * @param param
     * @return
     */
    public String params(String param) {
        checkPath();

        if (route.pathParameters.containsKey(param)) {
            int index = route.pathParameters.get(param);

            if (pathItems.length > index) {
                return URLDecoder.decode(pathItems[index], StandardCharsets.UTF_8);
            }
        }

        return null;
    }

    // ===========================================================================


//contextPath();            // the context path, e.g. "/hello"
//cookies();                // request cookies sent by the client
//params();                 // map with all parameters
//pathInfo();               // the path info
//protocol();               // the protocol, e.g. HTTP/1.1
//queryMap();               // the query map
//queryMap("foo");          // query map for a certain parameter
//queryParams();            // the query param list
//queryParams("FOO");       // value of FOO query param
//requestMethod();          // The HTTP method (GET, ..etc)
//scheme();                 // "http"
//servletPath();            // the servlet path, e.g. /result.jsp
//session();                // session management
//splat();                  // splat (*) parameters
//uri();                    // the uri, e.g. "http://example.com/foo"
//url();                    // the url. e.g. "http://example.com/foo"
//userAgent();              // user agent
}
