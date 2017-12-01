package com.server;

import java.rmi.RemoteException;

public class BulletinBoard implements BulletinBoardIntf{

    @Override
    public int getMessageCount() throws RemoteException {
        return 0;
    }

    @Override
    public String[] getMessages() throws RemoteException {
        return new String[0];
    }

    @Override
    public String getMessage(int index) throws RemoteException {
        return null;
    }

    @Override
    public void putMessage(String msg) throws RemoteException {

    }
}
