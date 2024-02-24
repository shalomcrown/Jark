package com.kirayim.jark;

import java.net.URI;
import java.util.List;

public interface Request {
    String body() throws Exception;

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

    Object getContext();
}
