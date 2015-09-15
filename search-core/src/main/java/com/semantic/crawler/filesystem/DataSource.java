/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.crawler.filesystem;

import java.net.URI;

/**
 *
 * @author cplonka
 */
public interface DataSource {
 
    public URI getType();
    
    public String getName();    
}