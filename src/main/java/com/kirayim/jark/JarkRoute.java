package com.kirayim.jark;

import java.util.Objects;

public class JarkRoute {
    HttpMethod httpMethod;
    String path;
    String acceptType;
    Object target;

    public JarkRoute(HttpMethod httpMethod, String path, String acceptType, Object target) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.acceptType = acceptType;
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JarkRoute jarkRoute = (JarkRoute) o;
        return httpMethod == jarkRoute.httpMethod && Objects.equals(path, jarkRoute.path) && Objects.equals(acceptType, jarkRoute.acceptType) && Objects.equals(target, jarkRoute.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, path, acceptType, target);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAcceptType() {
        return acceptType;
    }

    public void setAcceptType(String acceptType) {
        this.acceptType = acceptType;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
