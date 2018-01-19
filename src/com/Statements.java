package com;

public class Statements {

    public static final String queryAll = "SELECT * { ?s ?p ?o }\n";
    public static final String deleteAll = "DELETE where { ?s ?o ?p }\n";

    // space character at the end is needed to stack commands
    public static final String prefixFOAF = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
    public static final String prefixBB = "PREFIX bb: <http://omniskop.de/vs/bb>\n";
    public static final String prefixDATA = "PREFIX data: <https://omniskop.de/blazegraph/data/>\n";
    public static final String prefixRDF = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns>\n";
    public static final String prefixRDFS = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema>\n";
    public static final String prefixAll = prefixFOAF + prefixBB + prefixDATA + prefixRDF + prefixRDFS;

    public static final String insertData = "INSERT DATA { s p o . }"; //not functional, just the pattern
    public static final String insertMultiLineData = "INSERT DATA { s p o ; " + " p o . }";  //not functional, just the pattern

    public static String publishMessage(String subject, String content, String creator, String recipient) {
        return prefixAll + String.format("INSERT {\n" +
                        "    data:message rdf:type bb:Message;\n" +
                        "        bb:subject \"%s\";\n" +
                        "        bb:content \"%s\";\n" +
                        "        bb:creator %s;\n" + // TODO: creator macht Sorgen
                        "        bb:timestamp ?time;\n" +
                        "        bb:recipient \"%s\".}\n" +
                        "WHERE {BIND(NOW() as ?time)}",
                subject, content, creator, recipient);

    }

    public static String addUser(String firstName, String lastName, String email) {
        return prefixAll + String.format("INSERT DATA {\n"
                        + "data:user rdf:type bb:User;\n"
                        + "foaf:firstName \"%s\";\n"
                        + "foaf:lastName \"%s\";\n"
                        + "foaf:mbox \"%s\".}\n",
                firstName, lastName, email);
    }

    public static String getUser(String email) {
        return prefixAll + String.format("SELECT ?s WHERE {\n"
                        + "?s foaf:mbox \"%s\"\n",
                email);
    }

    public static String getMyMessages(String email) {
        return prefixAll + String.format("select ?content WHERE {\n"
                        + "?msgS bb:content ?content.\n"
                        + "?msgS bb:recipient ?receivers.\n"
                        + "VALUES ?receivers { \"all\" \"%s\" } .\n",
                email);
    }
}
