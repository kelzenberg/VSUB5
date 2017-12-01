package com.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements BulletinBoardIntf {

    private final int maxNumMessages;
    private final int messageLifetime;
    private final int maxLengthMessage;
    private final String nameOfService;

    private Server() {
        super();
        maxNumMessages = 20;
        messageLifetime = 600;
        maxLengthMessage = 160;
        nameOfService = "BulletinBoard";
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Server engine = new Server();
            BulletinBoardIntf stub = (BulletinBoardIntf) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(nameOfService, stub);
            System.out.println("BulletinBoard bound");
        } catch (Exception e) {
            System.err.println("BulletinBoard exception:");
            e.printStackTrace();
        }
    }

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
