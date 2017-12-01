package com;

import java.time.Instant;

public class Message {

    private final String message;
    private final Instant created;

    /**
     *
     * @param msg
     */
    public Message(String message) {
        this.message = message;
        this.created = Instant.now();
    }

    /**
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @return
     */
    public Instant getCreated() {
        return created;
    }

    /**
     * 
     * @return
     */
    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", created=" + created +
                '}';
    }
}
