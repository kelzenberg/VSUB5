package com;

import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.rdfconnection.RDFConnectionLocal;
import org.apache.jena.vocabulary.VCARD;

public class libTest {

    private final Model model;
    private final Resource johnSmith;
    private final RDFConnection rdf;

    public libTest(Model model, Resource johnSmith) {
        this.model = model;
        this.johnSmith = johnSmith;
        rdf = RDFConnectionFactory.connect("http://141.64.175.23:9999/blazegraph/sparql");

        Query query = BEFEHL;

        rdf.queryResultSet();
        rdf.update();

    }

    public void test() {
        // some definitions
        String personURI = "http://somewhere/JohnSmith";
        String fullName = "John Smith";

        // create an empty Model
        Model model = ModelFactory.createDefaultModel();

        // create the resource
        Resource johnSmith = model.createResource(personURI);

        // add the property
        johnSmith.addProperty(VCARD.FN, fullName);
    }
}
