package com;

import java.time.Instant;

public class Message {

    private final String firstName;
    private final String lastName;
    private final String email;
    private final String recipient;
    private final String subject;
    private final String content;
    private final Instant created;

    /**
     * Constructor to create a new Message object with a content
     *
     * @param content
     * @param firstName
     */
    public Message(String firstName, String lastName, String email, String recipient, String subject, String content, Instant created) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
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
    public String getRecipient() {
        return recipient;
    }

    /**
     * @return
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Get this objects content
     *
     * @return String content
     */
    public String getContent() {
        return content;
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
                "content='" + content + '\'' +
                ", created=" + created +
                '}';
    }
}
