package com;

public class Statements {

    public static final String queryAll = "SELECT * { ?s ?p ?o }\n";
    public static final String deleteAll = "DELETE where { ?s ?o ?p }\n";

    // space character at the end is needed to stack commands
    public static final String prefixFOAF = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
    public static final String prefixDC = "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n";
    public static final String prefixBB = "PREFIX bb: <http://omniskop.de/vs/bb>\n";
    public static final String prefixAll = prefixBB + prefixFOAF + prefixDC;

    public static final String insertData = "INSERT DATA { s p o . }"; //not functional, just the pattern
    public static final String insertMultiLineData = "INSERT DATA { s p o ; " + " p o . }";  //not functional, just the pattern

    public static final String abfrageMitFilter = "prefix foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "prefix dc: <http://purl.org/dc/elements/1.1/>\n" +
            "prefix bb: <http://omniskop.de/vs/bb>\n" +
            "SELECT ?user ?name ?mbox\n" +
            "WHERE {\n" +
            "  ?user foaf:type foaf:person .\n" +
            "  ?user foaf:mbox ?mbox .\n" +
            "  ?user foaf:name ?name .\n" +
            "  FILTER(?mbox IN (\"jannis@rieger-kaulsdorf.de\", \"all@all.de\"))\n" +
            "}";

}
