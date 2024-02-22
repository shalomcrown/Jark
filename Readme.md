# Jark

A library to make it easy to embed Rest services for Java, without dependencies (i.e. using the old 
and inefficient Sun HTTP server)

The motivation is that [Spark java](https://github.com/perwendel/spark) depends on Jetty 9, and after 
the 'java big bang' I need to move everything to Jetty 12. There are many parts of the system that use
Spark Java for simple REST interfaces, and it's difficult and wasteful to convert them all to Jetty.

There is a dependency on SLF4J, but that's all.
