/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.file.event;

import java.io.File;
import java.util.EventListener;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public interface FileSystemChangeListener extends EventListener {

    public void entryCreated(File file);

    public void entryDeleted(File file);

    public void entryModified(File file);
}