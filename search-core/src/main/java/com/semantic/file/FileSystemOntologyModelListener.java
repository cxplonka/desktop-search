/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.file;

import com.semantic.model.ODirectoryNode;
import com.semantic.model.OModel;
import com.semantic.model.OntologyNode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class FileSystemOntologyModelListener implements PropertyChangeListener {

    private final FileSystemWatch _watch;
    private OModel _model;

    public FileSystemOntologyModelListener(FileSystemWatch watch) {
        this._watch = watch;
    }

    public void setModel(OModel model) {
        if (this._model != null) {
            this._model.removePropertyChangeListener(this);
            register(model, false);
        }
        this._model = model;
        if (model != null) {
            model.addPropertyChangeListener(this);
            register(model, true);
        }
    }

    private void register(OntologyNode node, boolean register) {
        for (int i = 0; i < node.getNodeCount(); i++) {
            register(node.getChildAt(i), register);
        }
        if (node instanceof ODirectoryNode) {
            File file = ((ODirectoryNode) node).getDirectory();
            if (register) {
                _watch.registerAll(file);
            } else {
                _watch.unregisterService(file);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getNewValue() instanceof ODirectoryNode) {
            /* */
            File file = ((ODirectoryNode) pce.getNewValue()).getDirectory();
            /* un-/register directory on watch service */
            switch (pce.getPropertyName()) {
                case OntologyNode.PROPERTY_NODE_ADDED:
                    /* register service */
                    _watch.registerAll(file);
                    break;
                case OntologyNode.PROPERTY_NODE_REMOVED:
                    /* unregister service */
                    _watch.unregisterService(file);
                    break;
            }
        }
    }
}
