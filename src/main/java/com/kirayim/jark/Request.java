package com.kirayim.jark;

import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.util.List;

public class Request {
    HttpExchange exchange;
    HttpMethod method;
    URI uri;

    String path;

    List<String> acceptTypes;
    public Request(HttpExchange exchange, String basePath) {
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

    public HttpExchange getExchange() {
        return exchange;
    }

    public void setExchange(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getAcceptTypes() {
        return acceptTypes;
    }

    public void setAcceptTypes(List<String> acceptTypes) {
        this.acceptTypes = acceptTypes;
    }
}
