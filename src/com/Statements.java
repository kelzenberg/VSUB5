package com;

public class Statements {

    public static final String queryAll = "SELECT * { ?s ?p ?o }\n";
    public static final String deleteAll = "DELETE where { ?s ?o ?p }\n";

    // space character at the end is needed to stack commands
    public static final String prefixFOAF = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
    public static final String prefixBB = "PREFIX bb: <http://omniskop.de/vs/bb>\n";
    public static final String prefixDATA = "PREFIX data: <https://omniskop.de/blazegraph/data/>\n";
    public static final String prefixAll = prefixBB + prefixFOAF + prefixDATA;

    public static final String insertData = "INSERT DATA { s p o . }"; //not functional, just the pattern
    public static final String insertMultiLineData = "INSERT DATA { s p o ; " + " p o . }";  //not functional, just the pattern

    public static String createMessage(String subject, String content, String creator, String recipient) {
        return prefixAll + String.format("INSERT {\n" +
                        "    data:message rdf:type bb:Message ;\n" +
                        "        bb:subject \"%s\" ;\n" +
                        "        bb:content \"%s\" ;\n" +
                        "        bb:creator data:%s ;\n" +
                        "        bb:timestamp ?time;\n" +
                        "        bb:recipient data:%s .\n" +
                        "} WHERE {BIND(NOW() as ?time)}",
                subject, content, creator, recipient);
    }
}
