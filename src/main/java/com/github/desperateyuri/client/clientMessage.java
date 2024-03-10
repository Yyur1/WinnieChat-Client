package com.github.desperateyuri.client;

import java.util.Map;

public record clientMessage(Command command, Map<String, Object> map) {
    public enum Command{
        LOGIN, REGISTER
    }
}

