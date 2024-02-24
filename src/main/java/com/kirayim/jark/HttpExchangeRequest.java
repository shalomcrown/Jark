package com.kirayim.jark;

import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class HttpExchangeRequest implements Request {
    HttpExchange exchange;
    HttpMethod method;
    URI uri;

    String path;

    List<String> acceptTypes;

    String stringBody;

    byte[] rawBody;

    boolean gotBody = false;

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


//attributes();             // the attributes list
//attribute("foo");         // value of foo attribute
//attribute("A", "V");      // sets value of attribute A to V
//contentLength();          // length of request body
//contentType();            // content type of request.body
//contextPath();            // the context path, e.g. "/hello"
//cookies();                // request cookies sent by the client
//headers();                // the HTTP header list
//headers("BAR");           // value of BAR header
//host();                   // the host, e.g. "example.com"
//ip();                     // client IP address
//params("foo");            // value of foo path parameter
//params();                 // map with all parameters
//pathInfo();               // the path info
//port();                   // the server port
//protocol();               // the protocol, e.g. HTTP/1.1
//queryMap();               // the query map
//queryMap("foo");          // query map for a certain parameter
//queryParams();            // the query param list
//queryParams("FOO");       // value of FOO query param
//queryParamsValues("FOO")  // all values of FOO query param
//raw();                    // raw request handed in by Jetty
//requestMethod();          // The HTTP method (GET, ..etc)
//scheme();                 // "http"
//servletPath();            // the servlet path, e.g. /result.jsp
//session();                // session management
//splat();                  // splat (*) parameters
//uri();                    // the uri, e.g. "http://example.com/foo"
//url();                    // the url. e.g. "http://example.com/foo"
//userAgent();              // user agent
}
