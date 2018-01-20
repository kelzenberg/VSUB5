package com.server;

import com.BulletinBoardIntf;
import com.Exceptions.InvalidMessageException;
import com.Exceptions.ServerRuntimeException;
import com.Statements;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import static com.Statements.*;

public class Server implements BulletinBoardIntf {

    private static int maxNumMessages;
    private static int maxMessageLifeTime;
    private static int maxLengthMessage;
    private static String nameOfService;
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

            //rdf = RDFConnectionFactory.connect("http://localhost:9999/blazegraph/sparql", "http://localhost:9999/blazegraph/sparql", "http://localhost:9999/blazegraph/sparql");
            rdf = RDFConnectionFactory.connect("http://omniskop.de:8080/blazegraph/sparql", "http://omniskop.de:8080/blazegraph/sparql", "http://omniskop.de:8080/blazegraph/sparql");
            init();

            /* BEISPIEL QUERY:
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
            */

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
        deletesOldMessages();
        int count = 0;

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
        deletesOldMessages();
        ArrayList<String> temp = new ArrayList<>();

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
        deletesOldMessages();

        return null;
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
        deletesOldMessages();
        //int free = freeSlot();
        String trimmed = message.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidMessageException("Provided Message is empty. Please send us Content.");
        }
        if (trimmed.length() > maxLengthMessage) {
            throw new InvalidMessageException("Provided Message is too long. Please restrict yourself to "
                    + maxLengthMessage + " Characters.");
        }

    }

    /**
     * Deletes old (timed out) Messages after a certain time.
     * Will be called every time an action on the BulletinBoard is performed.
     *
     * @throws ServerRuntimeException
     */
    private void deletesOldMessages() throws ServerRuntimeException {

    }

    private static void init() {
        System.out.println("\n|---------- Listing all Methods: ----------\n");

        System.out.println("------- getUser():\n" + getUser("INPUT"));
        System.out.println("------- addUser():\n" + addUser("INPUT1", "INPUT2", "INPUT3"));
        System.out.println("------- getMyMessages():\n" + getMyMessages("INPUT"));
        System.out.println("------- publishMessage():\n" + publishMessage("INPUT1", "INPUT2", "INPUT3", "INPUT4"));
        System.out.println("------- deleteOldMessages():\n" + deleteOldMessages("INPUT"));

        System.out.println("---------- Methods listed. ----------|\n");

        System.out.println("|---------- Start Testing: ----------\n");

        //System.out.println(deleteAll);
        // deletes everything in TripleStore !!!
        //rdf.update(deleteAll);

        String query = addUser("Marylin", "RonMoe", "moe@mary.de");
        System.out.println("------- addUser():\n" + query);
        rdf.update(query);

        QueryExecution exec = rdf.query(getUser("moe@mary.de"));
        ResultSet results = exec.execSelect();
        String user = "";
        while (results.hasNext()) {
            QuerySolution next = results.next();
            user = next.get("s").toString();
            //System.out.println(user);
        }

        query = publishMessage("VS", "Dit isn Test.", user, "all");
        System.out.println("------- publishMessage():\n" + query);
        rdf.update(query);

        System.out.println("---------- Testing finished. ----------|\n");

        System.out.println("|---------- Start querying Results: ----------\n");

        exec = rdf.query("SELECT * { ?s ?p ?o }");
        results = exec.execSelect();

        while (results.hasNext()) {
            QuerySolution next = results.next();
            System.out.print("### " + next.get("s") + "  ");
            System.out.print(next.get("p") + "  ");
            System.out.println(next.get("o") + "  ");
        }

        System.out.println("\n---------- Querying Results finished. ----------|\n");

        exec.close();
    }
}
