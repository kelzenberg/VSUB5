package com;

import java.time.Instant;

public class Message {

    private final String msg;
    private final Instant created;

    public Message(String msg) {
        this.msg = msg;
        this.created = Instant.now();
    }
}
