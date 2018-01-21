package com.server;

import com.BulletinBoardIntf;
import com.Exceptions.InvalidMessageException;
import com.Exceptions.ServerRuntimeException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import static com.server.Statements.*;

public class Server implements BulletinBoardIntf {

    private static int maxNumMessages;
    private static int maxMessageLifeTime;
    private static int maxLengthMessage;
    private static String nameOfService;
    private static RDFConnection rdf;
    private static String host;

    /**
     * Constructor for the BulletinBoard Server
     */
    private Server() {
        super();
        maxNumMessages = 20;
        maxMessageLifeTime = 600;
        maxLengthMessage = 160;
        nameOfService = "BulletinBoard";
        host = "http://omniskop.de:8080/blazegraph/sparql";
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
            // connect to Blazegraph Triple Store
            connect();
            // start Initialization & Tests
            init();
            // close Connection to Blazegraph Triple Store
            close();
        } catch (Exception e) {
            System.err.println("BulletinBoard exception:");
            e.printStackTrace();
        }
    }

    /**
     * Connects to Blazegraph Host
     */
    private static void connect() {
        rdf = RDFConnectionFactory.connect(host, host, host);
        System.out.println("|- Connection established. -|");
    }

    /**
     * Closes Connection to Blazegraph Host
     */
    private static void close() {
        rdf.close();
        System.out.println("|- Connection closed. -|\n");
    }

    /**
     * Sends the given Query to the Host and returns an ArrayList with the results of the Query
     *
     * @param query for the Host in SPARQL
     * @return ArrayList\<QuerySolution\> with all Results of @param query
     */
    private static ArrayList<QuerySolution> query(String query) {
        connect();
        System.out.println("|- Send Query to Host...");
        System.out.println("|- with Query:\n" + query + "(End of Query) -|");
        ArrayList<QuerySolution> results = new ArrayList<>();
        QueryExecution exec = rdf.query(query);
        ResultSet resultSet = exec.execSelect();
        while (resultSet.hasNext()) {
            QuerySolution next = resultSet.next();
            results.add(next);
        }
        exec.close();
        System.out.println("... Returning Query Results. -|");
        close();
        return results;
    }

    /**
     * Updates the Host with the given Query
     *
     * @param query
     */
    private static void update(String query) {
        connect();
        System.out.println("|- Updating Host...");
        System.out.println("|- with Query:\n" + query + "(End of Query) -|");
        rdf.update(query);
        System.out.println("... Update Finished. -|");
        close();
    }

    /**
     * Gets the number of Messages on the BulletinBoard
     *
     * @return int number of pinned messages
     * @throws Exception
     */
    @Override
    public int getMessageCount() throws Exception {
        return query(Statements.getMessageCount()).size();
    }

    /**
     * Gets all Messages on the BulletinBoard as an Array
     *
     * @return String-Array with one Message object per slot
     * @throws Exception
     */
    @Override
    public String[] getMessages(String email) throws Exception {
        ArrayList<String> temp = new ArrayList<>();
        for (QuerySolution x : query(getMessagesForUser(email))) {
            temp.add(x.get("s").toString());
            // TODO: nur die Subjects returnen oder noch mehr?
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
        // TODO: get specific Message with ID/Index
        return "";
    }

    /**
     * Creates a new Message object with input message
     * and puts it into the next free slot on the BulletinBoard
     *
     * @param message
     * @param author
     * @throws Exception
     */
    @Override
    public void putMessage(String message, String author) throws Exception {
        String trimmed = message.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidMessageException("Provided Message is empty. Please send us Content.");
        }
        if (trimmed.length() > maxLengthMessage) {
            throw new InvalidMessageException("Provided Message is too long. Please restrict yourself to "
                    + maxLengthMessage + " Characters.");
        }
        // TODO: put a new Message on the Board
    }

    /**
     * Deletes old (timed out) Messages after a certain time.
     * Will be called every time an action on the BulletinBoard is performed.
     *
     * @throws ServerRuntimeException
     */
    private void deletesOldMessages() throws ServerRuntimeException {
        // TODO: delete old Message after Timeout (still needed?)
    }

    private static void init() {
        System.out.println("\n|---------- Listing all Methods: ----------|\n");

        System.out.println("------- getUser():\n" + getUser("INPUT"));
        System.out.println("------- addUser():\n" + addUser("INPUT1", "INPUT2", "INPUT3"));
        System.out.println("------- getMessagesForUser():\n" + getMessagesForUser("INPUT"));
        System.out.println("------- getMessagesForSubject():\n" + getMessagesForSubject("INPUT"));
        System.out.println("------- getMessagesForSubjectAndUser():\n" + getMessagesForSubjectAndUser("INPUT", "INPUT"));
        System.out.println("------- publishMessage():\n" + publishMessage("INPUT1", "INPUT2", "INPUT3", "INPUT4"));
        System.out.println("------- deleteOldMessages():\n" + deleteOldMessages());

        System.out.println("|---------- Start Testing: ----------|\n");

        // deletes everything in TripleStore !!!
        //update(deleteAll);

        update(addUser("Marylin", "RonMoe", "moe@mary.de"));

        //get the User with Email to use 'user' in publishMessage()
        String user = "";
        for (QuerySolution x : query(getUser("moe@mary.de"))) {
            user = x.get("s").toString();
        }
        update(publishMessage("VS", "Dit isn Test.", user, "all"));

        System.out.println("|---------- Querying all Results... ----------\n");

        for (QuerySolution x : query(queryAll)) {
            System.out.print("### " + x.get("s") + "  ");
            System.out.print(x.get("p") + "  ");
            System.out.println(x.get("o") + "  ");
        }

        System.out.println("\n---------- Querying Results finished. ----------|");

        System.out.println("|---------- MessageCount: " + query(Statements.getMessageCount()).size() + " ----------\n");
    }
}
