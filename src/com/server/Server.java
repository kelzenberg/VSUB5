package com.server;

import com.*;
import com.BulletinBoardIntf;
import com.Exceptions.*;

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
     * Constructor for the BulletinBoard Server
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
     * Runs the Server
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            Server engine = new Server();
            // mark the Server as an RMI object
            BulletinBoardIntf bb = (BulletinBoardIntf) UnicastRemoteObject.exportObject(engine, 0);
            // create RMI manager/registry
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(nameOfService, bb);
            System.out.println("BulletinBoard bound");
        } catch (Exception e) {
            System.err.println("BulletinBoard exception:");
            e.printStackTrace();
        }
    }

    /**
     * Gets the number of Messages on the BulletinBoard
     *
     * @return int number of pinned messages
     * @throws Exception
     */
    @Override
    public int getMessageCount() throws Exception {
        deleteOldMessages();
        int count = 0;
        for (Message message : messages) {
            if (message != null) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets all Messages on the BulletinBoard as an Array
     *
     * @return String-Array with one Message object per slot
     * @throws Exception
     */
    @Override
    public String[] getMessages() throws Exception {
        deleteOldMessages();
        ArrayList<String> temp = new ArrayList<>();
        // create pointer for circular Array
        int index = newestMessagePointer;
        while (true) {
            if (messages[index] == null) {
                break;
            }
            temp.add("MSG" + index + ": " + messages[index].getMessage());
            index--;

            // if pointer reached beginning of array
            if (index == -1) {
                // set to end of array
                index = maxNumMessages - 1;
            }
            // if pointer reached start position
            if (index == newestMessagePointer) {
                break;
            }
        }
        String[] output = new String[temp.size()];
        output = temp.toArray(output);
        return output;
    }

    /**
     * Gets a specific Message at the provided index of the Array
     *
     * @param index
     * @return String of the Message object at index
     * @throws Exception
     */
    @Override
    public String getMessage(int index) throws Exception {
        deleteOldMessages();
        if (index < 0 || index >= maxNumMessages || messages[index] == null) {
            throw new MessageNotFoundException("The BulletinBoard contains no Message with this index.");
        }
        return messages[index].getMessage();
    }

    /**
     * Creates a new Message object with input message
     * and puts it into the next free slot on the BulletinBoard
     *
     * @param msg
     * @throws Exception
     */
    @Override
    public void putMessage(String message) throws Exception {
        deleteOldMessages();
        int free = freeSlot();
        String trimmed = message.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidMessageException("Provided Message is empty. Please send us Content.");
        }
        if (trimmed.length() > maxLengthMessage) {
            throw new InvalidMessageException("Provided Message is too long. Please restrict yourself to "
                    + maxLengthMessage + " Characters.");
        }
        if (free != -1) {
            messages[free] = new Message(trimmed);
            // set the pointer to the put message
            newestMessagePointer = free;
        } else {
            throw new BulletinBoardFullException();
        }
    }

    /**
     * Cycles through the circular array
     * and checks if a free Slot on the BulletinBoard is available
     *
     * @return int index of free slot otherwise -1
     */
    private int freeSlot() {
        int free = (newestMessagePointer + 1) % maxNumMessages;
        if (messages[free] == null) {
            return free;
        }
        return -1;
    }

    /**
     * Deletes old (timed out) Messages after a certain time.
     * Will be called every time an action on the BulletinBoard is performed.
     *
     * @throws ServerRuntimeException
     */
    private void deleteOldMessages() throws ServerRuntimeException {
        try {
            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                if (message == null) continue;
                Duration messageLifeTime = Duration.between(message.getCreated(), Instant.now());
                if (messageLifeTime.toMillis() / 1000 > maxMessageLifeTime) {
                    System.out.println("Message deleted: " + i + ": \"" + messages[i].getMessage() + "\"");
                    messages[i] = null;
                }
            }
        } catch (Exception e) {
            System.out.println("ServerRuntimeException:");
            System.out.println(e);
            throw new ServerRuntimeException();
        }
    }
}
