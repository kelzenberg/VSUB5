package com;

import java.rmi.RemoteException;

public class InvalidMessageException extends RemoteException {

    private String error;

    public InvalidMessageException(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "InvalidMessageException{ " + error + " }";
    }
}
