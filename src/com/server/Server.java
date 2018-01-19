package com.server;

import com.BulletinBoardIntf;
import com.Exceptions.BulletinBoardFullException;
import com.Exceptions.InvalidMessageException;
import com.Exceptions.MessageNotFoundException;
import com.Exceptions.ServerRuntimeException;
import com.Message;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import static com.Statements.*;

public class Server implements BulletinBoardIntf {

    private static int maxNumMessages;
    private static int maxMessageLifeTime;
    private static int maxLengthMessage;
    private static String nameOfService;
    private static Message[] messages;
    private int newestMessagePointer;
    private static RDFConnection rdf;

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
            BulletinBoardIntf bb = (BulletinBoardIntf) UnicastRemoteObject.exportObject((Remote) engine, 0);
            // create RMI manager/registry
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(nameOfService, bb);

            //rdf = RDFConnectionFactory.connect("http://localhost:9999/blazegraph/sparql", "http://localhost:9999/blazegraph/sparql", "http://localhost:9999/blazegraph/sparql");
            rdf = RDFConnectionFactory.connect("http://omniskop.de:8080/blazegraph/sparql", "http://omniskop.de:8080/blazegraph/sparql", "http://omniskop.de:8080/blazegraph/sparql");
            init();

            QueryExecution exec = rdf.query("SELECT * { ?s ?p ?o }");
            ResultSet results = exec.execSelect();

            while (results.hasNext()) {
                QuerySolution next = results.next();
                System.out.print(next.get("s") + "\n");
                System.out.print(next.get("p") + "\n");
                System.out.print(next.get("o") + "\n");
                System.out.println("----------");
            }

            exec.close();
            rdf.close();

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
            temp.add("MSG" + index + ": " + messages[index].getContent());
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
        return messages[index].getContent();
    }

    /**
     * Creates a new Message object with input message
     * and puts it into the next free slot on the BulletinBoard
     *
     * @param msg
     * @throws Exception
     */
    @Override
    public void putMessage(String message, String author) throws Exception {
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
            messages[free] = new Message(trimmed, author);
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
                    System.out.println("Message deleted: " + i + ": \"" + messages[i].getContent() + "\"");
                    messages[i] = null;
                }
            }
        } catch (Exception e) {
            System.out.println("ServerRuntimeException:");
            System.out.println(e);
            throw new ServerRuntimeException();
        }
    }

    private static void init() {
        System.out.println("\n---------- Start initialization: ----------\n");

        System.out.println(deleteAll);
        rdf.update(deleteAll);

        String query = prefixAll
                + "INSERT DATA { "
                + "bb:user1 foaf:type foaf:person;\n"
                + "foaf:name \"Jannis\".\n"
                + "bbb:user2 foaf:type foaf:person;\n"
                + "foaf:name \"Nicolai\".\n"
                + "bb:user3 foaf:type foaf:person;\n"
                + "foaf:name \"Steffen\".}";
        System.out.println(query);
        rdf.update(query);

        System.out.println("\n---------- Initialization finished. ----------\n");
    }
}
