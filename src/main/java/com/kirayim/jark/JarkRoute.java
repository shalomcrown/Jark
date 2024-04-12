package com.kirayim.jark;

import java.util.*;
import java.util.regex.Pattern;

public class JarkRoute {
    HttpMethod httpMethod;
    String path;
    String acceptType;
    Object target;

    Pattern pathPattern;
    Map<String, Integer> pathParameters = new HashMap<>();

    boolean hasPathParameters = false;

    public JarkRoute(HttpMethod httpMethod, String path, String acceptType, Object target) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.acceptType = acceptType;
        this.target = target;


        ListIterator<String> it = Arrays.asList(path.split("/")).listIterator();
        StringBuffer pathBuffer = new StringBuffer();

        while (it.hasNext()) {
            int index = it.nextIndex();
            String pathItem = it.next();

            if (pathItem != null && pathItem.length() > 0 && pathItem.startsWith(":")) {
                pathParameters.put(pathItem.substring(1), 1);
                pathBuffer.append("[^/]*");
                hasPathParameters = true;
            } else {
                pathBuffer.append(pathItem);
            }

            if (it.hasNext()) {
                pathBuffer.append("/");
            }
        }

        if (hasPathParameters) {
            pathPattern = Pattern.compile(pathBuffer.toString(), Pattern.CASE_INSENSITIVE);
        }

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
