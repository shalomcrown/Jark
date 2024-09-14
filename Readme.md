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

Now adding a system for generating and using web pages to edit Java beans/pojos.

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

Since there are no dependencies, it is impossible to include a library like Jackson to convert objects 
to JSON before returning them (or accepting them)

**Updated - Bean editing**: The Apache commons bean utils and commons text are added as
dependencies, which shouldn't bother anyone. But still no Jackson...

Also URL path parameters aren't implemented, though that should be easy and nice, provided they can be
given as strings.

# Bean editor
In its simplest form, you can create a bean editor with default web page on port 8085
by simply

```java
  var editor = new BeanEditor(myObject);
```

This will create and set up a Jark instance, and serve a default web page with
the edit form embedded.

When the user submits the form, the fields of the bean are automatically updated with
the new values.

However, constructors are available with which you can supply a function to be called when
the bean is updated, and your own web page and Jark instance, with a tag to be replaced by the bean 
editing HTML, this would usually be in a form, because then the browser collects all
the data easily.

It works by generating the content of a form, giving each item an ID which is a dotted
path through the membership tree.


On form submission, the values are updated in the bean.

Arrays, collections.
For classes such as <i>javax.quantity.Quantity</i> that require custom
serializers, you can write your own by implementing `IBeanConverter` and 
registering with the BeanEditor using `addConverter`.

The serializer is passed information on the field and the tag. It can output
whatever HTML is needed to the string builder

The deserializer is passed the information, and a map of all the data
returned in the HTML form submission, so the fucntion can use any other 
tags it requires.

```aiignore

public class QuantityJarkSerializer implements IBeanConverter {

    /**
     * The given tag is used on the value and a tag with ".unit" appended
     * is used for the unit.
     * @param info
     * @param html
     * @param value
     */
    @Override
    public void serialize(BeanFormItemInfo info, StringBuilder html, Object value) {
        Quantity<?> quantity = (Quantity<?>) value;

        if (value == null) {
            return;
        }

        html.append("<table><tr><th>Value</th><th>Unit</th></tr>\n");
        html.append("<tr><td>");

        html.append("<input type=\"text\"");
        Number qValue = quantity.getValue();

        String stringValue = Objects.toString(qValue);

        if (StringUtils.isNotBlank(stringValue)) {
            html.append(" value=\"");
            html.append(StringEscapeUtils.escapeHtml4(stringValue));
            html.append("\"");
        }

        html.append(" name=\"").append(info.getTag()).append("\"");
        html.append(" id=\"").append(info.getPdesc().getName()).append("\"");
        html.append("/></td><td>\n");

        Unit<?> unit = quantity.getUnit();

        html.append("<input type=\"text\"");
        String unitValue = unit.toString();

        if (StringUtils.isNotBlank(stringValue)) {
            html.append(" value=\"");
            html.append(StringEscapeUtils.escapeHtml4(unitValue));
            html.append("\"");
        }

        html.append(" name=\"").append(info.getTag() + ".unit").append("\"");
        html.append(" id=\"").append(info.getPdesc().getName()).append("\"");
        html.append("/></td></tr>\n");
        html.append("</table>\n");
    }

    // =================================================================================

    @Override
    public void deserialize(BeanFormItemInfo info, Map<String, String> bodyMap) {
        try {
            String value = bodyMap.get(info.getTag());
            String unitValue = bodyMap.get(info.getTag() + ".unit");

            if (value == null || unitValue == null) {
                return;
            }

            Unit<?> newUnit = SimpleUnitFormat.getInstance().parse(unitValue);
            Number numberValue = NumberFormat.getInstance().parse(value);
            Quantity<?> newQantity =  Quantities.getQuantity(numberValue, newUnit);

            BeanUtils.setProperty(info.getBean(), info.getPdesc().getName(), newQantity);
        } catch (Exception e) {
            .... something ...
        }
    }
```



# Implementation status
* Basic operations work
* Filters aren't tested very well
* SSL works (use `secure()` function), though not tested with keystore as resource.
* Serving static content using 'location()' or 'externalLocation()' works fairly well. Note that there
is no difference between these functions, Jark tries to serve files from both the JARs and the file 
system, whichever it finds. In general the search order is
  - File system path as concatenation of target and remaing part of URL after removing path component
  - Same without leading '/' - in other words starting a current working folder instead of root
  - Same as resource
  - Same as resource without leading "/"
  - Same with forward slashed replace by backslashes  
* Much of the 'Request' and 'Response' are implemented

## Plans / missing
* Built-in Authentication system
* Sessions
* `halt()`
* `redirect()`
* GZip body support

## Later
* The code should detect provided Jetty or Grizzly libraries and use these instead of the 
built-in Http server if they exist.
