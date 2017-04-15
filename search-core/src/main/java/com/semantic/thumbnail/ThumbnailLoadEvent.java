/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.thumbnail;

import java.io.File;
import java.util.EventObject;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ThumbnailLoadEvent extends EventObject{

    private File[] thumbnails;
    
    public ThumbnailLoadEvent(Object source, File... thumbnails) {
        super(source);
        this.thumbnails = thumbnails;
    }

    public File[] getThumbnails() {
        return thumbnails;
    }
}