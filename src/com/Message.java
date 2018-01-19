package com;

import java.time.Instant;

public class Message {

    private final String firstName;
    private final String lastName;
    private final String email;
    private final String receiver;
    private final String topic;
    private final String message;
    private final Instant created;

    /**
     * Constructor to create a new Message object with a message
     *
     * @param message
     * @param firstName
     */
    public Message(String firstName, String lastName, String email, String receiver, String topic, String message, Instant created) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.receiver = receiver;
        this.topic = topic;
        this.message = message;
        this.created = created;
    }

    /**
     * @return
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * @return
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Get this objects message
     *
     * @return String message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets this objects timestamp on which it was created
     *
     * @return Instant time created
     */
    public Instant getCreated() {
        return created;
    }

    /**
     * Gets all available information about this Message object
     *
     * @return String Message information
     */
    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", created=" + created +
                '}';
    }
}
