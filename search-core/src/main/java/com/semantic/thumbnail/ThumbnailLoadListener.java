/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.thumbnail;

import java.util.EventListener;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public interface ThumbnailLoadListener extends EventListener{
    
    public void thumbnailLoaded(ThumbnailLoadEvent event);
}