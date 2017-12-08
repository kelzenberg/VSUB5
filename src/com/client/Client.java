package com.client;

import com.BulletinBoardIntf;

import java.rmi.RemoteException;

import com.Exceptions.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.io.*;
import java.lang.String;

public class Client {

   static BulletinBoardIntf bb;

   /**
    * Runs the Client
    *
    * @param args
    */
   public static void main(String args[]) {
      try {
         String name = "BulletinBoard";
         Registry registry = LocateRegistry.getRegistry();
         bb = (BulletinBoardIntf) registry.lookup(name);
         runCLI();
            
            
            /*
            int test = bb.getMessageCount();
            System.out.println("All Messages: " + test);
            try {
                bb.putMessage("Eine tolle Nachricht!");
            } catch(RemoteException error) {
                System.out.println(error);
            }
            test = bb.getMessageCount();
            System.out.println("All Messages: " + test);
            printAllMessages(bb);
            */
      } catch (Exception e) {
         System.err.println("ComputePi exception:");
         e.printStackTrace();
      }
   }

   /**
    * Prints all the Messages of the BulletinBoard to the Console
    *
    * @param bb BulletinBoard
    */
   private static void printAllMessages(BulletinBoardIntf bb) {
      String[] messages;
      try {
         messages = bb.getMessages();
      } catch (RemoteException error) {
         return;
      }
      System.out.println("Messages begin ===");
      for (String message : messages) {
         System.out.println(message.toString());
      }
      System.out.println("Messages end   ===");
   }

   /**
    * Runs the Command Line Interface that parses User inputs
    */
   private static void runCLI() {
      BufferedReader clReader = new BufferedReader(new InputStreamReader(System.in));
      String rawInput;
      String[] userInput;
      while (true) {
         System.out.print("\n> ");
         rawInput = readline(clReader);

         userInput = validateUserInput(rawInput);
         if (userInput == null) {
            continue;
         }

         parseUserInput(userInput);
      }
   }

   /**
    * Validates the User Input if evaluable
    *
    * @param rawInput as user input
    * @return String Array with command and user input
    */
   private static String[] validateUserInput(String rawInput) {
      rawInput = rawInput.trim();
      if (rawInput.length() == 0) {
         return null;
      }
      String[] out = rawInput.split(" ");
      return out;
   }

   /**
    * Parses the User input according to the wanted command
    *
    * @param userInput String Array
    */
   private static void parseUserInput(String[] userInput) {
      switch (userInput[0]) {
         case "help": // prints help information
            // TODO: print help information
            // System.out.println("");
            break;
         case "post": // posts a message
            if (userInput.length > 1) { // The user wrote the message directly after the post command
               String[] msg = Arrays.copyOfRange(userInput, 1, userInput.length);
               buildAndPostMessage(msg);
            } else { // Enter post mode where the user can enter the message.

            }
            break;
         case "count": // returns the number of messages on the BulletinBoard
            printMessageCount();
            break;
         case "read": // returns all messages on the BulletinBoard

            break;
         default:
            System.out.println("Unknown command. Type 'help' for a list of  all commands.");
      }
   }

    private static void printMessageCount() {
        try {
            int count = bb.getMessageCount();
            System.out.println("There are " + count + " messages on the bulletinboard.");
        } catch(RemoteException e) {
            System.out.println("The server encountered an unknown error:");
            System.out.println(e);
        }
    }

   /**
    * Builds the Message from the User input and sends it to the Server
    *
    * @param wordList
    */
   private static void buildAndPostMessage(String[] wordList) {
      String message = String.join(" ", wordList);
      try {
         bb.putMessage(message);
      } catch (InvalidMessageException e) {
         System.out.println("The server responded with an InvalidMessageException:");
         System.out.println(e);
      } catch (BulletinBoardFullException e) {
         System.out.println("The server responded with an BulletinBoardFullException:");
         System.out.println(e);
      } catch (RemoteException e) {
         System.out.println("The server encountered an unknown error:");
         System.out.println(e);
      }
   }

   /**
    * Reads the command line input from the user
    *
    * @param reader
    * @return String user input
    */
   private static String readline(BufferedReader reader) {
      try {
         String out = reader.readLine();
         if (out == null) { // end of stream
            System.out.println("End of readline stream reached. Exiting.");
            System.exit(0);
         }
         return out;
      } catch (IOException e) {
         System.out.println("An error occoured while reading console input. Extiting.");
         System.out.println(e);
         System.exit(1);
      }
      return null;
   }
}
