package com.kirayim.jark;

public interface Response {
    int getStatus();

    void status(int status);

    void setStatus(int status);

    Object getContext();

    void body(String bodyString);

    void body(byte[] bodyBytes);

    byte[] body();
}
