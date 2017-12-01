package com;

import java.time.Instant;

public class Message {

    private final String msg;
    private final Instant created;

    /**
     *
     * @param msg
     */
    public Message(String msg) {
        this.msg = msg;
        this.created = Instant.now();
    }

    /**
     *
     * @return
     */
    public String getMsg() {
        return msg;
    }

    /**
     *
     * @return
     */
    public Instant getCreated() {
        return created;
    }
}
