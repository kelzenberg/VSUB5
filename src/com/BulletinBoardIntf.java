package com;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BulletinBoardIntf extends Remote {

    /**
     *
     * @return
     * @throws RemoteException
     */
    int getMessageCount() throws RemoteException;

    /**
     *
     * @return
     * @throws RemoteException
     */
    String[] getMessages() throws RemoteException;

    /**
     *
     * @param index
     * @return
     * @throws RemoteException
     */
    String getMessage(int index) throws RemoteException;

    /**
     *
     * @param msg
     * @throws RemoteException
     */
    void putMessage(String msg) throws RemoteException;
}
