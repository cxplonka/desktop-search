/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.data.xml;

import java.net.URI;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
@XmlRootElement(name = "uri")
@XmlAccessorType(XmlAccessType.NONE)
public class URITypeWrapper extends XmlAdapter<URITypeWrapper, URI> {

    private static URI base;
    @XmlElement(name = "uri")
    private String locator;

    public URITypeWrapper() {
    }

    public URITypeWrapper(String locator) {
        this.locator = locator;
    }

    public static void setBase(URI base) {        
        URITypeWrapper.base = base;        
    }

    public static URI getBase() {
        return base;
    }

    @Override
    public URI unmarshal(URITypeWrapper v) throws Exception {
        if (base != null) {
            return base.resolve(v.locator);
        }
        return URI.create(v.locator);
    }

    @Override
    public URITypeWrapper marshal(URI v) throws Exception {
        if(v == null){
            return null;
        }
        /**/
        if (base != null) {
            return new URITypeWrapper(base.relativize(v).toString());
        }
        return new URITypeWrapper(v.toString());
    }
}