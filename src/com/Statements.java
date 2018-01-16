package com;

public class Statements {

    public static final String queryAll = "SELECT * { ?s ?p ?o }";
    public static final String deleteAll = "DELETE where { ?s ?o ?p }";

    // space character at the end is needed to stack commands
    public static final String prefixFOAF = "prefix foaf: <http://xmlns.com/foaf/0.1/> ";
    public static final String prefixDC = "prefix dc: <http://purl.org/dc/elements/1.1/> ";
    public static final String prefixOmnis = "prefix omnis: <http://omniskop.de/vs/> ";

    public static final String insertData = "INSERT DATA { s p o . }"; //not functional, just the pattern
    public static final String insertMultiLineData = "INSERT DATA { s p o ; " + " p o . }";  //not functional, just the pattern

}
