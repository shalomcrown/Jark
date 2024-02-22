package com.kirayim.jark;

@FunctionalInterface
public interface Filter {
    /**
     * Invoked when a request is made on this route's corresponding path e.g. '/hello'
     *
     * @param request  The request object providing information about the HTTP request
     * @param response The response object providing functionality for modifying the response
     * @return The content to be set in the response
     * @throws Exception implementation can choose to throw exception
     */
    void handle(Request request, Response response) throws Exception;
}
