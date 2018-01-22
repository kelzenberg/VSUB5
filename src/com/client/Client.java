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

    private static BulletinBoardIntf bb;
    private static BufferedReader clReader;
    private static String userMbox;
    private static String userFirstName;
    private static String userLastName;

    /**
     * Runs the Client
     *
     * @param args
     */
    public static void main(String args[]) {
        try {
            String name = "BulletinBoard";
            // get the RMI registry entries
            Registry registry = LocateRegistry.getRegistry("localhost");
            // get the name of the rmi entry
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

        System.out.println("Please enter your E-Mail address: ");
        userMbox = readline(clReader);
        if (userMbox != null) {
            userMbox = userMbox.trim();
        }

        System.out.println("Please enter your fist name: ");
        userFirstName = readline(clReader);
        if (userFirstName != null) {
            userFirstName = userFirstName.trim();
        }

        System.out.println("Please enter your last name: ");
        userLastName = readline(clReader);
        if (userLastName != null) {
            userLastName = userLastName.trim();
        }

        String[] userInput;
        System.out.println("Type a command or 'help' for a list of all commands.");
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
     * Validates and splits the User Input if evaluable
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
                System.out.println("help | post | count | read | read [index] | exit");
                break;
            case "post": // posts a message
                // Enter post mode where the user can enter the message.
                managePost(userInput);
                break;
            case "count": // returns the number of messages on the BulletinBoard
                printMessageCount();
                break;
            case "read": // returns all messages on the BulletinBoard
                if (userInput.length == 1) {
                    printAllMessages();
                } else {
                    printMessage(userInput[1]);
                }
                break;
            case "exit": // exits the client
                System.exit(0);
                break;
            default:
                System.out.println("Unknown command. Type 'help' for a list of all commands.");
        }
    }

    /**
     * Checks user input for recipient, subject & msg
     * and calls buildAndPostMessage()
     *
     * @param userInput
     */
    private static void managePost(String[] userInput) {
        System.out.println("Recipient: ");
        String recipient = readline(clReader);
        if (recipient != null) {
            recipient = recipient.trim();
        }

        System.out.println("Subject: ");
        String subject = readline(clReader);
        if (subject != null) {
            subject = subject.trim();
        }

        System.out.println("Enter the message to post:");
        String msg = readline(clReader);
        if (msg != null) {
            msg = msg.trim();
            if (msg.length() == 0) {
                System.out.println("Message was not send because it's empty, bruh.");
            } else {
                buildAndPostMessage(recipient, subject, msg);
            }
        }
    }

    /**
     * Prints the message with the index provided by the user input
     *
     * @param index of message on BulletinBoard
     */
    private static void printMessage(String index) {
        try {
            String messages = bb.getMessage(index);
            System.out.println("=> Message " + index + ": ");
            System.out.println(messages);
        } catch (MessageNotFoundException e) {
            System.out.println("There is no message with the index " + index);
        } catch (Exception e) {
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
            String[] messages = bb.getMessages("");
            if (messages.length == 0) {
                System.out.println("There are no messages. Fill that emptiness!");
                return;
            } else if (messages.length == 1) {
                System.out.println("There is only one message on the BulletinBoard:");
                System.out.println(messages[0]);
                return;
            } else {
                System.out.println("There are " + messages.length + " messages on the BulletinBoard:");
            }
            for (String message : messages) {
                System.out.println("=> Message:");
                System.out.println(message);
            }
        } catch (Exception e) {
            System.out.println("The server encountered an unexpected error:");
            System.out.println(e);
        }
    }

    /**
     * Counts all the messages on the BulletinBoard
     * and prints it to the command line
     */
    private static void printMessageCount() {
        try {
            int count = bb.getMessageCount();
            if (count == 0) {
                System.out.println("There are no messages. Fill that emptiness!");
            } else if (count == 1) {
                System.out.println("There is only one message on the BulletinBoard:");
            } else {
                System.out.println("There are " + count + " messages on the BulletinBoard.");
            }
        } catch (Exception e) {
            System.out.println("The server encountered an unexpected error:");
            System.out.println(e);
        }
    }

    /**
     * Sends the Message to the Server
     *
     * @param message
     */
    private static void buildAndPostMessage(String recipient, String subject, String message) {
        try {
            bb.putMessage(userFirstName, userLastName, userMbox, recipient, subject, message);
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
            System.out.println("An error occurred while reading console input. Exiting.");
            System.out.println(e);
            System.exit(1);
        }
        return null;
    }
}
