package ru.etu.cgvm.notations.rdf;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.irix.IRIException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;

import java.io.InputStream;

@Slf4j
public class RdfReader {

    // TODO
    private Model parseModel(String fileName) throws IRIException {
        Model model = ModelFactory.createDefaultModel();
        InputStream in = RDFDataMgr.open(fileName);
        return model.read(in, null);
    }
}
