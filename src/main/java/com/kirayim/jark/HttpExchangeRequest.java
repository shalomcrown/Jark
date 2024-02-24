package com.kirayim.jark;

import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.util.List;

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

    @Override
    public synchronized String body() throws Exception {

        if (gotBody == false) {
            var in = exchange.getRequestBody();
            rawBody = in.readAllBytes();
            stringBody =  new String(rawBody);
            in.close();
            gotBody = true;
        }

        return stringBody;
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
}
