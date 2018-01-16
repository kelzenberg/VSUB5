package com;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BulletinBoardIntf extends Remote {

    /**
     * Gets the number of Messages on the BulletinBoard
     *
     * @return int number of pinned messages
     * @throws Exception
     */
    int getMessageCount() throws Exception;

    /**
     * Gets all Messages on the BulletinBoard as an Array
     *
     * @return String-Array with one Message object per slot
     * @throws Exception
     */
    String[] getMessages() throws Exception;

    /**
     * Gets a specific Message at the provided index of the Array
     *
     * @param index
     * @return String of the Message object at index
     * @throws Exception
     */
    String getMessage(int index) throws Exception;

    /**
     * Creates a new Message object with input message
     * and puts it onto the BulletinBoard
     *
     * @param msg
     * @throws Exception
     */
    void putMessage(String message, String author) throws Exception;
}
