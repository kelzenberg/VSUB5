package com.server;

import java.util.UUID;

public class Statements {

    // \n character at the end is needed to stack commands
    public static final String prefixFOAF = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
    public static final String prefixBB = "PREFIX bb: <http://omniskop.de/vs/bb/>\n";
    public static final String prefixDATA = "PREFIX data: <https://omniskop.de/blazegraph/data/>\n";
    public static final String prefixRDF = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns>\n";
    public static final String prefixRDFS = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema>\n";
    public static final String prefixAll = prefixFOAF + prefixBB + prefixDATA + prefixRDF + prefixRDFS;

    //NOT FUNCTIONAL, just the pattern
    public static final String insertData = "INSERT DATA { s p o . }";

    // gets everything in TripleStore
    public static final String queryAll = "SELECT * { ?s ?p ?o }\n";
    // deletes everything in TripleStore
    public static final String deleteAll = "DELETE where { ?s ?o ?p }\n";

    /**
     * Gets the user with @param email
     *
     * @param email Email for desired User
     * @return String that can be queried and/or update in SPARQL
     */
    public static String getUser(String email) {
        return prefixAll + String.format("SELECT ?s WHERE {\n"
                        + "?s foaf:mbox \"%s\" . }\n",
                email);
    }

    /**
     * Adds a new user with @param firstName, lastName, email
     *
     * @param firstName of User
     * @param lastName  of User
     * @param email     of User
     * @return String that can be queried and/or update in SPARQL
     */
    public static String addUser(String firstName, String lastName, String email) {
        // TODO: prüfen, ob Email zu Nutzer existiert
        return prefixAll + String.format("INSERT DATA {\n"
                        + "data:user_%s rdf:type bb:User ;\n" // TODO: data:user'X' muss für X Zahl hochzählen
                        + "foaf:firstName \"%s\" ;\n"
                        + "foaf:lastName \"%s\" ;\n"
                        + "foaf:mbox \"%s\" . }\n",
                UUID.randomUUID(), firstName, lastName, email);
    }

    /**
     * Gets all the Messages that the User with @param email can read (or for 'all')
     *
     * @param email of User
     * @return String that can be queried and/or update in SPARQL
     */
    public static String getMessagesForUser(String email) {
        // TODO: prüfen, ob Email zu Nutzer existiert
        return prefixAll + String.format("SELECT ?content WHERE {\n"
                        + "?msgS bb:content ?content .\n"
                        + "?msgS bb:recipient ?receivers .\n"
                        + "VALUES ?receivers { \"all\" \"%s\" } . }\n",
                email);
    }

    /**
     * Gets all Messages found for @param subject
     *
     * @param subject of Message
     * @return String that can be queried and/or update in SPARQL
     */
    public static String getMessagesForSubject(String subject) {
        return prefixAll + String.format("SELECT ?s ?p ?o WHERE {\n"
                        + "?s ?p ?o .\n"
                        + "?s bb:subject \"%s\" . }\n",
                subject);
    }

    /**
     * Gets all Messages found for @param subject that @param email can read (or for 'all')
     *
     * @param subject of Message
     * @param email   of User
     * @return String that can be queried and/or update in SPARQL
     */
    public static String getMessagesForSubjectAndUser(String subject, String email) {
        return prefixAll + String.format("SELECT ?s ?p ?o WHERE {\n"
                        + "?s ?p ?o .\n"
                        + "?s bb:subject \"%s\" . \n"
                        + "?s bb:recipient ?receivers .\n"
                        + "VALUES ?receivers { \"all\" \"%s\" } . }\n",
                subject, email);
    }

    /**
     * Publishes a Message with @param subject, content from @param creator to @param recipient
     *
     * @param subject   of Message
     * @param content   of Message
     * @param creator   of Message
     * @param recipient of Message
     * @return String that can be queried and/or update in SPARQL
     */
    public static String publishMessage(String subject, String content, String creator, String recipient) {
        return prefixAll + String.format("INSERT {\n"
                        + "data:message_%s rdf:type bb:Message ;\n"
                        + "bb:subject \"%s\" ;\n"
                        + "bb:content \"%s\" ;\n"
                        + "bb:creator \"%s\" ;\n" // TODO: creator bekommt String als Input oder nicht?
                        + "bb:timestamp ?time ;\n"
                        + "bb:recipient \"%s\" . }\n"
                        + "WHERE { BIND(NOW() as ?time) }\n",
                UUID.randomUUID(), subject, content, creator, recipient);

    }

    /**
     * Deletes all Messages that are older than 10 Minutes (see Server.maxMessageLifeTime)
     *
     * @return String that can be queried and/or update in SPARQL
     */
    public static String deleteOldMessages() {
        return prefixAll + String.format("DELETE {?s ?p ?o} WHERE {\n"
                + "?s ?p ?o .\n"
                + "?s bb:timestamp ?time .\n"
                + "FILTER ((NOW() - ?time) > ?10min)\n"
                + "BIND ((\"2000-01-01T00:10:00.000Z\"^^xsd:dateTime) - (\"2000-01-01T00:00:00.000Z\"^^xsd:dateTime) as ?10min) }\n"
        );
    }
}
