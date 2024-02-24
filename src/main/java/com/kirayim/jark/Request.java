package com.kirayim.jark;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface Request {
    String body();

    byte[] bodyAsBytes();

    HttpMethod getMethod();

    void setMethod(HttpMethod method);

    URI getUri();

    URI uri();

    void setUri(URI uri);

    String getPath();

    void setPath(String path);

    String path();

    List<String> getAcceptTypes();

    void setAcceptTypes(List<String> acceptTypes);

    /**
     * Return server's context or exchange object. Deepnds on type of server.
     * For Built-in, this is the HttpExchange
     * <br/>Note that this is different from SparkJava's getRequest
     * @return
     */
    Object getContext();

    int contentLength();

    Map<String, List<String>> headers();
}
