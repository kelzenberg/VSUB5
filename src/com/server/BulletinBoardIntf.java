package com.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BulletinBoardIntf extends Remote {

    int getMessageCount() throws RemoteException;

    String[] getMessages() throws RemoteException;

    String getMessage(int index) throws RemoteException;

    void putMessage(String msg) throws RemoteException;
}
