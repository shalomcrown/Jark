# Jark
![Build](https://github.com/shalomcrown/Jark/actions/workflows/maven.yml/badge.svg)

Required minimum Java version: **21**

A library to make it easy to embed Rest services for Java, without dependencies (i.e. using the old 
and inefficient Sun HTTP server that is provided in the Oracle and OpenJDK runtime libraries).

The motivation is that [Spark java](https://github.com/perwendel/spark) depends on Jetty 9, and after 
the 'java big bang' I need to move everything to Jetty 12. There are many parts of the main project I work on,
that use Spark Java for simple REST interfaces, and it's difficult and wasteful to convert them all to Jetty.

Since it is intended to replace Spark Java, it borrows as much of the concepts and interfaces 
as possible, so moving to it will be easy.

# How to use it
```java
        Jark jark = Jark.ignite();
        jark.port(8080);
        jark.before("/test", (p, q) -> LOG("I was in the filter"))
        jark.get("/test", (p, q) -> "test");
        jark.start();
```

A 'route' is a tuple of URL path, HTTP method and accept MIME type. If any item is missing, 
it is assumed to match everything. Each route also has a function accepting `Request` and `Response`
objects, returning some response. The function can be a lambda.

Routes are defined with the `get`, `put`, `post` functions etc.

Matching routes are called in the order they were defined. 
More than one route can match a given request.

A 'filter' is the same as a 'route', except the function does not return anything. 
Filters are created using `before` and `after`.

If a `before` filter or a route throws an exception, processing will stop at tht point, 
and an HTTP 500 status will be returned. An `HTTPStatusException` is provided to give other codes.

The return values of all the routes are converted to strings and concatenated to provide the 
response body.

If you have used `Response.body(..)` in one of the routes, the binary data from that will be concatenated to
the final body after all the return values from the routes.

Filters are executed using code in the jark library, rather than delegating them to the server 
implementation This allows them to be matched as specified.

## Implementation status
* Basic operations work
* Filters aren't tested
* SSL works (use `secure()` function)
* Serving static content using 'location()' or 'externalLocation()' works partially. Note that there
is no difference between these functions, Jark tries to serve files from both the JARs and the file 
system, whichever it finds.
* Much of the 'Request' and 'Response' are implemented

## Plans / missing
* Response headers
* Finish the static content service
* Built-in Authentication system
* Sessions
* `halt()`
* `redirect()`

## Later
* The code should detect provided Jetty or Grizzly libraries and use these instead of the 
built-in Http server if they exist.
