package com.kirayim.jark;

import com.sun.net.httpserver.HttpExchange;

public class Response {
    HttpExchange exchange;
    int status = 200;

    public Response(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public HttpExchange getExchange() {
        return exchange;
    }

    public void setExchange(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public int getStatus() {
        return status;
    }

    public void status(int status) {
        this.status = status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
