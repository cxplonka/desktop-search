/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.thumbnail;

import java.io.File;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public interface Thumbnailer {

    public static final int DEFAULT_HEIGHT = 128;
    public static final int DEFAULT_WIDTH = 128;

    public void generateThumbnail(File input, File output, String mimeType) throws RuntimeException;

    public String[] getMimeTypes();
}