package com.Exceptions;

import java.rmi.RemoteException;

public class BulletinBoardFullException extends Exception {

    /**
     * Declares a full BulletinBoard
     *
     * @return
     */
    @Override
    public String toString() {
        return "BulletinBoardFullException{ BulletinBoard is full - please don't Spam }";
    }
}
