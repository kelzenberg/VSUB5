package com;

import java.rmi.RemoteException;

public class BulletinBoardFullException extends RemoteException {

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "BulletinBoardFullException{ BulletinBoard is full - please don't Spam }";
    }
}
