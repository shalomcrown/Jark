package com.kirayim.jark;

import java.util.List;

public interface Response {
    int getStatus();

    void status(int status);

    int status();

    void setStatus(int status);

    Object getContext();

    Object raw();

    void body(String bodyString);

    void body(byte[] bodyBytes);

    byte[] body();

    void type(String responseType);

    String type();

    void header(String key, String value); // sets header FOO with value bar

    String header(String key);
    public void header(String key, List<String> values);

//response.redirect("/example"); // browser redirect to /example
}
