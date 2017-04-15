/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model;

import com.semantic.data.JAXBStorage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ModelStore {
    
    private static final Logger log = Logger.getLogger(ModelStore.class.getName());

    public static OModel restore(File file) {
        try {
            if (file.exists()) {
                JAXBContext jc = new JAXBStorage().createContext();

                Unmarshaller unmarshaller = jc.createUnmarshaller();
                unmarshaller.setListener(new Unmarshaller.Listener() {

                    @Override
                    public void afterUnmarshal(Object target, Object parent) {
                        /* we must set the parents manuall, not possible with jaxb */
                        if (parent instanceof OntologyNode && target instanceof OntologyNode) {
                            OntologyNode parentNode = (OntologyNode) parent;
                            OntologyNode childNode = (OntologyNode) target;
                            childNode.setParent(parentNode);
                        }
                    }
                });
                return (OModel) unmarshaller.unmarshal(file);
            }
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        }
        /* exception occured, take default model */
        return new BasicModel();
    }

    public static void store(OModel model, File file) {
        try {
            JAXBContext jc = new JAXBStorage().createContext();

            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(model, file);
        } catch (JAXBException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }
}