package com.github.desperateyuri.client;

import java.util.Map;

public record serverMessage(Command command, Status status, Map<String, Object> map) {
    // In this map, it contains the ID of new register.
    public enum Command{
        LOGIN, REGISTER
    }
    public enum Status{
        OK,  ERROR
    }
}