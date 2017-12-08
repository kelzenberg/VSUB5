package com.server;

import com.BulletinBoardIntf;
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
    * Gets the number of Messages on the BulletinBoard
    *
    * @return int number of pinned messages
    * @throws RemoteException
    */
   @Override
   public int getMessageCount() throws RemoteException {
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
    * Gets a specific Message at the provided index of the Array
    *
    * @param index
    * @return String of the Message object at index
    * @throws RemoteException
    */
   @Override
   public String getMessage(int index) throws RemoteException {
      deleteOldMessages();
      if (messages[index] == null) {
         throw new MessageNotFoundException("The BulletinBoard contains no Message with this index.");
      }
      return messages[index].getMessage();
   }

   /**
    * Creates a new Message object with input message
    * and puts it onto the BulletinBoard
    *
    * @param msg
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
      if (trimmed.length() > maxLengthMessage) {
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
    * Checks if a free Slot on the BulletinBoard is available
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
    * Will be called everytime an action on the BulletinBoard is performed.
    */
   private void deleteOldMessages() {
      for (int i = 0; i < messages.length; i++) {
         Message message = messages[i];
         Duration messageLifeTime = Duration.between(message.getCreated(), Instant.now());
         if (messageLifeTime.toMillis() / 1000 > maxMessageLifeTime) {
            messages[i] = null;
            System.out.println("Message deleted: " + i + ": \"" + messages[i].getMessage() + "\"");
         }
      }
   }
}
