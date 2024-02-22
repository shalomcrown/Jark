package com.kirayim.jark;

public record JarkRoute (
    HttpMethod httpMethod,
    String path,
    String acceptType,
    Object target) {

}
