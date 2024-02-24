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

    Map<String, Object> attributes();             // the attributes list
    Object attribute(String key);         // value of foo attribute
    void attribute(String key, Object value);      // sets value of attribute A to V

    List<String> headers(String key);           // value of BAR header
    String host();                   // the host, e.g. "example.com"
    String ip();                     // client IP address

    int port();                   // the server port

    Object raw();                    // raw request handed in by whatever

    String protocol();               // the protocol, e.g. HTTP/1.1
    String scheme();                 // "http"

    String requestMethod(); // The HTTP method (GET, ..etc)

    String contentType();            // content type of request.body
//contextPath();            // the context path, e.g. "/hello"
//cookies();                // request cookies sent by the client
//params("foo");            // value of foo path parameter
//params();                 // map with all parameters
//pathInfo();               // the path info
//queryMap();               // the query map
//queryMap("foo");          // query map for a certain parameter
//queryParams();            // the query param list
//queryParams("FOO");       // value of FOO query param
//queryParamsValues("FOO")  // all values of FOO query param
//servletPath();            // the servlet path, e.g. /result.jsp
//session();                // session management
//splat();                  // splat (*) parameters
//uri();                    // the uri, e.g. "http://example.com/foo"
//url();                    // the url. e.g. "http://example.com/foo"
//userAgent();              // user agent
}
