package com.kirayim.jark;

import com.sun.net.httpserver.HttpExchange;

public class HttpExchangeResponse implements Response {
    HttpExchange exchange;
    int status = 200;

    public HttpExchangeResponse(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public HttpExchange getExchange() {
        return exchange;
    }

    public void setExchange(HttpExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void status(int status) {
        this.status = status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public Object getContext() {
        return exchange;
    }
}
