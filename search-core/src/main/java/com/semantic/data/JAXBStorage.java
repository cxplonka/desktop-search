/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.data;

import com.semantic.data.xml.HashMapEntryTypeAdapter;
import com.semantic.data.xml.URITypeWrapper;
import com.semantic.model.*;
import com.semantic.model.filter.*;
import com.semantic.util.property.JAXBKey;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class JAXBStorage {
    
    private final List<Class> context = new ArrayList<Class>();

    public JAXBStorage() {
        initStorage();
    }

    private void initStorage() {
        addAdapter(URI.class, new URITypeWrapper());
        /* */
        addToContext(SNode.class);
        addToContext(JAXBKey.class);
        addToContext(OntologyNode.class);
        addToContext(OModel.class);
        addToContext(OMinMaxFilter.class);
        addToContext(OFileDateFilter.class);
        addToContext(OFileSizeFilter.class);
        addToContext(OImageAspectRatioFilter.class);
        addToContext(OImageResolutionFilter.class);
        addToContext(OKeywordFilter.class);
        addToContext(OMimeTypeFilter.class);
        addToContext(ODirectoryNode.class);
        addToContext(OFileSystem.class);
        addToContext(OMusicFilter.class);
        addToContext(OVectorFileFilter.class);        
        addToContext(OPowerPointFilter.class);
        addToContext(OGeoHashFilter.class);
        addToContext(ORGBFilter.class);
        addToContext(OHasUserTagFilter.class);
        addToContext(OHasGPSFilter.class);
        addToContext(IQueryGenerator.CLAUSE.class);
        addToContext(OWordFilter.class);
        addToContext(OCalcFilter.class);
        addToContext(OFixedFileDateFilter.class);
    }

    public void addToContext(Class clazz) {
        context.add(clazz);
    }

    public void removeFromContext(Class clazz){
        context.remove(clazz);
    }

    public <T> void addAdapter(Class<T> clazz, XmlAdapter<?, T> adapter) {
        addToContext(adapter.getClass());
        HashMapEntryTypeAdapter.addAdapter(clazz, adapter);
    }

    public <T> void removeAdapter(Class<T> clazz) {
        HashMapEntryTypeAdapter.removeAdapter(clazz);
    }

    public JAXBContext createContext() throws JAXBException {
        return JAXBContext.newInstance(context.toArray(new Class[context.size()]));
    }    
}