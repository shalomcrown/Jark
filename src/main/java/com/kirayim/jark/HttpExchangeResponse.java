package com.kirayim.jark;

import com.sun.net.httpserver.HttpExchange;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class HttpExchangeResponse implements Response {
    HttpExchange exchange;
    int status = 200;

    byte[] body = null;

    String bodyString = null;

    public HttpExchangeResponse(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public HttpExchange getExchange() {
        return exchange;
    }

    @Override
    public Object raw() {
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
    public int status() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public Object getContext() {
        return exchange;
    }

    @Override
    public void body(String bodyString) {
        body = bodyString.getBytes();
    }

    @Override
    public void body(byte[] bodyBytes) {
        body = bodyBytes;
    }

    @Override
    public byte[] body() {
        return body;
    }

    @Override
    public void type(String responseType) {
        exchange.getResponseHeaders().set("Content-type", responseType);
    }

    @Override
    public String type() {
        return exchange.getResponseHeaders().getFirst("Content-type");
    }

    @Override
    public void header(String key, String value) {
        exchange.getResponseHeaders().put(key, List.of(value));
    }

    @Override
    public void header(String key, List<String> values) {
        exchange.getResponseHeaders().put(key, values);
    }


    @Override
    public String header(String key) {
        return exchange.getResponseHeaders().getFirst(key);
    }
}
