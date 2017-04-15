/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.crawler.filesystem;

import java.net.URI;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public interface DataSource {
 
    public URI getType();
    
    public String getName();    
}