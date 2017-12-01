package com.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BulletinBoardIntf extends Remote {

    public int getMessageCount() throws RemoteException;

    public String[] getMessages() throws RemoteException;

    public String getMessage(int index) throws RemoteException;

    public void putMessage(String msg) throws RemoteException;
}
