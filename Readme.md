# Jark
![Build](https://github.com/github/docs/actions/workflows/maven.yml/badge.svg)

Required minimum Java version: 21

A library to make it easy to embed Rest services for Java, without dependencies (i.e. using the old 
and inefficient Sun HTTP server that is provided in the Oracle and OpenJDK runtime libraries).

The motivation is that [Spark java](https://github.com/perwendel/spark) depends on Jetty 9, and after 
the 'java big bang' I need to move everything to Jetty 12. There are many parts of the main project I work on,
that use Spark Java for simple REST interfaces, and it's difficult and wasteful to convert them all to Jetty.

There is a dependency on SLF4J, but that's all.

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

A 'filter' is the same as a 'route', except the function does not return anything. 
Filters are created using `before` and `after`.

If a `before` filter or a route throws an exception, processing will stop at tht point, 
and an HTTP 500 status will be returned. An `HTTPStatusException` is provided to give other codes.

The return values of all the routes are converted to strings and concatenated to provide the 
response body.

Filters are executed using code in the jark library, rather than delegating them to the server 
implementation This allows them to be matched as specified.
