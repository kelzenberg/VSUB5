package com.client;

import com.BulletinBoardIntf;

import java.rmi.ConnectException;

import com.Exceptions.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.io.*;
import java.lang.String;

public class Client {
    
    static BulletinBoardIntf bb;
    static BufferedReader clReader;
    
    /**
    * Runs the Client
    *
    * @param args
    */
    public static void main(String args[]) {
        try {
            String name = "BulletinBoard";
            Registry registry = LocateRegistry.getRegistry("localhost");
            bb = (BulletinBoardIntf) registry.lookup(name);
            runCLI();
        } catch (ConnectException e) {
            System.out.println("Unable to connect to server.");
            System.out.println(e);
        } catch (Exception e) {
            System.err.println("Critical server side error:");
            e.printStackTrace();
            System.err.println("Exiting.");
        }
    }
    
    /**
    * Runs the Command Line Interface that parses User inputs
    */
    private static void runCLI() {
        clReader = new BufferedReader(new InputStreamReader(System.in));
        String rawInput;
        String[] userInput;
        while (true) {
            System.out.print("> ");
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
                System.out.println("Choose a command: ");
                System.out.println("help | post [message] | count | read | read [indices] | exit");
            break;
            case "post": // posts a message
                if (userInput.length > 1) { // The user wrote the message directly after the post command
                    String[] msg = Arrays.copyOfRange(userInput, 1, userInput.length);
                    buildAndPostMessage(msg);
                } else { // Enter post mode where the user can enter the message.
                    System.out.println("Enter the message to post:");
                    String msg = readline(clReader).trim();
                    if(msg.length() == 0) {
                        System.out.println("Empty message was not send.");
                    } else {
                        buildAndPostMessage(msg);
                    }
                }
            break;
            case "count": // returns the number of messages on the BulletinBoard
                printMessageCount();
            break;
            case "read": // returns all messages on the BulletinBoard
                if(userInput.length == 1){
                    printAllMessages();
                } else {
                    int index;
                    for(int i = 1;i < userInput.length;i++) {
                        try {
                            index = Integer.parseInt(userInput[i]);
                        } catch(NumberFormatException e) {
                            System.out.println(userInput[i] + " is not a number");
                            continue;
                        }
                        printMessage(index);
                    }
                }
                
            break;
            case "exit":
                System.exit(0);
            break;
            default:
                System.out.println("Unknown command. Type 'help' for a list of  all commands.");
        }
    }
    
    private static void printMessage(int index) {
        try {
            String messages = bb.getMessage(index);
            System.out.println("=> Message " + index + ": ");
            System.out.println(messages);
        } catch(MessageNotFoundException e) {
            System.out.println("There is no message with the index " + index);
        } catch(Exception e) {
            System.out.println("The server encountered an unexpected error:");
            System.out.println(e);
        }
    }
    
    /**
    * Prints all the Messages of the BulletinBoard to the Console
    *
    * @param bb BulletinBoard
    */
    private static void printAllMessages() {
        try {
            String[] messages = bb.getMessages();
            if(messages.length == 0) {
                System.out.println("There are no messages.");
                return;
            } else if(messages.length == 1) {
                System.out.println("There is one message on the bulletinboard:");
                System.out.println(messages[0]);
                return;
            } else {
                System.out.println("There are " + messages.length + " messages on the bulletinboard:");
            }
            for(int i = 0;i < messages.length;i++) {
                System.out.println("=> Message:");
                System.out.println(messages[i]);
            }
        } catch(Exception e) {
            System.out.println("The server encountered an unexpected error:");
            System.out.println(e);
        }
    }
    
    private static void printMessageCount() {
        try {
            int count = bb.getMessageCount();
            if(count == 0){
                System.out.println("There are no messages.");
            } else if(count == 1) {
                System.out.println("There is one message on the bulletinboard.");
            } else {
                System.out.println("There are " + count + " messages on the bulletinboard.");
            }
        } catch(Exception e) {
            System.out.println("The server encountered an unexpected error:");
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
        buildAndPostMessage(message);
    }
    
    /**
    * sends the message to the Server
    *
    * @param message
    */
    private static void buildAndPostMessage(String message) {
        try {
            bb.putMessage(message);
            System.out.println("Message posted!");
        } catch (InvalidMessageException e) {
            System.out.println("The server responded with an InvalidMessageException:");
            System.out.println(e);
        } catch (BulletinBoardFullException e) {
            System.out.println("The server responded with an BulletinBoardFullException:");
            System.out.println(e);
        } catch (Exception e) {
            System.out.println("The server encountered an unexpected error:");
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
