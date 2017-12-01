package com.server;

import com.BulletinBoardFullException;
import com.BulletinBoardIntf;
import com.InvalidMessageException;
import com.Message;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server implements BulletinBoardIntf {

    private static int maxNumMessages;
    private static int messageLifetime;
    private static int maxLengthMessage;
    private static String nameOfService;
    private static Message[] messages;
    private int newestMessagePointer;

    /**
     *
     */
    private Server() {
        super();
        maxNumMessages = 20;
        messageLifetime = 600;
        maxLengthMessage = 160;
        nameOfService = "BulletinBoard";
        newestMessagePointer = 0;
        messages = new Message[maxNumMessages];
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Server engine = new Server();
            BulletinBoardIntf bb = (BulletinBoardIntf) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(nameOfService, bb);
            System.out.println("BulletinBoard bound");
        } catch (Exception e) {
            System.err.println("BulletinBoard exception:");
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public int getMessageCount() throws RemoteException {
        int count = 0;
        for (Message message : messages){
            if (message != null){
                count++;
            }
        }
        return count;
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public String[] getMessages() throws RemoteException {
        ArrayList<String> temp = new ArrayList<>();
        int index = newestMessagePointer;
        while (true) {
            if (messages[index] == null) {
                break;
            }
            temp.add(messages[index].toString());
            index--;

            if (index == -1) {
                index = maxNumMessages - 1;
            }
            if (index == newestMessagePointer) {
                break;
            }
        }
        String[] output = new String[temp.size()];
        output = temp.toArray(output);
        return output;
    }

    /**
     *
     * @param index
     * @return
     * @throws RemoteException
     */
    @Override
    public String getMessage(int index) throws RemoteException {
        return null;
    }

    /**
     *
     * @param msg
     * @throws RemoteException
     */
    @Override
    public void putMessage(String msg) throws RemoteException {
        int free = freeSlot();
        if (msg.trim().isEmpty()) {
            throw new InvalidMessageException("Provided Message is empty. Please send us Content.");
        }
        if (free != -1) {
            messages[free] = new Message(msg.trim());
            newestMessagePointer = free;
        } else {
            throw new BulletinBoardFullException();
        }
    }

    /**
     *
     * @return
     */
    private int freeSlot() {
        int free = (newestMessagePointer + 1) % maxNumMessages;
        if (messages[free] == null) {
            return free;
        }
        return -1;
    }
}
