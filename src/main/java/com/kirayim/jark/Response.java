package com.kirayim.jark;

public interface Response {
    int getStatus();

    void status(int status);

    void setStatus(int status);

    Object getContext();
}
