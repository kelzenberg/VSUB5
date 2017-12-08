package com.server;

import com.BulletinBoardFullException;
import com.BulletinBoardIntf;
import com.InvalidMessageException;
import com.Message;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class Server implements BulletinBoardIntf {

    private static int maxNumMessages;
    private static int maxMessageLifeTime;
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
        maxMessageLifeTime = 600;
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
        try {
            Server engine = new Server();
            BulletinBoardIntf bb = (BulletinBoardIntf) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.createRegistry(1099);
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
        deleteOldMessages();
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
        deleteOldMessages();
        ArrayList<String> temp = new ArrayList<>();
        int index = newestMessagePointer;
        while (true) {
            if (messages[index] == null) {
                break;
            }
            temp.add(messages[index].getMessage());
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
        deleteOldMessages();
        // TODO: check if index exists
        return messages[index].getMessage();
    }

    /**
     *
     * @param message
     * @throws RemoteException
     */
    @Override
    public void putMessage(String message) throws RemoteException {
        deleteOldMessages();
        int free = freeSlot();
        String trimmed = message.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidMessageException("Provided Message is empty. Please send us Content.");
        }
        if (trimmed.length() > maxLengthMessage){
            throw new InvalidMessageException("Provided Message is too long. Please restrict yourself to "
                    + maxLengthMessage + " Characters.");
        }
        if (free != -1) {
            messages[free] = new Message(trimmed);
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

    private void deleteOldMessages() {
        for (int i = 0; i < messages.length; i++) {
            Message message = messages[i];
            Duration messageLifeTime = Duration.between(message.getCreated(), Instant.now());
            if (messageLifeTime.toMillis() / 1000 > maxMessageLifeTime) {
                messages[i] = null;
            }
        }
    }
}
