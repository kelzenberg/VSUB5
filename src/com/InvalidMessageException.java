package com;

import java.rmi.RemoteException;

public class InvalidMessageException extends RemoteException {

    private String error;

    /**
     *
     * @param error
     */
    public InvalidMessageException(String error) {
        this.error = error;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "InvalidMessageException{ " + error + " }";
    }
}
