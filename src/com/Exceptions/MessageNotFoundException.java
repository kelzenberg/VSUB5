package com.Exceptions;

public class MessageNotFoundException extends Exception {

    private String error;

    /**
     * Declares an invalid message (e.g. message too long)
     *
     * @param error
     */
    public MessageNotFoundException(String error) {
        this.error = error;
    }

    /**
     * Returns the error message
     *
     * @return String Error message
     */
    @Override
    public String toString() {
        return "InvalidMessageException{ " + error + " }";
    }
}
